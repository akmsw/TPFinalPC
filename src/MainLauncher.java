/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import Jama.Matrix;

public class MainLauncher {
    
    //Campos privados.
    private static double[][] incidenceArray = {
        {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 1,-1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0,-1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0,-1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0,-1,-1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0},
        { 0, 0, 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 0, 0}






        
        { 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 1,-1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0,-1, 0,-1, 0, 0, 0, 8, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,-1, 0,-1, 0, 0, 0, 8},
        { 0,-1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0,-1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 1, 0, 1, 0,-1,-1, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0,-1,-1, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0,-8, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0,-8},
        {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0},
        { 0, 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0},
        { 0, 0, 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0},
        { 0, 0, 0,-1,-1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 0, 0}
    };

    private static double[][] incidenceBackwardsArray = { //TODO: VER SI NOS HACE FALTA SINO BORRAR
        { 1, 0, 1, 0, 0},
        { 0, 1, 0, 0, 0},
        { 0, 0, 0, 1, 0},
        { 0, 0, 0, 0, 1},
        { 1, 0, 1, 0, 0}
    };

    //TODO: Cuando ya no se use borrar
    private static double[][] identityA = {
        {1,0,0,0,0},
        {0,1,0,0,0},
        {0,0,1,0,0},
        {0,0,0,1,0},
        {0,0,0,0,1}
    };

    //TODO: Ver si no se usa y borrar
    private static double[][] pInvariants = {
        {2, 2, 2, 2, 0},
        {0, 1, 1, 0, 1}
    };

    private static double[][] tInvariants = {
        { 8, 8, 0, 8, 0, 8, 0, 0, 0, 8, 0, 0, 0, 0, 0, 1, 0},
        { 8, 8, 0, 8, 0, 8, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 1},
        { 8, 0, 8, 0, 8, 0, 8, 0, 0, 0, 0, 8, 0, 0, 0, 1, 0},
        { 8, 0, 8, 0, 8, 0, 8, 0, 0, 0, 0, 0, 8, 0, 0, 0, 1},
        { 8, 8, 0, 8, 0, 0, 0, 8, 0, 8, 0, 0, 0, 8, 0, 1, 0},
        { 8, 8, 0, 8, 0, 0, 0, 8, 0, 0, 8, 0, 0, 8, 0, 0, 1},
        { 8, 0, 8, 0, 8, 0, 0, 0, 8, 0, 0, 8, 0, 0, 8, 1, 0},
        { 8, 0, 8, 0, 8, 0, 0, 0, 8, 0, 0, 0, 8, 0, 8, 0, 1},
    };

    private static double[] iMark = {};

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

        System.out.println("SOY EL BIG MOMMA SARANIC THREAD. VOY A CREAR " + incidence.getColumnDimension() + " THREADS.");
                
        for(int i=0; i<incidence.getColumnDimension(); i++) {
            threads[i] = new MyThread(identity.getMatrix(i, i, 0, identity.getColumnDimension()-1), monitor);
            threads[i].start();
        }
    }
}