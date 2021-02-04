/**
 * @author  Luna,       Lihué       Leandro
 * @author  Coronati,   Federico    Joaquín
 * @author  Merino,     Mateo
 * @author  Bonino,     Francisco   Ignacio
 * 
 * @since 01/08/2020
 */

import Jama.Matrix;

public class MarkingTest {

    private static double[][] incidenceArray = { //Matriz de incidencia de la red de Petri.
        {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 1,-1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0,-1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0,-1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
        { 0, 0, 0,-1,-1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0 },
        { 0, 0, 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 0, 0, 0, 1, 0, 0 },
        { 0, 0, 0, 0, 0, 1, 0, 1, 0,-1,-1, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0,-1,-1, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0,-1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0,-1 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0,-1, 0,-1, 0, 0, 0, 1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,-1, 0,-1, 0, 0, 0, 1 }
    };

    private static double[] iMark = { 1, 0, 0, 4, 0, 4, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8 }; //Marcado inicial de la red de Petri.

    /**
     * En esta clase se testeará si la ejecución del progarma fue correcta.
     * El arreglo 'leftT' es un arreglo que almacenará la cantidad de veces a disparar
     * cada transición en base al arreglo 'leftTransitions'.
     * La cadena 'leftTransitions' es el resultado de correr el archivo 'TestInvariant.py'
     * con la secuencia de transiciones disparadas durante la ejecución del programa
     * almacenada en el archivo log. Este arreglo contiene los números de las transiciones a disparar.
     * Se recorre la cadena 'leftTransitions' y se cuenta cuántas veces aparece cada número (transición)
     * y se sobrescribe la posición correspondiente a tal transición en el arreglo 'lefT' con el número
     * de veces que ésta aparece en la cadena recorrida.
     * Una vez que se arma el arreglo 'lefT', se resuelve la ecuación M(i+1) = Mi + I*F, siendo
     * Mi: El marcado actual de la red (en este caso, el inicial),
     * M(i+1): El marcado siguiente de la red.
     * I: La matriz de incidencia de la red.
     * F: Vector de disparo (lefT).
     * Luego de resolver esta ecuación debe verificarse que el resultado de la misma coincida con el
     * marcado final de la red impreso en el archivo log.
     */
    public static void main(String[] args) {
        Matrix incidence = new Matrix(incidenceArray);
        Matrix initialMarking = new Matrix(iMark, 1);

        double[] leftT = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        String leftTransitions = "2,0,0,2,0,1,0,2,0,1,0,2,0,1,0,4,1,0,14,8,";
        
        String aux = "";

        for(int i = 0; i < leftTransitions.length(); i++) {
            if(leftTransitions.charAt(i) != ',') {
                aux = aux + leftTransitions.charAt(i);
            } else {
                int index = Integer.valueOf(aux);
                leftT[index]++;
                aux = "";
            }
        }
        
        Matrix firing = new Matrix(leftT, 1);

        firing.print(0, 0);

        Matrix result = (initialMarking.transpose().plus(incidence.times(firing.transpose()))).transpose();

        result.print(0, 0);
    }
}