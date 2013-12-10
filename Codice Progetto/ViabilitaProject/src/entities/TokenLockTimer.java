/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import commonInterface.GlobalParameters;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rappresenta il token per il blocco delle risorse
 * @author Alessio_Gregory_Ricky
 */
/*TokenLock class -------------------------------------------------------------------------------------------*/
public class TokenLockTimer{
    
    //booleano per far passare un thread per volta dal metodo attendi
    private boolean isLocked;
    //CallCenter che tiene il token
    private long idCallCenter=-1;
    //Segnalazione che blocca il token
    private long idSignal=-1;

    /**
     * Ottenere id della segnalazione che blocca il token
     * @return id Segnalazione
     */
    public long getIdSignal() {
        return idSignal;
    }

    /**
     * Settare id della segnalazione 
     * @param idSignal id segnalazione
     */
    private synchronized void setIdSignal(long idSignal) {
        this.idSignal = idSignal;
    }
  
    
    
    //Oggetto di tipo deadlock
    private IDeadlock deadlock=null;
    
    public TokenLockTimer(){
        isLocked = false;
    }
    /**
     * Costruttore che prende un oggetto di tipo deadlock
     * @param deadlock contiene il metodo da eseguire se scatta timeout
     */
    public TokenLockTimer(IDeadlock deadlock){
        this();
        this.deadlock=deadlock;
    }

    /**
     * Da questo metodo passano uno per volta
     * @param idOffice id ufficio da bloccare
     * @param idCallCenter id del callcenter che vuole bloccare la risorsa
     * @param idSignal id della segnalazione che vuole bloccare la risorsa
     */
    public synchronized void setWaiting(final long idOffice,final long idCallCenter, long idSignal)
    {
        //Viene messo a vero se scatta il timeout
        final BooleanObject isOutTimed=new BooleanObject(false);
        //Viene messo a vero se non scatta il timeout
        final BooleanObject isOnTime=new BooleanObject(false);
        
        Timer uploadCheckerTimer = new Timer(true);
            uploadCheckerTimer.scheduleAtFixedRate(
                    new TimerTask() {
                     public void run() {
                                //verifico se è già stato svegliato dall'arrivo della richiesta
                                if(isOnTime.getValue())this.cancel();
                                else{
                                System.out.println("Timeout scattato sull'ufficio: "+idOffice);
                                //imposto isOutTimed a true così se viene svegliato non esegue nulla
                                isOutTimed.setValue(true);
                                //eseguo la procedura 
                                deadlockProcedure();
                                this.cancel();
                                }
                     }
                       }, GlobalParameters.TIMEOUT, 60*1000);
            //Solo il primo passa senza problemi gli altri aspettano
        while(isLocked)
        {
            try {
                System.out.println("Attendo l'ufficio: "+idOffice);
                this.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.out);
            }
        }
        //se si è svegliato ma già il TimerTask è stato eseguito non fa nulla
        if(isOutTimed.getValue());
        else{
        //altrimenti imposta isOnTime a true per specificare che è stato svegliato dalla sveglia
        //così che il timerTask non fa nulla
        System.out.println("Sono riuscito ad ottenere l'ufficio "+idOffice+" prima dello scadere del Timer");
        //Blocca tutti gli altri
        isLocked = true; 
        isOnTime.setValue(true);
        //Setta id del callcenter e della segnalazione che hanno bloccato la risorsa
        setIdCallCenter(idCallCenter);
        setIdSignal(idSignal);
        }
    }
    
    /**
     * Sveglio quelli in attesa
     */
    public synchronized void setWaked(long idOffice)
    {
        //Resetto gli id
        idCallCenter=-1;
        idSignal=-1;
        //sblocco
        isLocked = false;
        System.out.println("Ho liberato l'ufficio: "+idOffice);
        //Notifico a tutti
        this.notifyAll();
    }
    
    /**
     * 
     * @return Id del CallCenterChe detiene il token
     */
    public long getIdCallCenter() {
        return idCallCenter;
    }
    
    /**
     * Per settare il token, è privato
     * @param idCallCenter id callCenter
     */
    private synchronized void setIdCallCenter(long idCallCenter) {
        this.idCallCenter = idCallCenter;
    }

    /**
     * richiama execute dell'oggetto deadlock
     */
    protected void deadlockProcedure() {
        if(deadlock!=null) deadlock.execute();
    }
    /**
     * Per settare un'oggetto deadlock del quale eseguire il metodo execute
     * @param deadlock oggetto deadlock
     */
    public void setDeadlockProcedure(IDeadlock deadlock)
    {
        this.deadlock = deadlock;
    }
    
    
}
