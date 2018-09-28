package structures;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.lang.Math;
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

    /**
     * From the population, draw from the top 10% with 90% chance and from the bottom 90% from 10%
     */
    public Unit sample(Random rand) {
        if (rand.nextInt(100) < 90) {
            return this.population.get(rand.nextInt(10));
        } else {
            return this.population.get(10 + rand.nextInt(this.population.size() - 10));
        }
    }


    /* ArrayList operators 'overwriting' */
    public Unit get(int loc) {
        return this.population.get(loc);
    }

    public void add(Unit unit) {
        this.population.add(unit);
    }

    public void sort() {
        Collections.sort(this.population);
    }

    public void reverse() {
        Collections.reverse(this.population);
    }

    public int size() {
        return this.population.size();
    }

    public String toString() {
        String result = "";

        for (Unit unit: this.population) {
            result += Arrays.toString(unit.values) + ", ";
        }

        return result;
    }
}
