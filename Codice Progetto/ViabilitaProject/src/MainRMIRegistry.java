
import commonInterface.GlobalParameters;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Classe che contiene il main per la creazione dell'RMIRegistry
 * @author Alessio_Gregory_Ricky
 */
public class MainRMIRegistry {
    //Logger
    
    
    public static void main(String[] args)
    {
        try {
            Thread.sleep(2000);
            System.out.println("KILL EVENTUALLY RMIREGISTRY");
            //Lista dei comandi
            ArrayList<String> command2 = new ArrayList<>();
            //Aggiungo i comandi alla lista
            command2.add("killall");
            command2.add("rmiregistry");
            //Eseguo il killAll
            ProcessBuilder builder2 = new ProcessBuilder(command2);
            builder2.start();
            //Aspetto 1 secondo
            Thread.sleep(1000);
            System.out.println("RMI REGISTRY IS STARTING...");
            
            ArrayList<String> command = new ArrayList<>();
            
            command.add("rmiregistry");
            command.add(String.valueOf(GlobalParameters.RegistryPORT));
            
            ProcessBuilder builder = new ProcessBuilder(command);
            //Va creato dove c'è il package con le interfaccie
            builder.directory(new File("./build/classes/"));
            builder.start();
            
            System.out.println("...STARTED");
            
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
    
}
