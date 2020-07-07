/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import java.util.concurrent.Semaphore;
import java.util.ArrayList;

import Jama.Matrix;

public class Monitor {

    //Campos privados
    private ArrayList<Semaphore> conditionQueues;
    private Semaphore entry;
    private PetriNet pNet;
    private Log myLog;

    //Constructor
    /**
     * @param pNet Red de Petri que será controlada por el monitor.
     */
    public Monitor(PetriNet pNet) {
        this.pNet = pNet;
        this.entry = new Semaphore(1);
        
        conditionQueues = new ArrayList<Semaphore>();
        
        for(int i=0; i<(pNet.getIncidenceMatrix().getColumnDimension()); i++) { //for para inicializar los semaforos en las colas del monitor
            conditionQueues.add(new Semaphore(0));
        }

        try {
            this.myLog = new Log("ReportMonitor.txt");
        }
        catch (Exception e) {
            System.out.println("LOG ERROR");
        }
        
        //TODO
    }
    
    //----------------------------------------Métodos públicos---------------------------------

    public synchronized void catchMonitor() throws InterruptedException {
        entry.acquire();
    }

    public synchronized void exitMonitor() {
        entry.release();
    }

    /**
     * @param firingVector El vector de firing del thread.
     */
    public synchronized void tryFiring(Matrix firingVector) {
        if(pNet.stateEquationTest(firingVector)) {
            System.out.println("Succ ess ful firing");
            //aca se cambia la transition del firingvector del hilo
            exitMonitor();
        } else {
            
        }
    }
}