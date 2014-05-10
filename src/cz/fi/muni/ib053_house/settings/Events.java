/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fi.muni.ib053_house.settings;

/**
 *
 * @author Michal Keda
 */
public enum Events {
   INICIALIZACE, POHYB, POLOHA, TLACITKO;
    public static enum Pohyb{
           S, //stoji mimo patro
            P, // stoji v patre
            J //JEDE
    }
    public static enum Poloha {
    P,//pod stanici
    S,//ve stanici
    N,//nad stanici
    KD,//cidlo pod nejnizsi
    KN,// cidlo nad nejvyssi
    S0 //nejnizsi stanice
}
}
