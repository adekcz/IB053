/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fi.muni.ib053_house.entities;

import cz.fi.muni.ib053_house.gui.HouseController;
import cz.fi.muni.ib053_house.settings.Commands;
import cz.fi.muni.ib053_house.settings.Events;
import cz.fi.muni.ib053_house.settings.FloorType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Michal Keda
 */
public class FloorGuiElements {
    private int floorNumber;
    private final Sensor topSensor;
    private final Sensor middleSensor;
    private final Sensor bottomSensor;
    private final Rectangle outline;
    private final Rectangle button;
    private final Rectangle statusPanel;
    private Text status;
    private List<Node> allElements;
    private List<Sensor> sensors;

    public FloorGuiElements(Rectangle outline, int floorNumber, FloorType type) {
        this.floorNumber = floorNumber;
        this.outline = outline;
        outline.setFill(Color.WHITE);
        if(type.equals(FloorType.TOP)){
            this.topSensor =  new Sensor(new Circle(outline.getX(),outline.getY()+outline.getHeight()-outline.getHeight()/4,5), Events.Poloha.KN);
        } else {
            this.topSensor =  new Sensor(new Circle(outline.getX(),outline.getY()+outline.getHeight()-outline.getHeight()/4,5), Events.Poloha.N);
        }
        
        this.middleSensor =  new Sensor(new Circle(outline.getX(),outline.getY()+outline.getHeight(),5), Events.Poloha.S);
        if(type.equals(FloorType.BOTTOM)){
            this.bottomSensor =  new Sensor(new Circle(outline.getX(),outline.getY()+outline.getHeight()+outline.getHeight()/4,5), Events.Poloha.S0);
        } else {
            this.bottomSensor =  new Sensor(new Circle(outline.getX(),outline.getY()+outline.getHeight()+outline.getHeight()/4,5), Events.Poloha.P);
        }
        this.button = new Rectangle(outline.getX()+10, outline.getY()+ outline.getHeight()/2, outline.getHeight()/6,outline.getHeight()/6);
        this.button.setFill(Color.RED);

        this.button.setOnMouseClicked(new EventHandler<MouseEvent>() {
 
            public void handle(MouseEvent event) {
                System.out.println(floorNumber + " was clicked");
                //HouseController.getInstance().getHouse().setElevatorStatus(ElevatorStatus.UP_NORMAL);
                //HouseController.getInstance().getHouse().moveElevator();
                HouseController.getInstance().getCommunicator().tlacitko(floorNumber);
            }
        });
        
         this.status = new Text(outline.getX()+40 + this.button.getWidth(), outline.getY()+ outline.getHeight()/2 + this.button.getHeight(),"ZACATEK");
        this.statusPanel = new Rectangle(outline.getX()+30, outline.getY()+ outline.getHeight()/2, outline.getHeight()/6,outline.getHeight()/6);

        this.statusPanel.setFill(Color.WHITE);
        this.statusPanel.setStroke(Color.BLACK);
        
        allElements = new ArrayList<>();
        sensors = new ArrayList<>();

        allElements.add(outline);
        allElements.add(button);
        allElements.add(statusPanel);
        allElements.add(status);
        sensors.add(middleSensor);
        sensors.add(bottomSensor);
        sensors.add(topSensor);

        allElements.addAll(sensors.stream().map(s -> s.getOutline()).collect(Collectors.toList()));
        
        this.topSensor.getOutline().toFront();
        this.bottomSensor.getOutline().toFront();
        this.topSensor.getOutline().toFront();
    }

    public List<Node> getAllElementsUnmodifiable() {
        return Collections.unmodifiableList(allElements);
    }

    public Circle getTopSensor() {
        return topSensor.getOutline();
    }

    public Circle getMiddleSensor() {
        return middleSensor.getOutline();
    }

    public Circle getBottomSensor() {
        return bottomSensor.getOutline();
    }

    public Rectangle getOutline() {
        return outline;
    }

    public Rectangle getButton() {
        return button;
    }

    public Rectangle getStatusPanel() {
        return statusPanel;
    }

    private FillTransition ft;
    public void indikace(Commands.IndikaceStav stav) {
        if(ft!=null){
            ft.stop();
            ft = null;
        }
        switch(stav){
            case B:
                 ft = new FillTransition(Duration.millis(500), this.statusPanel, Color.WHITE, Color.GREENYELLOW);
                 ft.setCycleCount(Animation.INDEFINITE);
                 ft.setAutoReverse(true);
             
                 ft.play();
                break;
            case N:
                this.statusPanel.setFill(Color.WHITE);
                break;
            case S:
                this.statusPanel.setFill(Color.GREENYELLOW);
                break;
        }
    }


public Text getStatus() {
        return status;
    }

    public Iterable<Sensor> getSensors() {
        return Collections.unmodifiableList(sensors);
        
    }
}

