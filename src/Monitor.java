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
     * @param vector El vector donde se buscará el índice de la transición a disparar.
     * @return El índice de la transición a disparar.
     */
    public int getIndex(Matrix vector) {
        int index = 0;

        for(int i = 0; i < vector.getColumnDimension(); i++) {
            if(vector.get(0, i)==1) break;
            else index++;
        }

        return index;
    }

    /**
     * @return El conjunto de colas correspondientes a las transiciones de la red.
     */
    public ConditionQueues getConditionQueues() {
        return conditionQueues;
    }

    /**
     * @return La red de petri controlada por el monitor.
     */
    public PetriNet getPetriNet() {
        return pNet;
    }

    /**
     * @return La cola de entrada del monitor.
     */
    public Semaphore getEntryQueue() {
        return entry;
    }

    /**
     * @see #and()
     * @param and El vector de transiciones que tiene sólo una transición sensibilizada.
     * @return El índice de la transición sensibilizada.
     */
    public int getSingleEnabled(Matrix and) {
        int index = -1; // Nunca deberia retornar -1, indicaria error

        for(int i = 0; i < and.getColumnDimension(); i++)
            if(and.get(0, i) == 1) {
                index = i;
                break;
            }

        return index;
    }

    /**
     * En este método se calcula el resultado de la operación 'AND' entre el vector
     * de transiciones sensibilizadas y el vector de transiciones con hilos
     * encolados.
     * 
     * @return El vector que almacena las transiciones sensibilizadas con al menos
     *         un hilo encolado en ellas.
     */
    public Matrix and(Matrix a, Matrix b) {
        return a.arrayTimes(b);
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
     * @throws InterruptedException Si el hilo que quiso hacer el acquire fue
     *                              interrumpido en el proceso.
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
     * Este método calcula la cantidad de transiciones sensibilizadas que tienen
     * hilos esperando en sus colas.
     * 
     * @see #getAnd()
     * @param and El vector resultado de la operación 'and'.
     * @return La cantidad de transiciones sensibilizadas en la red con hilos
     *         encolados.
     */
    public int enabledAndQueued(Matrix and) {
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
    public boolean alphaTimeCheck(Matrix firingVector) {
        long alpha = (long) pNet.getAlphaVector().get(0, getIndex(firingVector));
        long currentTime = System.currentTimeMillis();
        long enabledAtTime = (long) pNet.getEnabledAtTime().get(0, getIndex(firingVector));

        workingTime = alpha - (currentTime - enabledAtTime);

        return alpha < (currentTime - enabledAtTime);
    }

    /**
     * @param firingVector Vector de disparo del hilo.
     * @return Si la transición pudo ser disparada o no.
     */
    public boolean tryFiring(Matrix firingVector) {
        try {
     //       System.out.println(Thread.currentThread().getId() + ": Intento Cachear monitor");
            catchMonitor();
    //        System.out.println(Thread.currentThread().getId() + ": Cachie el monitor");
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        if(!pNet.stateEquationTest(firingVector) || pNet.somebodyIsWorkingOn(firingVector)) {
      //      System.out.println(Thread.currentThread().getId() + ": No están dadas las condiciones para disparar, me voy a la cola de T" + getIndex(firingVector));
            
            exitMonitor();
            
            int queue = conditionQueues.getQueue(firingVector);
            
            try {
                conditionQueues.getConditionQueues().get(queue).acquire();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

         //   System.out.println(Thread.currentThread().getId() + ": Me despertaron, voy a disparar T" + getIndex(firingVector));            
        }

      //  System.out.println(Thread.currentThread().getId() + ": T" + getIndex(firingVector) + " esta sensibilizada" );

        if(pNet.hasCompleted()) return false;

        if(alphaTimeCheck(firingVector)) {
            pNet.fireTransition(firingVector);
          //  System.out.println(Thread.currentThread().getId() + ": Se disparo exitosamente T" + getIndex(firingVector));
        } else {
         //   System.out.println(Thread.currentThread().getId() + ": No paso alfa, salgo para trabajar");
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

        /*if(pNet.stateEquationTest(firingVector)) {
            System.out.println(Thread.currentThread().getId() + ": T" + getIndex(firingVector) + " esta sensibilizada" );

            if(alphaTimeCheck(firingVector)) {
                pNet.fireTransition(firingVector);
                System.out.println(Thread.currentThread().getId() + ": Se disparo exitosamente T" + getIndex(firingVector));
            } else {
                System.out.println(Thread.currentThread().getId() + ": No paso alfa, salgo para trabajar");
                exitMonitor();
                return false;
            }
        } else {
            System.out.println(Thread.currentThread().getId() + ": La transicion no esta sensibilizada, me voy a la cola de T" + getIndex(firingVector));
            
            exitMonitor();
            
            int queue = conditionQueues.getQueue(firingVector);
            
            try {
                conditionQueues.getConditionQueues().get(queue).acquire();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println(Thread.currentThread().getId() + ": Me despertaron, voy a disparar T" + getIndex(firingVector));
            
            pNet.fireTransition(firingVector);
            
            System.out.println(Thread.currentThread().getId() + ": Dispare after releaseo T" + getIndex(firingVector));
        }

        Matrix sensibilized = pNet.getEnabledTransitions();
        Matrix queued = conditionQueues.whoAreQueued();
        Matrix and = and(sensibilized,queued);

        if(enabledAndQueued(and)>0) {
            int choice = politics.decide(and);
            conditionQueues.getConditionQueues().get(choice).release();
        } else {
            exitMonitor();
        }*/

        return true;
    }
}