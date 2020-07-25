/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import java.util.concurrent.Semaphore;

import Jama.Matrix;

public class Monitor {

    // Campos privados.
    private long workingTime;
    private ConditionQueues conditionQueues;
    private Semaphore entry;
    private PetriNet pNet;
    private Politics politics;

    /**
     * Constructor.
     * 
     * Aquí se crea el objeto de tipo Politics que entrará en juego
     * ante cualquier conflicto que se presente en la red y se crean las colas de
     * condición para las transiciones y la cola de entrada del monitor.
     * 
     * @param pNet La red de Petri que será controlada por el monitor.
     */
    public Monitor(PetriNet pNet) {
        this.pNet = pNet;

        entry = new Semaphore(1, true);

        politics = new Politics();

        conditionQueues = new ConditionQueues(pNet.getIncidenceMatrix().getColumnDimension());
    }

    // ----------------------------------------Métodos públicos---------------------------------

    // ----------------------------------------Getters------------------------------------------

    /**
     * @return El tiempo de espera de la transición a disparar.
     */
    public long getWorkingTime() {
        if(workingTime<0) return 0;
        else return workingTime;
    }

    /**
     * @return La red de Petri controlada por el monitor.
     */
    public PetriNet getPetriNet() {
        return pNet;
    }

    /**
     * @return El conjunto de colas correspondientes a las transiciones de la red.
     */
    public ConditionQueues getConditionQueues() {
        return conditionQueues;
    }

    /**
     * @return La cola de entrada del monitor.
     */
    public Semaphore getEntryQueue() {
        return entry;
    }

    // ----------------------------------------Otros--------------------------------------------

    /**
     * En este método se toma el mutex del monitor.
     * 
     * @throws InterruptedException Si el hilo que quiso hacer el acquire fue
     *                              interrumpido en el proceso.
     */
    public synchronized void catchMonitor() throws InterruptedException {
     //   System.out.println(Thread.currentThread().getId() + ": Intento Cachear monitor");
        entry.acquire();
    }

    /**
     * @return Si se ha llegado a la condición de corte del programa.
     */
    public boolean hasCompleted() {
        return pNet.hasCompleted();
    }

    /**
     * @param firingVector Vector de disparo del hilo.
     * @return Si la transición pudo ser disparada o no.
     */
    public boolean tryFiring(Matrix firingVector) {
        try {
            catchMonitor();
  //          System.out.println(Thread.currentThread().getId() + ": Cachie el monitor");
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        if(!pNet.stateEquationTest(firingVector) || pNet.somebodyIsWorkingOn(firingVector)) {
    //        System.out.println(Thread.currentThread().getId() + ": No están dadas las condiciones para disparar, me voy a la cola de T" + getIndex(firingVector));
            
            exitMonitor();
            
            int queue = conditionQueues.getQueue(firingVector);
            
            try {
                conditionQueues.getConditionQueues().get(queue).acquire();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

      //      System.out.println(Thread.currentThread().getId() + ": Me despertaron, voy a disparar T" + getIndex(firingVector));            
        }

     //   System.out.println(Thread.currentThread().getId() + ": T" + getIndex(firingVector) + " esta sensibilizada" );

        if(pNet.hasCompleted()) return false;

        if(alphaTimeCheck(firingVector)) {
            pNet.fireTransition(firingVector);
    //        System.out.println(Thread.currentThread().getId() + ": Se disparo exitosamente T" + getIndex(firingVector));
        } else {
    //        System.out.println(Thread.currentThread().getId() + ": No paso alfa, salgo para trabajar");
            pNet.setWorkingVector(firingVector, (double)Thread.currentThread().getId());
            exitMonitor();
            return false;
        }

        Matrix sensibilized = pNet.getEnabledTransitions();
        Matrix queued = conditionQueues.whoAreQueued();
        Matrix and = and(sensibilized,queued);

        if(enabledAndQueued(and)>0) {
            int choice = politics.decide(and);
            conditionQueues.getConditionQueues().get(choice).release();
        } else {
            exitMonitor();
        }

        return true;
    }

    // ----------------------------------------Métodos privados---------------------------------

    // ----------------------------------------Getters------------------------------------------

    /**
     * @param vector El vector donde se buscará el índice de la transición a disparar.
     * @return El índice de la transición a disparar.
     */
    private int getIndex(Matrix vector) {
        int index = 0;

        for(int i = 0; i < vector.getColumnDimension(); i++) {
            if(vector.get(0, i)==1) break;
            else index++;
        }

        return index;
    }

    // ----------------------------------------Otros------------------------------------------

    /**
     * En este método se libera el mutex del monitor.
     */
    private void exitMonitor() {
        entry.release();
    }

    /**
     * En este método se calcula el resultado de la operación 'AND' entre el vector
     * de transiciones sensibilizadas y el vector de transiciones con hilos
     * encolados.
     * 
     * @return El vector que almacena las transiciones sensibilizadas con al menos
     *         un hilo encolado en ellas.
     */
    private Matrix and(Matrix a, Matrix b) {
        return a.arrayTimes(b);
    }

    /**
     * Este método calcula la cantidad de transiciones sensibilizadas que tienen
     * hilos esperando en sus colas.
     * 
     * @see #getAnd()
     * @param and El vector resultado de la operación 'and'.
     * @return La cantidad de transiciones sensibilizadas en la red con hilos
     *         encolados.
     */
    private int enabledAndQueued(Matrix and) {
        int aux = 0;

        for(int i=0; i<and.getColumnDimension(); i++)
            if(and.get(0,i)>0) aux++;

        return aux;
    }

    /**
     * Este método calcula la resta entre el tiempo de sensibilizado y el tiempo
     * actual, para chequear si transcurrió el tiempo alfa y ya se puede disparar la
     * transición. Además, se setea el tiempo que debe esperar el hilo que quiere
     * disparar una transición cuyo alfa aún no ha transcurrido.
     * 
     * @param firingVector El vector de disparo del hilo, de donde se rescata la
     *                     transición cuyo alfa se va a chequear.
     * @return Si el tiempo alfa ya ha transcurrido o no.
     */
    private boolean alphaTimeCheck(Matrix firingVector) {
        long alpha = (long) pNet.getAlphaVector().get(0, getIndex(firingVector));
        long currentTime = System.currentTimeMillis();
        long enabledAtTime = (long) pNet.getEnabledAtTime().get(0, getIndex(firingVector));

        workingTime = alpha - (currentTime - enabledAtTime);

        return alpha < (currentTime - enabledAtTime);
    }
}