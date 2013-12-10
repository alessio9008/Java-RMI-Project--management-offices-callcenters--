
import commonInterface.IUser;
import entities.User;

/**
 * Classe che contiene il main per la creazione dell'utente e di una sua segnalazione. 
 * @author Alessio_Gregory_Ricky
 */
public class MainUsers {
    
    public static void main (String args[]){
        //considero quattro uffici passati tramite gli args del main se non sono a null li memorizzo
        long office1,office2,office3,office4;
        if(args[3]==null)office1=0;
        else office1= Long.parseLong(args[3]);
        if(args[4]==null)office2=0;
        else office2= Long.parseLong(args[4]);
        if(args[5]==null)office3=0;
        else office3= Long.parseLong(args[5]);
        if(args[6]==null)office4=0;
        else office4= Long.parseLong(args[6]);
        /*Creo l'utente con nome args[0], e args[1] invece rappresenta id del callCenter 
        a cui si deve collegare args[2] se devo considerare i parametri del main come uffici o valori random*/
        User user=new User(args[0],Long.parseLong(args[1]),args[2],office1,office2,office3,office4);
        //Rende l'oggetto remoto la init e manda la richiesta al callcenter
        user.init();
        System.out.println("Main User Ok");
    }
}
