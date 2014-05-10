package cz.muni.fi.ib053;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

public class TCPServer {

    private ServerSocket serverSocket;

	public TCPServer(int port) throws IOException {

        try {
            // Otev�e serverov� socket.
            serverSocket = new ServerSocket(port);
            // Zisk� adresu pro v�echna s�ov� spojen�.
        } catch (IOException ex) {
            throw new IOException("TCP-Port nelze otevrit: " + port + "; ", ex);
        }
	}


	// �ek� na p��choz� spojen�, ve v�ce vl�knech lze �ekat na v�ce spojen� na t�m�e portu
    public TCPConnection waitForConnection() throws IOException {
        Socket socket = null;
        socket = serverSocket.accept();
        return new TCPConnection(socket);
    }


}
