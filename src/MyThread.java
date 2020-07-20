/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 07/07/2020
 */

import java.util.ArrayList;

import Jama.Matrix;

public class MyThread extends Thread {

    //Campos privados.
    private boolean working;
    private ArrayList<Matrix> myTransitions;
    private Matrix firingVector; // Este vector indica la transicion que se disparará o que se intentó disparar.
    private Monitor monitor;

    /**
     * Constructor.
     * 
     * @param sequence Secuencia de transiciones asociadas al hilo.
     * @param monitor Referencia al monitor que controla la red de Petri.
     */
    public MyThread(Matrix sequence, Monitor monitor) {
        this.monitor = monitor;
        
        working = false;

        myTransitions = new ArrayList<Matrix>();

        for (int i = 0; i < sequence.getColumnDimension(); i++)
            myTransitions.add(getTransitionVector((int) sequence.get(0, i)));
    }

    // ----------------------------------------Métodos públicos---------------------------------

    // ----------------------------------------Getters------------------------------------------

    /**
     * @return El vector de transiciones a disparar.
     */
    public Matrix getFiringVector() {
        return firingVector;
    }

    /**
     * @return El vector de transiciones asociadas al hilo.
     */
    public ArrayList<Matrix> getAssociatedTransitions() {
        return myTransitions;
    }

    // ----------------------------------------Overrides----------------------------------------

    /**
     * En este método, cada hilo tiene un indice que comienza en '0'
     * e itera entre la cantidad de transiciones asociadas al hilo.
     * Mientras no se haya concretado la condición de corte del programa,
     * se arma un vector de disparo con el índice que indique el i-ésimo
     * elemento obtenido del arreglo 'myTransitions'.
     * Luego, se intenta tomar el mutex del monitor. Si no se logra, el hilo
     * se encola en la entrada, cediendo el mutex. Si se logra entrar, se evalúa si
     * la ecuación de estado es correcta (si la transición está sensibilizada).
     * Si la transición no está sensibilizada, el hilo se encola en la
     * transición que quiso disparar, cediendo el mutex.
     * Si la transición está sensibilizada, el hilo no estuvo trabajando anteriormente
     * pero hay un hilo trabajando en dicha transición (esperando el alfa de la misma),
     * entonces se cede el mutex y el hilo se encola en la transición que quiso disparar.
     * Si todas las condiciones para disparar se dan, entonces se procede a chequear si
     * estamos en la ventana de tiempo para disparar la transición (entre alfa y beta).
     * Si aún no pasó el tiempo alfa, el hilo cede el mutex, se lo setea como
     * 'hilo trabajando' para evitar que otro hilo pueda intentar disparar esa transición
     * para la cual él está esperando el alfa, y duerme durante el tiempo necesario
     * para estar en la ventana de tiempo 'beta - alfa', teniendo que tomar el mutex
     * del monitor cuando se despierte.
     * Si estamos en la ventana de tiempo donde es posible disparar la transición, el hilo
     * la dispara, se le quita el estado de 'hilo trabajando', y pasa a chequear si hay
     * hilos encolados en transiciones sensibilizadas para llamar a la política o para
     * simplemente ceder el mutex.
     */
    @Override
    public void run() {
        int i = 0;

        while(!monitor.getPetriNet().hasCompleted()) {
            firingVector = myTransitions.get(i);

            try {
                monitor.catchMonitor();
                if(monitor.getPetriNet().hasCompleted()) break; /*Mientras estábamos peleando por el mutex puede que algún otro hilo
                                                                   haya ejecutado una transición que haya hecho llegar a la condición
                                                                   de corte del programa y, por lo tanto, hay que chequear si ésto sucedió.*/
            } catch(InterruptedException e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread().getId() + ": Error al entrar al monitor.");
            }

            if(!monitor.getPetriNet().stateEquationTest(firingVector)) {
                monitor.exitMonitor();

                try {
                    monitor.getConditionQueues().get(monitor.getIndexHigh(firingVector)).acquire();

                    if(monitor.getPetriNet().hasCompleted()) break;
                } catch(Exception e) {
                    e.printStackTrace();
                    System.out.println(Thread.currentThread().getId() + ": Error al encolar un hilo.");
                }
            } else {
                if(!working) { /*Hacemos este chequeo para contemplar el caso en el que un hilo estuvo esperando
                                 el tiempo alfa y ahora DEBE disparar la transición para la cual estuvo esperando.*/
                    if(monitor.getPetriNet().getWorkingVector().get(0, monitor.getIndexHigh(firingVector))==1) {
                        monitor.exitMonitor();

                        try {
                            monitor.getConditionQueues().get(monitor.getIndexHigh(firingVector)).acquire();

                            if(monitor.getPetriNet().hasCompleted()) break;
                        } catch(Exception e) {
                            e.printStackTrace();
                            System.out.println(Thread.currentThread().getId() + ": Error al encolar un hilo.");
                        }
                    }
                }
            }
            if(!monitor.alphaTimeCheck(firingVector)) {
                monitor.exitMonitor();

                try {
                    monitor.getPetriNet().getWorkingVector().set(0, monitor.getIndexHigh(firingVector), 1);
                    working = true;
                    sleep(monitor.getWorkingTime());
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    System.out.println(Thread.currentThread().getId() + ": Error en tiempo de sleep");
                }

                continue;
            }

            monitor.getPetriNet().fireTransition(firingVector);

            working = false;
                    
            Matrix EandW = monitor.getAnd();

            monitor.waitingCheck(EandW);
            
            i++;
            
            if(i>=myTransitions.size()) i=0; //Si ya cumplí la secuencia de disparos asociada, reseteo el índice para comenzarla nuevamente.
        }
    }

    // ----------------------------------------Otros------------------------------------------

    /**
     * En este método se crea un vector de una fila y con tantas columnas como transiciones
     * tenga la red. Luego, en base al índice que se recibe como parámetro, se setea un '1'
     * en esa posición, logrando así cualquier vector de disparo deseado.
     * 
     * @param i Índice de la transición que se quiere setear en el vector de disparo.
     * @return El vector con ceros y un '1' en la i-ésima transición.
     */
    public Matrix getTransitionVector(int i) {
        Matrix vector = new Matrix(1, monitor.getPetriNet().getIncidenceMatrix().getColumnDimension());
        
        vector.set(0, i, 1);
        
        return vector;
    }
}