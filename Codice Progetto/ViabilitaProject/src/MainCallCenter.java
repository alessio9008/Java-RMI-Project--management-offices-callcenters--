
import commonInterface.GlobalParameters;
import commonInterface.ICallCenter;
import commonInterface.ICallCenter_CallCenter;
import entities.CallCenter;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Classe che contiene il main che crea un oggetto CallCenter e lo inizializza
 * @author Alessio_Gregory_Ricky
 */
public class MainCallCenter {
    
    public static void main(String args[]){
        //Creo oggetto callCanter
        CallCenter callCenter=new CallCenter(Long.parseLong(args[0]));
        //init() lo esporta nel registry
        callCenter.init();
        System.out.println("CallCenter ID="+args[0]+" is started...");
    }
    
}
