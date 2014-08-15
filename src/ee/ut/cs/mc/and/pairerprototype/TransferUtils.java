package ee.ut.cs.mc.and.pairerprototype;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommon;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.ServerSocketThread;

public class TransferUtils {

	private static final String TAG = TransferUtils.class.getName();

	/** Checks if a given message should be broadcasted to other devices
	 *  and does  so where need be.*/
	public static void rebroadCastMsg(BTMessage message){
		ServerSocketThread serverThread = (ServerSocketThread) ServerSocketThread.getInstance();
//		ClientSocketThread clientThread = (ClientSocketThread) ClientSocketThread.getInstance();
		ArrayList<ObjectOutputStream> streams = getOutPutStreams();

		
//		BluetoothSocket[] sockets = {serverThread.getSocket()};
		ArrayList<BluetoothSocket> socketList = new ArrayList<BluetoothSocket>();
		socketList.add(serverThread.getSocket());
		socketList.addAll(BTCommon.getSockets());
		
		//broadcast to other BT devices
		Log.d(TAG, "Starting rebroadcast checks");
		for (int i= 0; i<streams.size(); i++){
			if (streams.get(i) !=null && socketList.get(i) != null &&
					!socketList.get(i).getRemoteDevice().getAddress().equals(message.fromMAC)){
				Log.d(TAG, String.format("Rebroadcasting, SOCKETADDRESS= %s, FROM=%s",
						socketList.get(i).getRemoteDevice().getAddress(),
						message.fromMAC));
				writeMsgToOOS(message, streams.get(i));
				return; //this return ensures were only sending data one-way
			}
		}
	}


	private static ArrayList<ObjectOutputStream> getOutPutStreams() {
		ArrayList<ObjectOutputStream> resultArray = new ArrayList<ObjectOutputStream>();
		resultArray.add(ServerSocketThread.getInstance().getObjectOutStream());
		resultArray.addAll(BTCommon.getOOSes());
		return resultArray;
	}

	private static void writeMsgToOOS(BTMessage message, ObjectOutputStream oos) {
		if (oos==null){
			return;
		}
		//Update "from" field which allows for rebroadcasting to work properly
		message.fromMAC = BTCommon.deviceMAC;
		try {
			Log.i(TAG, "Started writing stream");
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.v("","Finished outputstream");
	}

	public static void sendMessage(BTMessage chatMsg) {
		ArrayList<ObjectOutputStream> ooses = getOutPutStreams();
		for (ObjectOutputStream oos : ooses){
			writeMsgToOOS(chatMsg, oos);
		}
	}
}
