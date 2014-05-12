/*
 * To change this template, choose Tools | Templates
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
    private volatile ElevatorStatus elevatorStatus;

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
private void checkBounds(Shape elevator, List<FloorGuiElements> floors) {
  boolean collisionDetected = false;
  for(FloorGuiElements floor: floors){
      for (Shape static_bloc : floor.getSensors()) {
          if(static_bloc==null){
              continue;
          }
        if (static_bloc != elevator) {

          if (elevator.getBoundsInParent().intersects(static_bloc.getBoundsInParent())) {
                collisionDetected = true;
                  static_bloc.setFill(Color.PURPLE);
          }
        }
      }
  }

  if (collisionDetected) {
    elevator.setFill(Color.BLUE);
  } else {
    elevator.setFill(Color.GREEN);
  }
}
    public void moveElevator(){
       //System.out.println("in "+ elevator.getY());
        double deltaY = 2;
        double duration = 0;
        switch(elevatorStatus){
            case DOWN_NORMAL:
                duration= 10;
                break;
            case DOWN_SLOW:
                duration= 50;
                break;
            case UP_NORMAL:
                duration= 10;
                deltaY *= -1;
                break;
            case UP_SLOW:
                duration= 50;
                deltaY *= -1;
                break;
            case STILL:
                
                //(Y vytahu - Y nejvyssiho patra) % vyska patra
                int modularDistanceFromFloor = ((int) Math.abs(elevator.getY()-floors.get(floors.size()-1).getOutline().getY()))%FLOOR_HEIGHT;

                if(modularDistanceFromFloor<5 || FLOOR_HEIGHT-modularDistanceFromFloor < 5){
                      HouseController.getInstance().getCommunicator().pohyb(Events.Pohyb.S);
                }else{
                      HouseController.getInstance().getCommunicator().pohyb(Events.Pohyb.P);

                }
                return;
        }
        


            
            Path path = new Path();
            path.getElements().add(new MoveTo(getXForAnimation(elevator), getYForAnimation(elevator)));
            path.getElements().add(new LineTo(getXForAnimation(elevator), getYForAnimation(elevator)+deltaY));
            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(duration));
            //pathTransition.setDelay(Duration.millis(300));
            pathTransition.setPath(path);
            pathTransition.setNode(elevator);
            pathTransition.setOrientation(PathTransition.OrientationType.NONE);
            pathTransition.setCycleCount(1);
            pathTransition.play();

            final double tempDeltaY = deltaY;
            pathTransition.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    checkBounds(elevator, floors);
                   //System.out.println("finishIn: "+ elevator.getY());
                    elevator.setY(elevator.getY()+tempDeltaY);
                   //System.out.println("finishOut "+ elevator.getY());
                    moveElevator();
                }
            });

            //System.out.println("should be stopped");

    }
    public void moveElevatorTo(FloorGuiElements floor){
        //System.out.println("should be moving");
        double origin = getXForAnimation(elevator);
        double destination = getXForAnimation(shaft);
        double totalDestination = origin+destination;
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

            //System.out.println("should be stopped");
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
        this.elevatorStatus = elevatorStatus.STILL;
         this.lastSensor =  new Circle(GENERAL_MARGIN + GENERAL_WIDTH, GENERAL_MARGIN+FLOOR_HEIGHT, 5);

         //you should style elements by CSS
        shaft.setStroke(Color.BLACK);
        shaft.setFill(Color.WHITE);
        elevator.setOpacity(0.1);


        for(Node node: getAllElementsUnmodifiable()){
            if(node instanceof Shape){
                Shape shape = (Shape) node;
                //shape.setOpacity(0.1);
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

    public ElevatorStatus getElevatorStatus() {
        return elevatorStatus;
    }

    public void setElevatorStatus(ElevatorStatus elevatorStatus) {
        this.elevatorStatus = elevatorStatus;
    }


    
}
