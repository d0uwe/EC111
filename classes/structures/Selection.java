package structures;

import java.util.Comparator;
import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;

import java.util.Collections;
import structures.Unit;
import structures.Params;
import structures.Population;

public class Selection {

    public Selection() {
    }

    public void select_sample(Population population, Random rand) {
        ArrayList<Unit> tmp = population.getPopulation();
        population.sort();
        Collections.sort(population.getPopulation(), Collections.reverseOrder());
        for (int i = 0; i < Params.n_survivors; i++) {
            tmp.add(population.sample(rand));
        }
        population.setPopulation(tmp);
    }

    public void select_best(Population population) {
        ArrayList<Unit> tmp = population.getPopulation();
        Collections.sort(tmp, Collections.reverseOrder());
        population.setPopulation(new ArrayList<Unit>(tmp.subList(0, Params.n_survivors)));
    }

    public Unit randomSelect(Population p, Random rand) {
        return p.get(rand.nextInt(p.size()));
    }

    public Unit selectBestUnit(Population p, int k) {
       ArrayList<Unit> tmp = p.getPopulation();
       Collections.sort(tmp, Collections.reverseOrder());
       return tmp.get(k);
    }

    public Unit tournamentSelection(Population pop, int k, Random rand) {
        /**
        * choose k (the tournament size) individuals from the population at random
        * choose the best individual from the tournament with probability p
        * choose the second best individual with probability p*(1-p)
        * choose the third best individual with probability p*((1-p)^2)
        * and so on
        */

        Unit best = null;
        ArrayList<Unit> selections = new ArrayList<Unit>();
        int i = 0;
        while (i < k) {
            Unit rand_unit = this.randomSelect(pop, rand);
            if (selections.contains(rand_unit)) continue;

            selections.add(rand_unit);
            if (best == null) {
                best = rand_unit;
            }
            else if (rand_unit.getFitness() > best.getFitness()) {
                best = rand_unit;
            }
            i++;
        }
        return best;
    }


    public Population differential_selection(Population population, Population M) {
        Population new_pop = new Population();

        int pop_length = population.size();
        for (int i = 0; i < pop_length; i++) {
            Unit new_unit;
            if (population.get(i) == M.get(i)) {
                System.out.println("YES ITS THE SAME");
            }

            if (population.get(i).compareTo(M.get(i)) == 1) {
                new_unit = new Unit(population.get(i));
                new_pop.add(new_unit);
            } else {
                new_unit = new Unit(M.get(i));
                new_pop.add(new_unit);
            }
        }

        return new_pop;
    }

    public Population selection_mu_comma_lambda(Population children) {
        children.sort();
        Population new_pop = new Population();
        new_pop.setPopulation(new ArrayList<Unit>(children.getPopulation().subList(0, Params.pop_size)));
        return new_pop;
    }

    public Population mu_plus_lambda(Population mu, Population lambda) {
        mu.addArray(lambda);
        return selection_mu_comma_lambda(mu);
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
