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
    private Politics politics;
    //private Matrix and;
    private long waitingTime;
    
    /**
     * Constructor.
     * 
     * @param pNet Red de Petri que será controlada por el monitor.
     */
    public Monitor(PetriNet pNet) {
        this.pNet = pNet;

        entry = new Semaphore(1,true); //fair?

        politics = new Politics();
        
        conditionQueues = new ArrayList<Semaphore>();
        
        for(int i=0; i<(pNet.getIncidenceMatrix().getColumnDimension()); i++) //Bucle 'for' para inicializar los semáforos en las colas del monitor.
            conditionQueues.add(new Semaphore(0,true)); //fair?
    }
    
    // ----------------------------------------Métodos públicos---------------------------------

    // ----------------------------------------Getters------------------------------------------

    /**
     * @param vector Vector donde se buscará el índice de la transición a disparar.
     */
    public int getIndexHigh(Matrix vector) {
        int index = 0;
        
        for(int i=0; i<vector.getColumnDimension(); i++) {
            if(vector.get(0,i)==1) break;
            else index++;
        }
        
        return index;
    }

    /**
     * @return La red de petri controlada por el monitor.
     */
    public PetriNet getPetriNet() {
        return pNet;
    }

    /**
     * @return Conjunto de colas de condición.
     */
    public ArrayList<Semaphore> getConditionQueues() {
        return conditionQueues;
    }

    /**
     * @return La cola de entrada del monitor.
     */
    public Semaphore getEntryQueue() {
        return entry;
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

    /**
     * @return El vector que indica si hay hilos esperando
     *         en colas de transiciones sensibilizadas.
     */
    public Matrix getAnd() {
        return pNet.getEnabledTransitions().arrayTimes(whoAreQueued());
    }

    /**
     * @return El tiempo de espera de la transición a disparar.
     */
    public long getWaitingTime() {
        if(waitingTime<0) return 0;
        else return waitingTime;
    }

    // ----------------------------------------Otros------------------------------------------

    /**
     * @throws InterruptedException Si el hilo que quiso hacer el acquire
     *                              fue interrumpido en el proceso.
     */
    public synchronized void catchMonitor() throws InterruptedException {
        entry.acquire();
        //System.out.println(Thread.currentThread().getId() + ": Catchié el monitor");
    }

    public void exitMonitor() {
        entry.release();
        //System.out.println(Thread.currentThread().getId() + ": Exitié del monitor");
    }

    /**
     * @param and Vector resultado de la operación AND para ver quiénes
     *            están esperando en colas de transiciones sensibilizadas.
     */
    public void waitingCheck(Matrix and) {
        //and.print(0, 0);
        if(enabledAndWaiting(and)>1) { //Si tengo más de una transición sensibilizada con hilos encolados, llamo a Paul Erex.
            //System.out.println(Thread.currentThread().getId() + ": Hay al menos un hilo esperando en varias enableds transitions.");
            //System.out.println(Thread.currentThread().getId() + "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            int choice = politics.decide(and);
            conditionQueues.get(choice).release();
            //System.out.println(Thread.currentThread().getId() + ": Hago return. Cedo el mutex al de la transicion: " + choice);
            return; //Salimos del metodo y volvemos al run()
        } else if(enabledAndWaiting(and)==1) { //Si tengo sólo una, busco su índice.
            //System.out.println(Thread.currentThread().getId() + ": Hay al menos un hilo esperando en una enabled transition");
            int singlechoice = getSingleEnabled(and);
            conditionQueues.get(singlechoice).release();
            //System.out.println(Thread.currentThread().getId() + ": Hago return. Cedo el mutex a la single choice: " + singlechoice);
            return; //Salimos del metodo y volvemos al run()
        } else {
            //System.out.println(Thread.currentThread().getId() + ": Nobody's waiting on enabled transitions");
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

    /**
     * Este método calcula W_i - tiempo actual, para chequear
     * si pasó el tiempo alfa y se puede disparar la transición.
     * 
     * @param firingVector Vector de disparo del hilo, de donde se rescata la
     *                     transición cuyo alfa se va a chequear.
     * @return Si el tiempo alfa ya ha transcurrido o no.
     */
    public boolean alphaTimeCheck(Matrix firingVector) {
        long alpha = (long)pNet.getAlphaVector().get(0, getIndexHigh(firingVector)); //Se lee el vector de alphaTimes y se busca el alpha correspondiente a la posicion que indica el firingVector
        long currentTime = System.currentTimeMillis();
        long enabledAtTime = (long)pNet.getEnabledAtTime().get(0, getIndexHigh(firingVector));

        //System.out.println("alpha = " + alpha + "\ncurrentTime = " + currentTime + "\nenabledAtTime = " + enabledAtTime);
        
        waitingTime = alpha - (currentTime-enabledAtTime);

        //System.out.println("resta = " + (currentTime-enabledAtTime) + "\nwaitingTime = " + waitingTime);

        return alpha<(currentTime-enabledAtTime); //Se hace la resta entre el tiempo actual y el tiempo en que se sensibilizó (wi), y si ese tiempo es mayor que alpha entonces se retorna true
    }
}