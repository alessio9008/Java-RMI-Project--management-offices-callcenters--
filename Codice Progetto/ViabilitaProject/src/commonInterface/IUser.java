/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commonInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Metodi che possono essere richiamati su un User
 * @author Alessio_Gregory_Ricky
 */
public interface IUser extends Remote, Serializable{
    /**
     * Ottenere la lista degli uffici dopo aver ricevuto la richiesta dall'utente
     * @return Lista degli uffici
     * @throws RemoteException 
     */
    public LinkedBlockingQueue<Long> getList() throws RemoteException;
    /**
     * Indica di aver finito tutte le operazioni
     * @param message messaggio
     * @throws RemoteException 
     */
    public void finishSignal(String message) throws RemoteException;
    /**
     * Settare id dell'ultima segnalazione comunicandolo all'user
     * @param idSignal id Segnalazione
     * @throws RemoteException 
     */
    public void setSignalId(long idSignal) throws RemoteException;
}
