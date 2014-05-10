/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package elevatorcontrolunit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private ServerSocket serverSocket;

	public TCPServer(int port) throws IOException {

        try {
            // Otevøe serverový socket.
            serverSocket = new ServerSocket(port);
            // Ziská adresu pro všechna sí�ová spojení.
        } catch (IOException ex) {
            throw new IOException("Impossible to open TCP-port: " + port + "; ", ex);
        }
	}


	// èeká na pøíchozí spojení, ve více vláknech lze èekat na více spojení na témže portu
    public TCPConnection waitForConnection() throws IOException {
        Socket socket = serverSocket.accept();
        return new TCPConnection(socket);
    }


}