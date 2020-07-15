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
    private Log myLog; //TODO: Log debe contar cosas y detectar cuando detenerse
    private Politics politics;
    private Matrix and;

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
     * @return La red de petri controlada por el monitor.
     */
    public PetriNet getPetriNet() {
        return this.pNet;
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

    public void catchMonitor() throws InterruptedException {
        entry.acquire();
        System.out.println(Thread.currentThread().getId() + ": Catchié el monitor");
    }

    public void exitMonitor() {
        entry.release();
        System.out.println(Thread.currentThread().getId() + ": Exitié del monitor");
    }
    
    //TODO: AGREGAR SYNCRONIZED
    /**
     * @param firingVector Vector de firing del thread.
     */
    public void tryFiring(Matrix firingVector) {
        if(pNet.stateEquationTest(firingVector)) {    //Si la ecuación de estado da un resultado correcto, disparo
            pNet.fireTransition(firingVector);

            and = pNet.getEnabledTransitions().arrayTimes(whoAreQueued()); //Operacion logica AND entre vector de transiciones sensibilizadas y vector de colas con hilos en espera para disparar
            
            System.out.println(Thread.currentThread().getId() + ": Llamando a waitingCheck sin haber waiteado antes.");
            waitingCheck(and);
            //TODO: IMPORTANTEEEEEE hay que ver como trabajamos con el mutex para darle prioridad a los que estan waiting en lugar de permitir nuevos hilos de la cola de entrada.
        } else {
            System.out.println(Thread.currentThread().getId() + ": No pude disparar");            
            exitMonitor();

            try {
                System.out.println(Thread.currentThread().getId() + ": Me voy a encolar en la cola de condicion de la transicion: T" + getQueue(firingVector));
                conditionQueues.get(getQueue(firingVector)).acquire(); //Cuando se despierta se continua a partir de aca
                System.out.println(Thread.currentThread().getId() +  ": Me desperté, voy a triggerear la transicion: " + getQueue(firingVector));
                pNet.fireTransition(firingVector);
                and = pNet.getEnabledTransitions().arrayTimes(whoAreQueued());                
                System.out.println(Thread.currentThread().getId() + ": Llamando a waitingCheck despues de despertar.");
                waitingCheck(and);
            } catch(Exception e) {
                System.out.println(Thread.currentThread().getId() + "Error al encolar un hilo.");
            }
        }
    }

    /**
     * @param and Vector resultado de la operación AND para ver quiénes
     *            están esperando en colas de transiciones sensibilizadas.
     */
    public void waitingCheck(Matrix and) {
        and.print(0, 0);
        if(enabledAndWaiting(and)>1) { //Si tengo más de una transición sensibilizada, llamo a Paul Erex.
            System.out.println(Thread.currentThread().getId() + ": Hay más de un hilo esperando en enableds transitions.");
            int choice = politics.decide(and);
            conditionQueues.get(choice).release();                
            System.out.println(Thread.currentThread().getId() + ": Hago return. Cedo el mutex al de la transicion: " + choice);
            return; //Salimos del metodo y volvemos al run()
        }
        else if (enabledAndWaiting(and)==1) { //Si tengo sólo una, busco su índice.
            System.out.println(Thread.currentThread().getId() + ": Hay sólo un hilo esperando en una enabled transition");
            int singlechoice = getSingleEnabled(and);
            conditionQueues.get(singlechoice).release();
            System.out.println(Thread.currentThread().getId() + ": hace return. Cedo el mutex a la single choice: " + singlechoice);
            return; //Salimos del metodo y volvemos al run()
        }
        else {
            System.out.println(Thread.currentThread().getId() + ": Nobody's waiting");
            exitMonitor(); //Si no hay ningun hilo esperando en colas de transiciones actualmente sensibilizadas, me voy y no hago nada.
        }
    }

    /**
     * @return El arreglo de hilos que están esperando en las colas de las transiciones.
     */
    public Matrix whoAreQueued() {
        double[] aux = new double[pNet.getIncidenceMatrix().getColumnDimension()];
        
        for(Semaphore queue : conditionQueues) {
            if(queue.hasQueuedThreads()) aux[conditionQueues.indexOf(queue)] = 1;    
            else aux[conditionQueues.indexOf(queue)] = 0;
        }
        
        Matrix waitingThreads = new Matrix(aux,1);
        
        return waitingThreads;
    }

    /**
     * Este método calcula la cantidad de transiciones sensibilizadas
     * que tienen hilos esperando en sus colas.
     * 
     * @param and Vector resultado de la operación 'and'.
     * @return La cantidad de transiciones sensibilizadas en la red.
     */
    public int enabledAndWaiting(Matrix and) {
        int aux = 0;

        for(int i=0; i<and.getColumnDimension(); i++)
            if(and.get(0,i)>0) aux++;
        
        return aux;
    }
}