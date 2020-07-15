
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
    public MyThread(Matrix myTransitions, Monitor monitor) {
        this.myTransitions = myTransitions;
        this.monitor = monitor;
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
    public Matrix getAssociatedTransitions() {
        return myTransitions;
    }

    // ----------------------------------------Overrides----------------------------------------

    @Override
    public void run() {
        int i = 0;
       
        while(monitor.getPetriNet().getTransitionsFired()<10) { //El run termina luego de N transiciones exitosas entre todos los hilos en conjunto
            if(myTransitions.get(0,i)>0) {
                firingVector = getTransitionVector(i);
                
                try {
                    System.out.println(Thread.currentThread().getId() + ": Intento catchear el monitor");
                    monitor.catchMonitor();
                } catch(InterruptedException e) {
                    System.out.println(Thread.currentThread().getId() + ": Guacho hay bardo para entrar al monitor");
                }

                System.out.println(Thread.currentThread().getId() + ": Voy a disparar. fV:");
            
                firingVector.print(0,0);

                monitor.tryFiring(firingVector);

                //TODO: BORRAR ESTE SLEEP.
                System.out.println(Thread.currentThread().getId() +": Sleeping 2 seconds");
                try {
                    java.lang.Thread.sleep(2000); //Cada 2 segundos vuelvo a ejecutar el run().
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            i++;
            
            if(i>=myTransitions.getColumnDimension()) i=0; //la dimension de myTransitions deberia ser 19
        }

        System.out.println(Thread.currentThread().getId() +": Terminó mi run()");
    }

    // ----------------------------------------Otros------------------------------------------

    /**
     * @param i Índice de la transición que se quiere obtener.
     * @return Vector con ceros y un '1' en la i-ésima transición.
     */
    public Matrix getTransitionVector(int i) {
        Matrix vector = new Matrix(1,monitor.getPetriNet().getIncidenceMatrix().getColumnDimension());
        
        vector.set(0,i,1);
        
        return vector;
    }
}