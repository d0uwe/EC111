import org.vu.contest.ContestSubmission;

import org.vu.contest.ContestEvaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;



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
    private int evaluations_done = 0;

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


    public Population evolvePopulation(Population pop, int offspringSize, ContestEvaluation evaluation, Random rand) {

        if (evaluations_done == 0) {
            evaluations_done += pop.size();
        }
        evaluations_done += offspringSize;

        int min_split = Params.min_split;
        int max_split = Params.max_split;
        double mutate_ratio = Params.mutate_ratio;

        Selection selection = new Selection();
        Recombination recombination = new Recombination();

        int n_survivors = Params.n_survivors;
        Population offspringPop = new Population(pop.size(), rand);
        Population fittestPop = new Population(pop.size(), rand);

        for (int i = 0; i < offspringPop.size(); i++) {

            // SELECTION

            // Unit p1 = selection.randomSelect(pop, rand);
            // Unit p2 = selection.randomSelect(pop, rand);
            Unit p1;
            Unit p2;

            p1 = selection.tournamentSelection(pop, Params.tournament_size, rand);
            do {
                p2 = selection.tournamentSelection(pop, Params.tournament_size, rand);
            } while (p1 == p2);

            Mutation mutate = new Mutation(pop.size(), evaluations_done, evaluations_limit_);


            // REPRODUCTION
            int split = rand.nextInt(max_split - min_split) + min_split;
            // Unit child = recombination.uniform_cross_over(p1, p2, rand);
            // Unit child = recombination.cross_over(p1, p2, split);
            Unit child = recombination.whole_arithmetic(p1, p2);

            /*
            TODO: No effect
            Unit better_fitness;
            if (p1.compareTo(p2) < 0) { better_fitness = p1; } else { better_fitness = p2; }
            boolean is_single_sigma = better_fitness.getMutateMode() == Params.mutate_mode ? true : false;

            for (int j = 0; j < Params.gene_length; j++) {
                child.setSigma(j, better_fitness.getSigma(j));
                if (is_single_sigma) { break; }
            }
            */

            // MUTATION
            child = mutate.mutate_gaussian_single(child, mutate_ratio, rand);
            // child = mutate.uncorrelated(child, mutate_ratio, 1, rand);
            Double fitness = (Double) evaluation.evaluate(child.getValues());
            child.setFitness(fitness);
            offspringPop.getPopulation().set(i, child);
            break;
        }

        return offspringPop;
    }


    public void run() {

        if (System.getProperty("debug") != null) {
            Params.debug = Boolean.parseBoolean(System.getProperty("debug"));
        }

        if (System.getProperty("csv") != null) {
            Params.csv = Boolean.parseBoolean(System.getProperty("csv"));
        }

        // int evals = 0;
        int pop_size = Params.pop_size;
        try {
            pop_size = Integer.parseInt(System.getProperty("pop"));

        } catch (Exception e){
            // throw e;
        }

        assert pop_size <= evaluations_limit_;

        Population pop = new Population(pop_size, rnd_);

        int offspringSize = Params.n_survivors;
        int evals = 0; //pop_size + offspringSize;

        if (Params.csv) {
            System.out.println("eval,pop_size,avg_fitness,fitness_variance,mutation_amount,recombination_amount");
        }

        // And then we do it for the whole population
        while (evals < evaluations_limit_) {

            pop = this.evolvePopulation(pop, offspringSize, evaluation_, rnd_);
            // offspringSize = Math.min(offspringSize, evaluations_limit_-evals);
            // evals += offspringSize;
            evals++;

            if (Params.csv) {
                System.out.println(evals + "," + pop.size() + "," + pop.averageFitness() + "," + pop.getFitnessVariance() + "," +
                Params.mutation_amount + "," + Params.recombination_amount);
            }
            // System.out.println(evals + " - " + evaluations_limit_ + " - " + offspringSize + " - " + pop.size());
            /*
            selection.tournament_selection(population, Params.tournament_size, rnd_);
            // selection.select_survivors(population);
            recombination.recombination(population, min_split, max_split, rnd_);
            mutate.mutate_gaussian_single(population, pop_size, rnd_);
            //mutate.mutate_uniform(population, pop_size, rnd_);
            int curr_pop_size = population.size();

            for (int i = n_survivors; i < curr_pop_size; i++) {
                double new_fitness = (double) evaluation_.evaluate(population.get(i).getValues());
                population.getPopulation().get(i).setFitness(new_fitness);
                evals++;
                if (evals >= evaluations_limit_) {
                    break;
                }
            }
            */

            if (Params.debug) {
                String debug_message = "\n\n[DEBUG]\n\tevals: " + evals +
                "\n\tpop_size: " + pop.size() + "\n\tavg_fitness: " +
                pop.averageFitness() + "\n\tfitness_variance: " +
                pop.getFitnessVariance() +
                "\n\tmut_amnt: " + Params.mutation_amount +
                "\n\trecomb_amnt " + Params.recombination_amount +
                "\n[DEBUG]\n\n";
                System.out.println(debug_message);
            }
        }

        System.out.println("0x04");

        // print variance for every allele
        /*
        if (Params.debug) {
            System.out.println("\n\n\nVariance for all alleles:\n");
            double[] var = population.getGenomeVariance();
            for (double v : var) {
                System.out.println(v);
            }
            System.out.println("\n\n\n\n");
        }
        */
    }
}
