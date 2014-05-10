/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fi.muni.ib053_house.entities;

import javafx.animation.Animation.Status;

/**
 *
 * @author Michal Keda
 */
public class ElevatorStatus {
    private FloorGuiElements destinationFloor;
    private Status status;

    public ElevatorStatus(FloorGuiElements destinationFloor, Status status) {
        this.destinationFloor = destinationFloor;
        this.status = status;
    }

    public FloorGuiElements getDestinationFloor() {
        return destinationFloor;
    }

    public void setDestinationFloor(FloorGuiElements destinationFloor) {
        this.destinationFloor = destinationFloor;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    

}
