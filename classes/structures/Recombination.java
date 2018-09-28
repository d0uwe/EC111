package structures;

import java.util.Comparator;
import java.util.Random;
import java.util.ArrayList;
import structures.Params;
import structures.Unit;

public class Recombination {
    // double[] values;
    // double fitness = -1;
    // int size = 10;

    public Recombination() {
    }


    /**
     * Do recombination, assuming gene pool is filled with gaussian mutators.
     * @param p1 parent 1
     * @param p2 parent 2
     * @param rand Random generator
     *
     */
     public ArrayList<Unit> recombination(ArrayList<Unit> population, int min_split, int max_split, Random rand, int max_pop_size) {
        int curr_pop_size = population.size();
        for (int i = 0; i < max_pop_size - curr_pop_size; i++) {
            int split = rand.nextInt(max_split - min_split) + min_split;

            Unit p1 = population.get(rand.nextInt(curr_pop_size));
            Unit p2 = population.get(rand.nextInt(curr_pop_size));

            Unit child = cross_over(p1, p2, split);


            // Keep the sigmas simple; just take the better fitness one
            Unit better_fitness;
            if (p1.compareTo(p2) < 0) { better_fitness = p1; } else { better_fitness = p2; }
            boolean is_single_sigma = better_fitness.mutate_mode == Params.mutate_mode ? true : false;

            for (int j = 0; j < Params.gene_length; j++) {
                child.setSigma(j, better_fitness.getSigma(j));
                if (is_single_sigma) { break; }
            }

            population.add(child);
        }
        return population;
    }

    public Unit cross_over(Unit p1, Unit p2, int split) {
        Unit child = new Unit(p1.mutate_mode, Params.gene_length);
        for (int i = 0; i < Params.gene_length; i++) {
            if (i < split) {
                child.setValue(i, p1.getValue(i));
            } else {
                child.setValue(i, p2.getValue(i));
            }
        }
        return child;
    }
}

