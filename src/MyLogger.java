/**
 * @author  Luna,       Lihué       Leandro
 * @author  Coronati,   Federico    Joaquín
 * @author  Merino,     Mateo
 * @author  Bonino,     Francisco   Ignacio
 * 
 * @since 03/02/2021
 */

import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import Jama.Matrix;

import java.io.File;
import java.io.IOException;

public class MyLogger extends Thread {

    //Campos privados
    private PetriNet pNet; //Red de Petri del sistema.
    private Monitor monitor; //Monitor que controla la red de Petri.
    private File f;         //v
    private FileHandler FH; //Archivos necesarios para loggear.
    private Logger logger;  //^

    /**
     * Constructor.
     * 
     * Aquí se construye el hilo logger que se encargará de mostrar en pantalla las estadísticas
     * de ejecución del programa, y de despertar a los hilos que queden encolados una vez que
     * se haya llegado a la condición de corte para evitar deadlocks.
     * 
     * @param fileName  Nombre del archivo log a crear.
     * @param pNet      Red de Petri del sistema para chequear la condición de corte del programa.
     * @param monitor   Monitor controlador del sistema
     * 
     * @throws IOException  En caso de ocurrir un error en la creación del archivo donde se escribirá la información.
     */
    public MyLogger(String fileName, PetriNet pNet, Monitor monitor) throws IOException {
        this.pNet = pNet;
        this.monitor = monitor;

        f = new File(fileName);

        if(!f.exists()) f.createNewFile();

        SimpleFormatter formatter = new SimpleFormatter();

        FH = new FileHandler(fileName, true);

        FH.setFormatter(formatter);
    }

    /**
     * El hilo logger chequea todo el tiempo la condición de corte del programa.
     * Mientras no se haya cunplido tal condición, se imprimirá en el archivo log
     * cada un segundo las estadísticas de carga de las memorias y los procesadores
     * del sistema, junto con la cantidad de tareas asignadas a cada procesador.
     * Una vez que el programa llega a su condición de corte, se imprimen en el
     * archivo log las estadísticas finales junto con el marcado final en el que
     * quedó la red de Petri.
     * Finalmente, se despiertan todos los hilos que hayan quedado esperando en
     * alguna cola de alguna transición para evitar el deadlock que esto puede causar.
     */
    @Override
    public void run() {
        logger = Logger.getLogger("ReportTest");

        logger.addHandler(FH);
        logger.setLevel(Level.INFO);

        while(!pNet.hasCompleted()) {
            try {
                sleep(1000);
                logger.info("\n" + pNet.getMemoriesLoad() + 
                            "\n" + pNet.getProcessorsLoad() + 
                            "\n" + pNet.getProcessorsTasks());
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info("\n" + pNet.getMemoriesLoad() + 
                    "\n" + pNet.getProcessorsLoad() + 
                    "\n" + pNet.getProcessorsTasks());

        Matrix finalMarkingVector = pNet.getCurrentMarkingVector();

        String finalMarking = "[ ";

        for(int i = 0; i < finalMarkingVector.getColumnDimension(); i++)
            finalMarking += (int)finalMarkingVector.get(0, i) + " ";

        finalMarking += "]";        

		for(Semaphore queue : monitor.getConditionQueues().getSemaphore())
            if(queue.hasQueuedThreads())
                queue.release(queue.getQueueLength());
        
        if(monitor.getEntryQueue().hasQueuedThreads()) //Chequeo de hilos encolados en ArrivalRate.
            monitor.getEntryQueue().release(monitor.getEntryQueue().getQueueLength());
        
        logger.info("Secuencia de transiciones disparadas: \"" + pNet.getTransitionsSequence().toString() + "\"");
        logger.info("Marcado final de la red: " + finalMarking);
    }
}