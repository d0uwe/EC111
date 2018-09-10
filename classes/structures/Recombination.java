package structures;

import java.util.Comparator;
import java.util.Random;
import java.util.ArrayList;

import structures.Unit;

public class Recombination {
    // double[] values;
    // double fitness = -1;
    // int size = 10;

    public Recombination() {
    }


    /**
     * Do crossover between parents
     * @param population parent populations
     * @param min_split minimum amount of elements from one parents
     * @param max_split maximum amount of elements from one parents
     * @param rand Random generator
     * @param pop_size desired population size
     * @return the new population
     */
    private ArrayList<Unit> cross_over(ArrayList<Unit> population, int min_split, int max_split, Random rand, int max_pop_size) {
        int curr_pop_size = population.size();
        for (int i = 0; i < max_pop_size - curr_pop_size; i++) {
            int split = rand.nextInt(max_split - min_split) + min_split;
            int p1 = rand.nextInt(curr_pop_size);
            int p2 = rand.nextInt(curr_pop_size);

            Unit child = new Unit(10);

            // getSize n keer aanroepen, what would tim doolan do
            for (int j = 0; j < population.get(0).getSize(); j++) {
                if (j < split) {
                    child.setValue(j, population.get(p1).getValue(j));
                } else {
                    child.setValue(j, population.get(p2).getValue(j));
                }
            }
            population.add(child);
        }
        return population;
    }
}