/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 07/07/2020
 */

import Jama.Matrix;

public class MyThread extends Thread {

    //Campos privados
    private Matrix firingVector;    //Este vector indica la transicion que se disparará o que se intentó disparar. FJC
    private Matrix myTransitions;   //Este vector tiene las transiciones que puede disparar el hilo. FJC
    private Monitor monitor;

    /**
	 * Constructor.
	 * 
     * @param firingVector Vector de transiciones asociadas al hilo.
     * @param monitor Referencia al monitor que controla la red de Petri.
     */
    public MyThread(Matrix firingVector, Monitor monitor) {
        this.firingVector = firingVector;
        this.monitor = monitor;
    }

    //----------------------------------------Métodos públicos---------------------------------

    //----------------------------------------Getters------------------------------------------

    /**
     * @return El vector de transiciones a disparar.
     */
    public Matrix getFiringVector() {
        return firingVector;
    }

    /**
     * @return El vector de transiciones asociadas al hilo.
     */
    public Matrix getAssociatedTransitions() {
        return myTransitions;
    }

    //----------------------------------------Setters------------------------------------------

    /**
     * @param myTransitions El vector de transiciones asociadas a este hilo.
     */
    public void setAssociatedTransitions(Matrix myTransitions) {
        this.myTransitions = myTransitions;
    }

    //----------------------------------------Overrides------------------------------------------

    @Override
    public void run() {
        while(monitor.getTransitionsFired()<20) {
            try {
                monitor.catchMonitor();
            } catch(InterruptedException e) {
                System.out.println("Guacho hay bardo para entrar al monitor");
            }

            //Pal general case: elegir transition para firing (and & election)
            
            monitor.tryFiring(firingVector);
}