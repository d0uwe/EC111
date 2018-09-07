import org.vu.contest.ContestSubmission;

import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;

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

    private ArrayList<ArrayList<Double>> init_population(int pop_size, Random rand) {
        ArrayList<ArrayList<Double>> population = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < pop_size; i++) {
            ArrayList<Double> child = new ArrayList<Double>();
            for (int j = 0; j < 10; j++) {
                child.add((rand.nextDouble() - 0.5) * 10);
            }
            population.add(child);
        }
        return population;
    }

    private ArrayList<ArrayList<Double>> cross_over(ArrayList<ArrayList<Double>> population, int min_split, int max_split, Random rand, int pop_size) {
        int curr_pop_size = population.size();
        for (int i = 0; i < pop_size - population.size(); i++) {
            int split = rand.nextInt(max_split - min_split) + min_split;
            int p1 = rand.nextInt(curr_pop_size);
            int p2 = rand.nextInt(curr_pop_size);

            ArrayList<Double> child = new ArrayList<Double>();

            for (int j = 0; j < population.get(0).size(); j++) {
                if (j < split) {
                    child.add(population.get(p1).get(j));
                } else {
                    child.add(population.get(p2).get(j));
                }
            }
            population.add(child);
        }
        return population;
    }

    /**
     * Mutates parents with a certain amount of mutations.
     * @param population population to mutate on
     * @param min_muts minimum amount of mutations, inclusive
     * @param max_muts maximum amount of mutations, exclusive
     * @param rand random generator
     * @param pop_size desired population size
     * @return new population
     */
    private ArrayList<ArrayList<Double>> mutate(ArrayList<ArrayList<Double>> population, int min_muts, int max_muts, Random rand, int pop_size) {
        int curr_pop_size = population.size();
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < population.get(0).size(); i++) {
            indexes.add(i);
        }
        for (int i = 0; i < pop_size - population.size(); i++) {
            Collections.shuffle(indexes);
            int amnt_muts = rand.nextInt(max_muts - min_muts) + min_muts;
            int parent = rand.nextInt(curr_pop_size);
            ArrayList<Double> child = population.get(parent);

            for (int j = 0; j < amnt_muts; j++) {
                double curr_val = child.get(indexes.get(j));
                double new_val = curr_val + rand.nextDouble() - 0.5;
                if (new_val > 5){
                    new_val = 5;
                } else if (new_val < -5) {
                    new_val = -5;
                }
                child.set(indexes.get(j), new_val);
            }
            population.add(child);
        }
        return population;
    }

    public void run() {
        // Run your algorithm here

        Random rand = new Random();
        int evals = 0;
        int pop_size = 150;
        int min_split = 4;
        int max_split = 6;
        // init population
        ArrayList<ArrayList<Double>> population = init_population(pop_size, rand);
        ArrayList<Double> fitnesses = new ArrayList<>();
        for (int i = 0; i < pop_size; i++) {
            fitnesses.add((double) evaluation_.evaluate(population.get(i)));
            evals++;
            if (evals >= evaluations_limit_) {
                break;
            }
        }

//        population = mutate(population, 2, 4, rand, 4);
//        population = cross_over(population, 2, 8, rand, 4);

        // calculate fitness
        while (evals < evaluations_limit_) {
            ArrayList<ArrayList<Double>> children = cross_over(population, min_split, max_split, rnd_, pop_size);

            for (int i = 0; i < pop_size; i++) {
                fitnesses.set(i, (double) evaluation_.evaluate(children.get(i)));
            }

            // for (int i = 0; i < pop_size; i++) {
            //     ArrayList<Double> parent = population.get(i);
            //     // System.out.println(parent);

            //     // ArrayList<Double> child = cross_over(population.get(i), min_split, max_split, rand, pop_size);

            //     // Check fitness of unknown fuction
            //     // fitnesses.set(i, evaluation_.evaluate(child));


            //     // Select parents
            //     // Apply crossover / mutation operators
            //     double child[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
            //     // Select survivors
            // }
        }
    }
}
