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
        { 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0},
        { 0, 0, 0,-1,-1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0},
        { 0, 0, 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 0, 0},
        { 0, 0, 0, 0, 0, 1, 0, 1, 0,-1,-1, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0,-1,-1, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0,-8, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0,-8},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0,-1, 0,-1, 0, 0, 0, 8, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,-1, 0,-1, 0, 0, 0, 8},
    };

    private static double[][] incidenceBackwardsArray = {
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0},
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

    //Orden Ts: ArrivalRate AsignarP1 AssignarP2 EmpezarP1 EmpezarP2 FinalizarT1P1 FinalizarT1P2 FinalizarT2P1 FinalizarT2P2 P1M1 P1M2 P2M1 P2M2 ProcesarT2P1 ProcesarT2P2 VaciarM1 VaciarM2
    private static double[][] threadMakerA = {
        { 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        { 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        { 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        { 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        { 1, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0},
        { 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0},
        { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0},
        { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
    };

    private static double[] alphaTimesA = { 2000, 0, 0, 0, 0, 3000, 3000, 3000, 3000, 0, 0, 0, 0, 5000, 5000, 4000, 4000};
    //ArrivalRate: 2[s]
    //FinalizarTareas: 3[s]
    //ProcesarTareas2: 5[s]
    //VaciarMemorias: 4[s]
    //Los bethas los tomamos como infinitos para que no se desensibilicen las transiciones.

    private static double[] iMark = { 1, 0, 0, 4, 0, 4, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8};

    private static Log myLog;
    private static Monitor monitor;
    private static PetriNet pNet;

    public static void main(String args[]) {
       // Matrix identity = new Matrix(identityA);        
        Matrix incidence = new Matrix(incidenceArray);
        Matrix incidenceBackwards = new Matrix(incidenceBackwardsArray);
        Matrix initialMarking = new Matrix(iMark, 1); //1 es la cantidad de filas que quiero en la matriz
        Matrix placesInvariants = new Matrix(pInvariants);
        Matrix transitionInvariants = new Matrix(tInvariants);
        Matrix threadMaker = new Matrix(threadMakerA);
        Matrix alphaTimes = new Matrix(alphaTimesA, 1);
        
        pNet = new PetriNet(incidence, incidenceBackwards, initialMarking, placesInvariants, alphaTimes);
        
        /* TODO: DESCOMENTAR CUANDO QUIERAS USAR EL LOG
        try { //Inicializamos el hilo Log
            myLog = new Log("ReportMonitor.txt");
            myLog.start(); //El hilo Log comienza a correr para registrar toda la actividad
        } catch(Exception e) {
            System.out.println("LOG ERROR");
        }*/

        monitor = new Monitor(pNet, myLog);

        int threadQuantity = threadMaker.getRowDimension(); //Uno por cada invariante de transicion (sin las transiciones del vaciado) + 2 hilos extra para vaciar memorias
        
        MyThread[] threads = new MyThread[threadQuantity];

        System.out.println("SOY EL BIG MOMMA CHECHERO THREAD. VOY A CREAR " + threadQuantity + " THREADS.");
        
        for(int i=0; i<threadQuantity; i++) {
            threads[i] = new MyThread(threadMaker.getMatrix(i, i, 0, threadMaker.getColumnDimension()-1), monitor);
            threads[i].start();
        }
    }
}