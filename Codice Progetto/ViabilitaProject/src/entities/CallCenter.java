/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import commonInterface.GlobalParameters;
import commonInterface.ICallCenter;
import commonInterface.ICallCenter_CallCenter;
import commonInterface.ICallCenter_Office;
import commonInterface.ICallCenter_User;
import commonInterface.IOffice;
import commonInterface.IUser;
import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *Rappresenta il generico CallCenter
 * @author Alessio_Gregory_Ricky
 */


public class CallCenter implements ICallCenter_CallCenter,ICallCenter_Office,ICallCenter_User, Serializable{

    //Main variables
    //Callcenter ID
    private long id=-1;
    //Elenco di uffici registrati alla CallCenter
    private LinkedBlockingQueue<IOffice> myOffices=null;
    //Lista degli altri callcenter noti
    private Hashtable<Long,ICallCenter_CallCenter> otherCallCenter=null;
    //Lista delle richieste degli utenti
    private Hashtable<Long,Signal> userRequest=null;
    //Counter utilizzato per generare gli id delle richieste
    private long counter=0;
    //Registry
    private Registry registry=null;
    
    //Elenco di token LOCALI. (ovvero per la gestione delle proprie risorse)
    private TokenLockTimer[] token;
   
    public CallCenter(long id) {
        //Settaggio dell'id
        this.id=id;
        
        //Init delle strutture dati
        myOffices=new LinkedBlockingQueue<>(4);
        otherCallCenter=new Hashtable<>();
        userRequest=new Hashtable<>();
        
        //inti Array
        token=new TokenLockTimer[4];
        
        //Init dei token LOCALI
        for(int x=0;x<4;x++) token[x]=new TokenLockTimer();
        
        //Retrieve del registro 
        try {
            registry=LocateRegistry.getRegistry(GlobalParameters.RegistryHOST, GlobalParameters.RegistryPORT);
        } catch (RemoteException ex) {
            ex.printStackTrace(System.out);
        }
        System.out.println("New Call Center: "+id);
    }
    
    
    public void init(){
    try {
            //Mi tiro fuori lo stub
            ICallCenter stub= (ICallCenter)UnicastRemoteObject.exportObject(this,0);
            //Lo registro
            registry.rebind(String.valueOf(id), stub);
            
        } catch (RemoteException ex) {
            ex.printStackTrace(System.out);
        }
    
    }
    
 /*ICallCenter_CallCenter methods----------------------------------------------------------------------------*/
    
    /**
     * Richiede l'esecuzione del lavoro ad un ufficio remoto
     * @param idSignal idSegnalazione del richiamante
     * @param officeId ufficio richiesto
     * @param MyCallCenterId id del callcenter richiamante
     * @throws RemoteException 
     */
    @Override
    //CHIAMATO DA UN ALTRO CALLCENTER
    public void otherOffice(long idSignal,long officeId, long MyCallCenterId) throws RemoteException {
        System.out.println("Receiving a new signal from other CallCenter");
        //Cerco il mio ufficio
        for(IOffice office : myOffices){
            if(office.getOfficeId()==officeId){
                /*
                 *  Richiamo la procedure passando id della segalazione  
                 *  E id del callCenter che richiede l'ufficio che non è tra quelli LOCALI del callcenter chiamante.
                 */      
                office.procedure(idSignal, MyCallCenterId);
            }
        }
            
    }

    /**
     * Per mandare la probe quando scatta il timeOut
     * @param idOfficeToCheck lista degli uffici bloccati
     * @param whoSendTheProbe chi ha mandato la probe
     * @param idSignalBlocking l'id della segnalazione che sta bloccando l'ufficio in coda alla lista
     * @param idCallCenterSignalSource id del callCenter che ha scatenato il timeout
     * @param idSignalSource id della segnalazione che ha scatenato il timeout
     * @throws RemoteException 
     */
    @Override
    //CHIAMATO DA UN ALTRO CALLCENTER
    public synchronized void sendProbe(ConcurrentLinkedDeque<Long> idOfficeToCheck, long whoSendTheProbe, long idSignalBlocking, long idCallCenterSignalSource, long idSignalSource) throws RemoteException {
        System.out.println("Send probe inviata da: "+whoSendTheProbe);
        System.out.println("Send probe inviata per conto di: "+idCallCenterSignalSource);
        System.out.println("Gli uffici bloccati sono: "+idOfficeToCheck);
        
//       ti prendi la signal con quell'id
        Signal tempSignal = userRequest.get(idSignalBlocking); 
        //se non è null
        if(tempSignal != null)
            System.out.println("La Segnalazione con id :"+idSignalBlocking);
            //prendi gli uffici già bloccati da quella segnalazione e vedi se contiene l'ufficio in coda
            //(questo perchè nel frattempo che si invia la probe quell'ufficio, che era certamente bloccato allo scadere del timeout
            // può essersi sbloccato ).
            // Se l'ufficio è ancora bloccato
            if(tempSignal.getLockOffices().contains(idOfficeToCheck.getLast()))
            {
                System.out.println("L'ultimo dei bloccati è realmente bloccato");
                //verifica se non ho bloccato tutti gli uffici 
                if(!(tempSignal.isAllLock())){
                    System.out.println("Gli uffici non sono tutti bloccati");
                    //si prende la lista degli uffici ancora non bloccati
                    LinkedList<Long> tempList = new LinkedList<>(tempSignal.getOffices());
                    //prendo il primo ufficio non bloccato 
                    Long officeReq = tempList.get(tempSignal.getLockOffices().size());
                    System.out.println("il prossimo ufficio che mi serve è: "+officeReq);
//                    DEADLOCK CHECK AND NOTIFY
                    //se la lista contiene questo ufficio, allora c'è deadlock e si procede con
                    //la procedura di ripristino
                    if(idOfficeToCheck.contains(officeReq)){
                            System.out.println("Ufficio che mi serve è già contenuto nella lista");
//                          //otteniamo il callcenter propietario di quell'ufficio
                            ICallCenter_CallCenter callCenter = otherCallCenter.get(getCallCenterOfficeId(officeReq));
                            //chiede a quel CallCenter di farsi dire qual'è il callCenter che sta bloccando ( tramite un suo utente ) quella risorsa 
                            long CallCenterwhoLockOffice = callCenter.whoLock(officeReq);
                            System.out.println("Il callcenter che blocca l'ufficio: "+CallCenterwhoLockOffice);
                            long idSignaltoEliminate = callCenter.whoSignal(officeReq);
                            System.out.println("La segnalazione che blocca tutto è: "+idSignaltoEliminate);
                            //ottengo l'oggetto callCenter trovato all'istruzione precedente
                            ICallCenter_CallCenter lockingCallCenter = otherCallCenter.get(CallCenterwhoLockOffice);
                            lockingCallCenter.eliminateAndPosticipateSignal(idSignaltoEliminate);
                            
                            System.out.println("Deadlock risolto, Invio di ripetere la richiesta segnalazione al callcenter: "+idCallCenterSignalSource+" con signal id: "+idSignalSource);
                            getOtherCallCenter(idCallCenterSignalSource).answerProbe("Deadlock risolto,Riprovare",idSignalSource);
                        
                    }
                    //altrimenti non possiamo ancora dire se c'è o meno il deadlock
                    else{
                        System.out.println("Ufficio non ancora contenuto in lista");
                        //aggiunge l'ufficio in coda alla lista
                        System.out.println("aggiungo ufficio");
                        idOfficeToCheck.add(officeReq);
                        //se l'ufficio è locale, ovvero è mio
                        if(isLocalOffice(officeReq)){
                            System.out.println("è un mio ufficio locale");
                            //prendo l'id del callCenter che sta bloccando questi mio ufficio
                            long idCallCenter = token[retrieveOfficeID(officeReq)].getIdCallCenter();
                            System.out.println("idCallCenter che blocca il mio ufficio è: "+idCallCenter);
                            //se non sono io ( per conto di un mio utente ) a bloccare la risorsa
                            if(idCallCenter != id){
                                    System.out.println("Non sono io a bloccare il mio ufficio");
                                //mando la probe al callCenter che ha un'utente che sta bloccando la risorsa passandogli:
                                //la lista degli uffici bloccati
                                //l'id di chi sta mandando la probe
                                //l'id della segnalazione che sta bloccando l'ufficio in coda alla lista
                                //idCallCenter che ha scatenato il timeout
                                //idSegnalazione che ha scatenato il timeout
                                getOtherCallCenter(idCallCenter).sendProbe(idOfficeToCheck,id,token[retrieveOfficeID(officeReq)].getIdSignal(), idCallCenterSignalSource, idSignalSource);
                            }
                            //se sono io a bloccare la risorsa per conto di un mio utente
                            else
                            {
                                System.out.println("Sono io a bloccare il mio ufficio mando auto probe");
//                                auto-send probe ciclica LOCALE
                               sendProbe(idOfficeToCheck,id,token[retrieveOfficeID(officeReq)].getIdSignal(),idCallCenterSignalSource, idSignalSource);
                            }
                        }else
                        {
                            System.out.println("Ufficio non locale non è il mio");
//                          SE L'UFFICIO NON E' LOCALE DEVO INNESCARE LA PROCEDURA IN REMOTO
                            //si prende il callCenter che è propietario di quella risorsa
                            ICallCenter_CallCenter callCenter = getOtherCallCenter(getCallCenterOfficeId(officeReq));
                            //chiede a quel CallCenter di farsi dire qual'è il callCenter che sta bloccando ( tramite un suo utente ) quella risorsa 
                            long CallCenterwhoLockOffice = callCenter.whoLock(officeReq);
                            System.out.println("Il callcenter che blocca l'ufficio è: "+CallCenterwhoLockOffice);
                            //ottengo l'oggetto callCenter trovato all'istruzione precedente
                            ICallCenter_CallCenter lockingCallCenter = getOtherCallCenter(CallCenterwhoLockOffice);
                            //invia la probe a quel CallCenter mandando:
                            //la lista degli uffici bloccati
                            //l'id di chi sta mandando la probe
                            //l'id della segnalazione che sta bloccando l'ufficio in coda alla lista
                            //idCallCenter che ha scatenato il timeout
                            //idSegnalazione che ha scatenato il timeout
                            System.out.println("Mando la probe ad: "+CallCenterwhoLockOffice);
                            lockingCallCenter.sendProbe(idOfficeToCheck,id,callCenter.whoSignal(officeReq),idCallCenterSignalSource, idSignalSource);
                        }
                        
                    }
                    
                }else{
                    try {
                        System.out.println("Tutti sono già bloccati aspetta un po...");
                        //altrimenti si dovrebbe dire a chi ha inviato la probe di
                        //attendere un'altro poco
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace(System.out);
                        }
                        System.out.println("Deadlock evitato per conclusione task.Riprovare, mandato: "+idCallCenterSignalSource);
                        getOtherCallCenter(idCallCenterSignalSource).answerProbe("Deadlock evitato per conclusione task.Riprovare",idSignalSource);
                        
                 }
            }
            else{
                System.out.println("L'ultimo dei bloccati non è più bloccato");
//                VUOL DIRE CHE TUTTO SI E' GIA' RISOLTO perchè quell'ufficio si è sbloccato durante la creazione della probe.
//                DEVO NOTIFICARLO A CHI MI HA MANDATO LA PROBE!
                ICallCenter_CallCenter callCenter = getOtherCallCenter(idCallCenterSignalSource);
                System.out.println("Deadlock Evitato. Via libera!");
                callCenter.answerProbe("Deadlock Evitato. Via libera!",idSignalSource);
            }
        
    }
    
    /**
     * Serve per indicare ad un callCenter che può riprocessare la propria segnalazione
     * @param answare
     * @param idSignal id segnalazione
     * @throws RemoteException 
     */
    @Override
    public void answerProbe(String answare,long idSignal) throws RemoteException
    {
        System.out.println("Probe Answare: " + answare);
        //mi prendo la segnalazione con quell'id
        Signal tmpSignal = userRequest.get(idSignal);
        //verifico se è null
        if(tmpSignal!=null){
            //per tutti gli uffici bloccati all'interno di quella segnalazione
            for(Long officeLock : tmpSignal.getLockOffices()){
                //sveglia i thread bloccati su quegli uffici
                System.out.println("Sto sbloccando l'ufficio: "+officeLock);
                if(isLocalOffice(officeLock)){
                    System.out.println("è mio");
                    token[retrieveOfficeID(officeLock)].setWaked(officeLock);
                }
                else{
                    System.out.println("Non è mio lo chiedo al proprietario");
                    getOtherCallCenter(getCallCenterOfficeId(officeLock)).unlockToken(officeLock);
                }  
            }
            System.out.println("Rimuovo la segnalazione con id: "+tmpSignal.getId());
            //elimina la segnalazione precedente che deve essere riprocessata
            userRequest.remove((tmpSignal.getId()));
            //rinvio della segnalazione che aveva fatto scattare il timeout
            //questa viene inviata da capo, ovvero anche gli uffici che già aveva ottenuto
            //deve riottenerli
            sendSignal(tmpSignal.getUser());
            
        }
    }
    
    /**
     * Richiamata da un callCenter remoto per indicare la fine del lavoro da parte di un suo ufficio
     * @param idSignal id della segnalazione per conto della quale l'ufficio ha lavorato
     * @param idOffice ufficio che ha finito il lavoro
     * @throws RemoteException 
     */
     @Override
    //CHIAMATO DA UN ALTRO CALLCENTER
    public void finishOther(long idSignal,long idOffice) throws RemoteException {
        
            System.out.println("L'ufficio " + idOffice + " ha completato la richiesta locale (Notificata dall'esterno)");
            //Tiro fuori la segnalazione
            Signal tmpSignal=userRequest.get(idSignal);
            if(tmpSignal!=null){
                //rimuovo l'ufficio
                tmpSignal.removeLockOffice(new Long(idOffice));
                //Se la lista è vuota rilascio tutte le risorse
                if(tmpSignal.isLockEmpty()) release(idSignal);
            }
    }

    /**
     * 
     * @return id del callcenter su cui viene richiamato
     * @throws RemoteException 
     */
    @Override
    //CHIAMATA DA UN ALTRO CALLCENTER
    public long getId() throws RemoteException {
        return id;
    }
    /**
     * Richiede il lock su un ufficio remoto
     * @param idOffice Id Ufficio
     * @param MyCallCenterId Id CallCenter Richiamante
     * @param idSignal id Segnalazione
     * @return Stringa che indica se abbiamo preso il token
     * @throws RemoteException 
     */
    @Override
    //CHIAMATA DA UN ALTRO CALLCENTER
    public String retrieveToken(long idOffice, long MyCallCenterId, long idSignal) throws RemoteException {
        //Un altro callcenter ci sta chiedento il token
        final Long idOfficetmp=new Long(idOffice);
        final long MyCallCenterIdtmp=MyCallCenterId;
        final long idSignaltmp=idSignal;
        IDeadlock deadlock = new IDeadlock() {

            @Override
            public void execute() {
                try{
                    //Esecuzione della deadlock procedure a causa di una richiesta remota
                    System.out.println("execute Remote deadlock");
                    //Creo una nuova lista
                    ConcurrentLinkedDeque<Long> idOfficeToCheck=new ConcurrentLinkedDeque<>();
                    //Aggiungo l'ufficio che causa deadlock
                    idOfficeToCheck.add(idOfficetmp);
                    //Tira fuori il callCenter che blocca l'ufficio
                    ICallCenter_CallCenter callCenter=getOtherCallCenter(token[retrieveOfficeID(idOfficetmp)].getIdCallCenter());
                    //Manda la probe a quel callcenter
                    callCenter.sendProbe(idOfficeToCheck, id, token[retrieveOfficeID(idOfficetmp)].getIdSignal(), MyCallCenterIdtmp,idSignaltmp );
                }
                catch(Exception ex){
                    ex.printStackTrace(System.out);
                }
                }
            };
        //Configura la procedura in caso di deadlock
        token[retrieveOfficeID(idOffice)].setDeadlockProcedure(deadlock);
        
        //Si mette in waiting per cercare di ottenere il token
        token[retrieveOfficeID(idOffice)].setWaiting(idOffice,MyCallCenterId,idSignal);
        return "ok";
        
    }

    /**
     * Richiede lo sblocco del token per quel determinato ufficio remoto
     * @param idOffice id Ufficio
     * @throws RemoteException 
     */
    @Override
    //CHIAMATA DA UN ALTRO CALLCENTER
    public void unlockToken(long idOffice) throws RemoteException {
        token[retrieveOfficeID(idOffice)].setWaked(idOffice);
        
    }
    
   /**
     * Quale callcenter fatto il lock su questo ufficio
     * @param idOffice id Ufficio
     * @return id callcenter
     * @throws RemoteException 
     */
    @Override
    //CHIAMATO DA UN ALTRO CALLCENTER
    public long whoLock(long idOffice) throws RemoteException {
        
        return token[retrieveOfficeID(idOffice)].getIdCallCenter();
        
    }
    
    /**
     * id della segnalazione che ha bloccato l'ufficio
     * @param idOffice id Ufficio
     * @return id Segnalazione
     * @throws RemoteException 
     */
    @Override
    //CHIAMATO DA UN ALTRO CALLCENTER
    public long whoSignal(long idOffice) throws RemoteException {
        
        return token[retrieveOfficeID(idOffice)].getIdSignal();
        
    }
    
    /**
     * Metodo per eliminare la segnalazione che causa il deadlock e notificare all'utente che deve rinviare più tardi
     * @param idSignal id segnalazione
     * @throws RemoteException 
     */
    @Override
    //CHIAMATO DA UN'ALTRO CALLCENTER
    public void eliminateAndPosticipateSignal(long idSignal) throws RemoteException {
        System.out.println("Procedura di ripristino");
        //mi prendo la segnalazione con quell'id
        Signal tmpSignal = userRequest.get(idSignal);
        //verifico se è null
        if(tmpSignal!=null){
            System.out.println("Segnalazione mia");
            //per tutti gli uffici bloccati all'interno di quella segnalazione
            for( Long officeLock : tmpSignal.getLockOffices()){
                //sveglia i thread bloccati su quegli uffici
                System.out.println("Sto sbloccando l'ufficio: "+officeLock);
                if(isLocalOffice(officeLock)){
                    System.out.println("è mio");
                    token[retrieveOfficeID(officeLock)].setWaked(officeLock);
                }
                else{
                    System.out.println("Non è mio lo chiedo al proprietario");
                    getOtherCallCenter(getCallCenterOfficeId(officeLock)).unlockToken(officeLock);
                }
            }
            System.out.println("Tutti svegliati");
            System.out.println("Comunico all'utente");
            IUser user = tmpSignal.getUser();
            user.finishSignal("retryLater");
            userRequest.remove(new Long(idSignal));
            System.out.println("Risposta inviata");
        }
    }

//ICallCenter_Office methods----------------------------------------------------------------------------
    
    /**
      * Ufficio che si registra ne proprio callCenter
      * @param office id Ufficio
      * @throws RemoteException 
      */
    @Override
    //CHIAMATO DA UN OFFICE
    public void registerOffice(IOffice office) throws RemoteException {
        System.out.println("Registering a new OFFICE");
        //Registro l'ufficio in una coda di massimo 4 elementi
        myOffices.add(office);
    }

    /**
      * Viene richiamata dall'ufficio per indicare che ha finito le operazioni richieste
      * @param idSignal id Segnalazione
      * @param idCallCenter id CallCenter
      * @param idOffice id Ufficio
      * @throws RemoteException 
      */
    @Override
    //CHIAMATO DA UN OFFICE
    public void finishProcedure(long idSignal,long idCallCenter,long idOffice) throws RemoteException {
        
        //Se sono io ovvero il callcenter che aveva fatto la richiesta coincide con me stesso
        if(idCallCenter == id)
        {
            System.out.println("L'ufficio " + idOffice + " ha completato la richiesta locale.");
            //Tiro furi la segnalazione
            Signal tmpSignal=userRequest.get(idSignal);
            if(tmpSignal!=null){
                //rimuovo l'ufficio
                tmpSignal.removeLockOffice(new Long(idOffice));
                //Se la lista è vuota lascio tutte le risorse
                if(tmpSignal.isLockEmpty()) release(idSignal);
            }
            

        }else{
            System.out.println("Notifichiamo all'callCenter " + idCallCenter + " l'esecuzione dell'ufficio " + idOffice);
            //Tiro fuori il callCenter che aveva richiesto la risorsa
            ICallCenter_CallCenter other = getOtherCallCenter(idCallCenter);
            //Chiamo la finishOther su di lui
            other.finishOther(idSignal, idOffice);
        }
            
            
       
    }
    
    
    
    
/*ICallCenter_User methods------------------------------------------------------------------------------------------ */
     
    /**
     * Utente che manda una segnalazione al callCenter vuole gli uffici
     * @param user Passa un riferimento a se stesso.
     * @throws RemoteException 
     */
    @Override
    //CHIAMATO DA UN USER
    public void sendSignal(IUser user) throws RemoteException {
        //Ottengo la lista di uffici dall'utente che mi contatta.
        LinkedBlockingQueue<Long> officelist=user.getList();
        //Creo una signal alla quale è associato un counter che tiene conto del numero di richieste calcolate localmente
        //la lista di uffici ricevuta
        //lo stub dell'utente
        final Signal tempSignal=new Signal(counter, officelist, user);
        //conservo la signal dell'utente nella table
        userRequest.put(new Long(counter), tempSignal); 
        //ritorno il suo ID all'utente
        user.setSignalId(tempSignal.getId());
        //Inizio a ciclare, controllando gli uffici, se sono locali o meno.
        for(Long idOffice : officelist){
            
            //se è locale...
            if(isLocalOffice(idOffice)){
                final Long idOfficetmp=new Long(idOffice);
                
                IDeadlock deadlock = new IDeadlock() {

                    @Override
                    public void execute() {
                        try{
                            //Esecuzione della deadlock procedure a causa di una richiesta locale
                            System.out.println("execute Local deadlock");
                            //Creo una nuova lista
                            ConcurrentLinkedDeque<Long> idOfficeToCheck=new ConcurrentLinkedDeque<>();
                            //Aggiungo l'ufficio che causa deadlock
                            idOfficeToCheck.add(idOfficetmp);
                            //Tira fuori il callCenter che blocca l'ufficio
                            ICallCenter_CallCenter callCenter=getOtherCallCenter(token[retrieveOfficeID(idOfficetmp)].getIdCallCenter());
                             //Manda la probe a quel callcenter
                            callCenter.sendProbe(idOfficeToCheck, id, token[retrieveOfficeID(idOfficetmp)].getIdSignal(), id, tempSignal.getId());
                        }
                        catch(Exception ex){
                            ex.printStackTrace(System.out);
                        }
                        }
                };
                //Assegno la procedure al token in caso di timeOut
                token[retrieveOfficeID(idOffice)].setDeadlockProcedure(deadlock);
                //vado in wait fino a quando il token associato a quell'elemento non si libera..
                 token[retrieveOfficeID(idOffice)].setWaiting(idOffice,id,tempSignal.getId());
                 //Aggiungo l'ufficio agli uffici per cui ho ottenuto il token
                 tempSignal.addLockOffice(idOffice);
            }
            else{
                //Se non è il mio ufficio richiamo la retrieveToken su un altro CallCanter
                ICallCenter_CallCenter callCenter=getOtherCallCenter(getCallCenterOfficeId(idOffice));
                String answer = callCenter.retrieveToken(idOffice, id, tempSignal.getId());
                if(answer.equalsIgnoreCase("ok")) tempSignal.addLockOffice(idOffice);
                else System.out.println("Problemi nel locking del token di other");
            }
            //SLEEP DI PROVA PER FAR VERIFICARE IL DEADLOCK
            try {
                Thread.sleep(GlobalParameters.GUARDTIME_ACCESSTOKEN);
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.out);
            }
        }
        //Se tutti gli uffici che mi servono sono bloccati
        if(tempSignal.isAllLock())
        {
            //Esegue
            executeJobs(tempSignal.getId());
            
        }
        counter++;
    }
    
/*SubMethods ----------------------------------------------------------------------------------------------------*/
    /**
     * Retrieve dello stub associato al CallCenter
     * @param idCallCenter id del callcenter di cui si vuole ottenere lo stub
     * @return Lo stub del callCenter tramite il suo ID
     * @throws RemoteException 
     */
    private ICallCenter_CallCenter getOtherCallCenter(long idCallCenter) throws RemoteException{
        //Cerco l'altro callcenter tra quelli noti
        ICallCenter_CallCenter callCenter=otherCallCenter.get(new Long(idCallCenter));
        if(callCenter==null){
            try {
                    //Prendo il CallCenter che manca tra quelli noti
                    callCenter= (ICallCenter_CallCenter) registry.lookup(String.valueOf(idCallCenter));
                    //Lo memorizzo tra quelli noti
                    otherCallCenter.put(new Long(idCallCenter), callCenter);
                } catch (NotBoundException ex) {
                    ex.printStackTrace(System.out);
                } catch (AccessException ex) {
                    ex.printStackTrace(System.out);
                }
        }
        return callCenter;
        

    }
    
     
    /**
     * Esegue tutti i task dopo aver ottenuto tutti i lock.
     * @param idSignal id Segnalazione
     * @throws RemoteException 
     */
    private void executeJobs(long idSignal) throws RemoteException
    {   
        //Retrieve del signal, ovvero la richiesta dell'utente
        Signal tmpSignal=userRequest.get(idSignal);
        //Inizio a ciclare gli uffici
        for(Long idOffice : tmpSignal.getLockOffices())
        {
            //se è locale...
            if(isLocalOffice(idOffice))
            {
                //cerco l'ufficio in questione
                for(IOffice office : myOffices){
                    if(office.getOfficeId()==idOffice.longValue()){
                        /* Quando lo trovo richiamo la procedura su office passando id
                         * Della segnalazione e id del callcenter che ha richiamato l'esecuzione
                        */
                        office.procedure(idSignal, id);
                        
                    }
                }
            }
            else
            {
                //Se non è il mio ufficio richiamo la otherOffice su un altro CallCanter
                ICallCenter_CallCenter callCenter=getOtherCallCenter(getCallCenterOfficeId(idOffice));
                callCenter.otherOffice(idSignal,idOffice, id);
            }
        }
        
    }

    /**
     * Rilascia tutte le risorse dopo che la computazione è avvenuta.
     * @param idSignal id Segnalazione
     * @throws RemoteException 
     */
    private void release(long idSignal) throws RemoteException
    {
        //tira fuori la segnalazione
        Signal temp = userRequest.get(new Long(idSignal));
        if(temp != null)
        {
            //Prendo gli uffici che l'utente mi aveva richiesto
            LinkedBlockingQueue<Long> offices = temp.getOffices();
            for(Long idOffice : offices)
            {
                //Se sono locali li rilascio io
                if(isLocalOffice(idOffice)) token[retrieveOfficeID(idOffice)].setWaked(idOffice);
                else
                {
                    //Se non è il mio ufficio richiamo la unLockToken su un altro CallCanter
                    ICallCenter_CallCenter callCenter=getOtherCallCenter(getCallCenterOfficeId(idOffice));
                    callCenter.unlockToken(idOffice);
                }
            }
            temp.getUser().finishSignal("Ho finito la tua richiesta...");
        }
    }
    
   
    
    
/*Useful methods------------------------------------------------------------------------------------------ */
    /**
     * Tells if the office referenced is local.
     * @param idOffice id ufficio
     * @return boolean true se è un ufficio del callcenter locale altrimenti ritorna false
     */
    private boolean isLocalOffice(long idOffice)
    {
        long callCenterId=getCallCenterOfficeId(idOffice);
        if(callCenterId==id) return true;
        return false;
    }
    
    /**
     * Get the equivalent int position in the array, starting from the OfficeID.
     * Example: OfficeID : 34 -> return 4 
     * @param idOffice id Ufficio
     * @return the equivalent id
     */
    private int retrieveOfficeID(long idOffice)
    {
        //Ritorna la posizione dell'ufficio
        return (int)(idOffice%10) -1; 
    }
    
    /**
     * Restituisce l'ID della CallCenter
     * @param idOffice id Ufficio
     * @return id della callCenter a cui appartiene un ufficio
     */
    private long getCallCenterOfficeId(long idOffice){
        return ((long)(idOffice/10))*10;
    }

        
}