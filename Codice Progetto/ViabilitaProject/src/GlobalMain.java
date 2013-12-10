
import entities.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe che contiene il main globale per l'avvio automatico del sistema e degli user per la DEMO.
 * @author Alessio_Gregory_Ricky
 */
public class GlobalMain {
    
    public GlobalMain(){
    }

    
    
    public static void main(String args[]) throws InterruptedException, IOException{
        
        
        System.out.println("GLobalMain is starting...");
        
        //elimina i file relativi a prove precedenti
        File file = new File("./fileproject");
        if(file.isDirectory()){
            
            for(File f : file.listFiles()){
                if(f.isFile()){
                    f.delete();
                }
            }
        }
 
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }
        
        
        String[] arguments= new String[2];
        arguments[0]="1";
        arguments[1]="1";
 
        //execute RMIRegistry
        GlobalMain.execJarFile("RMIRegistry.jar", arguments,"RMIRegistry.txt");
        //guard sleep
        Thread.sleep(5000);
        //execute 5 CallCenter
        for(int i=1;i<6;i++){
            arguments[0]=String.valueOf(i*10); 
            GlobalMain.execJarFile("CallCenter.jar", arguments,"CallCenter_"+(i*10)+".txt");
        }
        //guard sleep
        Thread.sleep(1000);
        //execute 20 Office
        for(int i=1;i<6;i++){
            for(int z=1;z<5;z++){
            arguments[0]=String.valueOf((i*10)+z); 
            GlobalMain.execJarFile("Office.jar", arguments,"Office_"+((i*10)+z)+".txt");
            }
        }
        //guard sleep 
        Thread.sleep(5000);
        
        try{
        if(args[0].equalsIgnoreCase("demo1")){
                
                GlobalMain.sendDemo1(arguments);
        }
        }catch(NullPointerException e){
            System.out.println("Inserire gli argomenti nel main: demo1 oppure demo2");
        }
 
    }
 
 /**
  * 
  * @param jarFile nome del file .jar da eseguire
  * @param args argomenti da passare al file .jar da eseguire
  * @param nomefile nome del file dove salvare i Log di output
  * @throws InterruptedException 
  */
 public static void execJarFile(String jarFile,String args[],String nomefile) throws InterruptedException{
        try {
            String separator = System.getProperty("file.separator");
            //String classpath = System.getProperty("java.class.path");
            //String realPath= controlPath(classpath);
            String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
            ArrayList<String> list = new ArrayList<>();
            list.add("java");
            list.add("-jar");
            list.add("-Djava.security.policy=./project.policy");
            list.add("./dist/"+jarFile);
            for(int i=0;i<args.length;i++){
                list.add(args[i]);
            }
            

            //Process proc = Runtime.getRuntime().exec("java -jar ./dist/"+jarFile+" "+argstr);
            ProcessBuilder procb = new ProcessBuilder(list);
            File file = new File("./fileproject/"+nomefile);
            if(file.exists()){
             file.delete();
            }
            file.createNewFile();

            procb.redirectOutput(file);
            Process proc= procb.start();
            //proc.waitFor();
            // Then retreive the process output

           
        } catch (IOException ex) {
            Logger.getLogger(GlobalMain.class.getName()).log(Level.SEVERE, null, ex);
        }
 }
    
/***
 * UNUSED METHOD
 */
//    public static String controlPath(String path){
//        String realPath="";
//        StringTokenizer st= new StringTokenizer(path," ");
//        realPath= st.nextToken();
//        while(st.hasMoreTokens()){
//            realPath= realPath+ "\\ " +st.nextToken();
//        }
//        
//        st= new StringTokenizer(realPath,"&");
//        realPath= st.nextToken();
//        while(st.hasMoreTokens()){
//            realPath= realPath+ "\\&" +st.nextToken();
//        }
//        
//        String[] temp;
//        String delimiter = "build";
//        temp = realPath.split(delimiter);
//        realPath= temp[0]+"dist/";
//        return realPath;
//        
//    }
 
  
      public static void sendDemo1(String arguments[]) throws InterruptedException{
        /*Commentare questa parte per avviare altre DEMO--------------------------------------------------------------*/
        //execute User for DEMO1
            //User Gregory
            arguments= new String[7];
            arguments[0]="Gregory";
            arguments[1]="10";
            arguments[2]="manual"; //se si vogliono generare gli uffici da contattare nella segnalazione 
            //in modo casuale impostare questo valore a "random"
            arguments[3]="11";
            arguments[4]="12";
            arguments[5]="22";
            arguments[6]="21";
            //se si vogliono inviare meno di 4 uffici come segnalazione impostare l'id a "0"
            GlobalMain.execJarFile("User.jar", arguments,"User"+arguments[0]+".txt");
            
            //guard time per permettere il verificarsi del deadlock
            Thread.sleep(3000);
            
            //User Alessio
            arguments= new String[7];
            arguments[0]="Alessio";
            arguments[1]="20";
            arguments[2]="manual"; //se si vogliono generare gli uffici da contattare nella segnalazione 
            //in modo casuale impostare questo valore a "random"
            arguments[3]="21";
            arguments[4]="51";
            arguments[5]="52";
            arguments[6]="33";
            //se si vogliono inviare meno di 4 uffici come segnalazione impostare l'id a "0"
            GlobalMain.execJarFile("User.jar", arguments,"User"+arguments[0]+".txt");
            
            //guard time per permettere il verificarsi del deadlock
            Thread.sleep(3000);
            
            //User Ricky
            arguments= new String[7];
            arguments[0]="Ricky";
            arguments[1]="30";
            arguments[2]="manual"; //se si vogliono generare gli uffici da contattare nella segnalazione 
            //in modo casuale impostare questo valore a "random"
            arguments[3]="33";
            arguments[4]="41";
            arguments[5]="43";
            arguments[6]="11";
            //se si vogliono inviare meno di 4 uffici come segnalazione impostare l'id a "0"
            GlobalMain.execJarFile("User.jar", arguments,"User"+arguments[0]+".txt");
    }
     
  
}


