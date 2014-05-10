/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fi.muni.ib053_house.entities;

import cz.fi.muni.ib053_house.settings.FloorType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 *
 * @author Michal Keda
 */
public class HouseGuiElements {
    private final int  FLOOR_HEIGHT = 80;
    private final int  FLOOR_WIDTH = 200;
    private final int GENERAL_MARGIN = 40;
    
    //sizes of others component
    private final int GENERAL_WIDTH = 40;
    private final int GENERAL_HEIGHT = 40;

    
    private final int groundzero;
    private final int expectedNumberOfFloors; // 

    private Rectangle shaft;
    private List<FloorGuiElements> floors = new ArrayList<>();
    private Rectangle engine;
    private Circle lastSensor;
    private Rectangle elevator;
    private ElevatorStatus elevatorStatus;

   // nastupiste - tlacitko - indikace stavu vytahu
    // motor,
    // sachta s cidly pred, za, ve stanici + za posledni stanici

    
//TODO move to other class
    public static double getXForAnimation(Rectangle n){
        return n.getX()+n.getWidth()/2; // animations will take element from its middle (teziste asi)
    }
    public static double getYForAnimation(Rectangle n){
        return n.getY()+n.getHeight()/2;
    }

    public void moveElevatorTo(FloorGuiElements floor){
        System.out.println("should be moving");
        double origin = getXForAnimation(elevator);
        double destination = getXForAnimation(shaft);
        double totalDestination = origin+destination;
        for(int i = (int) origin; i<totalDestination; i++){
            Path path = new Path();
            path.getElements().add(new MoveTo(origin, getYForAnimation(elevator)));
            path.getElements().add(new LineTo(destination, getYForAnimation(floor.getOutline())));
            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(4000));
            pathTransition.setPath(path);
            pathTransition.setNode(elevator);
            pathTransition.setOrientation(PathTransition.OrientationType.NONE);
            pathTransition.setCycleCount(1);
            pathTransition.play();

            pathTransition.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    elevator.setY(floor.getOutline().getY());
                }
            });

            }
            System.out.println("should be stopped");
        }
     public List<Node> getAllElementsUnmodifiable() {
        List<Node> temp = new ArrayList<>();
        temp.add(shaft);
        temp.add(engine);
        temp.add(lastSensor);
        temp.add(elevator);
        for(FloorGuiElements floor: floors){
            temp.addAll(floor.getAllElementsUnmodifiable());

        }

        return Collections.unmodifiableList(temp.stream().filter((node) -> node !=null).collect(Collectors.toList()));
    }

    public HouseGuiElements(int groundzero, int numberOfFloors) {
        this.expectedNumberOfFloors = numberOfFloors;
        this.groundzero = groundzero;
        for(int i = 0; i<numberOfFloors;i++){
            addFloor();
        }
        this.shaft = new Rectangle(GENERAL_MARGIN, getHeight() - GENERAL_MARGIN  - (numberOfFloors-1)*FLOOR_HEIGHT, GENERAL_WIDTH, numberOfFloors*FLOOR_HEIGHT);
        this.elevator = new Rectangle(GENERAL_MARGIN, getHeight() - GENERAL_MARGIN , GENERAL_WIDTH, FLOOR_HEIGHT);
        this.elevatorStatus = new ElevatorStatus(floors.get(0), Animation.Status.STOPPED);
         this.lastSensor =  new Circle(GENERAL_MARGIN + GENERAL_WIDTH, GENERAL_MARGIN+FLOOR_HEIGHT, 5);

        shaft.setStroke(Color.ANTIQUEWHITE);
        elevator.setStroke(Color.AQUAMARINE);
        shaft.setOpacity(50);

        for(Node node: getAllElementsUnmodifiable()){
            if(node instanceof Shape){
                Shape shape = (Shape) node;
                shape.setOpacity(0.1);
            }
        }
    }

    public int getWidth(){
        return GENERAL_MARGIN*2 + FLOOR_WIDTH + GENERAL_WIDTH*2; //margins, floor, shaft with elevator, motor
    }
    public final int getHeight(){
        return GENERAL_MARGIN*2 + FLOOR_HEIGHT*expectedNumberOfFloors;
    
    }
    public void addFloor(){
        Rectangle floor = new Rectangle(FLOOR_WIDTH, FLOOR_HEIGHT);
        floor.setStroke(Color.BLUEVIOLET);
        floor.setX(GENERAL_MARGIN+GENERAL_WIDTH);
        if(floors.isEmpty()){
            floor.setY(getHeight() - GENERAL_MARGIN);
            floors.add(new FloorGuiElements(floor, 0, FloorType.BOTTOM));
        } else {
            FloorGuiElements last = floors.get(floors.size()-1);
            floor.setY(last.getOutline().getY() - FLOOR_HEIGHT);
            floors.add(new FloorGuiElements(floor, floors.size(), floors.size()==expectedNumberOfFloors ? FloorType.TOP: FloorType.REGULAR));
        }

    }

    public int getGroundzero() {
        return groundzero;
    }

    public int getFloorsCount() {
        return floors.size();
    }

    public Rectangle getShaft() {
        return shaft;
    }

    public List<FloorGuiElements> getFloors() {
        return floors;
    }

    public Rectangle getEngine() {
        return engine;
    }

    public Circle getLastSensor() {
        return lastSensor;
    }

    public Rectangle getElevator() {
        return elevator;
    }

    
}