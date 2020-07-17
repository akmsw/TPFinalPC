/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.concurrent.Semaphore;

import java.io.File;
import java.io.IOException;

public class Log extends Thread {

	//Campos privados
	private File f;
	private FileHandler FH;
	private Logger logger;
	private Monitor monitor;
	private ArrayList<Integer> transitionsSequence;
	private int stepToLog; //Cada ciertas transiciones checkear las memorias y demás

	/**
	 * Constructor.
	 * 
	 * @param fileName nombre del archivo log.
	 * @param monitor Monitor que controla la red de Petri.
	 */
	public Log(String fileName, Monitor monitor, int stepToLog) throws SecurityException, IOException {
		f = new File(fileName);
		
		if(!f.exists()) f.createNewFile();
		
		FH = new FileHandler(fileName,true);
		
		SimpleFormatter formatter = new SimpleFormatter();		
		
		FH.setFormatter(formatter);
		
		this.monitor = monitor;
		this.stepToLog = stepToLog;
		this.transitionsSequence = new ArrayList<Integer>();
	}

	// ----------------------------------------Overrides----------------------------------------
	
	@Override
	public void run() {
		logger = Logger.getLogger("ReportTest");
		
		logger.addHandler(FH);
		logger.setLevel(Level.INFO);

		logger.info("------------------------------------------------------------------------------");
		logger.info("START LOGGING");
		logger.info("------------------------------------------------------------------------------");

		while(!monitor.getPetriNet().hasCompleted()) {
			try {
				//monitor.getPetriNet().getLogNotifier().wait();
				wait();
			} catch(InterruptedException e) {
				//System.out.println("Error al esperar.");
			}

			monitor.getPetriNet().checkPlacesInvariants();

			transitionsSequence.add(monitor.getPetriNet().getLastFiredTransition());

			logger.info("Transición disparada: " + monitor.getPetriNet().getLastFiredTransition());

			if(monitor.getPetriNet().getTotalFired()%stepToLog==0) {
				monitor.getPetriNet().getMemoriesLoad();
				monitor.getPetriNet().getProcessorsLoad();
				monitor.getPetriNet().getProcessorsTasks();
			}
		}

		logger.info("------------------------------------------------------------------------------");
		logger.info("FINISH LOGGING");
		logger.info("------------------------------------------------------------------------------");

		for(Semaphore queue : monitor.getConditionQueues())
			if(queue.hasQueuedThreads())
				queue.release(queue.getQueueLength());
		
		if(monitor.getEntryQueue().hasQueuedThreads())
			monitor.getEntryQueue().release(monitor.getEntryQueue().getQueueLength());
	}
}