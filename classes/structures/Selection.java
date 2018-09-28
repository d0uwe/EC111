package structures;

import java.util.Comparator;
import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;

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
        population.shuffle();
        population.setPopulation(new ArrayList<Unit>(population.getPopulation().subList(0, k)));

        for (int i = 0; i < population.size(); i++) {
            Unit u = population.get_ranked_unit(i);
            if (i == 0) {
                if (rand.nextDouble() <= Params.tournament_p) {
                    new_units.add(u);
                }
            }
            else {
                if (rand.nextDouble() <= Params.tournament_p*Math.pow((1-Params.tournament_p), i)) {
                    new_units.add(u);
                }
            }
        }
        population.setPopulation(new_units);
    }
}
