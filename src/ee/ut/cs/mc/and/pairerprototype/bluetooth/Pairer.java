package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetooth;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class Pairer {

	protected String TAG = "Pairer";
	BroadcastReceiver receiver = null;
	IBluetooth ib;
	public Pairer(Context context) {

		ib = getIBluetooth();
		registerFilter(context);
	}

	private void registerFilter(Context context){
		Log.d(TAG, "Registering intent filter");
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				//do something based on the intent's action
				Log.d(TAG , "Received Pairing Request Intent");
				
				BluetoothDevice device = intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
				Log.d(TAG, device.getName());
				Log.d(TAG, "adapter="+BluetoothAdapter.getDefaultAdapter().getName());
				setBluetoothPairingPin(device);
				// set PIN
//				try {
//					Class<?> btDeviceInstance = Class.forName(BluetoothDevice.class.getCanonicalName());
//
//					Method convert = btDeviceInstance.getMethod("convertPinToBytes", String.class);
//					byte[] pin = (byte[]) convert.invoke(device, "1234");
//					
//					ib.setPin(device.getAddress(), pin);
//					device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
//					
//				} catch (NoSuchMethodException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (ClassNotFoundException e1) {
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					e.printStackTrace();
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
	            
	            
			}
		};
		context.registerReceiver(receiver, filter);
	}
	
	public void setBluetoothPairingPin(BluetoothDevice device)
	{
		
	    try {
	    	Class<?> btDeviceInstance = Class.forName(BluetoothDevice.class.getCanonicalName());
	    	Method convert = btDeviceInstance.getMethod("convertPinToBytes", String.class);
	    	byte[] pinBytes = (byte[]) convert.invoke(device, "1234");
	    	
	          Log.d(TAG, "Try to set the PIN");
	          Method m = device.getClass().getMethod("setPin", byte[].class);
	          m.invoke(device, pinBytes);
	          Log.d(TAG, "Success to add the PIN.");
	          try {
	        	  Thread.sleep(3000);
	                device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
	                BluetoothAdapter.getDefaultAdapter().getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
	                Log.d(TAG, "Success to setPairingConfirmation.");
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                Log.e(TAG, e.getMessage());
	                e.printStackTrace();
	            } 
	        } catch (Exception e) {
	          Log.e(TAG, e.getMessage());
	          e.printStackTrace();
	        }
	}

	/** Source for the following method:
	 * http://stackoverflow.com/questions/3462968/how-to-unpair-bluetooth-device-using-android-2-1-sdk
	 */
	private IBluetooth getIBluetooth() {
		IBluetooth ibt = null;

		try {
			Class c2 = Class.forName("android.os.ServiceManager");

			Method m2 = c2.getDeclaredMethod("getService",String.class);
			IBinder b = (IBinder) m2.invoke(null, "bluetooth");

			Class c3 = Class.forName("android.bluetooth.IBluetooth");

			Class[] s2 = c3.getDeclaredClasses();

			Class c = s2[0];
			Method m = c.getDeclaredMethod("asInterface",IBinder.class);
			m.setAccessible(true);
			ibt = (IBluetooth) m.invoke(null, b);


		} catch (Exception e) {
			Log.e("Pairer.java", "Exception " + e.getMessage());
		}

		return ibt;
	}

	public void destroy(Context context) {
		context.unregisterReceiver(receiver);		
	}

}
