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
    private Matrix incidence, initialMarking, currentMarking, enabledTransitions, placesInvariants;
    
    //Constructor
    /**
     * @param incidence La matriz de incidencia de la red de Petri.
     * @param initialMarking El vector de marcado inicial de la red de Petri.
     */
    public PetriNet(Matrix incidence, Matrix initialMarking, Matrix placesInvariants) {
        this.incidence = incidence;
        this.initialMarking = initialMarking;
        this.placesInvariants = placesInvariants;

        setCurrentMarkingingVector(initialMarking);
    }

    //----------------------------------------Public methods---------------------------------

    //----------------------------------------Getters----------------------------------------
    /**
     * @return La matriz de incidencia de la red de Petri.
     */
    public Matrix getIncidenceMatrix() {
        return incidence;
    }

    /**
     * @return El vector de marcado inicial de la red de Petri.
     */
    public Matrix getInitialMarkingingVector() {
        return initialMarking;
    }

    /**
     * @return El vector de marcado actual de la red de Petri.
     */
    public Matrix getCurrentMarkingingVector() {
        return currentMarking;
    }

    /**
     * @return Transiciones sensibilizadas en este momento.
     */
    public Matrix getEnabledTransitions() {
        //TODO
        return enabledTransitions;
    }

    /**
     * @param firingVector El vector de firing actual del vector.
     * @return Si el resultado de la ecuación de estado fue correcto y se pudo asignar el nuevo vector de estado de la red.
     */
    public boolean stateEquationTest(Matrix firingVector) {
        //TODO
        double[] test = {};
        
        Matrix aux = new Matrix(test,1);

        aux = currentMarking.plus(incidence.times(firingVector)); // Ecuación de estado
        
        for(int i=0; i<aux.getColumnDimension(); i++) {
            if(aux.get(0,i)<0) {
                return false;
            }
        }
        
        setCurrentMarkingingVector(aux);
        
        return true;
    }

    //----------------------------------------Setters----------------------------------------

    //----------------------------------------Setters----------------------------------------
    /**
Vectror de marcado actual de la red de pPetri     * @param currentMarking The Petri net current marking vector.
     * @param currentMarking The Petri net current marking vector.
     * @param currentMarking The Petri net current marking vector.
     */
    public void setCurrentMarkingingVector(Matrix currentMarking) {
        this.currentMarking = currentMarking;
    }
}