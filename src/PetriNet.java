/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import Jama.Matrix;

public class PetriNet {

    //Private class fields
    private Matrix incidence, initialMarking, currentMarking, enabledTransitions, placesInvariants;
    
    //Constructor
    /**
     * @param incidence The Petri net incidence matrix.
     * @param initialMarking The Petri net initial marking vector.
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
     * @return The Petri net incidence matrix.
     */
    public Matrix getIncidenceMatrix() {
        return incidence;
    }

    /**
     * @return The Petri net initial marking vector.
     */
    public Matrix getInitialMarkingingVector() {
        return initialMarking;
    }

    /**
     * @return The Petri net current marking vector.
     */
    public Matrix getCurrentMarkingingVector() {
        return currentMarking;
    }

    /**
     * @return The current enabled transitions.
     */
    public Matrix getEnabledTransitions() {
        //TODO
        return enabledTransitions;
    }

    /**
     * @param firingVector The current thread firing vector.
     * @return The Petri net state equation result (the new Petri net marking vector).
     */
    public Matrix getNextMarkingVector(Matrix firingVector) {
        //TODO
        return currentMarking;
    }

    //----------------------------------------Setters----------------------------------------
    /**
     * @param currentMarking The Petri net current marking vector.
     */
    public void setCurrentMarkingingVector(Matrix currentMarking) {
        this.currentMarking = currentMarking;
    }
}