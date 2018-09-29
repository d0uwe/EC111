import org.vu.contest.ContestSubmission;

import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import structures.Population;
import structures.Unit;
import structures.Recombination;
import structures.Selection;
import structures.Mutation;
import structures.Params;


public class player111 implements ContestSubmission {
    Random rnd_;
    ContestEvaluation evaluation_;
    private int evaluations_limit_;

    public player111() {
        rnd_ = new Random();
    }

    public void setSeed(long seed) {
        // Set seed of algortihms random process
        rnd_.setSeed(seed);
    }

    public void setEvaluation(ContestEvaluation evaluation) {
        // Set evaluation problem used in the run
        evaluation_ = evaluation;

        // Get evaluation properties
        Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        // evaluations_limit_ = 500;
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        // Do sth with property values, e.g. specify relevant settings of your algorithm
        if (isMultimodal) {
            // Do sth
        } else {
            // Do sth else
        }
    }

    // private ArrayList<Unit> init_population(int pop_size, Random rand) {
    //     ArrayList<Unit> population = new ArrayList<Unit>();
    //     for (int i = 0; i < pop_size; i++) {
    //         Unit child = new Unit(Params.mutate_mode, 10);
    //         for (int j = 0; j < 10; j++) {
    //             child.setValue(j, ((rand.nextDouble() - 0.5) * 10));
    //         }
    //     child.setFitness((double) evaluation_.evaluate(child.getValues()));
    //         // evals++;
    //         // if (evals >= evaluations_limit_) {
    //         //     break;
    //         // }
    //         population.add(child);
    //     }
    //     return population;
    // }


    /**
     * Mutates parents with a certain amount of mutations.
     * @param population population to mutate on
     * @param min_muts minimum amount of mutations, inclusive
     * @param max_muts maximum amount of mutations, exclusive
     * @param rand random generator
     * @param pop_size desired population size
     * @return new population
    private ArrayList<Unit> mutate(ArrayList<Unit> population, int pop_size, Mutation mutator, Random rand) {
        int current_pop_size = population.size();

        // Figure out how much you need to fill into the population
        int mutation_growth = (pop_size - current_pop_size) / 2;

        for (int i = 0; i < mutation_growth; i++) {
            //generate random number between 0 and current_pop_size
            Unit mutated_child = mutator.mutate_uniform(population.get(rand.nextInt(current_pop_size)));
            population.add(mutated_child);
        }
        return population;
    }
     */


    public void run() {
        if (System.getProperty("debug") != null) {
            Params.debug = Boolean.parseBoolean(System.getProperty("debug"));
        }

        int evals = 0;
        int pop_size = Params.pop_size;
        int min_split = Params.min_split;
        int max_split = Params.max_split;

        assert pop_size <= evaluations_limit_;

        Population population = new Population(pop_size, rnd_);
        // ArrayList<Unit> population = init_population(pop_size, rnd_);

        // Init_population does one evaluate for every unit in the population
        // There are guaranteed enough evals left for this (see assert)
        //evals += pop_size; // Except that we dont actually do this at all

        int n_survivors = Params.n_survivors;

        Selection selection = new Selection();
        Mutation mutate = new Mutation();
        Recombination recombination = new Recombination();


        // And then we do it for the whole population
        while (evals < evaluations_limit_) {
            selection.tournament_selection(population, Params.tournament_size, rnd_);
            //selection.select_survivors(population);
            recombination.recombination(population, min_split, max_split, rnd_);
            mutate.mutate_gaussian_single(population, pop_size, rnd_);

            int curr_pop_size = population.size();

            for (int i = n_survivors; i < curr_pop_size; i++) {
                double new_fitness = (double) evaluation_.evaluate(population.get(i).getValues());
                population.getPopulation().get(i).setFitness(new_fitness);
                evals++;
                if (evals >= evaluations_limit_) {
                    break;
                }
            }

            if (Params.debug) {
                String debug_message = "\n\n[DEBUG]\n\tevals: " + evals + "\n\tpop_size: " + curr_pop_size + "\n\tavg_fitness: " + population.averageFitness() + "\n\tfitness_variance: " + population.getFitnessVariance() + "\n[DEBUG]\n\n";
                System.out.println(debug_message);
            }
        }

        // print variance for every allele
        if (Params.debug) {
            System.out.println("\n\n\nVariance for all alleles:\n");
            double[] var = population.getGenomeVariance();
            for (double v : var) {
                System.out.println(v);
            }
            System.out.println("\n\n\n\n");
        }
    }
}
