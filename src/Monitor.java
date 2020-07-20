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

    //Campos privados.
    private long workingTime;
    private ArrayList<Semaphore> conditionQueues;
    private Semaphore entry;
    private PetriNet pNet;
    private Politics politics;
    
    /**
     * Constructor. Aquí se crea el objeto de tipo Politics que entrará en juego
     * ante cualquier conflicto que se presente en la red y se crean las colas
     * de condición para las transiciones y la cola de entrada del monitor.
     * 
     * @param pNet Red de Petri que será controlada por el monitor.
     */
    public Monitor(PetriNet pNet) {
        this.pNet = pNet;

        entry = new Semaphore(1,true);

        politics = new Politics();
        
        conditionQueues = new ArrayList<Semaphore>();
        
        for(int i=0; i<(pNet.getIncidenceMatrix().getColumnDimension()); i++)
            conditionQueues.add(new Semaphore(0));
    }
    
    // ----------------------------------------Métodos públicos---------------------------------

    // ----------------------------------------Getters------------------------------------------

    /**
     * @param vector El vector donde se buscará el índice de la transición a disparar.
     * @return Índice de la transición a disparar.
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
     * @return Conjunto de colas de condición de las transiciones.
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
     * @param and El vector de transiciones que tiene sólo una transición sensibilizada.
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
     * En este método se calcula el resultado de la operación 'AND'
     * entre el vector de transiciones sensibilizadas y el vector
     * de transiciones con hilos encolados.
     * 
     * @return El vector que almacena las transiciones sensibilizadas
     *         con al menos un hilo encolado en ellas.
     */
    public Matrix getAnd() {
        return pNet.getEnabledTransitions().arrayTimes(whoAreQueued());
    }

    /**
     * @return El tiempo de espera de la transición a disparar.
     */
    public long getWorkingTime() {
        if(workingTime<0) return 0;
        else return workingTime;
    }

    // ----------------------------------------Otros------------------------------------------

    /**
     * En este método se toma el mutex del monitor.
     * 
     * @throws InterruptedException Si el hilo que quiso hacer el acquire
     *                              fue interrumpido en el proceso.
     */
    public synchronized void catchMonitor() throws InterruptedException {
        entry.acquire();
    }

    /**
     * En este método se libera el mutex del monitor.
     */
    public void exitMonitor() {
        entry.release();
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
     * @param and El vector resultado de la operación AND para ver quiénes
     *            están esperando en colas de transiciones sensibilizadas.
     */
    public void waitingCheck(Matrix and) {
        if(enabledAndWaiting(and)>1) {
            int choice = politics.decide(and);
            conditionQueues.get(choice).release();
            return;
        } else if(enabledAndWaiting(and)==1) {
            int singlechoice = getSingleEnabled(and);
            conditionQueues.get(singlechoice).release();
            return;
        } else exitMonitor();
    }

    /**
     * En este método se calcula un vector que almacena las transiciones
     * que tienen hilos encolados.
     * 
     * @return El vector de transiciones con hilos encolados.
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
     * @param and El vector resultado de la operación 'and'.
     * @return La cantidad de transiciones sensibilizadas en la red con hilos encolados.
     */
    public int enabledAndWaiting(Matrix and) {
        int aux = 0;

        for(int i=0; i<and.getColumnDimension(); i++)
            if(and.get(0,i)>0) aux++;
        
        return aux;
    }    

    /**
     * Este método calcula la resta entre el tiempo de sensibilizado
     * y el tiempo actual, para chequear si transcurrió el tiempo alfa
     * y ya se puede disparar la transición.
     * Además, se setea el tiempo que debe esperar el hilo que quiere
     * disparar una transición cuyo alfa aún no ha transcurrido.
     * 
     * @param firingVector El vector de disparo del hilo, de donde se rescata la
     *                     transición cuyo alfa se va a chequear.
     * @return Si el tiempo alfa ya ha transcurrido o no.
     */
    public boolean alphaTimeCheck(Matrix firingVector) {
        long alpha = (long)pNet.getAlphaVector().get(0, getIndexHigh(firingVector));
        long currentTime = System.currentTimeMillis();
        long enabledAtTime = (long)pNet.getEnabledAtTime().get(0, getIndexHigh(firingVector));
        
        workingTime = alpha - (currentTime-enabledAtTime);

        return alpha<(currentTime-enabledAtTime);
    }
}