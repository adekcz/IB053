/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elevatorcontrolunit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Tobias
 */
public class ElevatorControlUnit {

    private static final int PORT_CABIN = 8080;
    private static final int PORT_HOUSE = 8081;
    private static final long WAIT_TIME = 50000;
    private static int houseFloors;
    private static int groundFloor;
    private static char movementHouse = 'P'; //S,P,J
    private static char movementCabin = 'K'; //N,D,S,K,P/O,C
    private static String load = "0";  // 0,10,100,101
    private static List<Integer> floors;
    private static int currentFloor;
    private static long openedTime = 0;
    private static TCPServer cabin = null;
    private static TCPServer house = null;
    private static TCPConnection fromCabin = null;
    private static TCPConnection fromHouse = null;

    /**
     * @param connection
     * @throws java.io.IOException
     */
    public static void inicialization(TCPConnection connection) throws IOException {
        while (true) {
            if (connection.readerReady()) {
                String tmp = connection.readLine();
                String init[] = tmp.split(";", 3);
                if (init.length >= 2) {
                    if (!init[0].equals("INICIALIZATION")) {
                        throw new IOException("Missing inicialization event");
                    }
                    int floorTmp = Integer.parseInt(init[1]);
                    if (houseFloors == 0) {
                        houseFloors = floorTmp;
                    } else if (houseFloors != floorTmp) {
                        fromCabin.close();
                        fromHouse.close();
                        throw new IOException("Number of floors doesn't match expected number");
                    }
                    if (init.length == 3) {
                        groundFloor = Integer.parseInt(init[2]);
                    } else if (init.length > 3) {
                        throw new IOException("Wrong inicialization event");
                    }
                    return;
                }
            }
        }
    }

    public static void buttonHouse(String event[]) throws IOException {
        int patro = Integer.parseInt(event[1]);
        if (patro != currentFloor && floors.isEmpty()) {
            floors.add(patro);
        } else if (patro == currentFloor) {
            if (movementCabin == 'K') {
                buttonOpen();
                return;
            } else {
                return;
            }
        } else {
            if (floors.contains(patro)) {
                return;
            }
            int directionFut = floors.get(0) - currentFloor;
            int directionWan = patro - currentFloor;
            if (directionFut * directionWan < 0) {
                return;
            }
            if (directionFut * directionWan > 0) {
                floors.add(patro);
                Collections.sort(floors);
            }

            //kdyz 0, zrovna projizdi kolem nebo stoji, ale to asi smula
        }
        fromHouse.write("INDIKACE;" + patro + ";S\n");
        fromCabin.write("INDIKACE;" + patro + ";B\n");
    }

    public static void buttonCabin(String event[]) throws IOException {
        int patro = Integer.parseInt(event[1]);
        if (floors.isEmpty()) {
            if (patro != currentFloor) {
                floors.add(patro);
            } else {
                buttonOpen();
                return;
            }
        } else {
            if (floors.contains(patro)) {
                return;
            }
            int directionFut = floors.get(0) - currentFloor;
            int directionWan = patro - currentFloor;
            if (directionFut * directionWan < 0) {
                return;
            }
            if (directionFut * directionWan > 0) {
                floors.add(patro);
                Collections.sort(floors);
            }
            if (directionFut * directionWan == 0) {
                if (movementHouse == 'S' && movementCabin != 'K') {
                    buttonOpen();
                    return;
                }
            }
        }
        fromHouse.write("INDIKACE;" + patro + ";B\n");
        fromCabin.write("INDIKACE;" + patro + ";S\n");
    }

    public static void buttonOpen() throws IOException {
        if (movementCabin != 'O' && movementHouse == 'P') {
            if (load.equals("0")) {
                fromCabin.write("ROZSVIT\n");
            }
            fromCabin.write("OPEN\n");
            movementCabin = 'O';
            System.out.println("open");
        }
    }

    public static void buttonClose() throws IOException {
        if (movementCabin == 'O' || movementCabin == 'S') {
            fromCabin.write("CLOSE\n");
            movementCabin = 'C';
            System.out.println("close");
        }
    }

    public static void go() throws IOException {
        if (load.equals("0") && floors.isEmpty()) {
            movementCabin = 'K';
            fromCabin.write("ZHASNI\n");
            fromHouse.write("PANEL;K;" + currentFloor + "\n");
        } else if (!floors.isEmpty()) {
            if (currentFloor < floors.get(0)) {
                fromHouse.write("JIZDA;N\n");
                movementCabin = 'N';
            } else {
                fromHouse.write("JIZDA;D\n");
                movementCabin = 'D';
            }
        }
    }

    public static void door(String event[]) throws IOException {
        switch (event[1]) {
            case "O":
                movementCabin = 'S';
                fromCabin.write("PANEL;S;" + currentFloor + "\n");
                fromHouse.write("PANEL;S;" + currentFloor + "\n");
                openedTime = System.currentTimeMillis();
                System.out.println("opening finished");
                break;
            case "Z":
                System.out.println("closing fnished");
                    go();
                    break;
            default:
                throw new IllegalArgumentException(event[1]);
        }
    }

    public static void movement(String event[]) throws IOException {
        switch (event[1]) {
            case "S":
                movementHouse = 'S';

                //fuuuck
                break;
            case "P":
                movementHouse = 'P';
                fromHouse.write("INDIKACE;" + currentFloor + ";N\n");
                fromCabin.write("INDIKACE;" + currentFloor + ";N\n");
                floors.remove(Integer.valueOf(currentFloor));
                fromCabin.write("OPEN\n");
                movementCabin = 'O';

                break;
            case "J":
                movementHouse = 'J';

                fromCabin.write("PANEL;" + movementCabin + ";" + currentFloor + "\n");
                fromHouse.write("PANEL;" + movementCabin + ";" + currentFloor + "\n");

                break;
            default:
                throw new IllegalArgumentException("Wrong parameter!");

        }
    }

    public static void load(String event[]) throws IOException {
        switch (event[1]) {
            case "0":

                break;
            case "10":

                break;
            case "100":
                movementCabin = 'S';
                break;
            case "101":
                movementCabin = 'P';
                fromCabin.write("PATRO;P;" + currentFloor + "\n");
                break;
            default:
                throw new IllegalArgumentException(event[1]);
        }
        load = event[1];
    }

    public static void errorStatus(String event[]) {
        if (event[1].equals("D")) {

        }
    }

    public static void readEventCabin() throws IOException {
        String event[] = fromCabin.readLine().split(";");
        System.out.println("Cabin: ");
        for (String s : event) {
            System.out.print(s);
        }
        System.out.println();
        switch (event[0]) {
            case "TLACITKO":
                buttonCabin(event);
                System.out.println(floors.toString());
                break;
            case "TLACITKO_OTEVRI":
                buttonOpen();

                break;
            case "TLACITKO_ZAVRI":
                buttonClose();

                break;
            case "DVERE":
                door(event);
                //System.out.println("doors");
                break;
            case "ZATIZENI":
                load(event);
                System.out.println("zatizeni: " + load);
                break;
            case "PRUCHOD":
                openedTime = System.currentTimeMillis();
                break;
            case "CHYBOVY_STAV":
                errorStatus(event);
                System.out.println("errorStatus");
                break;
            default:
                throw new UnsupportedOperationException("Unsupported event: " + event[0]);
        }
    }

    public static void position(String event[]) throws IOException {
        switch (event[1]) {
            case "P":
                if (movementCabin == 'N' && floors.contains(currentFloor + 1)) {
                    fromHouse.write("JIZDA;P\n");
                }
                break;
            case "S":
                if (movementCabin == 'N') {
                    ++currentFloor;
                } else {
                    --currentFloor;
                }

                if (floors.contains(currentFloor)) {
                    fromHouse.write("JIZDA;0\n");
                } else {
                    fromCabin.write("PANEL;" + movementCabin + ";" + currentFloor + "\n");
                    fromHouse.write("PANEL;" + movementCabin + ";" + currentFloor + "\n");
                }
                break;
            case "N":
                if (movementCabin == 'D' && floors.contains(currentFloor - 1)) {
                    fromHouse.write("JIZDA;P\n");
                }
                break;
            case "KD":
                fromHouse.write("JIZDA;0\n");
                break;
            case "KN":
                fromHouse.write("JIZDA;0\n");
                break;
            case "S0":
                fromHouse.write("JIZDA;0\n");
                break;
            default:
                throw new IllegalArgumentException(event[1]);
        }
        System.out.println("Jizda: " + movementCabin + " floor: " + currentFloor);
    }

    public static void readEventHouse() throws IOException {
        String event[] = fromHouse.readLine().split(";");
        System.out.println("House: ");
        for (String s : event) {
            System.out.print(s);
        }
        System.out.println();
        switch (event[0]) {
            case "POHYB":
                movement(event);
                System.out.println("Movement: " + movementHouse);
                break;
            case "POLOHA":
                position(event);
                break;
            case "TLACITKO":
                buttonHouse(event);
                System.out.println(floors.toString());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported event: " + event[0]);
        }
    }

    public static void main(String[] args) {

        try {
            cabin = new TCPServer(PORT_CABIN);
            house = new TCPServer(PORT_HOUSE);
            fromCabin = null;
            fromHouse = null;
            while (fromCabin == null && fromHouse == null) {
                if (fromCabin == null) {
                    fromCabin = cabin.waitForConnection();
                }
                if (fromHouse == null) {
                    fromHouse = house.waitForConnection();
                }
            }

            inicialization(fromCabin);
            inicialization(fromHouse);
            floors = new ArrayList<>();
            currentFloor = groundFloor;
            fromHouse.write("PANEL;K;" + currentFloor + "\n");
            fromCabin.write("PANEL;K;" + currentFloor + "\n");
            System.out.println(houseFloors + " " + groundFloor);

            while (true) {
                if (fromCabin.readerReady()) {
                    readEventCabin();
                }
                if (fromHouse.readerReady()) {
                    readEventHouse();
                }
                if (movementCabin == 'S' && System.currentTimeMillis() - openedTime > WAIT_TIME) {
                    buttonClose();
                }
                if (movementHouse == 'P' && movementCabin != 'S' && movementCabin != 'C' && !floors.isEmpty() && !load.equals("0")) {
                    go();
                }
            }

        } catch (IOException | IllegalArgumentException ioe) {
            System.err.println("Connection failed!");
            ioe.printStackTrace();
        }
    }

}
