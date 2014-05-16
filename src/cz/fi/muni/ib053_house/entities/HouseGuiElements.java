/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package cz.fi.muni.ib053_house.entities;

import cz.fi.muni.ib053_house.gui.HouseController;
import cz.fi.muni.ib053_house.helpers.GuiHelper;
import cz.fi.muni.ib053_house.settings.Commands;
import cz.fi.muni.ib053_house.settings.Events;
import cz.fi.muni.ib053_house.settings.FloorType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    
    private final int FLOOR_HEIGHT = 80;
    private final int FLOOR_WIDTH = 200;
    private final int GENERAL_MARGIN = 40;
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
    private Timer timer = new Timer();

    
    
    // nastupiste - tlacitko - indikace stavu vytahu
    // motor,
    // sachta s cidly pred, za, ve stanici + za posledni stanici
    public HouseGuiElements(int groundzero, int numberOfFloors) {
        this.expectedNumberOfFloors = numberOfFloors;
        this.groundzero = groundzero;
        for (int i = 0; i < numberOfFloors; i++) {
            addFloor();
        }
        this.shaft = new Rectangle(GENERAL_MARGIN, getHeight() - GENERAL_MARGIN - (numberOfFloors - 1) * FLOOR_HEIGHT, GENERAL_WIDTH, numberOfFloors * FLOOR_HEIGHT);
        this.elevator = new Rectangle(GENERAL_MARGIN, getHeight() - GENERAL_MARGIN, GENERAL_WIDTH, FLOOR_HEIGHT);
        this.elevatorStatus = ElevatorStatus.STILL;
        this.lastSensor = new Circle(GENERAL_MARGIN + GENERAL_WIDTH, GENERAL_MARGIN + FLOOR_HEIGHT, 5);
        
        //you should style elements by CSS
        shaft.setStroke(Color.BLACK);
        shaft.setFill(Color.WHITE);
        elevator.setOpacity(0.1);
        
      //  for (Node node : getAllElementsUnmodifiable()) {
      //      if (node instanceof Shape) {
      //          Shape shape = (Shape) node;
      //          //shape.setOpacity(0.1);
      //      }
      //  }
    }
    
    public Timer getTimer() {
        return timer;
    }
    

    public void emptyTimers(TimerTask task, Timer timer){
        sendable = true;
        if(task!=null){
            task.cancel();
        }
        if (timer != null){
            timer.cancel();
            timer.purge();
        }
    }

    //simple solution for concurent running timer job
    private static volatile boolean sendable = false;
    public void moveElevator(){
        sendable = false;
        timer  = new Timer();
        int duration = 80;
        if (elevatorStatus.equals(ElevatorStatus.UP_SLOW) || elevatorStatus.equals(ElevatorStatus.DOWN_SLOW)){
            duration = 300;

        }
        final Timer reference = timer;
        timer.scheduleAtFixedRate(new TimerTask() {
            private boolean dead = false;
            private Timer ref = reference;

            @Override
            public void run() {
                if(dead || sendable){
                    return;
                }
                double deltaY = 1;
                switch (elevatorStatus) {
                    case DOWN_NORMAL:
                        //HouseController.getInstance().getCommunicator().pohyb(Events.Pohyb.J);
                        break;
                    case DOWN_SLOW:
                        //HouseController.getInstance().getCommunicator().pohyb(Events.Pohyb.J);
                        break;
                    case UP_NORMAL:
                        deltaY = -1;
                        //HouseController.getInstance().getCommunicator().pohyb(Events.Pohyb.J);
                        break;
                    case UP_SLOW:
                        deltaY = -1;
                        //HouseController.getInstance().getCommunicator().pohyb(Events.Pohyb.J);
                        break;
                    case STILL:
                        emptyTimers(this, ref);
                        dead = true;
                        //(Y vytahu - Y nejvyssiho patra) % vyska patra
                        int nonAbsoluteDistance = (int) (elevator.getY() - floors.get(floors.size() - 1).getOutline().getY()) % FLOOR_HEIGHT;
                        
                        if (normalizeModulo(nonAbsoluteDistance, 80) < 12) {
                            HouseController.getInstance().getCommunicator().pohyb(Events.Pohyb.P);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    elevator.setY(elevator.getY() - normalizeModulo(nonAbsoluteDistance, 80));
                                }});
                            //move house exactly to right floor
                            System.out.println("STOJI V: " + Events.Pohyb.P);
                            return;
                        }
                        if (Math.abs(normalizeModulo(-nonAbsoluteDistance, 80)) < 12) {
                            HouseController.getInstance().getCommunicator().pohyb(Events.Pohyb.P);
                            //move house exactly to right floor
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    elevator.setY(elevator.getY() + normalizeModulo(-nonAbsoluteDistance, 80));
                                }});
                            System.out.println("STOJI V: " + Events.Pohyb.P);
                            return;
                        }
                        HouseController.getInstance().getCommunicator().pohyb(Events.Pohyb.S);
                        System.out.println("STOJI V: " + Events.Pohyb.S);
                        return;
                }
                
                
                
                final double tempDeltaY = deltaY;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        GuiHelper.checkBounds(elevatorStatus, elevator, floors);
                        elevator.setY( elevator.getY()+tempDeltaY);
                    }});
                
            }
        }, 0, duration);
    }
  
    
    private int normalizeModulo(int n1, int modulo) {
        
        int modN1 = n1 % modulo;
        modN1 = modN1 < 0 ? modN1 + modulo : modN1;
        return modN1;
    }
    
    private boolean modularEquals(int n1, int n2, int modulo) {
        int modN1 = n1 % modulo;
        int modN2 = n2 % modulo;
        modN1 = modN1 < 0 ? modN1 + modulo : modN1;
        modN2 = modN2 < 0 ? modN2 + modulo : modN2;
        if (modN1 == modN2) {
            return true;
        }
        
        return false;
        
    }
    
    public void moveElevatorTo(FloorGuiElements floor) {
//System.out.println("should be moving");
        double origin = GuiHelper.getXForAnimation(elevator);
        double destination = GuiHelper.getXForAnimation(shaft);
        Path path = new Path();
        path.getElements().add(new MoveTo(origin, GuiHelper.getYForAnimation(elevator)));
        path.getElements().add(new LineTo(destination, GuiHelper.getYForAnimation(floor.getOutline())));
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
    
    public List<Node> getAllElementsUnmodifiable() {
        List<Node> temp = new ArrayList<>();
        temp.add(shaft);
        temp.add(engine);
        temp.add(lastSensor);
        temp.add(elevator);
        for (FloorGuiElements floor : floors) {
            temp.addAll(floor.getAllElementsUnmodifiable());
            
        }
        
        return Collections.unmodifiableList(temp.stream().filter((node) -> node != null).collect(Collectors.toList()));
    }
    
    
    
    public void addFloor() {
        Rectangle floor = new Rectangle(FLOOR_WIDTH, FLOOR_HEIGHT);
        floor.setStroke(Color.BLUEVIOLET);
        floor.setX(GENERAL_MARGIN + GENERAL_WIDTH);
        if (floors.isEmpty()) {
            floor.setY(getHeight() - GENERAL_MARGIN);
            floors.add(new FloorGuiElements(floor, 0, FloorType.BOTTOM));
            
        } else if (expectedNumberOfFloors-1 == floors.size()) { //-1 bcs current is not added to list
            FloorGuiElements last = floors.get(floors.size() - 1);
            floor.setY(last.getOutline().getY() - FLOOR_HEIGHT);
            floors.add(new FloorGuiElements(floor, floors.size(), FloorType.TOP));
        } else {
            FloorGuiElements last = floors.get(floors.size() - 1);
            floor.setY(last.getOutline().getY() - FLOOR_HEIGHT);
            floors.add(new FloorGuiElements(floor, floors.size(), FloorType.REGULAR));
        }
        
    }
    
    public int getWidth() {
        return GENERAL_MARGIN * 2 + FLOOR_WIDTH + GENERAL_WIDTH * 2; //margins, floor, shaft with elevator, motor
    }
    
    public final int getHeight() {
        return GENERAL_MARGIN * 2 + FLOOR_HEIGHT * expectedNumberOfFloors;
        
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
    
    public void controlDoors(Commands.PanelSmer smer) {
        if (smer.equals(smer.S)) {
            elevator.setFill(Color.BLACK);
        }
        if (smer.equals(smer.K)) {
            elevator.setFill(Color.GREEN);
        }
    }
    
}
