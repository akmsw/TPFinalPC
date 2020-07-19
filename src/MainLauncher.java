
/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import java.util.ArrayList;

import Jama.Matrix;

public class MainLauncher {

    // Campos privados.
    private static double[][] incidenceArray = { { -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
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
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, -8, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, -8 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 8, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 8 }, };

    private static double[][] incidenceBackwardsArray = { { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
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
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0 }, };

    /*
     * Orden plazas (izquierda a derecha): 0: P0 1: ColaProcesos 2: ColaP1 3:
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
            { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 }, // RecursoTarea + ProcesandoP1 + ProcesandoP2 +
                                                                         // Tarea2P1 + Tarea2P2 = 1
    };

    private static double[][] tInvariants = {
        { 8, 8, 0, 8, 0, 8, 0, 0, 0, 8, 0, 0, 0, 0, 0, 1, 0 },
        { 8, 8, 0, 8, 0, 8, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 1 },
        { 8, 0, 8, 0, 8, 0, 8, 0, 0, 0, 0, 8, 0, 0, 0, 1, 0 },
        { 8, 0, 8, 0, 8, 0, 8, 0, 0, 0, 0, 0, 8, 0, 0, 0, 1 },
        { 8, 8, 0, 8, 0, 0, 0, 8, 0, 8, 0, 0, 0, 8, 0, 1, 0 },
        { 8, 8, 0, 8, 0, 0, 0, 8, 0, 0, 8, 0, 0, 8, 0, 0, 1 },
        { 8, 0, 8, 0, 8, 0, 0, 0, 8, 0, 0, 8, 0, 0, 8, 1, 0 },
        { 8, 0, 8, 0, 8, 0, 0, 0, 8, 0, 0, 0, 8, 0, 8, 0, 1 }
    };

    /*
     * LOS EX CAMINOS QUE USABAMOS private static double[][] caminitosxd = { { 1, 1,
     * 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, // { 1, 1, 0, 1, 0, 1, 0, 0, 0,
     * 0, 1, 0, 0, 0, 0, 0, 0}, // { 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0,
     * 0}, // { 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}, // { 1, 1, 0, 1,
     * 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0}, // { 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1,
     * 0, 0, 1, 0, 0, 0}, // { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0},
     * // { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0}, // { 0, 0, 0, 0, 0,
     * 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
     * 0, 0, 0, 1}, };
     */

    // LOS EX CAMINOS QUE USABAMOS
    /*
     * private static double[][] caminitosconArrivalSingle = {
     * { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, { 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0,
     * 0, 0, 0}, { 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, { 1, 0, 1, 0,
     * 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, { 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0,
     * 1, 0, 0, 0, 0}, { 1, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0}, { 1, 1,
     * 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0}, { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0,
     * 0, 1, 0, 0, 1, 0, 0}, { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0}, {
     * 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, { 0, 0, 0, 0, 0, 0, 0, 0,
     * 0, 0, 0, 0, 0, 0, 0, 0, 1}, };
     */

    /*
     * Orden transiciones (izquierda a derecha):
     * 0: ArrivalRate
     * 1: AsignarP1
     * 2: AsignarP2
     * 3: EmpezarP1
     * 4: EmpezarP2
     * 5: FinalizarT1P1
     * 6: FinalizarT1P2
     * 7: FinalizarT2P1
     * 8: FinalizarT2P2
     * 9: P1M1
     * 10: P1M2
     * 11: P2M1
     * 12: P2M2 
     * 13: ProcesarT2P1 
     * 14: ProcesarT2P2 
     * 15: VaciarM1 
     * 16: VaciarM2
     */
    private static ArrayList<Matrix> threadPaths;

    /*
     * private static double[] a = {0};
     * private static double[] b = {1,3,5};
     * private static double[] c = {1,3,5};
     * private static double[] d = {2,4,6};
     * private static double[] e = {2,4,6};
     * private static double[] f = {1,3,13,7};
     * private static double[] g = {1,3,13,7};
     * private static double[] h = {2,4,14,8};
     * private static double[] i = {2,4,14,8};
     * private static double[] j = {9};
     * private static double[] k = {10};
     * private static double[] l = {11};
     * private static double[] m = {12};
     * private static double[] n = {15};
     * private static double[] o = {16};
     */

    //private static double[] a = {0,1,3,0,1,0,1,0,1,0,1,5,9,3,5,9,3,5,9,3,5,9,3,5,9,0,1,3,0,1,0,1,5,9,3,5,9,3,5,9,15};

   /* private static double[] a = { 0, 1, 3, 5 };
    private static double[] b = { 0, 1, 3, 5 };
    private static double[] c = { 0, 2, 4, 6 };
    private static double[] d = { 0, 2, 4, 6 };
    private static double[] e = { 0, 1, 3, 13, 7 };
    private static double[] f = { 0, 1, 3, 13, 7 };
    private static double[] g = { 0, 2, 4, 14, 8 };
    private static double[] h = { 0, 2, 4, 14, 8 };
    private static double[] i = { 9 };
    private static double[] j = { 10 };
    private static double[] k = { 11 };
    private static double[] l = { 12 };
    private static double[] m = { 15 };
    private static double[] n = { 16 }; */

    // private static double[] a = {0}; //ArrivalRate
    /*
     * private static double[] b = {0,1,3,5}; //ArrivalRate private static double[]
     * c = {0,1,3,5}; //ArrivalRate private static double[] d = {0,2,4,6}; // -
     * private static double[] e = {0,2,4,6}; // private static double[] f =
     * {0,1,3,13,7}; // private static double[] g = {0,1,3,13,7}; // private static
     * double[] h = {0,2,4,14,8}; // private static double[] i = {0,2,4,14,8}; //
     * private static double[] j = {9}; // private static double[] k = {10}; //
     * private static double[] l = {11}; // private static double[] m = {12}; //
     * private static double[] n = {15}; // private static double[] o = {16}; //
     */

    
     private static double[] a = {0};  
     private static double[] b = {1};
     private static double[] c = {2};
     private static double[] d = {3}; 
     private static double[] e = {4}; 
     private static double[] f = {5}; 
     private static double[] g = {6};
     private static double[] h = {7}; 
     private static double[] i = {8};  
     private static double[] j = {9};
     private static double[] k = {10}; 
     private static double[] l = {11}; 
     private static double[] m = {12};
     private static double[] n = {13};
     private static double[] o = {14};
     private static double[] p = {15};
     private static double[] q = {16};
     

    private static Matrix path1 = new Matrix(a, 1);
    private static Matrix path2 = new Matrix(b, 1);
    private static Matrix path3 = new Matrix(c, 1);
    private static Matrix path4 = new Matrix(d, 1);
    private static Matrix path5 = new Matrix(e, 1);
    private static Matrix path6 = new Matrix(f, 1);
    private static Matrix path7 = new Matrix(g, 1);
    private static Matrix path8 = new Matrix(h, 1);
    private static Matrix path9 = new Matrix(i, 1);
    private static Matrix path10 = new Matrix(j, 1);
    private static Matrix path11 = new Matrix(k, 1);
    private static Matrix path12 = new Matrix(l, 1);
    private static Matrix path13 = new Matrix(m, 1);
    private static Matrix path14 = new Matrix(n, 1);
    private static Matrix path15 = new Matrix(o,1);
    private static Matrix path16 = new Matrix(p,1);
    private static Matrix path17 = new Matrix(q,1);

    private static double[] alphaTimesA = { 25, 0, 0, 0, 0, 50, 50, 50, 50, 0, 0, 0, 0, 75, 75, 100, 100 };
    // private static double[] alphaTimesA = { 300, 0, 0, 0, 0, 400, 400, 400, 400,
    // 0, 0, 0, 0, 400, 400, 500, 500};
    // private static double[] alphaTimesA = { 200, 0, 0, 0, 0, 250, 250, 250, 250,
    // 0, 0, 0, 0, 250, 250, 300, 300};
    // ArrivalRate: 2[s]
    // FinalizarTareas: 3[s]
    // ProcesarTareas2: 5[s]
    // VaciarMemorias: 4[s]
    // Los betas los tomamos como infinitos para que no se desensibilicen las
    // transiciones.

    private static double[] iMark = { 1, 0, 0, 4, 0, 4, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8 };

    private static final int stopCondition = 1000; // Cantidad de tareas que se tienen que finalizar para terminar la ejecucion
    private static final int stepToLog = 50; // Cada tantas tareas se checkea el balance de carga en procesadores y memorias

    private static Log myLog;
    private static Monitor monitor;
    private static PetriNet pNet;

    public static void main(String args[]) {
        Matrix incidence = new Matrix(incidenceArray);
        Matrix incidenceBackwards = new Matrix(incidenceBackwardsArray);
        Matrix initialMarking = new Matrix(iMark, 1); // '1' es la cantidad de filas que quiero en la matriz.
        Matrix placesInvariants = new Matrix(pInvariants);
        Matrix transitionInvariants = new Matrix(tInvariants);
        Matrix alphaTimes = new Matrix(alphaTimesA, 1);

        threadPaths = new ArrayList<Matrix>();

        Object lock = new Object();

        threadPaths.add(path1);
        threadPaths.add(path2);
        threadPaths.add(path3);
        threadPaths.add(path4);
        threadPaths.add(path5);
        threadPaths.add(path6);
        threadPaths.add(path7);
        threadPaths.add(path8);
        threadPaths.add(path9);
        threadPaths.add(path10);
        threadPaths.add(path11);
        threadPaths.add(path12);
        threadPaths.add(path13);
        threadPaths.add(path14);
        threadPaths.add(path15);
        threadPaths.add(path16);
        threadPaths.add(path17);

        pNet = new PetriNet(incidence, incidenceBackwards, initialMarking, placesInvariants, transitionInvariants, alphaTimes, stopCondition, lock);

        monitor = new Monitor(pNet);

        // TODO: DESCOMENTAR CUANDO QUIERAS USAR EL LOG
        try { // Inicializamos el hilo log.
            myLog = new Log("ReportMonitor.txt", monitor, stepToLog, lock);
            myLog.start(); // El hilo Log comienza a correr para registrar toda la actividad.
        } catch (Exception e) {
            System.out.println("Error al crear el log.");
        }

        try {
            java.lang.Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int threadQuantity = threadPaths.size(); //Uno por cada invariante de transicion (sin las transiciones del vaciado) + 2 hilos extra para vaciar memorias
        
        MyThread[] threads = new MyThread[threadQuantity];

        //System.out.println("SOY EL BIG MOMMA CHECHERO THREAD. VOY A CREAR " + threadQuantity + " THREADS.");
        
        pNet.setEnabledTransitions();
        
        for(int i=0; i<threadQuantity; i++) {
            threads[i] = new MyThread(threadPaths.get(i), monitor);
            threads[i].start();
        }
    }
}