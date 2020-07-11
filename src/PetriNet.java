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
    private Matrix incidence, incidenceBackwards, initialMarking, currentMarking, enabledTransitions, placesInvariants, aux;
    private double[] auxVector = {};

    /**
	 * Constructor.
	 * 
     * @param incidence La matriz de incidencia de la red.
     * @param incidenceBackwards Matriz 'backwards' de incidencia de la red.
     * @param initialMarking El vector de marcado inicial de la red.
     * @param placesInvariants Los invariantes de plaza de la red.
     */
    public PetriNet(Matrix incidence, Matrix incidenceBackwards, Matrix initialMarking, Matrix placesInvariants) {
        this.incidence = incidence;
        this.incidenceBackwards = incidenceBackwards;
        this.initialMarking = initialMarking;
        this.placesInvariants = placesInvariants;
        this.enabledTransitions = new Matrix(1, incidence.getColumnDimension()); //Inicializo el vector E de transiciones sensibilizadas con todos 0, del tamaño del vector de marcado, con 1 sola fila. FJC
        this.aux = new Matrix(auxVector,1);

        setCurrentMarkingVector(this.initialMarking); //Inicializamos el vector de marcado actual igual al vector de marcado inicial
    }

    //----------------------------------------Métodos públicos---------------------------------

    //----------------------------------------Getters------------------------------------------

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

    //----------------------------------------Setters------------------------------------------

    /**
     * @param currentMarking Vector de marcado actual de la red de Petri.
     */
    public void setCurrentMarkingVector(Matrix currentMarking) {
        this.currentMarking = currentMarking;
    }

    /**
     * Este método recorre la matriz de incidencia 'backwards' chequeando si
     * la columna (transición) está sensibilizada (el peso de cada arco es menor
     * o igual a la cantidad de tokens de la plaza). Seteamos en '1' la transición
     * sensibilizada en el vector 'enabledTransitions'.
     */
    public void setEnabledTransitions() {
        boolean currentTransitionEnabled;

        for(int j=0; j<incidenceBackwards.getColumnDimension(); j++) { //Itero columnas es decir Transiciones
            currentTransitionEnabled = true;
            
            for(int i=0; i<incidenceBackwards.getRowDimension(); i++) //Itero filas es decir Plazas
                if(incidenceBackwards.get(i,j)>currentMarking.get(0,i)) { //Si el peso del arco es mayor a la cantidad de tokens en la plaza que conecta a esa transicion j
                    currentTransitionEnabled = false; //currentMarking.get(i,0) antes estaba en (0,i) pero lo cambiamos cuando transpusimos el currentMarking
                    break;
                }

            if(currentTransitionEnabled) enabledTransitions.set(0,j,1); //Si la transicion se detectó como sensibilizada, escribo un 1 en la posicion j del arreglo enabledTransicions. FJC
            else enabledTransitions.set(0,j,0); //Si la transicion se detectó como no sensibilizada, escribo un 0 en la posicion j del arreglo enabledTransicions. FJC
        }
    }

    //----------------------------------------Otros------------------------------------------

    /**
     * Este método testea si es posible realizar el disparo de la transición
     * con el vector de firing del hilo.
     * 
     * @param firingVector El vector de firing actual del vector.
     * @return Si el resultado de la ecuación de estado fue correcto y
     *         se pudo asignar el nuevo vector de estado de la red.
     */
    public boolean stateEquationTest(Matrix firingVector) {
        this.aux = stateEquation(firingVector);

        System.out.println("STATE EQUATION TEST");
        
        for(int i=0; i<this.aux.getColumnDimension(); i++) //Si alguno de los índices es menor que cero,
            if(this.aux.get(0,i)<0) {
                System.out.println("ROMPIMO");
                return false;
            }          //la ecuación de estado fue errónea (no se pudo disparar) así que devolvemos 'false'.
        
        System.out.println("PUEDO DISPARAR");
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
    }

    /**
     * Este método calcula la ecuación de estado.
     * 
     * @param firingVector Vector de disparo del hilo.
     * @return El resultado de la ecuación de estado.
     */
    public Matrix stateEquation(Matrix firingVector) {
        System.out.println("HABEMVS STATUM EQVATIONIS");
        return (currentMarking.transpose().plus(incidence.times(firingVector.transpose()))).transpose(); //Ecuación de estado.
    }
}