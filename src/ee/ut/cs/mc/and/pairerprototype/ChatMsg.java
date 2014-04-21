package ee.ut.cs.mc.and.pairerprototype;

import java.io.Serializable;

import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommon;

public class ChatMsg implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5638569254949554692L;
	String content;
	String author;
	String from;
	
	public ChatMsg(String content, String from) {
		this.content = content;
		this.from = from;
	}
	
	public ChatMsg(String content) {
		this.content = content;
		this.from = BTCommon.deviceMAC;
		this.author = BTCommon.deviceMAC;
	}

	@Override
	public String toString() {
		if (author.equals(BTCommon.deviceMAC)){
			return "Me: "+ content;
		}
		return content;
	}

}
