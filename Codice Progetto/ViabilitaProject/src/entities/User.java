/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import GUI.UserGUI;
import commonInterface.GlobalParameters;
import commonInterface.ICallCenter_User;
import commonInterface.IUser;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Rappresenta il generico user
 * @author Alessio_Gregory_Ricky
 */
public class User implements IUser{

    //Generics
    private String name;
    //PrivateResoruces indica gli uffici che si vogliono richiedere
    private LinkedBlockingQueue<Long> offices;
    //id del callcenter da contattare
    private long callCenterID;
    //riferimento al callCenter da contattare
    private ICallCenter_User callCenter;
    
    //ID associato al segnale
    private long idSignal= -1;
    
    //Eventuale gui
    private UserGUI gui=null;
    /**
     * Costruttore 
     * @param name Nome User
     * @param callCenterID Id callCenter da contattare
     * @param manualRandom indica se le richieste devono essere generate in modo random o meno
     * @param office1 se non sono random office1
     * @param office2 se non sono random office2
     * @param office3 se non sono random office3
     * @param office4 se non sono random office4 
     */
    public User(String name, long callCenterID,String manualRandom,long office1,long office2, long office3, long office4 )
    {
        //Richiama un altro cotruttore
        this(name,callCenterID); 
        
        System.out.println("Creating new User"+name);
        //Usa dei metodi per riempire la lista in base se la scelta è random oppure manuale
        if(manualRandom.equalsIgnoreCase("random")){
            populateRandom();
        }else if(manualRandom.equalsIgnoreCase("manual")){
                populateManual(office1,office2,office3,office4);
            
        }
    }
    /**
     * 
     * @param name Nome Dell'utente
     * @param callCenterID Id callcenter da contattare
     */
    public User(String name, long callCenterID) {
        this(name);
        this.callCenterID = callCenterID;
    }
    
    /**
     * Costruttore
     * @param name nome 
     */
    public User(String name) {
        this.name = name;
        this.offices = new LinkedBlockingQueue<>(4);
    }
    
    /**
     * Per aggiungere un ufficio alla lista
     * @param idOffice id ufficio
     */
    public void addOffice(long idOffice){
        this.offices.add(new Long(idOffice));
        System.out.println("Ufficio aggiunto: "+idOffice);
    }

    /**
     * Per settare id del callCenter da contattare
     * @param callCenterID id CallCenter
     */
    public void setCallCenterID(long callCenterID) {
        this.callCenterID = callCenterID;
        System.out.println("IdCallCenterSettato: "+callCenterID);
    }
    
    
 
/*ICallCenter_User methods---------------------------------------------------------------------------------*/
   /**
     * Ottenere la lista degli uffici dopo aver ricevuto la richiesta dall'utente
     * @return Lista degli uffici
     * @throws RemoteException 
     */
    @Override
    public LinkedBlockingQueue<Long> getList() throws RemoteException {
        return offices;
    }

    /**
     * Indica di aver finito tutte le operazioni
     * @param message messaggio
     * @throws RemoteException 
     */
    @Override
    public synchronized void finishSignal(String message) throws RemoteException {
        //Se la gui è null
        if(gui==null){
            //se il messaggio è retryLater
            if(message.equalsIgnoreCase("retryLater")){
                       String answer= "Spiacenti, si è verificato un deadlock con la vostra segnalazione\n";
                        System.out.println(answer);
            }else{
                //Se tutto è Ok stampo il messaggio
                System.out.println("Signal message:" +message);
            }
        }else{ //Nel caso è presente la gui non reinvio la segnalazione in modo automatico 
            //lo faccio fare a mano all'utente
            if(message.equalsIgnoreCase("retryLater")){
                gui.setResponce("Signal completed: provare ad inviare la segnalazione più tardi");
            }else{
                gui.setResponce("Signal completed:" + message);
            }
        }
    }
    
    /**
     * Settare id dell'ultima segnalazione comunicandolo all'user
     * @param idSignal id Segnalazione
     * @throws RemoteException 
     */
    @Override
    public void setSignalId(long idSignal) throws RemoteException {
        this.idSignal=idSignal;
        System.out.println("ID associato all'ultima segnalazione inviata: "+idSignal);
    }
    
    
    
    
    
 /*Useful methods-------------------------------------------------------------------------------------------*/
    /**
     * Per settare la gui nel caso creiamo un user con la GUI
     * @param gu riferimento ad UserGUI
     */
    public void setGUI(UserGUI gu){
        this.gui=gu;
        System.out.println("GUI settata");
    }
    
    
    
    /**
     * Export class to remote reference.
     * 
     */
    protected void exportSelf()
    {
        try {
            UnicastRemoteObject.exportObject(this,0);
        } catch (RemoteException ex) {
            ex.printStackTrace(System.out);
        }
    }
    /**
     * Generate 4 different signal.
     * Populate the local list in modo random.
     */
    protected void populateRandom()
    {
        //Se la lista è inizializzata
        if(offices != null)
        {
            try {
                //La svuoto
                offices.clear();
                //La popolo in modo random
                for(int x=0; x<4; x++) 
                    offices.put(getRandom());
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.out);
            }
        }
    }
    
    /**
     * Generate 4 different signal.
     * Populate the local list con gli uffici passati.
     */
    protected void populateManual(Long office1,Long office2, Long office3, Long office4)
    {
        //Se la lista è inizializzata
        if(offices!=null){
                //La svuoto
                offices.clear(); 
                try {
                    //Inserisco gli uffici
                    if(office1!=0)offices.put(office1);
                    if(office2!=0)offices.put(office2);
                    if(office3!=0)offices.put(office3);
                    if(office4!=0)offices.put(office4);
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.out);
                }
                
            }
    }
    
    /**
     * Init the standard procedure.
     * 
     */
    public void init()
    {
        try {
            System.out.println("INIT the User...");
            //If can't retrieve the security manager, just create once.
                if(System.getSecurityManager() == null)
                    System.setSecurityManager(new SecurityManager());
            //locate the registry
                Registry registry = LocateRegistry.getRegistry(GlobalParameters.RegistryHOST,GlobalParameters.RegistryPORT);
            //getting the servant
                System.out.println("Chiamo il callCenter: "+callCenterID+ "\n"
                                    +"Con questi uffici: "+offices
                                    );
                callCenter = (ICallCenter_User) registry.lookup(String.valueOf(this.callCenterID));
            //register self
                exportSelf();
                
            //send signal to the callsenter
                callCenter.sendSignal(this);
                System.out.println("Sending the signal");       
        } catch (NotBoundException ex) {
            ex.printStackTrace(System.out);
        } catch (AccessException ex) {
            ex.printStackTrace(System.out);
        } catch (RemoteException ex) {
            ex.printStackTrace(System.out);
        }
            
    }
    /**
     * Generate a random id callcenter*10 + office
     * Example: 23 = callcenter 2° -> office 3°
     * @return Valore random di ufficio
     */
    private long getRandom() {
        //Genero callcenter random
        long callcenter = 1+(long)(Math.random()*4);
        //Genero office random
        long office = 1+(long)(Math.random()*3);

        return ((callcenter*10) + office);
    }
  
}
