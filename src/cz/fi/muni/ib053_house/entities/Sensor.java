/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fi.muni.ib053_house.entities;

import cz.fi.muni.ib053_house.settings.Commands;
import cz.fi.muni.ib053_house.settings.Events;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Michal Keda
 */
public class Sensor {
    private final Circle outline;
    private final Events.Poloha type;
    private boolean  collisionDetected = false;

    public Sensor(double x, double y, Events.Poloha type) {
        this.outline = new Circle(x, y, 5);
        this.type = type;
    }

    public Circle getOutline() {
        return outline;
    }

    public Events.Poloha getType() {
        return type;
    }

    public boolean isCollisionDetected() {
        return collisionDetected;
    }
    public void setCollisionDetected(boolean c) {
        this.collisionDetected = c;
    }

    
    
}
