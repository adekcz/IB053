package resources;

import cz.fi.muni.ib053_house.tcp.TCPServer;
import cz.fi.muni.ib053_house.tcp.TCPConnection;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestTCPServer {

	private static final int PORT = 8081;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//use different thread to listen to events.
		try {

			TCPServer listener = new TCPServer(PORT);
			TCPConnection connection = listener.waitForConnection();
            //should get from server...
			connection.write("INDIKACE;3;N\n");
			while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TestTCPServer.class.getName()).log(Level.SEVERE, null, ex);
                }
				if (connection.readerReady()) {
					String str = connection.readLine();
					System.out.println("--> " + str);
					if (str.contains("Q")) {
						connection.write("Q");
						break;
					}
				}
			}
			connection.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
