import java.util.concurrent.Semaphore;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import Jama.Matrix;

import java.io.File;
import java.io.IOException;

public class MyLogger extends Thread {
    PetriNet pNet;
    Monitor monitor;
    private File f;
    private FileHandler FH;
    private Logger logger;

    public MyLogger(String fileName, PetriNet pNet, Monitor monitor) throws IOException {
        this.pNet = pNet;
        this.monitor = monitor;

        f = new File(fileName);

        if (!f.exists())
            f.createNewFile();

        SimpleFormatter formatter = new SimpleFormatter();

        FH = new FileHandler(fileName, true);

        FH.setFormatter(formatter);
    }

    @Override
    public void run() {
        logger = Logger.getLogger("ReportTest");

        logger.addHandler(FH);
        logger.setLevel(Level.INFO);

        while (!pNet.hasCompleted()) {
            try {
                sleep(1000);
                logger.info("\n" +pNet.getMemoriesLoad() + 
                "\n" +pNet.getProcessorsLoad() + 
                "\n" +pNet.getProcessorsTasks());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        logger.info("\n" +pNet.getMemoriesLoad() + 
                "\n" +pNet.getProcessorsLoad() + 
                "\n" +pNet.getProcessorsTasks());

        Matrix finalMarkingVector = monitor.getPetriNet().getCurrentMarkingVector();

        String finalMarking = "[ ";

        for(int i = 0; i < finalMarkingVector.getColumnDimension(); i++)
            finalMarking += (int)finalMarkingVector.get(0, i) + " ";

        finalMarking += "]";

        logger.info(finalMarking);

        //Recorrermos las colas de la red de Petri para despertar y terminar la ejecución de los hilos que hayan quedado durmiendo.
		for(Semaphore queue : monitor.getConditionQueues().getSemaphore())
            if(queue.hasQueuedThreads())
                queue.release(queue.getQueueLength());
        
        if(monitor.getEntryQueue().hasQueuedThreads()) //Para ArrivalRate
            monitor.getEntryQueue().release(monitor.getEntryQueue().getQueueLength());
        
        logger.info(pNet.getTransitionsSequence().toString());
    }
}