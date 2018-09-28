package structures;

import java.util.Comparator;
import java.util.Random;
import java.util.ArrayList;

import structures.Unit;
import structures.Params;

public class Selection {

    public Selection() {
    }

    public void select_survivors(Population population) {
        population.sort();
        population.reverse();
        ArrayList<Unit> tmp = population.getPopulation();

        population.setPopulation(new ArrayList<Unit>(tmp.subList(0, Params.n_survivors)));
    }

    public void tournament_selection(Population population, int k) {
    }
}
