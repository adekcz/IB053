package cz.fi.muni.ib053_house.gui;

import cz.fi.muni.ib053_house.entities.HouseGuiElements;
import cz.fi.muni.ib053_house.settings.Commands;
import static cz.fi.muni.ib053_house.settings.Settings.runtimeSettings;
import cz.fi.muni.ib053_house.tcp.Communicator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class HouseController {
    public Communicator comm; 
    public HouseGuiElements house = new HouseGuiElements(2, 5);

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
    private static HouseController instance;

    public static HouseController getInstance() {
        return instance;
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

               // comm = new Communicator();
         //       Rectangle rect = new Rectangle(500,800,100,50);
         //       rect.setStroke(Color.BLUE);
         //       canvas.getChildren().add(rect);

                canvas.setPrefSize(house.getWidth(), house.getHeight());
                canvas.getChildren().addAll(house.getAllElementsUnmodifiable());
                
            }
            public void jizda(Commands.JizdaSmer smer){
                final String message = "Dum by mel naanimovat vytah smerem: " + smer + "\n";
                addText(txtAreaOutput, message);
                
                
            }
            
            public void indikace(int patro, Commands.IndikaceStav stav){
                final String message ="Dum by mel indikovat vytah v patre: " + patro + " ve stavu: " + stav+ "\n";
                addText(txtAreaOutput, message);
                
                
            }
            
            public void panel(Commands.PanelSmer smer, int patro){
                
                final String message ="Dum by mel blikat panely smer: " + smer + " k patru: " + patro+ "\n";
                addText(txtAreaOutput, message);
            }
            private void addText(TextArea textArea, String message) {
                textArea.setText(message);
                
            }
            
            public void setSettingsArea() {
                lblControlUnitPort.setText(runtimeSettings.getControlUnitPort() + "");
                lblFloorCount.setText(runtimeSettings.getFloorCount()+ "");
                lblGroundZero.setText(runtimeSettings.getGroundZero()+ "");
                lblServerUrl.setText(runtimeSettings.getServerUrl()+ "");
                System.out.println("should be set");
            }
            
}
