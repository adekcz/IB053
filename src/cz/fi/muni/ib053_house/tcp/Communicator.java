/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fi.muni.ib053_house.tcp;

import cz.fi.muni.ib053_house.gui.House;
import cz.fi.muni.ib053_house.gui.HouseController;
import cz.fi.muni.ib053_house.settings.Commands;
import static cz.fi.muni.ib053_house.settings.Commands.INDIKACE;
import static cz.fi.muni.ib053_house.settings.Commands.JIZDA;
import static cz.fi.muni.ib053_house.settings.Commands.PANEL;
import cz.fi.muni.ib053_house.settings.Events;
import static cz.fi.muni.ib053_house.settings.Settings.runtimeSettings;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michal Keda
 */
public class Communicator {

    public static TCPConnection transmitter;
    //remove cyclic dependency house -> communicator -> house
    private HouseController houseController;
    volatile private boolean killYourself = false;



    public Communicator() {
        this.houseController = HouseController.getInstance();
        houseController.setSettingsArea();
        try {
            transmitter = new TCPConnection(runtimeSettings.getServerUrl(), runtimeSettings.getControlUnitPort());
            inicializace(runtimeSettings.getFloorCount(), runtimeSettings.getGroundZero());
        } catch (IOException ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, ex);
        }

        new Thread() {
            @Override
            public void run() {
                while (!killYourself) {
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        if (transmitter.readerReady()) {
                            String[] command = transmitter.readLine().split(";");

                            switch (Commands.valueOf(command[0])) {
                                case INDIKACE:
                                    houseController.indikace(Integer.parseInt(command[1]), Commands.IndikaceStav.valueOf(command[2]));
                                    break;
                                case JIZDA:
                                    houseController.jizda(Commands.JizdaSmer.getEnum(command[1]));
                                    break;
                                case PANEL:
                                    houseController.panel(Commands.PanelSmer.valueOf(command[1]), Integer.parseInt(command[2]));
                                    break;

                            }
                            System.out.println("RECIEVED: " + Arrays.toString(command));
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex){
                        Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    transmitter.close();
                } catch (IOException ex) {
                    Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    //will be private
    public void sendMessage(String str) {
        System.out.println("SENDING: " + str);
        try {
            transmitter.write(str + "\n");
        } catch (IOException ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void inicializace(int floorCount, int groundLevel) {
        sendMessage(Events.INICIALIZACE + ";" + floorCount + ";" + groundLevel);
    }

    public void pohyb(Events.Pohyb state) {
        sendMessage(Events.POHYB + ";" + state);

    }

    public void poloha(Events.Poloha sensorType) {
        sendMessage(Events.POLOHA + ";" + sensorType);

    }

    public void tlacitko(int floor) {
        sendMessage(Events.TLACITKO + ";" + floor);
    }
    public boolean isKillYourself() {
        return killYourself;
    }

    public void setKillYourself(boolean killYourself) {
        this.killYourself = killYourself;
    }
}
