/**
 * @author  Luna,       Lihué       Leandro
 * @author  Coronati,   Federico    Joaquín
 * @author  Merino,     Mateo
 * @author  Bonino,     Francisco   Ignacio
 * 
 * @since 01/07/2020
 */

import Jama.Matrix;

public class PetriNet {

    //Campos privados.
    private Object lock;
    private int lastFiredTransition, totalFired, stopCondition, stepToLog;
    private double[] auxVector = {};
    private Matrix incidence, incidenceBackwards; //Matrices a utilizar.
    private Matrix initialMarking, currentMarking; //Vectores relativos al marcado de la red.
    private Matrix enabledTransitions, enabledAtTime; //Vectores relativos a la sensibilización de transiciones.
    private Matrix placesInvariants; //Vectores relativos a los invariantes de la red.
    private Matrix aux; //Vector auxiliar para el cálculo de la ecuación de estado de la red.
    private Matrix alphaTimes; //Vector con los alfas de cada transición.
    private Matrix workingVector; //Vector que indica los hilos que están trabajando en las transiciones.
    private Matrix firedTransitions; //Vectores que almacena el número de veces que se disparó cada transición.

    /**
	 * Constructor.
	 * 
     * @param   incidence           La matriz de incidencia de la red.
     * @param   incidenceBackwards  La matriz 'backwards' de incidencia de la red.
     * @param   initialMarking      El vector de marcado inicial de la red.
     * @param   placesInvariants    Los invariantes de plaza de la red.
     * @param   alphaTimes          Los tiempos 'alfa' asociados a cada transición.
     * @param   stopCondition       La condición de corte del programa (cuántas tareas se deben finalizar para terminar el programa).
     * @param   lock                El lock para sincronizar la escritura en el Log con el disparo de transiciones
     * @param   stepToLog           Cada cuántas transiciones disparadas se debe dar permiso al hilo Log para tomar registro de las estadísticas de la red.
     */
    public PetriNet(Matrix incidence, Matrix incidenceBackwards, Matrix initialMarking, Matrix placesInvariants, Matrix alphaTimes, int stopCondition, Object lock, int stepToLog) {
        this.incidence = incidence;
        this.incidenceBackwards = incidenceBackwards;
        this.initialMarking = initialMarking;
        this.placesInvariants = placesInvariants;
        this.alphaTimes = alphaTimes;
        this.stopCondition = stopCondition;
        this.lock = lock;
        this.stepToLog = stepToLog;

        firedTransitions = new Matrix(1, incidence.getColumnDimension());

        enabledTransitions = new Matrix(1, incidence.getColumnDimension());
        
        aux = new Matrix(auxVector, 1);

        enabledAtTime = new Matrix(1, incidence.getColumnDimension()); //Vector que almacena los instantes de sensiblizado de cada transición.

        workingVector = new Matrix(1, incidence.getColumnDimension());

        setCurrentMarkingVector(initialMarking);
    }

    // ----------------------------------------Métodos públicos---------------------------------

    // ----------------------------------------Getters------------------------------------------

    /**
     * @return  La matriz de incidencia de la red de Petri.
     */
    public Matrix getIncidenceMatrix() {
        return incidence;
    }

    /**
     * @return  El vector de marcado inicial de la red de Petri.
     */
    public Matrix getInitialMarkingVector() {
        return initialMarking;
    }

    /**
     * @return  El vector de marcado actual de la red de Petri.
     */
    public Matrix getCurrentMarkingVector() {
        return currentMarking;
    }

    /**
     * @return  El vector de transiciones sensibilizadas.
     */
    public Matrix getEnabledTransitions() {
        return enabledTransitions;
    }

    /**
     * @return  El vector con la cantidad de veces que fue disparada cada transición.
     */
    public Matrix getTransitionsFired() {
        return firedTransitions;
    }

    /**
     * @return  El vector de tiempos alfa asociados a las transiciones.
     */
    public Matrix getAlphaVector() {
        return alphaTimes;
    }

    /**
     * @return  El vector de los tiempos de sensibilizado de cada transición.
     */
    public Matrix getEnabledAtTime() {
        return enabledAtTime;
    }

    /**
     * @return  El vector que indica si las transiciones tienen un hilo trabajando en ellas.
     */
    public Matrix getWorkingVector() {
        return workingVector;
    }

    /**
     * @return  La carga de las memorias.
     */
    public String getMemoriesLoad() {
        return "Cantidad de escrituras en memoria 1: " + (firedTransitions.get(0, 9) + firedTransitions.get(0, 11)) + "\nCantidad de escrituras en memoria 2: " + (firedTransitions.get(0, 10) + firedTransitions.get(0, 12));
    }
    
    /**
     * @return  La carga de los procesadores (AsignarP1 y AsignarP2).
     */
    public String getProcessorsLoad() {
        return "Carga del procesador 1: " + firedTransitions.get(0, 1) + "\nCarga del procesador 2: " + firedTransitions.get(0, 2);
    }

    /**
     * @return  La cantidad de tareas ejecutadas en cada procesador individualmente (FinalizarTXPX).
     */
    public String getProcessorsTasks() {
        return "Cantidad de ejecuciones de T1 en procesador 1: " + firedTransitions.get(0, 5) + "\nCantidad de ejecuciones de T2 en procesador 1: " + firedTransitions.get(0, 7) +
               "\nCantidad de ejecuciones de T1 en procesador 2: " + firedTransitions.get(0, 6) + "\nCantidad de ejecuciones de T2 en procesador 2: " + firedTransitions.get(0, 8);
    }

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
    public int getIndex(Matrix vector) {
        int index = 0;
        
        for(int i = 0; i < vector.getColumnDimension(); i++) {
            if(vector.get(0, i) == 1) break;
            else index++;
        }
        
        return index;
    }

    /**
     * @return  El índice de la última transición disparada.
     */
    public int getLastFiredTransition() {
        return lastFiredTransition;
    }

    /**
     * @return  La cantidad total de transiciones disparadas hasta el momento.
     */
    public int getTotalFired() {
        return totalFired;
    }

    /**
     * @return  La condición de corte del programa (cuántas tareas
     *          deben completarse para finalizar la ejecución).
     */
    public int getStopCondition() {
        return stopCondition;
    }

    // ----------------------------------------Setters------------------------------------------

    /**
     * Este método cambia el vector de marcado actual de la red de Petri.
     * 
     * @param   currentMarking  El nuevo vector de marcado de la red de Petri.
     */
    public void setCurrentMarkingVector(Matrix currentMarking) {
        this.currentMarking = currentMarking;
    }

    /**
     * Este método setea el instante de tiempo en el que se
     * sensibiliza la transición cuyo índice es el recibido por parámetro.
     * 
     * @param   index   El índice de la transición que está sensibilizada.
     * @param   time    El instante de tiempo en el que se sensibilizó la transición.
     */
    public void setEnabledAtTime(int index, long time) {
        enabledAtTime.set(0, index, (double)time);
    }

    /**
     * En este método se setea si hay alguien trabajando (y su ID)
     * en la posición correspondiente a la transición en la que el hilo
     * está trabajando para evitar que hayan dos o más hilos trabajando
     * en una misma transición.
     * 
     * @param   firingVector    El vecto de disparo del hilo.
     * @param   value           El valor a almacenar en dicha posición.
     */
    public void setWorkingVector(Matrix firingVector, double value) {
        this.workingVector.set(0, getIndex(firingVector), value); //Se setea un "1" que indica que se está trabajando (tiempo alfa) en esa transicion
    }

    /**
     * Este método recorre la matriz de incidencia 'backwards' chequeando si
     * la columna (transición) está sensibilizada (el peso de cada arco es menor
     * o igual a la cantidad de tokens de la plaza). Seteamos un '1' en el índice
     * de la transición en el vector 'enabledTransitions' si la misma está sensibilizada;
     * si no lo está, se setea un '0' en dicha posición.
     */
    public void setEnabledTransitions() {
        boolean currentTransitionEnabled;

        long currentTime = System.currentTimeMillis(); //Establezco el tiempo una sola vez para denotar que todas las transiciones se sensibilizaron "al mismo tiempo".

        for(int j = 0; j < incidenceBackwards.getColumnDimension(); j++) {
            currentTransitionEnabled = true;
            
            for(int i = 0; i < incidenceBackwards.getRowDimension(); i++)
                if(incidenceBackwards.get(i, j) > currentMarking.get(0, i)) {
                    currentTransitionEnabled = false;
                    break;
                }
            
            if(currentTransitionEnabled) {
                enabledTransitions.set(0, j, 1);
                setEnabledAtTime(j, currentTime);
            } else enabledTransitions.set(0, j, 0);
        }
    }

    // ----------------------------------------Otros--------------------------------------------

    /**
     * Este método chequea si hay alguien trabajando en la transición que
     * el hilo quiere disparar.
     * 
     * @param   firingVector    El vector de firing actual del hilo.
     * 
     * @return  Si hay algún hilo trabajando su tiempo alfa en una transición.
     */
    public boolean somebodyIsWorkingOn(Matrix firingVector) {
        int index = getIndex(firingVector);

        if(workingVector.get(0, index) == 0) return false;
        else if(workingVector.get(0, index) != Thread.currentThread().getId()) return true;
        else return false;
    }

    /**
     * Este método testea si es posible realizar el disparo de la transición
     * con el vector de firing del hilo.
     * 
     * @param   firingVector    El vector de firing actual del hilo.
     * 
     * @return  Si el resultado de la ecuación de estado fue correcto y
     *          se pudo asignar el nuevo vector de estado de la red.
     */
    public boolean stateEquationTest(Matrix firingVector) {
        aux = stateEquation(firingVector);
        
        for(int i = 0; i < this.aux.getColumnDimension(); i++)
            if(this.aux.get(0, i) < 0) return false;
        
        return true;
    }
    
    /**
     * Este método actualiza el vector de marcado de la red, aumenta en 1
     * la cantidad de veces que fue disparada la transición indicada en
     * el vector de disparo del hilo, actualiza el valor de la última transición
     * disparada, avisa al hilo Log para tomar nota del disparo y registrarlo, y
     * finalmente incrementa la cantidad total de transiciones disparadas hasta
     * el momento.
     *  
     * @param   firingVector    El vector de disparo del hilo.
     */
    public void fireTransition(Matrix firingVector) {
        setCurrentMarkingVector(stateEquation(firingVector));
        
        firedTransitions = firedTransitions.plus(firingVector); //Aumento las transiciones disparadas.

        lastFiredTransition = getIndex(firingVector);

        setWorkingVector(firingVector, 0);

        checkPlacesInvariants();
        
        setEnabledTransitions();

        totalFired++;

        if(getTotalFired() % stepToLog == 0) {
            synchronized(lock) {
                lock.notify();

                try {
                    lock.wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("\nError esperando en fireTransition.\n");
                }
            }
        }
        else if(hasCompleted()) {
            synchronized(lock) {
                lock.notify();
            }
        }
    }

    /**
     * Este método calcula la ecuación de estado.
     * 
     * @param   firingVector    El vector de disparo del hilo.
     * 
     * @return  El resultado de la ecuación de estado.
     */
    public Matrix stateEquation(Matrix firingVector) {
        return (currentMarking.transpose().plus(incidence.times(firingVector.transpose()))).transpose();
    }

    /**
     * @return  Si la condición de corte del programa se ha alcanzado.
     */
    public boolean hasCompleted() {
        double aux = 0;

        aux += firedTransitions.get(0, 5) + firedTransitions.get(0, 6) + firedTransitions.get(0, 7) + firedTransitions.get(0, 8);
        
        return aux >= stopCondition;
    }

    /**
     * En este método se chequea si se respetan los invariantes de plaza de la red.
     * Para hacerlo, se itera en la matriz de invariantes de plaza comparándola con
     * las plazas del marcado actual, verificando si la cantidad de tokens encontrados
     * se corresponde con la cantidad de tokens que debería tener el invariante de plaza.
     */
    public void checkPlacesInvariants() {
        int invariantAmount; //La cantidad de tokens que se mantiene invariante.
        int tokensAmount; //La cantidad de tokens que se van contando en las plazas.

        //Validacion de tamaños
        if(placesInvariants.getColumnDimension() != currentMarking.getColumnDimension()) {
            //System.out.println("Error. Dimensiones no coincidentes para validación de invariantes de plaza.");
            return;
        }
        
        for(int j = 0; j < placesInvariants.getRowDimension(); j++) {
            invariantAmount = 0;
            tokensAmount = 0;

            for(int i = 0; i < currentMarking.getColumnDimension(); i++)
                if(placesInvariants.get(j, i) > 0) {
                    invariantAmount = (int)placesInvariants.get(j, i);
                    tokensAmount = tokensAmount + (int)currentMarking.get(0, i);
                }

            if(tokensAmount != invariantAmount) 
                System.out.println("Error en el invariante de plaza: IP" + j);
        }
    }
}