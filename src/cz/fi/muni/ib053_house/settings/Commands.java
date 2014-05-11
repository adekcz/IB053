/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fi.muni.ib053_house.settings;


/**
 *
 * @author Michal Keda
 */
public enum Commands {
    JIZDA, PANEL, INDIKACE;
    
    
    public static enum IndikaceStav{
        S,//sviti
        N,//nesviti
        B //bliká
    }
    public static enum JizdaSmer {
        O, D, N, P;
        @Override
        public String toString() {
            switch(this){
                case O: return "0"; //returns character for ZERO
                case D: return "D";
                case N: return "N";
                case P: return "P";
                default: throw new IllegalStateException();
            }
            
        }
        public static JizdaSmer getEnum(String value) {
            for(JizdaSmer v : values()){
                if("0".equalsIgnoreCase(value)){
                    return O;
                }
                if(v.toString().equalsIgnoreCase(value)){
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }
    public static enum PanelSmer {
        N,//nahoru
        D,//dolu
        S,//stoji
        K //v klidu
    }
}
