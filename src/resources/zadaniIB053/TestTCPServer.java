package cz.muni.fi.ib053;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class TestTCPServer {

	private static final int PORT = 8081;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			TCPServer listener = new TCPServer(PORT);
			TCPConnection connection = listener.waitForConnection();
			connection.write("Ahoj!\n");
			while (true) {
				if (connection.readerReady()) {
					String str = connection.readLine();
					System.out.println("--> " + str);
					if (str.contains("Q")) {
						connection.write("Q");
						break;
					}
					connection.write("Diky: [" + str + "]\n");
				}
			}
			connection.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
