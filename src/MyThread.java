
/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 07/07/2020
 */

import Jama.Matrix;

public class MyThread extends Thread {

    // Campos privados
    private Matrix firingVector; // Este vector indica la transicion que se disparará o que se intentó disparar. FJC
    private Matrix myTransitions; // Este vector tiene las transiciones que puede disparar el hilo. FJC
    private Monitor monitor;

    /**
     * Constructor.
     * 
     * @param firingVector Vector de transiciones asociadas al hilo.
     * @param monitor      Referencia al monitor que controla la red de Petri.
     */
    public MyThread(Matrix firingVector, Monitor monitor) {
        this.firingVector = firingVector;
        this.monitor = monitor;
    }

    // ----------------------------------------Métodos
    // públicos---------------------------------

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
    public Matrix getAssociatedTransitions() {
        return myTransitions;
    }

    // ----------------------------------------Setters------------------------------------------

    /**
     * @param myTransitions El vector de transiciones asociadas a este hilo.
     */
    public void setAssociatedTransitions(Matrix myTransitions) {
        this.myTransitions = myTransitions;
    }

    // ----------------------------------------Overrides------------------------------------------

    @Override
    public void run() {
        while (monitor.getTransitionsFired() < 20) {
            try {
                System.out.println(Thread.currentThread().getId() + ": Intento catchear el monitor");
                monitor.catchMonitor();
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getId() + ": Guacho hay bardo para entrar al monitor");
            }

            // Pal general case: elegir transition para firing (and & election)
            System.out.println(Thread.currentThread().getId() + ": voy a disparar ");
            firingVector.print(0,0);
            monitor.tryFiring(firingVector);

            //TODO: BORRAR ESTE SLEEP.
            try {
                java.lang.Thread.sleep(2000); //Cada 2 segundos vuelvo a ejecutar el run().
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}