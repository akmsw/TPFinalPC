/**
 * @author  Luna,       Lihué       Leandro
 * @author  Coronati,   Federico    Joaquín
 * @author  Merino,     Mateo
 * @author  Bonino,     Francisco   Ignacio
 * 
 * @since 01/08/2020
 */

import Jama.*;

public class MarkingTest {

    private static double[][] incidenceArray = {
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

    private static double[] iMark = { 1, 0, 0, 4, 0, 4, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8 };

    public static void main(String[] args) {
        Matrix incidence = new Matrix(incidenceArray);
        Matrix initialMarking = new Matrix(iMark, 1);

        double[] leftT = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        String leftTransitions = "0,2,0,1,0,2,0,1,0,2,0,1,0,2,0,3,1,0,5,";
        
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