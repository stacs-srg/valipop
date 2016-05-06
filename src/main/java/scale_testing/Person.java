package scale_testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Person {

    public static int linePopulationCount = 10;
    private static int nextID = 0;
    public Random rand = new Random(System.nanoTime());
    public int id;
    public int yob;
    public Person spouse = null;
    public List<Person> children = new ArrayList<Person>();
    public Person(int yob) {
        id = getNextID();
        Center.index.globalIndex.put(id, this);
        this.yob = yob;

        if (nextID < linePopulationCount) {
            if (rand.nextDouble() > 0.25) {
                int diff = rand.nextInt(20) - 10;
                spouse = new Person(this, yob + diff);

                if (rand.nextDouble() > 0.1) {
                    children.add(new Person(yob + 25));

                    if (rand.nextDouble() > 0.5) {
                        children.add(new Person(yob + 30));

                        if (rand.nextDouble() > 0.7) {
                            children.add(new Person(yob + 32));
                        }
                    }

                }

                spouse.children.addAll(children);
//                print();
//                System.out.println(children.size());

                Center.complex.byYear.get(spouse.yob).malesbyNumberChildren.get(spouse.children.size()).people.put(spouse.id, spouse);
            }

        }

//        print();
        Center.complex.byYear.get(yob).malesbyNumberChildren.get(children.size()).people.put(id, this);


    }

    public Person(Person spouse, int yob) {
        id = getNextID();
        Center.index.globalIndex.put(id, this);
        if (yob < 0) {
            yob = 0;
        }
        this.yob = yob;
        this.spouse = spouse;
    }

    public static int getNextID() {
        return nextID++;
    }

    public void addChild() {
        children.add(new Person(yob));

        Center.complex.byYear.get(yob).malesbyNumberChildren.get(children.size() - 1).people.remove(id);
        Center.complex.byYear.get(yob).malesbyNumberChildren.get(children.size()).people.put(id, this);

    }

    public void print() {
        if (spouse == null) {
            System.out.print("i: " + id + "   y: " + yob + "   s: ---   c: ");
        } else {
            System.out.print("i: " + id + "   y: " + yob + "   s: " + spouse.id + "   c: ");
        }

        for (Person p :
                children) {
            System.out.print(p.id + " ");
        }
        System.out.println();
    }

}
