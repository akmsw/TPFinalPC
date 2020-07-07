/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import Jama.Matrix;

public class MainLauncher {

    public static void main(String args[]) {
        //Matriz de incidencia de la red de Petri dada.
        double[][] i = {{-1, 0,-1, 0, 1},
                        { 1,-1, 0, 0, 0},
                        { 0, 0, 1,-1, 0},
                        { 0, 1, 0, 1,-1},
                        {-1, 1,-1, 1, 0}};
        
        double[] iMark = {2, 0, 0, 0, 1};

        double[][] pInvariants = {
            {2, 2, 2, 2, 0},
            {0, 1, 1, 0, 1}        
        };

        Matrix incidence = new Matrix(i);
        Matrix initialMarking = new Matrix(iMark,1);
        Matrix placesInvariants = new Matrix(pInvariants);
        
        PetriNet pNet = new PetriNet(incidence, initialMarking, placesInvariants);
        
        Monitor monitor = new Monitor(pNet);
    }
}       