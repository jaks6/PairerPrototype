package ee.ut.cs.mc.and.pairerprototype;

import java.io.IOException;
import java.io.ObjectOutputStream;

import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommon;

public class Chat {

	private static final String TAG = Chat.class.getName();;

	public static void sendMessage(ChatMsg message, ObjectOutputStream oos){
		Log.i(TAG, "Started writing stream");
		if (oos==null){
			return;
		}
		//Update "from"
		message.from = BTCommon.deviceMAC;
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.v("","Finished outputstream");
	}
}
