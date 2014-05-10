package cz.muni.fi.ib053;

import java.io.IOException;

public class TestTCPClient {

	private static final String server = "localhost";
	private static final int PORT = 8080;
	private static final String [] str = { "jedna", "dve", "tri", "ctyri", "Q" };
	private static final long WAIT_TIME_MILLIS = 2000;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			TCPConnection connection = new TCPConnection(server, PORT);
			connection.write("Zdar!\n");
			int uk = 0;
			long lastWrite = 0;
			while (true) {
				if (connection.readerReady()) {
					String str = connection.readLine();
					if (str.contains("Q")) {
						break;
					}
					System.out.println("--> " + str);
				}
				if (uk < str.length && (lastWrite == 0 || lastWrite + WAIT_TIME_MILLIS < System.currentTimeMillis())) {
					System.out.println(str[uk] + " -->");
					connection.write(str[uk] + "\n");
					uk++;
					lastWrite = System.currentTimeMillis();
				}
			}
			connection.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
