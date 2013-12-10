/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import commonInterface.GlobalParameters;
import commonInterface.ICallCenter_Office;
import commonInterface.IOffice;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rappresenta il generico ufficio
 * @author Alessio_Gregory_Ricky
 */
public class Office implements IOffice{
    
    //id ufficio
    private long idOffice=-1;
    //CallCenter remoto da contattare
    private ICallCenter_Office remoteCallCenter;
    
   /**
    * Costruttore con id dell'ufficio come parametro
    * @param idOffice idOffice
    */
    public Office(long idOffice) {
        try {
            this.idOffice = idOffice;
            this.remoteCallCenter = null;
            System.out.println("Creating new Office: ID "+idOffice);
            
             //If can't retrieve the security manager, just create once.
                if(System.getSecurityManager() == null)
                    System.setSecurityManager(new SecurityManager());
                
                
            
           
            
            //getCallCenterID
            long callCenterId= ((long)(idOffice/10))*10;
            System.out.println("Id CallCenter: "+callCenterId);
            
           //Ottiene il suo CallCenter
            Registry registry = LocateRegistry.getRegistry(GlobalParameters.RegistryHOST,GlobalParameters.RegistryPORT);
            remoteCallCenter= (ICallCenter_Office)registry.lookup(String.valueOf(callCenterId));
            
            System.out.println("exporting Office Object");
            exportSelf();
            
            //appena viene creato si deve registrare al CallCenter associato
            remoteCallCenter.registerOffice(this);
            
            
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        
        
        
    }

   
/*ICallCenter_Office methods----------------------------------------------------------------------------------------*/
    /**
     * Richiedere di eseguire determinate operazioni ad un Ufficio
     * @param idSignal id Segnalazione
     * @param idCallCenter id CallCenter
     * @throws RemoteException 
     */
    @Override
    public synchronized void procedure(long idSignal, long idCallCenter) throws RemoteException {
        try {
                  //procedura di disbrigo pratica
                  System.out.println("Dispatching procedure request");
                  //implementazione del disbrigo
                  int tmin=GlobalParameters.TMIN_PROCEDURE;
                  int tmax=GlobalParameters.TMAX_PROCEDURE;
                  Random r = new Random();
                  int delay = r.nextInt(tmax-tmin) + tmin;

                  Thread.sleep(delay*100);
                  //alla fine del disbrigo
                  //chiamo il metodo remoto sul callCenter per indicare la fine della procedura mandando
                  //l'ID della segnalazione, del callCenter chiamante e dell'Ufficio
                  remoteCallCenter.finishProcedure(idSignal,idCallCenter,idOffice);
              } catch (InterruptedException ex) {
                  ex.printStackTrace(System.out);
              }    
    }

    /**
     * Ritorna id dell'ufficio
     * @return id Office
     * @throws RemoteException 
     */
    @Override
    public long getOfficeId() throws RemoteException {
        return idOffice;
     }
    
 /*Useful methods----------------------------------------------------------------------------------------*/
     /**
     * Export class to remote reference.
     * 
     */
    protected void exportSelf()
    {
        try {
            UnicastRemoteObject.exportObject(this,0);
        } catch (RemoteException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
