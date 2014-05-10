package cz.muni.fi.ib053.elevator.GUI;

import java.awt.Dimension;

import javax.swing.*;

import cz.muni.fi.ib053.elevator.ElevatorCabin;

public class Main {
	public static void main(String[] args) {
		String server;
		int port;
		String[] btnLabels;
		int groundLevel;

		// TODO: propper initialization
		server = "localhost";
		port = 8080;
		btnLabels = new String[] { "-2", "-1", "0", "1", "2", };
		groundLevel = 2;

		ElevatorCabin cabin = new ElevatorCabin(btnLabels, groundLevel, server,
				port);
		cabin.initializeConnection();

		Frame jms = new Frame(cabin);
		jms.setSize(new Dimension(500, 500));
		jms.setVisible(true);
		jms.setResizable(true);
	}
}
