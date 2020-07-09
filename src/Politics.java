/**
 * @author Luna, Lihué Leandro
 * @author Coronati, Federico Joaquín
 * @author Merino, Mateo
 * @author Bonino, Francisco Ignacio
 * @since 07/07/2020
 */

import java.lang.Math;

import java.util.Random;
import java.util.ArrayList;

import Jama.Matrix;

public class Politics {
    
    //Campos privados
    private ArrayList<Double> indexes;
    private Random randomGenerator;

    //Constructor
    public Politics() {
        indexes = new ArrayList<Double>();
        randomGenerator = new Random();
    }

    //----------------------------------------Métodos públicos---------------------------------

    //----------------------------------------Otros------------------------------------------
    
    /**
     * @param and Vector con más de una transición sensibilizada.
     * @return El índice de la transición elegida para disparar.
     */
    public int decide(Matrix and) {
        indexes.clear();
        
        for(int i=0; i<and.getColumnDimension(); i++)  //Se recorre el vector AND para ver cuantas transiciones e hilos esperando hay
            if(and.get(0,i)>0) indexes.add(and.get(0,i)); //Si el elemento es mayor que 0, se agrega al arraylist indexes
        
        return (int)Math.round(indexes.get(randomGenerator.nextInt(indexes.size()-1))); //Retorna con un numero random entre las transiciones con hilo esperando que hay
    }
}