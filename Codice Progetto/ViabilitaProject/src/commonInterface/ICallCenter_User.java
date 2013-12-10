/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commonInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *Interfaccia che contiene i metodi che possono essere richiamati solo dagli user
 * @author Alessio_Gregory_Ricky
 */
public interface ICallCenter_User extends ICallCenter{
    /**
     * Utente che manda una segnalazione al callCenter vuole gli uffici
     * @param user Passa un riferimento a se stesso.
     * @throws RemoteException 
     */
    public void sendSignal(IUser user) throws RemoteException;
    
}
