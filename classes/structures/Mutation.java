package structures;

import structures.Unit;
import structures.Unit.MutateMode;

import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;
import structures.Params;
import java.util.Arrays;
import java.util.Collections;

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
                     mutated_child = mutate_gaussian_single(unit, rand, population.bestFitness());
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

    public Unit mutate_gaussian_single(Unit unit, Random rand, double bestFitness) {
        Unit new_unit = new Unit(unit);

        // double new_sigma = Params.initial_mutate_sigma - ((Params.initial_mutate_sigma - Params.final_min_sigma)/Params.total_evals * Params.evals);
        // new_unit.setSigma(0, new_sigma);

        // Exponential
        double new_sigma = (double)Math.exp(-Params.evals/(Params.total_evals/4.0))*Params.initial_mutate_sigma;
        new_unit.setSigma(0, new_sigma);

        // Genome mutation rate
        int unit_size = new_unit.getSize();
        for (int i = 0; i < unit_size; i++) {
            if (rand.nextDouble() >= 0.5 || bestFitness <= 9.9) {
                new_unit.setValue(i, new_unit.getValue(i) + (rand.nextGaussian() * new_unit.getSigma(0)));
            }
        }
        return new_unit;
    }

    public void mutate_gaussian_single_best(Population population, int pop_size, Random rand) {

        int current_pop_size = population.size();
        ArrayList<Unit> tmp = population.getPopulation();
        Collections.sort(tmp, Collections.reverseOrder());
        for (int i = 0; i < Params.mutation_amount; i++) {
            Unit mutated_child = mutate_gaussian_single(tmp.get(0), rand, population.bestFitness());
            population.add(mutated_child);
        }
    }

    public void mutate_gaussian_single(Population population, int pop_size, Random rand) {

        int current_pop_size = population.size();
        for (int i = 0; i < Params.mutation_amount; i++) {
            Unit mutated_child = mutate_gaussian_single(population.get(rand.nextInt(current_pop_size)), rand, population.bestFitness());
            population.add(mutated_child);
        }
    }

    public void mutate_gaussian_population(Population population, int pop_size, Random rand) {
        int current_pop_size = population.size();
        for (int i = 0; i < current_pop_size; i++) {
            if (rand.nextDouble() >= 0.9999) {
                Unit mutant = population.get(i);
                mutant = mutate_gaussian_single(mutant, rand, population.bestFitness());
                population.set(i, mutant);
            }
        }
    }


    public Population mutate_differential(Population population, Selection selection, int pop_size, Random rand) {
        int current_pop_size = population.size();

        // The mutant population is defined as M
        // We can copy the original and just edit all units in this.
        Population M = new Population(population);
        for (int i = 0; i < Params.mutantSize; i++) {
            M.addArray(population);
        }

        //Population new_pop = new Population();
        int new_pop_size = M.size();
        for (int i = 0; i < new_pop_size; i++) {
            /*
            Unit x, y, z;

            x = selection.tournamentSelection(M, Params.tournament_size, rand);
            do {
                y = selection.tournamentSelection(M, Params.tournament_size, rand);
            } while (y == x);

            do {
                z = selection.tournamentSelection(M, Params.tournament_size, rand);
            } while ((z == x) || (z == y));
            */

            Unit x = M.get(i);
            Unit xprime = new Unit(x);

            Unit y, z;
            do {
                y = population.get(rand.nextInt(pop_size));
            } while (y.equals(x));
            int bullshit = 0;
            do {
                bullshit++;
                z = population.get(rand.nextInt(pop_size));
            } while (((z.equals(x)) || (z.equals(y))) && bullshit < 5);

            // double F = Params.F - ((Params.F - Params.final_min_sigma)/Params.total_evals * Params.evals);
            double F = (double)Math.exp(-Params.evals/(Params.total_evals/2.0))*Params.F;
            if (!Params.decayF) {
                F = Params.F;
            }
            for (int j = 0; j < Params.gene_length; j++) {
                x.setValue(j, x.getValue(j) + F * (y.getValue(j) - z.getValue(j)));
                if ((x.getValue(j) > 5.0) || (x.getValue(j) < -5.0)) {
                    x.setValue(j, (rand.nextDouble() - 0.5) * 10);
                }

                // change back to what it was, which is sort of "crossover"
                if (rand.nextDouble() < Params.Cr) {
                    int new_i = i % pop_size;
                    x.setValue(j, population.get(new_i).getValue(j));
                }
            }
        }

        return M;
    }


    public Unit mutate_gaussian_multi(Unit unit, Random rand) {
        // TODO
        return unit;
    }


    public void mutate_gaussian_multi(Population population, int pop_size, Random rand) {
        // TODO
    }
}
