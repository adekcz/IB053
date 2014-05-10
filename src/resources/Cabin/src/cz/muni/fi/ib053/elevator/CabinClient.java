package cz.muni.fi.ib053.elevator;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import cz.muni.fi.ib053.elevator.ElevatorCabin.LightState;

public class CabinClient {

	private TCPConnection connection;
	private ElevatorCabin cabin;

	public CabinClient(String server, int port, ElevatorCabin cabin) {
		this.cabin = cabin;
		try {
			connection = new TCPConnection(server, port);
			(new Thread(new TcpListeningThread())).start();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send(String message) {
		System.out.println("Cabin: Trying to send: " + message);
		try {
			connection.write(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void lvlBtnPressed(int level) {
		send("TLACITKO;" + level + "\n");
	}

	public void initialize() {
		send("INICIALIZACE;" + cabin.getLevelCount() + ";"
				+ cabin.getGroundLevel() + "\n");
	}

	private class TcpListeningThread implements Runnable {

		public TcpListeningThread() {
			// Nothing to do...
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (connection.readerReady()) {
						String str = connection.readLine();
						System.out.println("Cabin: Received: " + str);
						executeMessage(str);
					}
				}
			} catch (IOException e) {
				System.err.println("Connection problem");
			}
		}

		private void executeMessage(String message) {
			String[] tokens = message.split(";");
			if (tokens.length < 1)
				return;

			switch (tokens[0]) {
			case "OTEVRI":
				break;
			case "ZAVRI":
				break;
			case "ROZSVIT":
				cabin.turnOnTheLights();
				break;
			case "ZHASNI":
				cabin.turnOffTheLights();
				break;
			case "PANEL":
				break;
			case "INDIKACE":
				if (tokens.length != 3)
					return; // maybe throw or log
				int level;
				try {
					level = Integer.parseInt(tokens[1]);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					return;
				}
				switch (tokens[2]) {
				case "S":
					cabin.changeBtnLight(level, LightState.SHINE);
					break;
				case "N":
					cabin.changeBtnLight(level, LightState.DARK);
					break;
				case "B":
					cabin.changeBtnLight(level, LightState.FLASH);
					break;
				default:
					return; // +log something
				}
				break;
			default: // log something
			}

		}
	}
}