package cz.muni.fi.ib053.elevator;

import java.util.Arrays;

public class ElevatorCabin {

	private int levelCount;
	private int groundLevel;
	private String[] levelLabels;
	private LightState[] buttonLight;
	private CabinClient tcpClient;
	private boolean lightsOn;

	public ElevatorCabin(String[] labels, int groundLevel, String server,
			int port) {
		if (labels.length == 0)
			throw new IllegalArgumentException(
					"Constructor of elevator cabin requires array of minimal length 1");

		levelCount = labels.length;
		levelLabels = new String[levelCount];
		System.arraycopy(labels, 0, levelLabels, 0, levelCount);

		buttonLight = new LightState[levelCount];
		Arrays.fill(buttonLight, LightState.DARK);

		tcpClient = new CabinClient(server, port, this);
		this.groundLevel = groundLevel;
	}

	public int getLevelCount() {
		return levelCount;
	}

	public int getGroundLevel() {
		return groundLevel;
	}

	public LightState getLightState(int level) {
		return buttonLight[level];
	}

	public String getLevelLabel(int level) {
		return levelLabels[level];
	}

	public boolean lightsOn() {
		return lightsOn;
	}

	public void turnOnTheLights() {
		lightsOn = true;
		update();
	}

	public void turnOffTheLights() {
		lightsOn = false;
		update();
	}

	public void lvlBtnPressed(int level) {
		tcpClient.lvlBtnPressed(level);
	}

	public void changeBtnLight(int level, LightState state) {
		if (level < 0 || level >= levelCount)
			throw new IllegalArgumentException();

		buttonLight[level] = state;
		update();
	}

	public void initializeConnection() {
		tcpClient.initialize();
	}

	public void update() {
		// TODO: fire an event ??????
	}

	public enum LightState {
		DARK, SHINE, FLASH;
	}

}
