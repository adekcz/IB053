/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fi.muni.ib053_house.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Michal Keda
 */
public class House extends Application {

    

     @Override
     public void start(Stage stage) throws Exception {
         FXMLLoader loader =  new FXMLLoader(getClass().getResource("HouseFxml.fxml"));
         Parent root =  loader.load();
         
         Scene scene = new Scene(root, 800,600);
         
         stage.setScene(scene);
        
         final HouseController controller = (HouseController)loader.getController();
        ((Stage) root.getScene().getWindow()).setOnCloseRequest(new EventHandler<WindowEvent>() {
             @Override
             public void handle(WindowEvent event) {
                 System.out.println("Koncim");
                 controller.getCommunicator().setKillYourself(true);
                 controller.getHouse().getTimer().cancel();
                 Platform.exit();
             }
         });
         stage.show();
     }
     
     
     /**
      * @param args the command line arguments
      */
     public static void main(String[] args) {
         launch(args);
         Platform.setImplicitExit(true);
         
     }
     
     
     
     
}
