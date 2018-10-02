package structures;

import structures.Unit;
import java.util.Random;
import java.util.ArrayList;
import structures.Params;

public class Mutation {
    double upper_bound;
    double lower_bound;

    int pop_size;
    double evaluations_done;
    double evaluation_limit;

    public Mutation(int pop_size, int evaluations_done, int evaluation_limit) {
        this.pop_size = pop_size;
        this.evaluations_done = evaluations_done;
        this.evaluation_limit = evaluation_limit;
    }

    public Mutation(double upper_bound, double lower_bound) {
        this.upper_bound = upper_bound;
        this.lower_bound = lower_bound;
    }

    public Mutation() {
        upper_bound =  5.0;
        lower_bound = -5.0;
    }

    public void mutate(Population population, Random rand) {

        int current_pop_size = population.size();
        int mutation_growth = Params.mutation_amount;
        double mutate_ratio = Params.mutate_ratio;

        for (int i = 0; i < mutation_growth; i++) {
            // Get a random unit
            Unit unit = population.get(rand.nextInt(current_pop_size));
            Unit mutated_child;

            // Apply the appropriate mutation function
            switch (unit.mutate_mode) {
                case UNIFORM:
                     mutated_child = mutate_uniform(unit, rand);
                     break;
                case GAUSS_SINGLE:
                     mutated_child = mutate_gaussian_single(unit, mutate_ratio, rand);
                     break;
                case GAUSS_MULTI:
                     mutated_child = mutate_gaussian_multi(unit, rand);
                     break;
                default:
                     throw new java.lang.Error("ERROR: unit has no mutation type.");
            }

            // Add to the population
            population.add(mutated_child);
        }
    }



    public Unit mutate_uniform(Unit unit, Random rand) {
        Unit new_unit = new Unit(unit);
        int unit_size = new_unit.getSize();

        for (int i = 0; i < unit_size; i++) {
            new_unit.setValue(i, (rand.nextDouble() - 0.5) * 10);
        }

        return new_unit;
    }

    public void mutate_uniform(Population population, int pop_size, Random rand) {

        int current_pop_size = population.size();
        int mutation_growth = Params.mutation_amount; // (pop_size - current_pop_size) / 2;

        for (int i = 0; i < mutation_growth; i++) {
        //generate random number between 0 and current_pop_size
            Unit mutated_child = mutate_uniform(population.get(rand.nextInt(current_pop_size)), rand);
            population.add(mutated_child);
        }
    }

    public Unit mutate_gaussian_single(Unit unit, double mutate_ratio, Random rand) {
        Unit new_unit = new Unit(unit);
        int unit_size = new_unit.getSize();

        for (int i = 0; i < unit_size; i++) {
            // System.out.println(mutate_ratio);
            // TODO: Enabling this gives other scores????!!!!
            // if (rand.nextDouble() <= mutate_ratio) {
                new_unit.setValue(i, new_unit.getValue(i) + (rand.nextGaussian() * 0.5)); // new_unit.getSigma(0)));
            // }
        }
        return new_unit;
    }


    public void mutate_gaussian_single(Population population, double mutate_ratio, Random rand) {

        int current_pop_size = population.size();
        int mutation_growth = Params.mutation_amount;

        for (int i = 0; i < mutation_growth; i++) {
            if (rand.nextDouble() <= mutate_ratio) {
                Unit mutated_child = mutate_gaussian_single(population.get(rand.nextInt(current_pop_size)), mutate_ratio, rand);
                population.add(mutated_child);
            }
        }
    }


    public Unit mutate_gaussian_multi(Unit unit, Random rand) {
        // TODO
        return unit;
    }


    public void mutate_gaussian_multi(Population population, int pop_size, Random rand) {
        // TODO
    }

    public Unit uncorrelated(Unit unit, double mutation_ratio, int alpha, Random rand) {
        for (int i = 0; i < unit.getSize(); i++) {
            if (rand.nextDouble() <= mutation_ratio) {
                double sigma;
                sigma = 1.0 - ((double) evaluations_done / (double) evaluation_limit);
                sigma = Math.pow(sigma, alpha);
                unit.setSigma(i, sigma);
                double value = unit.getValue(i) + unit.ni[i] * unit.getSigma(i);
                unit.setValue(i, value);
            }
        }
        return unit;
    }
}
