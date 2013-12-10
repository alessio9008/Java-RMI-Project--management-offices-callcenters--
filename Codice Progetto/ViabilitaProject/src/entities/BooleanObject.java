/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

/**
 * Per creare un Wrapper Boolean come oggetto modificabile
 * a differenza della classe java.lang.Boolean
 * @author Alessio_Gregory_Ricky
 */
public class BooleanObject {
    //Valore boleano
    private boolean value;

    /**
     * Costruttore che setta il valore BooleanObject a value
     * @param value valore booleano
     */
    public BooleanObject(boolean value) {
        this.value=value;
    }

    /**
     * Ritorna il valore di BooleanObject 
     * @return valore booleano
     */
    public synchronized boolean getValue() {
        return value;
    }

    /**
     * Setta il valore BooleanObject a value
     * @param value valore booleano
     */
    public synchronized void setValue(boolean value) {
        this.value = value;
    }
    
    
    
    
}
