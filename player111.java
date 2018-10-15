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


    public void run() {

        String evaluation_type = null;
        if (System.getProperty("debug") != null) {
            Params.debug = Boolean.parseBoolean(System.getProperty("debug"));
        }

        if (System.getProperty("log") != null) {
            Params.log = Integer.parseInt(System.getProperty("log")) != 0;
        }

        if (System.getProperty("population") != null) {
            Params.pop_size = Integer.parseInt(System.getProperty("population"));
        }

        if (System.getProperty("F") != null) {
            Params.F = Double.parseDouble(System.getProperty("F"));
        }

        if (System.getProperty("Cr") != null) {
            Params.Cr = Double.parseDouble(System.getProperty("Cr"));
        }

        int evals = 0;
        if (System.getProperty("survp") != null) {
            Params.survivor_percentage = Float.parseFloat(System.getProperty("survp"));
        }

        if (System.getProperty("islands") != null) {
            Params.use_islands = Integer.parseInt(System.getProperty("islands")) != 0;
        }

        Params.update_params();
        int pop_size = Params.pop_size;

        Population population = new Population(pop_size, rnd_);
        int min_split = Params.min_split;
        int max_split = Params.max_split;

        assert pop_size <= evaluations_limit_;


        int n_survivors = Params.n_survivors;
        Selection selection = new Selection();
        Mutation mutate = new Mutation();
        Recombination recombination = new Recombination();


        for (Unit unit: population.getPopulation()) {
            if (Params.evals >= evaluations_limit_) {
                break;
            }
            unit.setFitness((double) evaluation_.evaluate(unit.getValues()));
            Params.evals++;
        }


        // And then we do it for the whole population
        if (Params.log) {
            System.out.println("eval,pop_size,fitness_avg,fitness_variance,fitness_best,mutation_amount,recombination_amount,island");
        }

        Params.use_islands = false;
        if (Params.use_islands) {
            run_islands();
        } else {
            while (Params.evals < evaluations_limit_) {

                Population M = mutate.mutate_differential(population, pop_size, rnd_);
                for (Unit unit: M.getPopulation()) {
                    if (Params.evals >= evaluations_limit_) {
                        break;
                    }
                    unit.setFitness((double) evaluation_.evaluate(unit.getValues()));
                    Params.evals++;
                }
                population = selection.mu_plus_lambda(population, M);
                int curr_pop_size = population.size();


                if (Params.log) {
                    System.out.println(Params.evals + "," + population.size() + "," + population.averageFitness() + "," + population.getFitnessVariance() + "," +
                    population.bestFitness() + "," + Params.mutation_amount + "," + Params.recombination_amount + "," + "0");
                }
            }
        }
    }

    private void run_islands() {
        int evals = 0;
        int pop_size = Params.pop_size;
        ArrayList<Population> islands = new ArrayList<>();
        for(int i = 0; i < Params.num_islands; i++) {
            islands.add(new Population(pop_size, rnd_));
        }
        int n_survivors = Params.n_survivors;
        Selection selection = new Selection();
        Mutation mutate = new Mutation();
        Recombination recombination = new Recombination();


        while (evals < evaluations_limit_) {
            for (int j = 0; j < Params.island_exchange_gens; j++) {
                for (Population population : islands) {
                    selection.tournament_selection(population, Params.tournament_size, rnd_);
                    // selection.select_survivors(population);
                    recombination.recombination(population, selection, Params.min_split, Params.max_split, rnd_);
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
                }
            }
            ArrayList<ArrayList<Unit>> exchanges = new ArrayList<>();
            for (Population population : islands) {
                exchanges.add(population.emigrate(Params.immigrants, rnd_));
            }
            Collections.shuffle(exchanges, rnd_);
            for (Population population : islands) {
                population.immigrate(exchanges.get(0));
                exchanges.remove(0);
            }
            if (Params.log) {
                for (Population population : islands) {
                    System.out.println(evals + "," + population.size() + "," + population.averageFitness() + "," + population.getFitnessVariance() + "," +
                    population.bestFitness() + "," +
                    Params.mutation_amount + "," + Params.recombination_amount + "," + islands.indexOf(population));
                }
            }
        }
    }
}
