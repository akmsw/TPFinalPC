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
    private ArrayList<Integer> indexes;
    private Random randomGenerator;

    //Constructor
    public Politics() {
        indexes = new ArrayList<Integer>();
        randomGenerator = new Random();
    }

    //----------------------------------------Métodos públicos---------------------------------

    //----------------------------------------Otros--------------------------------------------
    
    /**
     * @param and Vector con más de una transición sensibilizada.
     * @return El índice de la transición elegida para disparar.
     */
    public int decide(Matrix and) {
        System.out.println("Politics Deciding...");
        indexes.clear();
        
        for(int i=0; i<and.getColumnDimension(); i++)  //Se recorre el vector AND para ver cuántas transiciones e hilos esperando hay.
            if(and.get(0,i)>0) indexes.add(i); //Si el elemento es mayor que 0, se agrega al arraylist indexes.
        
        int choice = (int)Math.round(randomGenerator.nextInt(indexes.size()-1)); //Se elige aleatoriamente entre las transiciones sensibilizadas, con distribucion uniforme
        int indexChosen = (int)Math.round(indexes.get(choice)); //Muy importante hacer el parseval porque la matrix tiene double

        System.out.println("Decided: " + indexChosen);
        return indexChosen; //Retorna con un numero random entre las transiciones con hilo esperando que hay.
    }
}