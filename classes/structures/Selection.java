package structures;

import java.util.Comparator;
import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;

import java.util.Collections;
import structures.Unit;
import structures.Params;

public class Selection {

    public Selection() {
    }

    public void select_survivors(Population population) {
        population.sort();
        population.reverse();
        ArrayList<Unit> tmp = population.getPopulation();

        Population tmp_population = population;
        population.setPopulation(new ArrayList<Unit>(tmp.subList(0, Params.n_survivors)));
    }


    public void tournament_selection(Population population, int k, Random rand) {
        /**
        * choose k (the tournament size) individuals from the population at random
        * choose the best individual from the tournament with probability p
        * choose the second best individual with probability p*(1-p)
        * choose the third best individual with probability p*((1-p)^2)
        * and so on
        */
        ArrayList<Unit> new_units = new ArrayList<Unit>();

        for (int i = 0; i < Params.n_survivors; i++) {
            ArrayList<Unit> k_units = population.uniform_sample(k, rand);
            // Get the best.
            Collections.sort(k_units, Collections.reverseOrder());

            // System.out.println("FITNESS " + k_units.get(0).getFitness());

            Unit best_unit = k_units.get(0);

            new_units.add(best_unit);
            population.remove(best_unit);
            


            //System.out.println(best_unit.i);
            // population.removeIf(t -> t.i == 1);
            // population.
            // population.get(i).setValue(k_units.get(0));
            // System.out.println(k_units.get(0).getFitness());
            /*
            for (int j = 0; j < k_units.size(); j++) {
                if (j == 0) {
                    if (rand.nextDouble() <= Params.tournament_p) {
                        population.setValue(i, k_units[j]); 
                    }
                }i
                else {
                    if (rand.nextDouble() <= Params.tournament_p*Math.pow((1-Params.tournament_p), i)) {
                        population.setValue(i, k_units[j]);
                    }
                }
            }
            */
        }

        // System.out.println(new_units.size()); 
        population.setPopulation(new_units); 
        /*
        for (int i = 0; i < population.size(); i++) {
            Unit u = population.get_ranked_unit(i);
            if (i == 0) {
                if (rand.nextDouble() <= Params.tournament_p) {
                    // new_units.add(u);
                }
            }
            else {
                if (rand.nextDouble() <= Params.tournament_p*Math.pow((1-Params.tournament_p), i)) {
                    new_units.add(u);
                }
            }
        }
        population.setPopulation(new_units);
        */
    }
}
