package structures;

import structures.Unit;
import java.util.Random;
import java.util.ArrayList;
import structures.Params;

public class Mutation {
    double upper_bound;
    double lower_bound;

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
                     mutated_child = mutate_gaussian_single(unit, rand);
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

    public Unit mutate_gaussian_single(Unit unit, Random rand) {
        Unit new_unit = new Unit(unit);
        int unit_size = new_unit.getSize();

        for (int i = 0; i < unit_size; i++) {
            new_unit.setValue(i, new_unit.getValue(i) + (rand.nextGaussian() * new_unit.getSigma(0)));
        }

        return new_unit;
    }


    public void mutate_gaussian_single(Population population, int pop_size, Random rand) {

        int current_pop_size = population.size();
        int mutation_growth = Params.mutation_amount;

        for (int i = 0; i < mutation_growth; i++) {
            Unit mutated_child = mutate_gaussian_single(population.get(rand.nextInt(current_pop_size)), rand);
            population.add(mutated_child);
        }
    }


    public Population mutate_differential(Population population, int pop_size, Random rand) {
        int current_pop_size = population.size();
        int mutation_growth = Params.mutation_amount;

        // The mutant population is defined as M
        // We can copy the original and just edit all units in this.
        Population M = new Population(population);
        int pop_size = population.size();

        for (int i = 0; i < pop_size; i++) {
            Unit x = M.get(i);

            do {
                Unit y = population.get(rand.nextInt(pop_size));
            } while (y == x);

            do {
                Unit z = population.get(rand.nextInt(pop_size));
            } while ((z == x) || (z == y));


            for (int i = 0; i < unit_size; i++) {
                x.setValue(i, x.getValue(i) + params.F * (y.getValue(i) - z.getValue(i)));
            }


            // Might as well do the crossover in here too
            if (rand.nextDouble() > Params.Cr) {
                x.setValue(i, population.get(i).getValue(i));
            }
        }

        Population new_pop = new Population();
        // ELITISMMMM
        for (int i = 0; i < pop_size; i++) {
            if
        }

    }


    public Unit mutate_gaussian_multi(Unit unit, Random rand) {
        // TODO
        return unit;
    }


    public void mutate_gaussian_multi(Population population, int pop_size, Random rand) {
        // TODO
    }
}
