/**
 * @author  Luna,       Lihué       Leandro
 * @author  Coronati,   Federico    Joaquín
 * @author  Merino,     Mateo		Marcelo
 * @author  Bonino,     Francisco   Ignacio
 * 
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

	//Campos privados.
	private Object lock;
	private int stepToLog;
	private ArrayList<String> transitionsSequence;
	private File f;
	private FileHandler FH;
	private Logger logger;
	private Monitor monitor;

	/**
	 * Constructor.
	 * 
	 * @param	fileName 	El nombre del archivo log.
	 * @param	monitor		El monitor que controla la red de Petri.
	 * @param	stepToLog	El paso que se utilizará para escribir en el log
	 * 						(cada cuántas transiciones disparadas escribiremos).
	 * @param	lock		El lock que interactúa entre el Log y el hilo que disparó.
	 * @param	tInvariants Los invariantes de transición de la red.
	 * 
	 * @throws	IOException Si hubo un error al crear el archivo log.
	 */
	public Log(String fileName, Monitor monitor, int stepToLog, Object lock, Matrix tInvariants) throws IOException {
		this.monitor = monitor;
		this.stepToLog = stepToLog;
		this.lock = lock;

		transitionsSequence = new ArrayList<String>();

		f = new File(fileName);
		
		if(!f.exists()) f.createNewFile();
		
		SimpleFormatter formatter = new SimpleFormatter();		

		FH = new FileHandler(fileName, true);
		
		FH.setFormatter(formatter);
	}

	// ----------------------------------------Métodos públicos---------------------------------

	// ----------------------------------------Overrides----------------------------------------
	
	/**
	 * Este método hace que el objeto Log escriba en un archivo .txt información importante
	 * sobre la ejecución del programa. Esta información consta de la transición disparada,
	 * la carga de las memorias, la carga de los procesadores y la ejecución de las tareas
	 * en los procesadores. Esto se escribe cada 'stepToLog' transiciones disparadas y mientras
	 * no se haya llegado a la condición de corte del programa.
	 * Además, se almacena en un arreglo cada transición disparada por cada hilo para luego
	 * imprimirla en el archivo .txt y poder analizar en él los invariantes de transición al finalizar.
	 * Por cada transición disparada y escrita, se chequea que se cumplan los invariantes
	 * de plaza de la red.
	 */
	@Override
	public void run() {
		logger = Logger.getLogger("ReportTest");
		
		logger.addHandler(FH);
		logger.setLevel(Level.INFO);

		logger.info("------------------------------------------------------------------------------" + 
					"\nSTART LOGGING" + 
					"\n------------------------------------------------------------------------------");

		int transitionInvariantsAmount = 0;

		while(!monitor.getPetriNet().hasCompleted()) {
			synchronized(lock) {
				try {
					lock.wait();
				} catch(InterruptedException e) {
					e.printStackTrace();
					System.out.println("Error en espera en el log.");
				}
			}
			
			transitionsSequence.add("T" + monitor.getPetriNet().getLastFiredTransition() + "");
			
			monitor.getPetriNet().checkPlacesInvariants();

			if(monitor.getPetriNet().getTotalFired() % stepToLog == 0)
				logger.info("\n" + monitor.getPetriNet().getMemoriesLoad() + 
							"\n" + monitor.getPetriNet().getProcessorsLoad() + 
							"\n" + monitor.getPetriNet().getProcessorsTasks() );	

			synchronized(lock) {
				lock.notify();
			}
		}

		Matrix finalMarkingVector = monitor.getPetriNet().getCurrentMarkingVector();
		
		String finalMarking = "[ ";

		for(int i = 0; i < finalMarkingVector.getColumnDimension(); i++)
			finalMarking += (int)finalMarkingVector.get(0, i) + " ";
		
		finalMarking += "]";
		
		logger.info("Se completaron exitosamente " + monitor.getPetriNet().getStopCondition() + " tareas." + 
					"\n" + monitor.getPetriNet().getMemoriesLoad() + 
					"\n" + monitor.getPetriNet().getProcessorsLoad() + 
					"\n" + monitor.getPetriNet().getProcessorsTasks() + 
					"\nSe dispararon "+ (transitionsSequence.size()-transitionInvariantsAmount) + " transiciones.");
		transitionsSequence.add("");
		logger.info(transitionsSequence.toString());

		logger.info("Marcado final:\n" + finalMarking + "\n" + 
					"------------------------------------------------------------------------------" + 
					"\nFINISH LOGGING" + 
					"\n------------------------------------------------------------------------------");

		//Recorrermos las colas de la red de Petri para despertar y terminar la ejecución de los hilos que hayan quedado durmiendo.
		for(Semaphore queue : monitor.getConditionQueues().getSemaphore())
			if(queue.hasQueuedThreads())
				queue.release(queue.getQueueLength());
		
		if(monitor.getEntryQueue().hasQueuedThreads()) //Para ArrivalRate
			monitor.getEntryQueue().release(monitor.getEntryQueue().getQueueLength());
	}
}