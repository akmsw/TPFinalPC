/**
 * @author  Luna,       Lihué       Leandro
 * @author  Coronati,   Federico    Joaquín
 * @author  Merino,     Mateo
 * @author  Bonino,     Francisco   Ignacio
 * 
 * @since 07/07/2020
 */

import java.lang.Math;

import java.util.Random;
import java.util.ArrayList;

import Jama.Matrix;

public class Policy {
    
    //Campos privados.
    private ArrayList<Integer> indexes;
    private Random randomGenerator;

    //Constructor.
    public Policy() {
        indexes = new ArrayList<Integer>();
        randomGenerator = new Random();
    }

    // ----------------------------------------Métodos públicos---------------------------------
    
    /**
     * En este método se decide qué transición será elegida para despertar a los hilos
     * que tenga encolados. Para esto, se recorre el vector resultado de la operación 'AND'
     * para ver cuántas transiciones con hilos esperando hay.
     * Se almacena el índice de estas transiciones en un arreglo y se hace una
     * elección aleatoria con distribución uniforme entre todos los índices que se hayan guardado.
     * 
     * @param and El vector con más de una transición sensibilizada que tienen al menos un hilo encolado.
     * 
     * @return El índice de la transición elegida para disparar.
     */
    public int decide(Matrix and) {
        indexes.clear();
        
        for(int i = 0; i < and.getColumnDimension(); i++)
            if(and.get(0, i) > 0) indexes.add(i);
        
        int choice = (int)Math.round(randomGenerator.nextInt(indexes.size()));
        int indexChosen = (int)Math.round(indexes.get(choice));

        return indexChosen;
    }
}