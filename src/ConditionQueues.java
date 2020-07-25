/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 25/07/2020
 */

import java.util.concurrent.Semaphore;
import java.util.ArrayList;

import Jama.Matrix;

public class ConditionQueues {

    //Campos privados
    private ArrayList<Semaphore> conditionQueues;
    private int quantity;

    /**
     * Constructor.
     * 
     * @param quantity Cantidad de colas a crear.
     */
    public ConditionQueues(int quantity) {
        conditionQueues = new ArrayList<Semaphore>();
        
        this.quantity = quantity;
        
        for(int i=0; i<(quantity); i++)
            conditionQueues.add(new Semaphore(0));
    }

    /**
     * @return Conjunto de colas de condición de las transiciones.
     */
    public ArrayList<Semaphore> getConditionQueues() {
        return conditionQueues;
    }

     /**
     * En este método se calcula un vector que almacena las transiciones
     * que tienen hilos encolados.
     * 
     * @return El vector de transiciones con hilos encolados.
     */
    public Matrix whoAreQueued() {
        double[] aux = new double[this.quantity];
        
        for(Semaphore queue : conditionQueues) {
            if(queue.hasQueuedThreads()) aux[conditionQueues.indexOf(queue)] = 1;    
            else aux[conditionQueues.indexOf(queue)] = 0;
        }
        
        Matrix waitingThreads = new Matrix(aux,1);
        
        return waitingThreads;
    }

    /**
     * @param firingVector El vector de disparo del hilo.
     * @return La cola correspondiente a la transición que se quiso disparar.
     */
    public int getQueue(Matrix firingVector) {
        int index = 0;

        for(int i = 0; i < firingVector.getColumnDimension(); i++) {
            if(firingVector.get(0, i) == 1) break;
            else index++;
        }

        return index;
    }

     /**
     * En este método se chequea si hay hilos encolados en transiciones sensibilizadas.
     * Si hay más de una transición sensibilizada con al menos un hilo encolado en cada una,
     * se llama al objeto de tipo Politics para decidir a quién despertar y cederle el mutex
     * del monitor. Si hay sólo una transición sensibilizada con al menos un hilo encolado en ella,
     * entonces no se decide mediante la política, sino que se le cede el mutex al hilo que se
     * despierte de esa cola. Si no hay ninguna transición sensibilizada con hilos encolados,
     * entonces se cede el mutex del monitor para que los hilos que están en la cola de entrada
     * compitan por él.
     * 
     * @see #getAnd()
     * @param and El vector resultado de la operación AND para ver quiénes
     *            están esperando en colas de transiciones sensibilizadas.
     */
  /*  public void waitingCheck(Matrix and) {
        if(enabledAndWaiting(and)>1) {
            int choice = politics.decide(and);
            conditionQueues.get(choice).release();
            return;
        } else if(enabledAndWaiting(and)==1) {
            int singlechoice = getSingleEnabled(and);
            conditionQueues.get(singlechoice).release();
            return;
        } else exitMonitor();
   
    } */
}