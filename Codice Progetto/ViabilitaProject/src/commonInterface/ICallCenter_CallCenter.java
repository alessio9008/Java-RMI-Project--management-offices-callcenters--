/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commonInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Metodi che possono essere invocati solo da altri callcenter
 * @author Alessio_Gregory_Ricky
 */
public interface ICallCenter_CallCenter extends ICallCenter {
    /**
     * 
     * @return id del callcenter su cui viene richiamato
     * @throws RemoteException 
     */
    public long getId() throws RemoteException;
    /**
     * Richiede l'esecuzione del lavoro ad un ufficio remoto
     * @param idSignal idSegnalazione del richiamante
     * @param officeId ufficio richiesto
     * @param MyCallCenterId id del callcenter richiamante
     * @throws RemoteException 
     */
    public void otherOffice(long idSignal,long officeId,long MyCallCenterId) throws RemoteException;
    /**
     * Richiede il lock su un ufficio remoto
     * @param idOffice Id Ufficio
     * @param MyCallCenterId Id CallCenter Richiamante
     * @param idSignal id Segnalazione
     * @return Una stringa per confermare la ricezione del token
     * @throws RemoteException 
     */
    public String retrieveToken(long idOffice, long MyCallCenterId, long idSignal) throws RemoteException;
    /**
     * Richiede lo sblocco del token per quel determinato ufficio remoto
     * @param idOffice id Ufficio
     * @throws RemoteException 
     */
    public void unlockToken(long idOffice) throws RemoteException;
    /**
     * Richiamata da un callCenter remoto per indicare la fine del lavoro da parte di un suo ufficio
     * @param idSignal id della segnalazione per conto della quale l'ufficio ha lavorato
     * @param idOffice ufficio che ha finito il lavoro
     * @throws RemoteException 
     */
    public void finishOther(long idSignal,long idOffice)throws RemoteException;
    /**
     * Quale callcenter fatto il lock su questo ufficio
     * @param idOffice id Ufficio
     * @return id callcenter
     * @throws RemoteException 
     */
    public long whoLock(long idOffice) throws RemoteException;
    /**
     * id della segnalazione che ha bloccato l'ufficio
     * @param idOffice id Ufficio
     * @return id Segnalazione
     * @throws RemoteException 
     */
    public long whoSignal(long idOffice) throws RemoteException;
    
    
    /**
     * Per mandare la probe quando scatta il timeOut
     * @param idOfficeToCheck lista degli uffici bloccati
     * @param whoSendTheProbe chi ha mandato la probe
     * @param idSignalBlocking l'id della segnalazione che sta bloccando l'ufficio in coda alla lista
     * @param idCallCenterSignalSource id del callCenter che ha scatenato il timeout
     * @param idSignalSource id della segnalazione che ha scatenato il timeout
     * @throws RemoteException 
     */
    public void sendProbe(ConcurrentLinkedDeque<Long> idOfficeToCheck,long whoSendTheProbe, long idSignalBlocking, long idCallCenterSignalSource, long idSignalSource) throws RemoteException;
    /**
     * Serve per indicare ad un callCenter che può riprocessare la propria segnalazione
     * @param answare Indica la risposta mandata nella probe
     * @param idSignal id segnalazione
     * @throws RemoteException 
     */
    public void answerProbe(String answare,long idSignal) throws RemoteException;
    
    /**
     * Metodo per eliminare la segnalazione che causa il deadlock e notificare all'utente che deve rinviare più tardi
     * @param idSignal id segnalazione
     * @throws RemoteException 
     */
    public void eliminateAndPosticipateSignal(long idSignal) throws RemoteException;
}
