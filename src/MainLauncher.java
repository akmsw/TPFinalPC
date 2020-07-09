/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import Jama.Matrix;

public class MainLauncher {
    
    //Matriz de incidencia de la red de Petri dada.
    private static double[][] incidenceArray = {
        {-1, 0,-1, 0, 1},
        { 1,-1, 0, 0, 0},
        { 0, 0, 1,-1, 0},
        { 0, 1, 0, 1,-1},
        {-1, 1,-1, 1, 0}
    };

    private static double[][] incidenceBackwardsArray = {
        { 1, 0, 1, 0, 0},
        { 0, 1, 0, 0, 0},
        { 0, 0, 0, 1, 0},
        { 0, 0, 0, 0, 1},
        { 1, 0, 1, 0, 0}
    };

    private static double[][] identityA = {
        {1,0,0,0,0},
        {0,1,0,0,0},
        {0,0,1,0,0},
        {0,0,0,1,0},
        {0,0,0,0,1}
    };

    private static double[][] pInvariants = {
        {2, 2, 2, 2, 0},
        {0, 1, 1, 0, 1}
    };

    private static double[] iMark = {2, 0, 0, 0, 1};

    private static Log myLog;
    private static Monitor monitor;
    private static PetriNet pNet;

    public static void main(String args[]) {
        Matrix identity = new Matrix(identityA);        
        Matrix incidence = new Matrix(incidenceArray);
        Matrix incidenceBackwards = new Matrix(incidenceBackwardsArray);
        Matrix initialMarking = new Matrix(iMark, 1); //1 es la cantidad de filas que quiero en la matriz
        Matrix placesInvariants = new Matrix(pInvariants);
        
        pNet = new PetriNet(incidence, incidenceBackwards, initialMarking, placesInvariants);
        
        try { //Inicializamos el hilo Log
            myLog = new Log("ReportMonitor.txt");
            myLog.start(); //El hilo Log comienza a correr para registrar toda la actividad
        }
        catch (Exception e) {
            System.out.println("LOG ERROR");
        }

        monitor = new Monitor(pNet, myLog);
        
        MyThread[] threads = new MyThread[incidence.getColumnDimension()];
                
        for(int i=0; i<incidence.getColumnDimension(); i++) {
            threads[i] = new MyThread(identity.getMatrix(i, i, 0, identity.getColumnDimension()-1), monitor);
            threads[i].start();
        }
    }
}