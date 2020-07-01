/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 01/07/2020
 */

import java.util.concurrent.*;
import java.util.ArrayList;

public class Monitor {

    //Private class fields
    private ArrayList<Semaphore> conditionQueues;
    private PetriNet pNet;

    //Constructor
    public Monitor(PetriNet pNet) {
        this.pNet = pNet;
        //TODO
    }
}