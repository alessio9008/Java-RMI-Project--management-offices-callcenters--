/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commonInterface;

/**
 * Classe che contiene i parametri statici di funzionamento del sistema.
 * @author Alessio_Gregory_Ricky
 */
public class GlobalParameters {
    
    //Indirizzo e porta rmiregistry
    public static String RegistryHOST = "localhost";
    public static int RegistryPORT = 19000;
    //tempo di attesa prima di inviare la probe
    public static int TIMEOUT = 20000;
    //tempi minimi e massimi di disbrigo della pratica all'interno di un'ufficio
    public static int TMIN_PROCEDURE = 2;
    public static int TMAX_PROCEDURE = 5;
    //tempo di guardia tra l'acquisizione di un token ed un'altra
    public static int GUARDTIME_ACCESSTOKEN= 10;
    
}
