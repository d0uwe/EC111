import org.vu.contest.ContestSubmission;

import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


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
        // evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        evaluations_limit_ = 500;
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

    private class Unit implements Comparable<Unit> {
        double[] values;
        double fitness = -1;
    
        public Unit() {
            values = new double[10];
        }
    
        public Unit(int value_size) {
            values = new double[value_size];
        }

        // copy constructor
        public Unit(Unit unit) {
            values = unit.values;
            fitness = unit.fitness;
        }
    
        // @Override
        public int compareTo(Unit a) {
            return fitness > a.fitness ? 1 : fitness < a.fitness ? -1 : 0;
        }
    }

    private ArrayList<Unit> init_population(int pop_size, Random rand) {
        ArrayList<Unit> population = new ArrayList<Unit>();
        for (int i = 0; i < pop_size; i++) {
            Unit child = this.new Unit(10);
            for (int j = 0; j < 10; j++) {
                child.values[j] = (rand.nextDouble() - 0.5) * 10;

                child.fitness = (double) evaluation_.evaluate(population.get(i));
                // evals++;
                // if (evals >= evaluations_limit_) {
                //     break;
                // }
            }
            population.add(child);
        }
        return population;
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
    private ArrayList<Unit> cross_over(ArrayList<Unit> population, int min_split, int max_split, Random rand, int pop_size) {
        int curr_pop_size = population.size();
        for (int i = 0; i <= pop_size - population.size(); i++) {
            int split = rand.nextInt(max_split - min_split) + min_split;
            int p1 = rand.nextInt(curr_pop_size);
            int p2 = rand.nextInt(curr_pop_size);

            Unit child = this.new Unit(10);

            for (int j = 0; j < population.get(0).values.length; j++) {
                if (j < split) {
                    child.values[j] = population.get(p1).values[j];
                } else {
                    child.values[j] = population.get(p2).values[j];
                }
            }
            population.set(i, child);
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
    private ArrayList<Unit> mutate(ArrayList<Unit> population, int min_muts, int max_muts, Random rand, int pop_size) {
        int curr_pop_size = population.size();
        ArrayList<Integer> indexes = new ArrayList<>();

        // TODO: find some arange() thing for java to replace this
        for (int i = 0; i < population.get(0).values.length; i++) {
            indexes.add(i);
        }
        for (int i = 0; i <= pop_size - population.size(); i++) {
            Collections.shuffle(indexes);
            int amnt_muts = rand.nextInt(max_muts - min_muts) + min_muts;
            int parent = rand.nextInt(curr_pop_size);
            Unit child = new Unit(population.get(parent));

            for (int j = 0; j < amnt_muts; j++) {
                double curr_val = child.values[indexes.get(j)];
                double new_val = curr_val + rand.nextDouble() - 0.5;
                if (new_val > 5){
                    new_val = 5;
                } else if (new_val < -5) {
                    new_val = -5;
                }
                child.values[indexes.get(j)] = new_val;
            }
            population.add(child);
        }
        return population;
    }

    private ArrayList<Unit> select_survivors(ArrayList<Unit> population, int n_survivors) {
        Collections.sort(population);
        return new ArrayList<Unit>(population.subList(0, n_survivors));
    }

    public void run() {
        // Run your algorithm here

        Random rand = new Random();
        int evals = 0;
        int pop_size = 150;
        int min_split = 4;
        int max_split = 6;
        // init population

        assert pop_size <= evaluations_limit_;

        ArrayList<Unit> population = init_population(pop_size, rand);

        // Init_population does one evaluate for every unit in the population
        // There are guaranteed enough evals left for this (see assert)
        evals += pop_size;

        // System.out.println(population[0].toString());
        // // System.out.println((double) evaluation_.evaluate());
        // System.out.println((double) evaluation_.evaluate(population[0]));

        // for (int i = 0; i < population.size(); i++) {
        //     fitnesses.add((double) evaluation_.evaluate(population.get(i)));
        //     evals++;
        //     if (evals >= evaluations_limit_) {
        //         break;
        //     }
        // }

        for (Unit child : population) {
            for (double number : child.values) {
                System.out.print(number);
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();

        //population = mutate(population, 2, 4, rand, 4);
//        population = cross_over(population, 2, 8, rand, 4);

        for (Unit child : population) {
            for (double number : child.values) {
                System.out.print(number);
            }
            System.out.println();
        }
        // calculate fitness

        int n_survivors = pop_size / 2;
        while (evals < evaluations_limit_) {
            System.out.println(evals);

            population = select_survivors(population, n_survivors);

            // ArrayList<Unit> children = cross_over(population, min_split, max_split, rnd_, pop_size);
            population = cross_over(population, min_split, max_split, rnd_, pop_size);

            for (int i = n_survivors; i < pop_size; i++) {
                population.get(i).fitness = (double) evaluation_.evaluate(population.get(i));
            }


            // for (int i = population.size(); i < children.size(); i++) {
            //     fitnesses.add((double) evaluation_.evaluate(children.get(population.size() + i)));
            //     evals++;
            //     if (evals >= evaluations_limit_) {
            //         break;
            //     }
            // }

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


