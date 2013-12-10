/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import commonInterface.IOffice;
import commonInterface.IUser;
import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Rappresenta la segnalazione
 * @author Alessio_Gregory_Ricky
 */
public class Signal implements Serializable{
    //Id segnalazione
    private long id=-1;
    //Elenco uffici da contattare
    private LinkedBlockingQueue<Long> offices=null;
    //Utente che ha mandato la segnalazione
    private IUser user=null;
    
    //Uffici già aquisiti
    private LinkedBlockingQueue<Long> lockOffices=null;
    
    //booleano che indica che la signal è stata processata
    private boolean executeFinish=false;

    /**
     * Serve per costrire l'oggetto segnalazione
     * @param id id seganalazione
     * @param offices elenco uffici richiesti
     * @param user user per mandare la risposta
     */
    public Signal(long id,LinkedBlockingQueue<Long> offices,IUser user) {
        this.id=id;
        this.offices=offices;
        this.user=user;
        lockOffices = new LinkedBlockingQueue<>(4);

    }
    /**
     * aggiunge un ufficio tra quelli bloccati
     * @param idOffice id ufficio
     */
    public void addLockOffice(Long idOffice){
        lockOffices.add(idOffice);
    }
    /**
     * Rimuove un ufficio tra quelli bloccati
     * @param idOffice id ufficio
     */
    public void removeLockOffice(Long idOffice){
        lockOffices.remove(idOffice);
    }
    /**
     * Vede se tutti gli uffici richiesti sono stati bloccati
     * @return true se tutti gli uffici richiesti sono stati bloccati altrimenti ritorna false
     */
    public boolean isAllLock(){
        return offices.size()==lockOffices.size();
    }
    
    /**
     * Se non ci sono uffici bloccati
     * @return Booleano che insica se la lista è vuota
     */
    public boolean isLockEmpty(){
        return lockOffices.isEmpty();
    }
    /**
     * Ritorna la lista degli uffici bloccati
     * @return Lista uffici bloccati
     */
    public LinkedBlockingQueue<Long> getLockOffices() {
        return lockOffices;
    }
    
    /**
     * Costruttore con id e User soltanto
     * @param id id
     * @param user user
     */
    public Signal(long id,IUser user) {
        this.id=id;
        this.user=user;
    }
    /**
     * Costruttore una signal con id
     * @param id id
     */
    public Signal(long id) {
        this.id=id;
    }
    /**
     * settare id della signalazione
     * @param id id
     */
    public synchronized void setId(long id) {
        this.id = id;
    }

    /**
     * Settare gli uffici nella segnalazione
     * @param offices lista uffici
     */
    public synchronized void setOffices(LinkedBlockingQueue<Long> offices) {
        this.offices = offices;
    }

    /**
     * Settare l'user della segnalazione
     * @param user user
     */
    public synchronized void setUser(IUser user) {
        this.user = user;
    }

    /**
     * ritorna id Segalazione
     * @return id segnalazione
     */
    public synchronized long getId() {
        return id;
    }

    /**
     * Lista degli uffici richiesti
     * @return ritorna la lista degli uffici richiesti
     */
    public synchronized LinkedBlockingQueue<Long> getOffices() {
        return offices;
    }
    /**
     * Ritorna l'utente a cui mandare la risposta
     * @return user
     */
    public synchronized IUser getUser() {
        return user;
    }

    /**
     * Stringa che rappresenta la segnalazione
     * @return Stringa che rappresenta la segnalazione
     */
    @Override
    public String toString() {
        return "Signal{" + "id=" + id + ", offices=" + offices + ", user=" + user + '}';
    }
    
    
}
