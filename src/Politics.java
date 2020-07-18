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
//import java.util.Collections;

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

    // ----------------------------------------Métodos públicos---------------------------------

    // ----------------------------------------Otros--------------------------------------------
    
    /**
     * @param and Vector con más de una transición sensibilizada que tienen
     *            al menos un hilo encolado.
     * @return El índice de la transición elegida para disparar.
     */
    public int decide(Matrix and) {
        //System.out.println("Politics Deciding...");
        
        indexes.clear();
        
        //and.print(0, 0);
        
        for(int i=0; i<and.getColumnDimension(); i++)  //Se recorre el vector AND para ver cuántas transiciones e hilos esperando hay.
            if(and.get(0,i)>0) indexes.add(i); //Si el elemento es mayor que 0, se agrega al arraylist indexes.
        
        //int indexChosen;
        
        /*if(indexes.contains(15) && indexes.contains(16)) {
            if((int)Math.round(randomGenerator.nextInt(2))==1) indexChosen = 16;
            else indexChosen = 15;
        } else if(indexes.contains(9) && indexes.contains(10)) {
            if((int)Math.round(randomGenerator.nextInt(2))==1) indexChosen = 10;
            else indexChosen = 9;
        } else if(indexes.contains(11) && indexes.contains(12)) {
            if((int)Math.round(randomGenerator.nextInt(2))==1) indexChosen = 12;
            else indexChosen = 11;
        } else if(indexes.contains(1) && indexes.contains(2)) {
            if((int)Math.round(randomGenerator.nextInt(2))==1) indexChosen = 2;
            else indexChosen = 1;
        } else {
            indexChosen = Collections.max(indexes);
        }*/

       int choice = (int)Math.round(randomGenerator.nextInt(indexes.size())); //Se elige aleatoriamente entre las transiciones sensibilizadas, con distribucion uniforme
       int indexChosen = (int)Math.round(indexes.get(choice)); //Muy importante hacer el parseval porque la matrix tiene double

        //System.out.println("Decided: " + indexChosen);
        
        return indexChosen; //Retorna con un numero random entre las transiciones con hilo esperando que hay.
    }
}