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
        
        for(int i=0; i<(pNet.getIncidenceMatrix().getColumnDimension()); i++) { //Bucle 'for' para inicializar los semáforos en las colas del monitor.
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
     * @param vector Vector donde se buscará el índice de la transición a disparar.
     */
    public int getQueue(Matrix vector) {    //El vector contiene la transicion que se intentó disparar y convierto ese indice en un entero
        int queue = 0;
        
        for(int i=0; i<vector.getColumnDimension(); i++) {
            if(vector.get(0,i)==1) break;
            else queue++;
        }
        
        return queue;
    }

    /**
     * @param firingVector Vector de firing del thread.
     */
    public synchronized void tryFiring(Matrix firingVector) {
        if(pNet.stateEquationTest(firingVector)) {
            System.out.println("Succ ess ful firing");
            //aca se cambia la transition del firingvector del hilo
            exitMonitor();
        } else {
            try {
                conditionQueues.get(getQueue(firingVector)).acquire();
            }
            catch (Exception e) {
                System.out.println("rompió");
            }
        }
    }
}