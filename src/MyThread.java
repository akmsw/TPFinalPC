/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 07/07/2020
 */

import Jama.Matrix;

public class MyThread implements Runnable {

    //Private class fields
    private Matrix firingVector;
    private Monitor monitor;

    //Constructor
    /**
     * @param firingVector Vector de transiciones asociadas al hilo a crear.
     * @param monitor Referencia al monitor que controla la red de Petri.
     */
    public MyThread(Matrix firingVector, Monitor monitor) {
        this.firingVector = firingVector;
        this.monitor = monitor;
    }

    public void run() {
        

    }
}