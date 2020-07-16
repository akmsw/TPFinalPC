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
    private Matrix incidence, incidenceBackwards, initialMarking, currentMarking, enabledTransitions, placesInvariants, aux, enabledAtTime, alphaTimes, workingVector;
    private double[] auxVector = {};
    private int transitionsFired;

    /**
	 * Constructor.
	 * 
     * @param incidence La matriz de incidencia de la red.
     * @param incidenceBackwards Matriz 'backwards' de incidencia de la red.
     * @param initialMarking El vector de marcado inicial de la red.
     * @param placesInvariants Los invariantes de plaza de la red.
     * @param alphaTimes Los tiempos 'alfa' asociados a cada transición.
     */
    public PetriNet(Matrix incidence, Matrix incidenceBackwards, Matrix initialMarking, Matrix placesInvariants, Matrix alphaTimes) {
        this.incidence = incidence;
        this.incidenceBackwards = incidenceBackwards;
        this.initialMarking = initialMarking;
        this.placesInvariants = placesInvariants;
        this.alphaTimes = alphaTimes;

        this.transitionsFired = 0;

        this.enabledTransitions = new Matrix(1, incidence.getColumnDimension()); //Inicializo el vector E de transiciones sensibilizadas con todos 0, del tamaño del vector de marcado, con 1 sola fila. FJC
        
        this.aux = new Matrix(auxVector,1);

        this.enabledAtTime = new Matrix(1, incidence.getColumnDimension()); //Vector que almacena los W_i

        this.workingVector = new Matrix(1, incidence.getColumnDimension()); //Vector que indica cuales transiciones están siendo ejecutadas en este momento

        setCurrentMarkingVector(this.initialMarking); //Inicializamos el vector de marcado actual igual al vector de marcado inicial
        
        setEnabledTransitions();
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
    public int getTransitionsFired() {
        return transitionsFired; //TODO: Cuando cambiemos al criterio de colores debe ser un vector que lleve la cuenta de cada transicion
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
    public void setEnabledAtTime(int index, long time){
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

            if(currentTransitionEnabled) {
                enabledTransitions.set(0,j,1); //Si la transicion se detectó como sensibilizada, escribo un 1 en la posicion j del arreglo enabledTransicions. FJC
                setEnabledAtTime(j,currentTime); //Establezco el tiempo en que se sensibilizaron las transiciones subsiguientes
            } else enabledTransitions.set(0,j,0); //Si la transicion se detectó como no sensibilizada, escribo un 0 en la posicion j del arreglo enabledTransicions. FJC
        }
    }



    // ----------------------------------------Otros------------------------------------------

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

        System.out.println(Thread.currentThread().getId() + ": STATE EQUATION TEST. Marcado actual:");        

        aux.print(0, 0); //BORRAR ESTE PRINT
        
        for(int i=0; i<this.aux.getColumnDimension(); i++) //Si alguno de los índices es menor que cero,
            if(this.aux.get(0,i)<0) {
                System.out.println(Thread.currentThread().getId() + ": ROMPIMO");
                return false;
            } //la ecuación de estado fue errónea (no se pudo disparar) así que devolvemos 'false'.
        
        System.out.println(Thread.currentThread().getId() + ": La transición está sensibilizada. Hay que chequear el alfa.");
        
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
        
        System.out.println(Thread.currentThread().getId() + ": Se disparó la transicion: fV: ");
        
        firingVector.print(0,0);
        
        System.out.println(Thread.currentThread().getId() + ": Exito al disparar transicion.");// + getQueue(firingVector)); 
        
        transitionsFired++; //Aumento las transiciones disparadas
        
        System.out.println(Thread.currentThread().getId() + ": Cantidad de transiciones disparadas: " + transitionsFired);

        setEnabledTransitions();
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
}