/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commonInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Metodi che possono essere richiamati da remoto sugli office
 * @author Alessio_Gregory_Ricky
 */
public interface IOffice extends Remote, Serializable {
    /**
     * Richiedere di eseguire determinate operazioni ad un Ufficio
     * @param idSignal id Segnalazione
     * @param idCallCenter id CallCenter
     * @throws RemoteException 
     */
    public void procedure(long idSignal,long idCallCenter) throws RemoteException;
    /**
     * Ritorna id dell'ufficio
     * @return id Office
     * @throws RemoteException 
     */
    public long getOfficeId() throws RemoteException;
}
