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

import Jama.Matrix;

public class Log extends Thread {

	//Campos privados
	private File f;
	private FileHandler FH;
	private Logger logger;
	private Monitor monitor;
	private ArrayList<String> transitionsSequence;
	private int stepToLog; //Cada ciertas transiciones checkear las memorias y demás
	private Object lock;

	/**
	 * Constructor.
	 * 
	 * @param fileName nombre del archivo log.
	 * @param monitor Monitor que controla la red de Petri.
	 * @param stepToLog Paso que se utilizará para escribir en el log
	 * 					(cada cuántas transiciones disparadas escribiremos).
	 * @param lock Lock que interactúa entre el Log y el hilo que disparó.
	 */
	public Log(String fileName, Monitor monitor, int stepToLog, Object lock) throws SecurityException, IOException {
		f = new File(fileName);
		
		if(!f.exists()) f.createNewFile();
		
		FH = new FileHandler(fileName,true);
		
		SimpleFormatter formatter = new SimpleFormatter();		
		
		FH.setFormatter(formatter);
		
		this.monitor = monitor;
		this.stepToLog = stepToLog;
		this.transitionsSequence = new ArrayList<String>();
		this.lock = lock;
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

		Matrix initialMark, currentMark, aux;
		boolean transitionInvariant;

		initialMark = monitor.getPetriNet().getInitialMarkingVector();

		while(!monitor.getPetriNet().hasCompleted()) {
			transitionInvariant = true;
			
			synchronized(lock) {
				try {
					//System.out.println("EL LOG ESTÁ ESPERANDO");
					lock.wait();
				} catch(InterruptedException e) {
					System.out.println("Error al esperar.");
				}
			}

			currentMark = monitor.getPetriNet().getCurrentMarkingVector();

			aux = initialMark.minus(currentMark);

			for(int i=0; i<aux.getColumnDimension(); i++)
				if(aux.get(0, i)!=0) {
					transitionInvariant = false;
					break;
				}
			
			if(transitionInvariant)
				System.out.println("***********************************************************************************************************************************");
			
			//monitor.getPetriNet().checkPlacesInvariants();
			transitionsSequence.add(monitor.getPetriNet().getLastFiredTransition() + "");

			//logger.info("Transición disparada: T" + monitor.getPetriNet().getLastFiredTransition());

			if(monitor.getPetriNet().getTotalFired()%stepToLog==0) {
				logger.info("\n"+monitor.getPetriNet().getMemoriesLoad() + "\n" + monitor.getPetriNet().getProcessorsLoad() + "\n" + monitor.getPetriNet().getProcessorsTasks() );	
			}

			synchronized(lock) {
				lock.notify();
			}
		}
		
		logger.info("Se completaron exitosamente 1000 tareas." + 
					"\n" + monitor.getPetriNet().getMemoriesLoad() + 
					"\n" + monitor.getPetriNet().getProcessorsLoad() + 
					"\n" + monitor.getPetriNet().getProcessorsTasks() +
					"\nSe dispararon "+ transitionsSequence.size() + " transiciones.");
		
		logger.info(transitionsSequence.toString());

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