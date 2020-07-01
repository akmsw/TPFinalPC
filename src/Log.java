/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import java.io.File;
import java.io.IOException;

public class Log {

	//Private class fields
	private File f;
	private FileHandler FH;
	private Logger logger;

	//Constructor
	/**
	 * @param fileName The name of the log file.
	 */
	public Log(String fileName) throws SecurityException, IOException {
		f = new File(fileName);
		
		if(!f.exists()) {
			f.createNewFile();
		}
		
		FH = new FileHandler(fileName,true);
		
		SimpleFormatter formatter = new SimpleFormatter();
		
		FH.setFormatter(formatter);
	}

	//----------------------------------------Public Methods----------------------------------------

	public void writeLog() {
		logger = Logger.getLogger("ReportTest");
		
		logger.addHandler(FH);
		logger.setLevel(Level.INFO);

		//Message shown before ending program.
		logger.info("------------------------------------------------------------------------------");
		logger.info("END OF LOG: ");
		logger.info("------------------------------------------------------------------------------");

		System.out.println("");
	}
}