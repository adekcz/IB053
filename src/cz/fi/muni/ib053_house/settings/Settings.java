/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package cz.fi.muni.ib053_house.settings;

import cz.fi.muni.ib053_house.helpers.FileUtils;

/**
 *
 * @author Michal Keda
 */
public class Settings {
    public static String SETTING_PATH = "./SETTINGS.txt";
    public static RuntimeSettings runtimeSettings = new RuntimeSettings();
    
    public static  class RuntimeSettings {
        
        private final int controlUnitPort;
        private final String serverUrl;
        private final int floorCount;
        private final int groundZero;
        
        public int getGroundZero() {
            return groundZero;
        }
        
        private RuntimeSettings() {
            String[] settings = FileUtils.getLines(Settings.SETTING_PATH).get(0).split(";");
            
            this.serverUrl = settings[0];
            this.controlUnitPort = Integer.parseInt(settings[1]);
            this.floorCount = Integer.parseInt(settings[2]);
            this.groundZero = Integer.parseInt(settings[3]);
            
        }
        
        
        public int getControlUnitPort() {
            return controlUnitPort;
        }
        
        public String getServerUrl() {
            return serverUrl;
        }
        
        public int getFloorCount() {
            return floorCount;
        }
        
        
    }
    
    
}
