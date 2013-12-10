
import entities.Office;

/**
 * Classe che contiene il main che crea un'oggetto ufficio
 * @author Alessio_Gregory_Ricky
 */
public class MainOffice {
    
    public static void main(String args[]){
        //Creazione dell'oggetto Office ed uso args[0] come id
        Office office=new Office(Long.parseLong(args[0]));
        System.out.println("Main Office Ok");
    
    }
}
