package cz.fi.muni.ib053_house.gui;

import cz.fi.muni.ib053_house.entities.ElevatorStatus;
import cz.fi.muni.ib053_house.entities.FloorGuiElements;
import cz.fi.muni.ib053_house.entities.HouseGuiElements;
import cz.fi.muni.ib053_house.settings.Commands;
import cz.fi.muni.ib053_house.settings.Events;
import static cz.fi.muni.ib053_house.settings.Settings.runtimeSettings;
import cz.fi.muni.ib053_house.tcp.Communicator;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class HouseController {

    private HouseGuiElements house;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Rectangle elevator;

    @FXML
    private Label helloLabel;

    @FXML
    private Label lblControlUnitPort;

    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane canvas;

    @FXML
    private Label lblCurrentFloor;

    @FXML
    private Label lblFloorCount;

    @FXML
    private Label lblGroundZero;

    @FXML
    private Label lblServerUrl;

    @FXML
    private TextArea txtAreaOutput;
    @FXML
    private BorderPane brdrPn;

 @FXML
    private CheckBox chckBxSlow;

    private static HouseController instance;
    private Communicator comm;

    public static HouseController getInstance() {
        return instance;
    }

    public Communicator getCommunicator() {
        return comm;
    }

    public HouseGuiElements getHouse() {
        return house;
    }

    @FXML
    void initialize() {

        assert elevator != null : "fx:id=\"elevator\" was not injected: check your FXML file 'HouseFxml.fxml'.";
        assert helloLabel != null : "fx:id=\"helloLabel\" was not injected: check your FXML file 'HouseFxml.fxml'.";
        assert lblControlUnitPort != null : "fx:id=\"lblControlUnitPort\" was not injected: check your FXML file 'HouseFxml.fxml'.";
        assert lblCurrentFloor != null : "fx:id=\"lblCurrentFloor\" was not injected: check your FXML file 'HouseFxml.fxml'.";
        assert lblFloorCount != null : "fx:id=\"lblFloorCount\" was not injected: check your FXML file 'HouseFxml.fxml'.";
        assert lblGroundZero != null : "fx:id=\"lblGroundZero\" was not injected: check your FXML file 'HouseFxml.fxml'.";
        assert lblServerUrl != null : "fx:id=\"lblServerUrl\" was not injected: check your FXML file 'HouseFxml.fxml'.";
        assert txtAreaOutput != null : "fx:id=\"txtAreaOutput\" was not injected: check your FXML file 'HouseFxml.fxml'.";

        AnchorPane.setBottomAnchor(canvas, 0.0);
        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setTopAnchor(canvas, 0.0);

        AnchorPane.setBottomAnchor(brdrPn, 0.0);
        AnchorPane.setLeftAnchor(brdrPn, 0.0);
        AnchorPane.setRightAnchor(brdrPn, 0.0);
        AnchorPane.setTopAnchor(brdrPn, 0.0);

        instance = this;

        comm = new Communicator();
        house = new HouseGuiElements(runtimeSettings.getGroundZero(), runtimeSettings.getFloorCount());

        canvas.setPrefSize(house.getWidth(), house.getHeight());
        canvas.getChildren().addAll(house.getAllElementsUnmodifiable());

    }
  

    public void indikace(int patro, Commands.IndikaceStav stav) {
        final String message = "Dum by mel indikovat vytah v patre: " + patro + " ve stavu: " + stav + "\n";

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                house.getFloors().get(patro).indikace(stav);
            }
        });
        System.out.println(message);

    }

    public void jizda(Commands.JizdaSmer smer) {
                switch (smer) {
                    case D:
                        house.setElevatorStatus(ElevatorStatus.DOWN_NORMAL);
                        comm.pohyb(Events.Pohyb.J);
                        break;
                    case N:
                        house.setElevatorStatus(ElevatorStatus.UP_NORMAL);
                        comm.pohyb(Events.Pohyb.J);
                        break;
                    case P:
                        if (house.getElevatorStatus().equals(ElevatorStatus.DOWN_NORMAL)) {
                            house.setElevatorStatus(ElevatorStatus.DOWN_SLOW);
                            comm.pohyb(Events.Pohyb.J);
                        }
                        if (house.getElevatorStatus().equals(ElevatorStatus.UP_NORMAL)) {
                            house.setElevatorStatus(ElevatorStatus.UP_SLOW);
                            comm.pohyb(Events.Pohyb.J);
                        }
                        break;
                    case O:
                        house.setElevatorStatus(ElevatorStatus.STILL);
                        break;
                }
                
                house.moveElevator();
        final String message = "Dum by mel naanimovat vytah smerem: " + smer + "\n";
        System.out.println(message);
        
    }
    
    public void panel(Commands.PanelSmer smer, int patro) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String text = (patro < 10 ? "0" + patro : patro) + "\t";
                house.controlDoors(smer);
                switch (smer) {
                    case D:
                        text += "\u2193"; //sipka dolu
                        break;
                    case K:
                        text += "KLID";
                        
                        break;
                    case N:
                        text += "\u2191"; //sipka nahoru
                        break;
                    case S:
                        text += "STOJI";
                        break;
                }
                for (FloorGuiElements floor : house.getFloors()) {
                    floor.getStatus().setText(text);
                }
            }
        });
        final String message = "Dum by mel blikat panely smer: " + smer + " k patru: " + patro + "\n";
        System.out.println(message);
    }
    
    
    public void setSettingsArea() {
        lblControlUnitPort.setText(runtimeSettings.getControlUnitPort() + "");
        lblFloorCount.setText(runtimeSettings.getFloorCount() + "");
        lblGroundZero.setText(runtimeSettings.getGroundZero() + "");
        lblServerUrl.setText(runtimeSettings.getServerUrl() + "");
        System.out.println("should be set");
    }
    


    // ------------------------ DEBUG CODE \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ 
    @FXML
    void doluClicked(MouseEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                house.setElevatorStatus(chckBxSlow.isSelected() ? ElevatorStatus.DOWN_SLOW: ElevatorStatus.DOWN_SLOW);
                house.moveElevator();
            }
        });
    }


    @FXML
    void nahoruClicked(MouseEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                house.setElevatorStatus(chckBxSlow.isSelected() ? ElevatorStatus.UP_SLOW: ElevatorStatus.UP_NORMAL);
                house.moveElevator();
                }
        });
    }
    @FXML
    void stopClicked(MouseEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                house.setElevatorStatus(ElevatorStatus.STILL);
            }
        });
    }

    @FXML
    void indikaceClicked(MouseEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int pick = new Random().nextInt(Commands.IndikaceStav.values().length);
                Commands.IndikaceStav stav = Commands.IndikaceStav.values()[pick];
                indikace(new Random().nextInt(house.getFloorsCount()), stav);
            }
        });
    }

    @FXML
    void signalClicked(MouseEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int pick = new Random().nextInt(Commands.PanelSmer.values().length);
                Commands.PanelSmer smer = Commands.PanelSmer.values()[pick];
                panel(smer, pick);
            }
        });
    }
}
