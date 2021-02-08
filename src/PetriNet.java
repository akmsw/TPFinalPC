
/**
 * @author  Luna,       Lihué       Leandro
 * @author  Coronati,   Federico    Joaquín
 * @author  Merino,     Mateo
 * @author  Bonino,     Francisco   Ignacio
 * 
 * @since 01/07/2020
 */

import java.util.ArrayList;

import Jama.Matrix;

public class PetriNet {

    //Campos privados.
    private int stopCondition; //Condición de corte del programa.
    private double[] auxVector = {}; //Vector auxiliar para cálculos.
    private ArrayList<String> transitionsSequence; //Arreglo con la secuencia de transiciones disparadas en orden.
    private Matrix incidence, incidenceBackwards; //Matrices de incidencia (+ y -) a utilizar.
    private Matrix currentMarking; //Vector relativo al marcado actual de la red.
    private Matrix enabledTransitions; //Vector que contiene el estado de sensibilización de las transiciones.
    private Matrix enabledAtTime; //Vector que contiene el instante de tiempo en el que una transición fue sensibilizada.
    private Matrix placesInvariants; //Vector relativo a los invariantes de plaza de la red.
    private Matrix aux; //Vector auxiliar para el cálculo de la ecuación de estado de la red.
    private Matrix alphaTimes; //Vector con los alfas de cada transición.
    private Matrix firedTransitions; //Vector que almacena el número de veces que se disparó cada transición.

    /**
	 * Constructor.
	 * 
     * @param   incidence           La matriz de incidencia de la red (I).
     * @param   incidenceBackwards  La matriz 'backwards' de incidencia de la red (I-).
     * @param   placesInvariants    Los invariantes de plaza de la red.
     * @param   alphaTimes          Los tiempos 'alfa' asociados a cada transición.
     * @param   stopCondition       La condición de corte del programa (cuántas tareas se deben finalizar para terminar el programa).
     */
    public PetriNet(Matrix incidence, Matrix incidenceBackwards, Matrix placesInvariants, Matrix alphaTimes, int stopCondition) {
        this.incidence = incidence;
        this.incidenceBackwards = incidenceBackwards;
        this.placesInvariants = placesInvariants;
        this.alphaTimes = alphaTimes;
        this.stopCondition = stopCondition;

        firedTransitions = new Matrix(1, incidence.getColumnDimension());

        enabledTransitions = new Matrix(1, incidence.getColumnDimension());

        aux = new Matrix(auxVector, 1);

        enabledAtTime = new Matrix(1, incidence.getColumnDimension());

        transitionsSequence = new ArrayList<String>();
    }

    //----------------------------------------Métodos públicos---------------------------------

    //----------------------------------------Getters------------------------------------------

    /**
     * @return  La matriz de incidencia de la red de Petri.
     */
    public Matrix getIncidenceMatrix() {
        return incidence;
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
     * @return  La secuencia de transiciones disparadas.
     */
    public ArrayList<String> getTransitionsSequence() {
        return transitionsSequence;
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
     * @return  La carga de las memorias.
     */
    public String getMemoriesLoad() {
        return "Cantidad de escrituras en memoria 1: " + (firedTransitions.get(0, 9) + firedTransitions.get(0, 11)) + 
               "\nCantidad de escrituras en memoria 2: " + (firedTransitions.get(0, 10) + firedTransitions.get(0, 12));
    }
    
    /**
     * @return  La carga de los procesadores (AsignarP1 y AsignarP2).
     */
    public String getProcessorsLoad() {
        return "Carga del procesador 1: " + firedTransitions.get(0, 1) + 
               "\nCarga del procesador 2: " + firedTransitions.get(0, 2);
    }

    /**
     * @return  La cantidad de tareas ejecutadas en cada procesador individualmente (FinalizarTXPX).
     */
    public String getProcessorsTasks() {
        return "Cantidad de ejecuciones de T1 en procesador 1: " + firedTransitions.get(0, 5) + 
               "\nCantidad de ejecuciones de T2 en procesador 1: " + firedTransitions.get(0, 7) + 
               "\nCantidad de ejecuciones de T1 en procesador 2: " + firedTransitions.get(0, 6) + 
               "\nCantidad de ejecuciones de T2 en procesador 2: " + firedTransitions.get(0, 8);
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

    //----------------------------------------Setters------------------------------------------

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
     * Este método recorre la matriz de incidencia 'backwards' chequeando si
     * la columna (transición) está sensibilizada (si el peso de cada arco es menor
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

    //----------------------------------------Otros--------------------------------------------
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
     * Este método se encarga de actualizar el estado de la red en general,
     * considerando el nuevo vector de marcado y las transiciones sensibilizadas
     * en base al mismo. Además, chequea los invariantes de plaza.
     * 
     * @param   firingVector    El vector de disparo del hilo.
     */
    public void fireTransition(Matrix firingVector) {
        setCurrentMarkingVector(stateEquation(firingVector));
        
        firedTransitions = firedTransitions.plus(firingVector); //Aumento las transiciones disparadas.

        transitionsSequence.add("T" + getIndex(firingVector) + "");

        checkPlacesInvariants();
        
        setEnabledTransitions();
    }

    /**
     * Este método calcula la ecuación de estado de la red.
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

        //Las transiciones 5, 6, 7, y 8 son las tareas que nos interesa contar y monitorear.
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