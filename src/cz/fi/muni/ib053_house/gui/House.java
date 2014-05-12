/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fi.muni.ib053_house.gui;

import cz.fi.muni.ib053_house.settings.Commands;
import cz.fi.muni.ib053_house.settings.Settings;
import static cz.fi.muni.ib053_house.settings.Settings.runtimeSettings;
import cz.fi.muni.ib053_house.tcp.Communicator;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import resources.TestTCPServer;

/**
 *
 * @author Michal Keda
 */
public class House extends Application {

    

     @Override
     public void start(Stage stage) throws Exception {
         Parent root = FXMLLoader.load(getClass().getResource("HouseFxml.fxml"));
         
         Scene scene = new Scene(root, 800,600);
         
         stage.setScene(scene);
         stage.show();
     }
     
  
     /**
      * @param args the command line arguments
      */
     public static void main(String[] args) {
         launch(args);
         
     }
     
   
     
     
}
