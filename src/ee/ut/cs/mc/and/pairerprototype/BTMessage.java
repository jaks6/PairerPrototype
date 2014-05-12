package ee.ut.cs.mc.and.pairerprototype;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import android.os.Environment;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommon;

public class BTMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5942052673102206578L;
	public static final int TYPE_CHATMESSAGE = 0;
	public static final int TYPE_FILE = 1;
	
	int type;
	String chatContent;
	File fileContent;
	byte[] fileBytes;
	String author;
	String authorNick;
	String fromMAC;
	String destinationNick;
	
	
	public BTMessage(String chatContent, String authorNick, String fromMAC,
			String destinationNick) {
		this.type = TYPE_CHATMESSAGE;
		this.chatContent = chatContent;
		this.authorNick = authorNick;
		this.fromMAC = fromMAC;
	}
	
	public BTMessage(File fileContent, String destinationNick) {
		this.type = TYPE_FILE;
		
		this.destinationNick = destinationNick;
		
		this.fileContent = fileContent;
		try {
			fileBytes = org.apache.commons.io.FileUtils.readFileToByteArray(fileContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.authorNick= App.getUserNick();
		this.fromMAC = BTCommon.deviceMAC;
	}

	public BTMessage(String chatContent, String destinationNick) {
		this.type = TYPE_CHATMESSAGE;
		
		this.destinationNick = destinationNick;
		this.chatContent = chatContent;
		this.authorNick= App.getUserNick();
		this.fromMAC = BTCommon.deviceMAC;
	}
	
	public String toString(){
		String result = "";
		if (this.type == TYPE_CHATMESSAGE){
			if (this.authorNick == App.getUserNick()){ 
				result+= "Me: ";
			} else {
				result += this.authorNick +": " ;
			}
			return result+ this.chatContent;
		}
		return result;
	}
	
	File getFileFromBytes() throws IOException{
		File result = null;
		if (fileBytes != null){
			File downloadsPath = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS);
			fileContent.getName();
			result = new File(downloadsPath.getAbsolutePath() + fileContent.getName());
			org.apache.commons.io.FileUtils.writeByteArrayToFile(
					result, fileBytes);
		}
		return result;
		
	}

}
