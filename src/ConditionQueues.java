/**
 * @author  Luna, Lihué Leandro
 *          Coronati, Federico Joaquín
 *          Merino, Mateo
 *          Bonino, Francisco Ignacio
 * 
 * @since 25/07/2020
 */

import java.util.concurrent.Semaphore;
import java.util.ArrayList;

import Jama.Matrix;

public class ConditionQueues {

    //Campos privados.
    private int quantity;
    private ArrayList<Semaphore> conditionQueues;

    /**
     * Constructor.
     * 
     * @param   quantity    Cantidad de colas a crear.
     */
    public ConditionQueues(int quantity) {
        conditionQueues = new ArrayList<Semaphore>();
        
        this.quantity = quantity;
        
        for(int i=0; i<(quantity); i++)
            conditionQueues.add(new Semaphore(0));
    }

    // ----------------------------------------Métodos públicos---------------------------------

    // ----------------------------------------Getters------------------------------------------

    /**
     * @return  El conjunto de colas de condición de las transiciones de la red.
     */
    public ArrayList<Semaphore> getConditionQueues() {
        return conditionQueues;
    }

    /** 
     * @param   firingVector    El vector de disparo del hilo.
     * 
     * @return  La cola correspondiente a la transición que se quiso disparar.
     */
    public int getQueue(Matrix firingVector) {
        int index = 0;

        for(int i = 0; i < firingVector.getColumnDimension(); i++) {
            if(firingVector.get(0, i) == 1) break;
            else index++;
        }

        return index;
    }

    // ----------------------------------------Otros--------------------------------------------

    /**
     * En este método se calcula un vector que almacena las transiciones
     * que tienen hilos encolados.
     * 
     * @return  El vector de transiciones con hilos encolados.
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
}