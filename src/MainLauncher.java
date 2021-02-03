/**
 * @author  Luna,       Lihué       Leandro
 * @author  Coronati,   Federico    Joaquín
 * @author  Merino,     Mateo
 * @author  Bonino,     Francisco   Ignacio
 * 
 * @since 01/07/2020
 */

import java.io.IOException;
import java.util.ArrayList;

import Jama.Matrix;

public class MainLauncher {

    // Campos constantes privados.
    private static final int stopCondition = 1000; // Cantidad de tareas que se tienen que finalizar para terminar la
                                                   // ejecución del programa.

    // Campos privados.
    private static double[][] incidenceArray = { // Matriz I
            { -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, -1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, -1, -1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 1, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 1, 0, -1, -1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, -1, -1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, -1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, -1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 1 } };

    private static double[][] incidenceBackwardsArray = { // Matriz I-
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0 } };

    /*
     * Orden de plazas (izquierda a derecha): 0: P0 1: ColaProcesos 2: ColaP1 3:
     * LimiteColaP1 4: ColaP2 5: LimiteColaP2 6: Procesador1 7: Procesador2 8:
     * RecursoTareas 9: ProcesandoP1 10: ProcesandoP2 11: Tarea2P1 12: Tarea2P2 13:
     * ListoP1 14: ListoP2 15: M1 16: M2 17: DisponibleM1 18: DisponibleM2
     */
    private static double[][] pInvariants = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 8, 0 }, // M1 +
                                                                                                           // DisponibleM1
                                                                                                           // = 8
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 8 }, // M2 + DisponibleM2 = 8
            { 0, 0, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // ColaP1 + LimiteColaP1 = 4
            { 0, 0, 0, 0, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // ColaP2 + LimiteColaP2 = 4
            { 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // P0 + ColaProcesos = 1
            { 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0 }, // Procesador1 + ProcesandoP1 + Tarea2P1 +
                                                                         // ListoP1 = 1
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0 }, // Procesador2 + ProcesandoP2 + Tarea2P2 +
                                                                         // ListoP2 = 1
            { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 } // RecursoTarea + ProcesandoP1 + ProcesandoP2 +
                                                                        // Tarea2P1 + Tarea2P2 = 1
    };

    private static ArrayList<Matrix> threadPaths;

    private static double[] p0 = { 1, 3, 5, 9, 15 };
    private static double[] p1 = { 1, 3, 5, 10, 16 };
    private static double[] p2 = { 2, 4, 6, 11, 15 };
    private static double[] p3 = { 2, 4, 6, 12, 16 };
    private static double[] p4 = { 1, 3, 13, 7, 9, 15 };
    private static double[] p5 = { 1, 3, 13, 7, 10, 16 };
    private static double[] p6 = { 2, 4, 14, 8, 11, 15 };
    private static double[] p7 = { 2, 4, 14, 8, 12, 16 };
    private static double[] p8 = { 0 };

    private static Matrix path1 = new Matrix(p0, 1);
    private static Matrix path2 = new Matrix(p1, 1);
    private static Matrix path3 = new Matrix(p2, 1);
    private static Matrix path4 = new Matrix(p3, 1);
    private static Matrix path5 = new Matrix(p4, 1);
    private static Matrix path6 = new Matrix(p5, 1);
    private static Matrix path7 = new Matrix(p6, 1);
    private static Matrix path8 = new Matrix(p7, 1);
    private static Matrix path9 = new Matrix(p8, 1);

    // Los betas los tomamos como infinitos para que no se desensibilicen las
    // transiciones.
    private static double[] alphaTimesA = { 2, 0, 0, 0, 0, 5, 5, 5, 5, 0, 0, 0, 0, 7, 7, 4, 4 }; // Alfas de las
                                                                                                 // transiciones.
    // private static double[] alphaTimesA = { 100, 0, 0, 0, 0, 80, 80, 80, 80, 0,
    // 0, 0, 0, 170, 170, 250, 250 };

    private static double[] iMark = { 1, 0, 0, 4, 0, 4, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8 }; // Marcado inicial de
                                                                                                 // la red.

    private static Monitor monitor;
    private static PetriNet pNet;

    /**
     * Método principal. Aquí sólo se instancian y ejecutan los hilos.
     * 
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        Matrix incidence = new Matrix(incidenceArray);
        Matrix incidenceBackwards = new Matrix(incidenceBackwardsArray);
        Matrix initialMarking = new Matrix(iMark, 1); //'1' es la cantidad de filas que quiero en la matriz.
        Matrix placesInvariants = new Matrix(pInvariants);
        Matrix alphaTimes = new Matrix(alphaTimesA, 1);

        threadPaths = new ArrayList<Matrix>();

        threadPaths.add(path1);
        threadPaths.add(path2);
        threadPaths.add(path3);
        threadPaths.add(path4);
        threadPaths.add(path5);
        threadPaths.add(path6);
        threadPaths.add(path7);
        threadPaths.add(path8);
        threadPaths.add(path9);

        pNet = new PetriNet(incidence, incidenceBackwards, initialMarking, placesInvariants, alphaTimes, stopCondition);

        monitor = new Monitor(pNet);

        int threadQuantity = threadPaths.size(); //Cantidad de hilos a crear: uno por cada invariante de transicion (sin las transiciones del vaciado) + 2 hilos extra para vaciar memorias.
        
        MyThread[] threads = new MyThread[threadQuantity];
        
        pNet.setEnabledTransitions(); //Seteo de las transiciones sensibilizadas dado el marcado inicial de la red.
        
        for(int i = 0; i < threadQuantity; i++) {
            threads[i] = new MyThread(threadPaths.get(i), monitor,pNet);
            threads[i].start();
        }

        //Creación y ejecución del hilo Log.
        try {
            MyLogger log = new MyLogger("ReportMonitor.txt", pNet, monitor);
            log.start();
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Error al crear el log.");
        }
    }
}