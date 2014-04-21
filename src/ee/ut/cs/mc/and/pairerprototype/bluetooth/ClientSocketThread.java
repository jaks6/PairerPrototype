package ee.ut.cs.mc.and.pairerprototype.bluetooth;


public class ClientSocketThread extends SocketThread {
	private static ClientSocketThread instance = null;
	public ClientSocketThread() {
		super();
		type = "Client";
	}
	public static SocketThread getInstance() {
        if(instance == null) {
           instance = new ClientSocketThread();
        }
        return instance;
     }
    
}