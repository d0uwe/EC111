package structures;

import structures.Unit;
import java.util.Random;
import java.util.ArrayList;

public class Population {

    private ArrayList<Unit> population = new ArrayList<Unit>();


    public ArrayList<Unit> getPopulation() { return this.population; }
    public void setPopulation(ArrayList<Unit> population) { this.population = population; }
    public Population(int pop_size, Random rand, Unit.MutateMode MUTATE_MODE) {

        for (int i = 0; i < pop_size; i++) {
            Unit child = new Unit(MUTATE_MODE, 10);
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
}
