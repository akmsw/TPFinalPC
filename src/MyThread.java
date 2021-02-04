/**
 * @author  Luna,       Lihué       Leandro
 * @author  Coronati,   Federico    Joaquín
 * @author  Merino,     Mateo
 * @author  Bonino,     Francisco   Ignacio
 * 
 * @since 07/07/2020
 */

import java.util.ArrayList;

import Jama.Matrix;

public class MyThread extends Thread {

    //Campos privados.
    private ArrayList<Matrix> myTransitions; //Arreglo de transiciones asociadas al hilo.
    private Matrix firingVector; //Este vector indica la transicion que se disparará o que se intentó disparar.
    private Monitor monitor; //Monitor que controla la red de Petri.
    private PetriNet pNet; //Red de Petri que modela el sistema.
    private int transition; //Transición que se quiere disparar.

    /**
     * Constructor.
     * 
     * @param   sequence    Secuencia de transiciones asociadas al hilo.
     * @param   monitor     Referencia al monitor que controla la red de Petri.
     */
    public MyThread(Matrix sequence, Monitor monitor,PetriNet pNet) {
        this.monitor = monitor;
        this.pNet = pNet;

        myTransitions = new ArrayList<Matrix>();

        for(int i = 0; i < sequence.getColumnDimension(); i++)
            myTransitions.add(getTransitionVector((int)sequence.get(0, i)));
    }

    //----------------------------------------Métodos públicos---------------------------------

    //----------------------------------------Getters------------------------------------------

    /**
     * @return  El vector de disparo del hilo.
     */
    public Matrix getFiringVector() {
        return firingVector;
    }

    /**
     * @return  El vector de transiciones asociadas al hilo.
     */
    public ArrayList<Matrix> getAssociatedTransitions() {
        return myTransitions;
    }

    /**
     * En este método se crea un vector de una fila y tantas columnas como transiciones
     * tenga la red. Luego, en base al índice que se recibe como parámetro, se setea un '1'
     * en esa posición, logrando así cualquier vector de disparo deseado.
     * 
     * @param   i   Índice de la transición que se quiere setear en el vector de disparo.
     * 
     * @return  El vector con ceros y un '1' en la i-ésima transición.
     */
    public Matrix getTransitionVector(int i) {
        Matrix vector = new Matrix(1, monitor.getPetriNet().getIncidenceMatrix().getColumnDimension());
        
        vector.set(0, i, 1);
        
        return vector;
    }

    /**
     * Este método actualiza el valor del índice de la transición a disparar.
     * Si el valor del índice incrementado es mayor o igual a la cantidad de
     * transiciones asociadas significa que ya completamos la secuencia y se
     * debe comenzar de nuevo con la misma, por lo que se resetea el contador.
     */
    public void nextTransition() {
        transition++;
        if(transition >= myTransitions.size()) transition = 0;
    }

    //----------------------------------------Overrides----------------------------------------

    /**
     * En este método, cada hilo tiene un índice que comienza en '0' e itera entre
     * la cantidad de transiciones asociadas al hilo. Mientras no se haya concretado
     * la condición de corte del programa, se arma un vector de disparo con el
     * índice que indique el i-ésimo elemento obtenido del arreglo 'myTransitions' y
     * se intenta disparar la misma por medio del monitor. Luego de disparar la
     * transición, se actualiza el valor del índice para armar el siguiente vector
     * de disparo.
     */
    @Override
    public void run() {
        while(!pNet.hasCompleted()) {
            firingVector = myTransitions.get(transition);
            
            //System.out.println(Thread.currentThread().getId() + ": Quiero disparar T" + monitor.getIndex(firingVector));
            
            if(monitor.tryFiring(firingVector)) {
                nextTransition();
            } else {
                try {
                    //System.out.println(Thread.currentThread().getId() + ": Me voy a dormir " + monitor.getWorkingTime(Thread.currentThread().getId()));
                    sleep(monitor.getWorkingTime(Thread.currentThread().getId()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(Thread.currentThread().getId() + ": Terminó mi run()");
    }
}