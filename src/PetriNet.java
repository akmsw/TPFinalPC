/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import Jama.Matrix;

public class PetriNet {

    //Campos privados
    private Matrix incidence, incidenceBackwards, initialMarking, currentMarking, enabledTransitions, placesInvariants, transitionInvariants, aux, enabledAtTime, alphaTimes, workingVector, firedTransitions;
    private double[] auxVector = {};
    private int lastFiredTransition, totalFired;
    private int stopCondition;
    private Object lock;

    /**
	 * Constructor.
	 * 
     * @param incidence La matriz de incidencia de la red.
     * @param incidenceBackwards Matriz 'backwards' de incidencia de la red.
     * @param initialMarking El vector de marcado inicial de la red.
     * @param placesInvariants Los invariantes de plaza de la red.
     * @param alphaTimes Los tiempos 'alfa' asociados a cada transición.
     * @param stopCondition Condición de corte del programa (cuántas tareas
     *                      se deben finalizar para terminar el programa).
     * @param lock Lock para sincronizar la escritura en el Log con el disparo de transiciones
     */
    public PetriNet(Matrix incidence, Matrix incidenceBackwards, Matrix initialMarking, Matrix placesInvariants, Matrix transitionInvariants, Matrix alphaTimes, int stopCondition, Object lock) {
        this.incidence = incidence;
        this.incidenceBackwards = incidenceBackwards;
        this.initialMarking = initialMarking;
        this.placesInvariants = placesInvariants;
        this.transitionInvariants = transitionInvariants;
        this.alphaTimes = alphaTimes;
        this.stopCondition = stopCondition;
        this.lock = lock;

        this.firedTransitions = new Matrix(1, incidence.getColumnDimension());

        this.enabledTransitions = new Matrix(1, incidence.getColumnDimension()); //Inicializo el vector de transiciones sensibilizadas con todos 0, del tamaño del vector de marcado, con 1 sola fila. FJC
        
        this.aux = new Matrix(auxVector,1);

        this.enabledAtTime = new Matrix(1, incidence.getColumnDimension()); //Vector que almacena los W_i

        this.workingVector = new Matrix(1, incidence.getColumnDimension()); //Vector que indica cuales transiciones están siendo ejecutadas en este momento

        setCurrentMarkingVector(this.initialMarking); //Inicializamos el vector de marcado actual igual al vector de marcado inicial
        
        //setEnabledTransitions();
    }

    // ----------------------------------------Métodos públicos---------------------------------

    // ----------------------------------------Getters------------------------------------------

    /**
     * @return La matriz de incidencia de la red de Petri.
     */
    public Matrix getIncidenceMatrix() {
        return incidence;
    }

    /**
     * @return El vector de marcado inicial de la red de Petri.
     */
    public Matrix getInitialMarkingVector() {
        return initialMarking;
    }

    /**
     * @return El vector de marcado actual de la red de Petri.
     */
    public Matrix getCurrentMarkingVector() {
        return currentMarking;
    }

    /**
     * @return Vector de transiciones sensibilizadas.
     */
    public Matrix getEnabledTransitions() {
        return enabledTransitions;
    }

    /**
     * @return La cantidad de transiciones que se dispararon hasta el momento.
     */
    public Matrix getTransitionsFired() {
        return firedTransitions; //TODO: Cuando cambiemos al criterio de colores debe ser un vector que lleve la cuenta de cada transicion
    }

    /**
     * @return El vector de tiempos alfa asociados a las transiciones.
     */
    public Matrix getAlphaVector() {
        return alphaTimes;
    }

    /**
     * @return El vector de los W_i.
     */
    public Matrix getEnabledAtTime() {
        return enabledAtTime;
    }

    /**
     * @return Vector de transiciones que tienen al menos
     *         un hilo trabajando en ellas.
     */
    public Matrix getWorkingVector() {
        return workingVector;
    }

    /**
     * @return La carga de las memorias.
     */
    public String getMemoriesLoad() {
        return "Cantidad de escrituras en memoria 1: " + (firedTransitions.get(0,9) + firedTransitions.get(0,11)) + "\nCantidad de escrituras en memoria 2: " + (firedTransitions.get(0,10) + firedTransitions.get(0,12));
    }
    
    /**
     * @return La carga de los procesadores (AsignarP1 y AsignarP2).
     */
    public String getProcessorsLoad() {
        return "Carga del procesador 1: " + firedTransitions.get(0,1) + "\nCarga del procesador 2: " + firedTransitions.get(0,2);
    }

    /**
     * @return La cantidad de tareas ejecutadas en cada procesador individualmente (FinalizarTXPX).
     */
    public String getProcessorsTasks() {
        return "Cantidad de ejecuciones de T1 en procesador 1: " + firedTransitions.get(0,5) + "\nCantidad de ejecuciones de T2 en procesador 1: " + firedTransitions.get(0,7) +
               "\nCantidad de ejecuciones de T1 en procesador 2: " + firedTransitions.get(0,6) + "\nCantidad de ejecuciones de T2 en procesador 2: " + firedTransitions.get(0,8);
    }

    /**
     * @param vector Vector donde se buscará el índice de la transición a disparar.
     */
    public int getIndex(Matrix vector) {
        int index = 0;
        
        for(int i=0; i<vector.getColumnDimension(); i++) {
            if(vector.get(0,i)==1) break;
            else index++;
        }
        
        return index;
    }

    /**
     * @return El objeto que hará las veces de lock para el log.
     */
  //  public Object getLogNotifier() {
       // return logNotifier;
  //  }

    /**
     * @return El índice de la última transición disparada.
     */
    public int getLastFiredTransition() {
        return lastFiredTransition;
    }

    /**
     * @return La cantidad total de transiciones disparadas hasta el momento.
     */
    public int getTotalFired() {
        return totalFired;
    }

    // ----------------------------------------Setters------------------------------------------

    /**
     * @param currentMarking Vector de marcado actual de la red de Petri.
     */
    public void setCurrentMarkingVector(Matrix currentMarking) {
        this.currentMarking = currentMarking;
    }

    /**
     * @param index Índice de la transición que está sensibilizada.
     * @param time Instante de tiempo en el que se sensibilizó la transición.
     */
    public void setEnabledAtTime(int index, long time) {
        enabledAtTime.set(0, index, (double)time);
    }

    /**
     * Este método recorre la matriz de incidencia 'backwards' chequeando si
     * la columna (transición) está sensibilizada (el peso de cada arco es menor
     * o igual a la cantidad de tokens de la plaza). Seteamos en '1' la transición
     * sensibilizada en el vector 'enabledTransitions'.
     */
    public void setEnabledTransitions() {
        boolean currentTransitionEnabled;        
        long currentTime = System.currentTimeMillis(); //Establezco el tiempo una sola vez para denotar que todas las transiciones se sensibilizaron "al mismo tiempo"

        for(int j=0; j<incidenceBackwards.getColumnDimension(); j++) { //Itero columnas es decir Transiciones
            currentTransitionEnabled = true;
            
            for(int i=0; i<incidenceBackwards.getRowDimension(); i++) //Itero filas es decir Plazas
                if(incidenceBackwards.get(i,j)>currentMarking.get(0,i)) { //Si el peso del arco es mayor a la cantidad de tokens en la plaza que conecta a esa transicion j
                    currentTransitionEnabled = false; //currentMarking.get(i,0) antes estaba en (0,i) pero lo cambiamos cuando transpusimos el currentMarking
                    break;
                }
            
            //CAMBIO: argumento del if. Doble chequeo de si alguien está trabajando en esa transición (acá y en el run de myThread).
            if(currentTransitionEnabled) { //&& getWorkingVector().get(0,j)!=1) { //Si la transición está sensibilizada y no hay nadie trabajando en ella...
                enabledTransitions.set(0,j,1); //Escribo un 1 en la posicion j del arreglo enabledTransicions. FJC
                setEnabledAtTime(j,currentTime); //Establezco el tiempo en que se sensibilizaron las transiciones subsiguientes
            } else enabledTransitions.set(0,j,0); //Si la transicion se detectó como no sensibilizada, escribo un 0 en la posicion j del arreglo enabledTransicions. FJC
        }
    }

    // ----------------------------------------Otros--------------------------------------------

    /**
     * Este método testea si es posible realizar el disparo de la transición
     * con el vector de firing del hilo.
     * 
     * @param firingVector El vector de firing actual del vector.
     * @return Si el resultado de la ecuación de estado fue correcto y
     *         se pudo asignar el nuevo vector de estado de la red.
     */
    public boolean stateEquationTest(Matrix firingVector) {
        aux = stateEquation(firingVector);

        //System.out.println(Thread.currentThread().getId() + ": STATE EQUATION TEST. Marcado actual:");        

        //aux.print(0, 0); //BORRAR ESTE PRINT
        
        for(int i=0; i<this.aux.getColumnDimension(); i++) //Si alguno de los índices es menor que cero,
            if(this.aux.get(0,i)<0) {
                //System.out.println(Thread.currentThread().getId() + ": ROMPIMO");
                return false;
            } //la ecuación de estado fue errónea (no se pudo disparar) así que devolvemos 'false'.
        
        //System.out.println(Thread.currentThread().getId() + ": La transición está sensibilizada. Hay que chequear el alfa.");
        
        return true;
    }
    
    /**
     * Si todo salió bien en el stateEquationTest,
     * cambiamos el vector de marcado.
     * 
     * @param firingVector Vector de disparo del hilo.
     */
    public void fireTransition(Matrix firingVector) {
        setCurrentMarkingVector(stateEquation(firingVector));
        
       // System.out.println(Thread.currentThread().getId() + ": Se disparó la transicion: fV: ");
        
        //firingVector.print(0,0);
        
        //System.out.println(Thread.currentThread().getId() + ": Exito al disparar transicion." + getIndex(firingVector) );
        
        firedTransitions = firedTransitions.plus(firingVector); //Aumento las transiciones disparadas.

        lastFiredTransition = getIndex(firingVector);

        getWorkingVector().set(0, getIndex(firingVector), 0);

        synchronized(lock) {
            lock.notify();
            try {
                System.out.println(Thread.currentThread().getId() + ": YO HILO ESTOY ESPERANDO");
                lock.wait();
            } catch(InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        setEnabledTransitions();

        //getLogNotifier().notify();

        //notify();

        totalFired++;

      //  System.out.println("Cantidad de transiciones disparadas hasta el momento: " + totalFired +
      //                    "\nCantidad de tareas completadas hasta el momento: " + (firedTransitions.get(0, 5) + firedTransitions.get(0, 6) + firedTransitions.get(0, 7) + firedTransitions.get(0, 8)));
    }

    /**
     * Este método calcula la ecuación de estado.
     * 
     * @param firingVector Vector de disparo del hilo.
     * @return El resultado de la ecuación de estado.
     */
    public Matrix stateEquation(Matrix firingVector) {
        return (currentMarking.transpose().plus(incidence.times(firingVector.transpose()))).transpose(); //Ecuación de estado.
    }

    /**
     * @return Si la condición de corte del programa se ha concretado.
     */
    public boolean hasCompleted() {
        double aux = 0;
        aux += firedTransitions.get(0, 5) + firedTransitions.get(0, 6) + firedTransitions.get(0, 7) + firedTransitions.get(0, 8);
        
        return aux>=stopCondition;
    }

    /**
     * @return Si los invariantes de plaza se mantienen luego de cada disparo de cada transición.
     */
    public void checkPlacesInvariants() {
        boolean check = true;
        int invariantAmount; //La cantidad de tokens que se mantiene invariante
        int tokensAmount; //La cantidad de tokens que voy contando en las plazas

        //Validacion de tamaños
        if(this.placesInvariants.getColumnDimension() != this.currentMarking.getColumnDimension()) {
            //System.out.println("Error fatal: El tamaño del vector de marcado no coindice con el tamaño de los invariantes de plazas.");
            return;
        }
        
        for(int j=0; j<this.placesInvariants.getRowDimension(); j++) {//Itero en los invariantes de plazas
            invariantAmount = 0;
            tokensAmount = 0;

            for(int i=0; i<this.currentMarking.getColumnDimension(); i++) //Itero en plazas del marcado actual
                if(this.placesInvariants.get(j, i) > 0) { //Si el elemento del invariante es mayor a cero debo considerarlo
                    invariantAmount = (int)this.placesInvariants.get(j, i);
                    tokensAmount = tokensAmount + (int)this.currentMarking.get(j, i);
                }

            if(tokensAmount != invariantAmount) { //Si la cantidad de tokens acumulados es distinta de los que indica el invariante
                //System.out.println("Error en Invariante de Plaza: IP" + j);
                check = false;
            }
        }
        if(check) System.out.println("Los invariantes de plaza se respetan.");
    }
}