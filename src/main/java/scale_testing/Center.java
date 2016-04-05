package scale_testing;

import java.util.HashMap;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Center {

    public static GlobalIndex index = new GlobalIndex();


    public static BucketComplex complex = new BucketComplex();

    public static int numberOfLines = 500000;

    public static void main(String[] args) {

        for(int i = 0; i < numberOfLines; i++) {
            new Person(0);
            Person.linePopulationCount += 10;
        }



        for(Integer i : index.globalIndex.keySet()) {
            index.globalIndex.get(i).print();
        }

        System.out.println();
        System.out.println();
        System.out.println();


        Person p = index.globalIndex.get(95);
        p.print();

        Person p2 = complex.byYear.get(p.yob).malesbyNumberChildren.get(p.children.size()).people.get(p.id);
        p2.print();

        System.out.println(p == p2);

        p.addChild();

        p.print();
        p2.print();

        Person p3 = index.globalIndex.get(95);
        p.print();

        Person p4 = complex.byYear.get(p.yob).malesbyNumberChildren.get(p.children.size()).people.get(p.id);
        p2.print();

    }


}
