package uk.ac.standrews.cs.valipop.implementations;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DoublePrecisionTesting {

    public static void main(String[] args) {

        for(int i = 0; i <= 10; i++) {
            double sum = 0;
            double maxJ = 1E9;
            for(int j = 1; j < maxJ; j++) {
                sum += i / maxJ;
            }
            System.out.println(sum);
        }
    }
}
