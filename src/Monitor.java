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
    private int transitionsFired;
    private ArrayList<Semaphore> conditionQueues;
    private Semaphore entry;
    private PetriNet pNet;
    private Log myLog; //TODO: Log debe contar cosas y detectar cuando detenerse
    private Politics politics;

    /**
     * Constructor.
     * 
     * @param pNet Red de Petri que será controlada por el monitor.
     */
    public Monitor(PetriNet pNet, Log myLog) {
        this.pNet = pNet;
        this.myLog = myLog;

        this.entry = new Semaphore(1);

        politics = new Politics();
        
        conditionQueues = new ArrayList<Semaphore>();
        
        transitionsFired = 0;
        
        for(int i=0; i<(pNet.getIncidenceMatrix().getColumnDimension()); i++) //Bucle 'for' para inicializar los semáforos en las colas del monitor.
            conditionQueues.add(new Semaphore(0));
    }
    
    //----------------------------------------Métodos públicos---------------------------------

    //----------------------------------------Getters------------------------------------------

    /**
     * @param vector Vector donde se buscará el índice de la transición a disparar.
     */
    public int getQueue(Matrix vector) {
        int queue = 0;
        
        for(int i=0; i<vector.getColumnDimension(); i++) {
            if(vector.get(0,i)==1) break;
            else queue++;
        }
        
        return queue;
    }

    /**
     * @return La cantidad de transiciones que se dispararon hasta el momento.
     */
    public int getTransitionsFired() {
        return transitionsFired; //TODO: Cuando cambiemos al criterio de colores debe ser un vector que lleve la cuenta de cada transicion
    }

    /**
     * @param and Vector de transiciones que tiene sólo una sensibilizada.
     * @return El índice de la transición sensibilizada.
     */
    public int getSingleEnabled(Matrix and) {
        int index = -1; //Nunca deberia retornar -1, indicaria error

        for(int i=0; i<and.getColumnDimension(); i++)
            if(and.get(0,i)==1) {
                index = i;
                break;
            }

        return index;
    }

    //----------------------------------------Otros------------------------------------------

    public synchronized void catchMonitor() throws InterruptedException {
        entry.acquire();
    }

    public synchronized void exitMonitor() {
        entry.release();
    }
    
    /**
     * @param firingVector Vector de firing del thread.
     */
    public synchronized void tryFiring(Matrix firingVector) {
        if(pNet.stateEquationTest(firingVector)) {    //Si la ecuación de estado da un resultado correcto,
            pNet.fireTransition();                    //disparo
            
            System.out.println("Succ eggs fool fire ring"); 

            transitionsFired++;                         //Aumento las transiciones disparadas
            
            pNet.setEnabledTransitions();

            Matrix and = pNet.getEnabledTransitions().arrayTimes(whoAreQueued()); //Operacion logica AND entre vector de transiciones sensibilizadas y vector de colas con hilos en espera para disparar
            
            if(enabledAndWaiting(and)>1) { //Si tengo más de una transición sensibilizada, llamo a Paul Erex.
                conditionQueues.get(politics.decide(and)).release();
                exitMonitor();
            }
            else if (enabledAndWaiting(and)==1) { //Si tengo sólo una, busco su índice.
                conditionQueues.get(getSingleEnabled(and)).release();
                exitMonitor();
            }
            else exitMonitor(); //Si no hay ninguna, me voy y no hago nada.
            //TODO: IMPORTANTEEEEEE hay que ver como trabajamos con el mutex para darle prioridad a los que estan waiting en lugar de permitir nuevos hilos de la cola de entrada.
        } else {
            exitMonitor();

            try {
                conditionQueues.get(getQueue(firingVector)).acquire();
            }
            catch(Exception e) {
                System.out.println("rompió");
            }
        }
    }

    /**
     * Este método devuelve el arreglo de hilos que están esperando
     * en las colas de las transiciones.
     */
    public synchronized Matrix whoAreQueued() {
        double[] aux = new double[pNet.getIncidenceMatrix().getColumnDimension()];
        
        for(Semaphore queue : conditionQueues) {
            if(queue.hasQueuedThreads()) aux[conditionQueues.indexOf(queue)] = 1;    
            else aux[conditionQueues.indexOf(queue)] = 0;
        }
        
        Matrix waitingThreads = new Matrix(aux,1);
        
        return waitingThreads;
    }

    /**
     * @param and Vector resultado de la operación 'and'.
     * @return La cantidad de transiciones sensibilizadas en la red.
     */
    public int enabledAndWaiting(Matrix and) {
        int aux = 0;

        for(int i=0; i<and.getColumnDimension(); i++)
            if(and.get(0,i)==1) aux++;
        
        return aux;
    }
}