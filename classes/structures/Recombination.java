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
     * Do crossover between parents
     * @param population parent populations
     * @param min_split minimum amount of elements from one parents
     * @param max_split maximum amount of elements from one parents
     * @param rand Random generator
     * @param pop_size desired population size
     * @return the new population
     */
    public ArrayList<Unit> cross_over(ArrayList<Unit> population, int min_split, int max_split, Random rand, int max_pop_size) {
        int curr_pop_size = population.size();
        for (int i = 0; i < max_pop_size - curr_pop_size; i++) {
            int split = rand.nextInt(max_split - min_split) + min_split;

            // Get two parents from the population (at random)
            Unit p1 = population.get(rand.nextInt(curr_pop_size));
            Unit p2 = population.get(rand.nextInt(curr_pop_size));

            // Create a new child, set its mutation mode to the p1 (?)
            Unit child = new Unit(p1.mutate_mode, Params.gene_length);

            for (int j = 0; j < Params.gene_length; j++) {
                if (j < split) {
                    child.setValue(j, p1.getValue(j));
                } else {
                    child.setValue(j, p2.getValue(j));
                }
            }
            population.add(child);
        }
        return population;
    }

    /**
     * Do recombination, assuming gene pool is filled with gaussian mutators.
     * @param p1 parent 1
     * @param p2 parent 2
     * @param rand Random generator
     *
     */
     public ArrayList<Unit> cross_over_gaussian(ArrayList<Unit> population, int min_split, int max_split, Random rand, int max_pop_size) {
        int curr_pop_size = population.size();
        for (int i = 0; i < max_pop_size - curr_pop_size; i++) {
            int split = rand.nextInt(max_split - min_split) + min_split;

            Unit p1 = population.get(rand.nextInt(curr_pop_size));
            Unit p2 = population.get(rand.nextInt(curr_pop_size));

            Unit child = new Unit(p1.mutate_mode, Params.gene_length);

            // The actual cross over part
            for (int j = 0; j < Params.gene_length; j++) {
                if (j < split) {
                    child.setValue(j, p1.getValue(j));
                } else {
                    child.setValue(j, p2.getValue(j));
                }
            }


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
}

