package cz.muni.fi.ib053.elevator.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cz.muni.fi.ib053.elevator.ElevatorCabin;

public class Frame extends JFrame {
	private ElevatorCabin cabin;
	private JPanel mainPanel;

	public Frame(ElevatorCabin cabin) {
		this.cabin = cabin;
		mainPanel = new JPanel();
		this.add(mainPanel);

		for (int i = 0; i < cabin.getLevelCount(); i++) {
			JButton btn = new JButton("Button " + cabin.getLevelLabel(i));

			btn.addActionListener(new LevelButtonListener(cabin, i));

			mainPanel.add(btn);
		}
	}

	private class LevelButtonListener implements ActionListener {
		private ElevatorCabin cabin;
		private int level;

		public LevelButtonListener(ElevatorCabin cabin, int level) {
			this.cabin = cabin;
			this.level = level;
		}

		public void actionPerformed(ActionEvent e) {
			cabin.lvlBtnPressed(level);
			;
		}
	}

}
