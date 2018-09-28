package structures;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import structures.Unit;
import structures.Params;
import java.lang.Math;

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

    /**
     * Return the average fitness of the population.
     */
    public double averageFitness() {
        double avg_fitness = 0.0;

        for (Unit unit: this.population) {
            if (Params.debug && (unit.getFitness() < 0)) { System.out.println(unit.getFitness()); }
            avg_fitness += unit.getFitness();
        }

        return (avg_fitness / this.population.size());
    }

    public double deviationFitness() {
        double avg_fitness = averageFitness();
        double deviation_sum = 0;

        for (Unit unit: this.population) {
            deviation_sum += (unit.getFitness() - avg_fitness);
        }

        return Math.sqrt((deviation_sum / this.population.size()));
    }


    public String toString() {
        String result = "";

        for (Unit unit: this.population) {
            result += Arrays.toString(unit.values) + ", ";
        }

        return result;
    }
}
