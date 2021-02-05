/**
 * @author  Luna,       Lihué       Leandro
 * @author  Coronati,   Federico    Joaquín
 * @author  Merino,     Mateo
 * @author  Bonino,     Francisco   Ignacio
 * 
 * @since 01/07/2020
 */

import java.util.HashMap;
import java.util.concurrent.Semaphore;

import Jama.Matrix;

public class Monitor {

    //Campos privados.
    private HashMap<Long, Long> workingTime;
    private ConditionQueues conditionQueues;
    private Semaphore entry;
    private PetriNet pNet;
    private Policy policy;

    /**
     * Constructor.
     * 
     * Aquí se crea el objeto de tipo Policy que entrará en juego
     * ante cualquier conflicto que se presente en la red y se crean las colas de
     * condición para las transiciones y la cola de entrada del monitor.
     * 
     * @param   pNet    La red de Petri que será controlada por el monitor.
     */
    public Monitor(PetriNet pNet) {
        this.pNet = pNet;

        entry = new Semaphore(1, true);

        policy = new Policy();

        conditionQueues = new ConditionQueues(pNet.getIncidenceMatrix().getColumnDimension());

        workingTime = new HashMap<Long, Long>();
    }

    //----------------------------------------Métodos públicos---------------------------------

    //----------------------------------------Getters------------------------------------------

    /**
     * Este método retorna el tiempo de espera correspondiente al hilo que lo solicita.
     * 
     * @param   id  El identificador del hilo.
     * 
     * @return  El tiempo de espera para disparar una transición.
     */
    public synchronized long getWorkingTime(Long id) {
        return workingTime.get(id);
    }

    /**
     * @return  El conjunto de colas correspondientes a las transiciones de la red.
     */
    public ConditionQueues getConditionQueues() {
        return conditionQueues;
    }

    /**
     * @return  La cola de entrada del monitor.
     */
    public Semaphore getEntryQueue() {
        return entry;
    }

    //----------------------------------------Setters------------------------------------------

    /**
     * Este método almacena el tiempo de espera correspondiente a cada hilo.
     * 
     * @param   id      Identificador del hilo.
     * @param   time    El tiempo de espera para disparar una transición.
     */
    public synchronized void setWorkingTime(Long id, Long time) {
        workingTime.put(id, time);
    }

    //----------------------------------------Otros--------------------------------------------

    /**
     * En este método se toma el mutex del monitor.
     * 
     * @throws  InterruptedException    Si el hilo que quiso hacer el acquire
     *                                  fue interrumpido en el proceso.
     */
    public void catchMonitor() throws InterruptedException {
        entry.acquire();
    }

    /**
     * En este método se comienza tomando el mutex del monitor. Luego, mientras
     * no se haya llegado a la condición de corte del programa, se chequea si
     * la ecuación de estado da un resultado correcto y si además no hay nadie
     * trabajando en la transición que el hilo que entró quiere disparar. Si se dan las
     * condiciones, se chequea el tiempo 'alfa' de la transición para ver si el
     * hilo debería ir a dormir (simulación de ejecución de una tarea) o no,
     * y luego se dispara la transición (cambio de estado de la red). Si el hilo
     * se debe ir a dormir, entonces se toma el índice i de la transición y se setea
     * en '1' el i-ésimo elemento del vector de trabajo para indicar que ya hay
     * alguien trabajando en esa transición y no debe meterse otro hilo. Luego de esto,
     * el hilo libera el mutex para ir a dormir fuera del monitor.
     * Luego de esperar el tiempo necesario (terminar la tarea) y antes de que el hilo
     * libere el mutex del monitor, se chequea si hay algún hilo esperando en la cola
     * de alguna transición sensibilizada. Si es así, se le pasa el mutex
     * (si hay más de un hilo en estas condiciones se llama al objeto de tipo Policy y
     * se decide de manera aleatoria uniforme). Si no hay nadie en esas condiciones,
     * se libera el mutex del monitor.
     * Cabe aclarar que antes de hacer el disparo de alguna transición se chequea nuevamente
     * si se llegó a la condición de corte del programa, dado que puede darse el caso en el que
     * mientras un hilo estaba durmiendo, otros hilos pueden haber ejecutado otras transiciones
     * que hicieron llegar a la condición de corte del programa.
     * 
     * @param   firingVector    El vector de disparo del hilo.
     * 
     * @return  Si la transición pudo ser disparada o no.
     */
    public boolean tryFiring(Matrix firingVector) {
        try {
            catchMonitor();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        if(!pNet.stateEquationTest(firingVector) || pNet.somebodyIsWorkingOn(firingVector)) {
            exitMonitor();
            
            int queue = conditionQueues.getQueue(firingVector);
            
            try {
                conditionQueues.getSemaphore().get(queue).acquire();
                if(pNet.hasCompleted()) return false; //Si un hilo se despierta en este punto y ya se completaron 1000 tareas, debe salir sin disparar nada.
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(alphaTimeCheck(firingVector)) {
            pNet.fireTransition(firingVector);
        } else {
            pNet.setWorkingVector(firingVector, (double)Thread.currentThread().getId());
            
            exitMonitor();
            
            return false;
        }

        //Chequeo de hilos encolados en transiciones sensibilizadas para despertarlos con la política establecida.
        Matrix sensibilized = pNet.getEnabledTransitions();
        Matrix queued = conditionQueues.whoAreQueued();
        Matrix and = and(sensibilized, queued);

        if(enabledAndQueued(and) > 0) {
            int choice = policy.decide(and);
            conditionQueues.getSemaphore().get(choice).release();
        } else {
            exitMonitor();
        }

        return true;
    }

    //----------------------------------------Métodos privados---------------------------------

    //----------------------------------------Getters------------------------------------------

    /**
     * Este método devuelve el índice donde está el '1' en el
     * vector que recibe como parámetro. Si el vector recibido
     * es un vector de disparo, se devuelve el índice de la
     * transición a disparar.
     * 
     * @param   vector  El vector donde se buscará el índice de la transición a disparar.
     * 
     * @return  El índice de la transición a disparar.
     */
    private int getIndex(Matrix vector) {
        int index = 0;

        for(int i = 0; i < vector.getColumnDimension(); i++) {
            if(vector.get(0, i) == 1) break;
            else index++;
        }

        return index;
    }

    //----------------------------------------Otros------------------------------------------

    /**
     * En este método se libera el mutex del monitor.
     */
    private void exitMonitor() {
        entry.release();
    }

    /**
     * @param   sensibilized    Vector de transiciones sensibilizadas.
     * @param   queued          Vector de transiciones con hilos encolados.
     * 
     * @return  El vector que almacena las transiciones sensibilizadas con al menos
     *          un hilo encolado en ellas.
     */
    private Matrix and(Matrix sensibilized, Matrix queued) {
        return sensibilized.arrayTimes(queued);
    }

    /**
     * @param   and El vector resultado de la operación 'AND'.
     * 
     * @return  La cantidad de transiciones sensibilizadas en la red con hilos
     *          encolados.
     */
    private int enabledAndQueued(Matrix and) {
        int aux = 0;

        for(int i = 0; i < and.getColumnDimension(); i++)
            if(and.get(0, i) > 0) aux++;

        return aux;
    }

    /**
     * Este método calcula la resta entre el tiempo de sensibilizado y el tiempo
     * actual para chequear si transcurrió el tiempo alfa y ya se puede disparar la
     * transición. Además, se setea el tiempo que debe esperar el hilo que quiere
     * disparar una transición cuyo alfa aún no ha transcurrido.
     * 
     * @param   firingVector    El vector de disparo del hilo, de donde se rescata la
     *                          transición cuyo alfa se va a chequear.
     * 
     * @return  Si el tiempo alfa ya ha transcurrido o no.
     */
    private boolean alphaTimeCheck(Matrix firingVector) {
        long alpha = (long)pNet.getAlphaVector().get(0, getIndex(firingVector)); //Tiempo 'alfa' asignado a la transición.
        long currentTime = System.currentTimeMillis(); //Tiempo actual del sistema.
        long enabledAtTime = (long)pNet.getEnabledAtTime().get(0, getIndex(firingVector)); //Momento en el que la transición se sensibilizó.

        if(alpha < (currentTime - enabledAtTime)) return true;
        else {
            setWorkingTime(Thread.currentThread().getId(), alpha - (currentTime - enabledAtTime));
            return false;
        }
    }
}