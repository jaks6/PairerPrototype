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
	String fromNick;
	String authorNicK;
	
	public ChatMsg(String content, String from, String fromNick) {
		this.content = content;
		this.from = from;
		this.fromNick = fromNick;
	}
	
	public ChatMsg(String content) {
		this.content = content;
		this.fromNick = App.getUserNick();
		this.from = BTCommon.deviceMAC;
		this.author = App.getUserNick();
	}

	@Override
	public String toString() {
		if (author.equals(App.getUserNick())){
			return "Me: "+ content;
		}
		return author +": "+content;
	}

}
