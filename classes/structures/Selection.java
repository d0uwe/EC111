package structures;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.ArrayList;

import structures.Unit;

public class Selection {

    public Selection() {
    }

    public ArrayList<Unit> select_survivors(ArrayList<Unit> population, int n_survivors) {
        Collections.sort(population);
        return new ArrayList<Unit>(population.subList(0, n_survivors));
    }

    public void tournament_selection(ArrayList<Unit> population, int k) {
        
    }
}