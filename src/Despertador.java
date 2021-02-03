import java.util.concurrent.Semaphore;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import java.io.File;
import java.io.IOException;

public class Despertador extends Thread {
    PetriNet pNet;
    Monitor monitor;
    private File f;
	private FileHandler FH;
	private Logger logger;

    public Despertador(String fileName, PetriNet pNet, Monitor monitor) throws IOException {
        this.pNet = pNet;
        this.monitor = monitor;

        f = new File(fileName);
		
		if(!f.exists()) f.createNewFile();
		
		SimpleFormatter formatter = new SimpleFormatter();

		FH = new FileHandler(fileName, true);
		
		FH.setFormatter(formatter);
    }

    @Override
    public void run() {
        logger = Logger.getLogger("ReportTest");
		
		logger.addHandler(FH);
        logger.setLevel(Level.INFO);
        
        while(!pNet.hasCompleted()) { }

        System.out.println("UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");

        //Recorrermos las colas de la red de Petri para despertar y terminar la ejecución de los hilos que hayan quedado durmiendo.
		for(Semaphore queue : monitor.getConditionQueues().getSemaphore())
            if(queue.hasQueuedThreads())
                queue.release(queue.getQueueLength());
        
        if(monitor.getEntryQueue().hasQueuedThreads()) //Para ArrivalRate
            monitor.getEntryQueue().release(monitor.getEntryQueue().getQueueLength());
        
        System.out.println("DESPERTÉ A TOOELMUNDOOO -- fin del programa");

        logger.info(pNet.getTransitionsSequence().toString());
    }
}