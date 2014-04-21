package ee.ut.cs.mc.and.pairerprototype.bluetooth;


public class ServerSocketThread extends SocketThread {
	private static ServerSocketThread instance = null;
	public ServerSocketThread() {
		super();
		type = "Server";
	}
	public static SocketThread getInstance() {
        if(instance == null) {
           instance = new ServerSocketThread();
        }
        return instance;
     }
    
}