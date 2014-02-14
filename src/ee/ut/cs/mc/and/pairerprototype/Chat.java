package ee.ut.cs.mc.and.pairerprototype;

import java.io.IOException;
import java.io.PrintWriter;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Chat {

	private BluetoothSocket socket = null;
	private Handler handler;

	public Chat(Handler handler, BluetoothSocket socket){
		this.socket = socket;
		this.handler = handler;
	}

	public void sendMessage(String message){
		Log.i("sendData", "Started writing stream");
		if (! socketExists()) return;

		try {
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			writer.write(message);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.v("","Finished outputstream");
	}


	private boolean socketExists() {
		if(socket==null){
			//Notify UI of connected status:
			Message msg_Toast = handler.obtainMessage();
			msg_Toast.what = MainActivityHandler.DISPLAY_TOAST;
			msg_Toast.obj = "No connection (socket null)";
			handler.sendMessage(msg_Toast);
			return false;
		}
		return true;
	}

}
