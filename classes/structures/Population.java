package structures;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import structures.Unit;
import structures.Params;

public class Population {

    private ArrayList<Unit> population = new ArrayList<Unit>();


    public ArrayList<Unit> getPopulation() { return this.population; }
    public void setPopulation(ArrayList<Unit> population) { this.population = population; }
    public Population(int desired_pop_size, Random rand) {
        for (int i = 0; i < desired_pop_size; i++) {
            Unit child = new Unit(Params.mutate_mode, 10);
            for (int j = 0; j < 10; j++) {
                child.setValue(j, ((rand.nextDouble() - 0.5) * 10));
            }
            // child.setFitness((double) evaluation_.evaluate(child.getValues()));
            // evals++;
            // if (evals >= evaluations_limit_) {
            //     break;
            // }
            this.population.add(child);
        }
    }

    public String toString() {
        String result = "";

        for (Unit unit: this.population) {
            result += Arrays.toString(unit.values) + ", ";
        }

        return result;
    }
}
