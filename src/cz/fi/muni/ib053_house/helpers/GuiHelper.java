/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fi.muni.ib053_house.helpers;

import cz.fi.muni.ib053_house.entities.ElevatorStatus;
import cz.fi.muni.ib053_house.entities.FloorGuiElements;
import cz.fi.muni.ib053_house.entities.Sensor;
import cz.fi.muni.ib053_house.gui.HouseController;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Michal Keda
 */
public class GuiHelper {
       public static boolean checkCollision(ElevatorStatus elevatorStatus, Rectangle s1, Circle s2) {
        if (elevatorStatus.equals(ElevatorStatus.UP_NORMAL) || elevatorStatus.equals(ElevatorStatus.UP_SLOW)) {
            if (Math.abs(s1.getY() - s2.getCenterY()) < 3) {
                return true;
            }
        }
        if (elevatorStatus.equals(ElevatorStatus.DOWN_NORMAL) || elevatorStatus.equals(ElevatorStatus.DOWN_SLOW)) {
            if (Math.abs(s1.getY() + s1.getHeight() - s2.getCenterY()) < 3) {
                return true;
            }
        }
        return false;
    } 
           public static void checkBounds(ElevatorStatus elevatorStatus,Rectangle elevator, List<FloorGuiElements> floors) {
        Sensor collided = null;
        for (FloorGuiElements floor : floors) {
            for (Sensor sensor : floor.getSensors()) {
                Circle checkedSensor = sensor.getOutline();
                //if (elevator.getBoundsInParent().intersects(checkedSensor.getBoundsInParent())) {
                if (GuiHelper.checkCollision( elevatorStatus, elevator, checkedSensor)) {
                    if (sensor.isCollisionDetected()) {
                        continue;
                    }
                    collided = sensor;
                    if (!sensor.isCollisionDetected()) {
                        sensor.setCollisionDetected(true);
                    }
                    
                } else {
                    sensor.setCollisionDetected(false);
                    sensor.getOutline().setFill(Color.PURPLE);
                }
            }
        }
        
        if (collided != null && collided.isCollisionDetected()) {
            HouseController.getInstance().getCommunicator().poloha(collided.getType());
            System.out.println("collision" + collided.getType());
            
            collided.getOutline().setFill(Color.GREEN);
        }
    }
    public static double getXForAnimation(Rectangle n) {
        return n.getX() + n.getWidth() / 2; // animations will take element from its middle (teziste asi)
    }
    
    public static double getYForAnimation(Rectangle n) {
        return n.getY()+ n.getHeight() / 2;
    }

}


