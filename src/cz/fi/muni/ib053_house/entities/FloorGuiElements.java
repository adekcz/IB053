/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fi.muni.ib053_house.entities;

import cz.fi.muni.ib053_house.gui.HouseController;
import cz.fi.muni.ib053_house.settings.FloorType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Michal Keda
 */
public class FloorGuiElements {
    private int floorNumber;
    private final Circle topSensor;
    private final Circle middleSensor;
    private final Circle bottomSensor;
    private final Rectangle outline;
    private final Rectangle button;
    private final Rectangle statusPanel;
    private List<Node> allElements;

    public FloorGuiElements(Rectangle outline, int floorNumber, FloorType type) {
        this.floorNumber = floorNumber;
        this.outline = outline;
        if(!type.equals(FloorType.TOP)){
            this.topSensor =  new Circle(outline.getX(),outline.getY()+outline.getHeight()-outline.getHeight()/4,5);
        } else {
            this.topSensor=null;
        }
        this.middleSensor =  new Circle(outline.getX(),outline.getY()+outline.getHeight(),5);
        if(!type.equals(FloorType.BOTTOM)){
            this.bottomSensor =  new Circle(outline.getX(),outline.getY()+outline.getHeight()+outline.getHeight()/4,5);
        } else {
            this.bottomSensor=null;
        }
        this.button = new Rectangle(outline.getX()+10, outline.getY()+ outline.getArcHeight()/2, outline.getHeight()/6,outline.getHeight()/6);
        this.button.setFill(Color.RED);

        final FloorGuiElements thiss = this;
        this.button.setOnMouseClicked(new EventHandler<MouseEvent>() {
 
            public void handle(MouseEvent event) {
                System.out.println(floorNumber + " was clicked");
                HouseController.getInstance().house.moveElevatorTo(thiss);
            }
        });
        this.statusPanel = new Rectangle(outline.getX()+30, outline.getY()+ outline.getArcHeight()/2, outline.getHeight()/6,outline.getHeight()/6);
        allElements = new ArrayList<>();
        allElements.add(topSensor);
        allElements.add(middleSensor);
        allElements.add(bottomSensor);
        allElements.add(outline);
        allElements.add(button);
        allElements.add(statusPanel);
    }

    public List<Node> getAllElementsUnmodifiable() {
        return Collections.unmodifiableList(allElements);
    }

    public Circle getTopSensor() {
        return topSensor;
    }

    public Circle getMiddleSensor() {
        return middleSensor;
    }

    public Circle getBottomSensor() {
        return bottomSensor;
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
    



    


}

