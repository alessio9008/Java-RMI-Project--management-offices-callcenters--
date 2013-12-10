/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package commonInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *interfaccia che contiene i metodi che possono essere richiamati solo dagli uffici
 * @author Alessio_Gregory_Ricky
 */
public interface ICallCenter_Office extends ICallCenter{
    
     /**
      * Ufficio che si registra ne proprio callCenter
      * @param office id Ufficio
      * @throws RemoteException 
      */
     public void registerOffice(IOffice office) throws RemoteException;
     /**
      * Viene richiamata dall'ufficio per indicare che ha finito le operazioni richieste
      * @param idSignal id Segnalazione
      * @param idCallCenter id CallCenter
      * @param idOffice id Ufficio
      * @throws RemoteException 
      */
     public void finishProcedure(long idSignal,long idCallCenter,long idOffice) throws RemoteException;
     
}
    