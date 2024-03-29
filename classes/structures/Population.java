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
    public void addArray(Population pop) { this.population.addAll(pop.getPopulation()); }

    // Copy constructor
    public Population(Population pop) {
        //this.population = new ArrayList<Unit>(pop.population);
        for (int i = 0; i < pop.size(); i++) {
            this.population.add(new Unit(pop.get(i)));
        }
    }

    // construct a new empty population
    public Population() {}

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

    public double bestFitness() {
        Unit bestUnit = get_ranked_unit(0);
        return bestUnit.getFitness();
    }

    public double getSigmaAverage() {
        double avgSigma = 0.0;
        /*
        for (Unit unit : this.population) {
            avgSigma += unit.getSigma(0);
        }
        return (avgSigma / this.population.size());
        */
        return this.population.get(0).getSigma(0);
    }

    public double averageDistance() {
        double avgDistance = 0.0;
        for (int i = 0; i < this.population.size()-1; i++) {
            Unit unit1 = population.get(i);
            Unit unit2 = population.get(i+1);
            avgDistance += unit1.euclideanDistance(unit2);
        }
        return (double)(avgDistance / this.population.size());
    }

    public double averageCosineSimilarity() {
        double avgCosinePop = 0.0;
        for (int i = 0; i < this.population.size(); i++) {
            double avgCosine = 0.0;
            Unit unit1 = population.get(i);
            for (int j = 0; j < this.population.size(); j++) {
                Unit unit2 = population.get(j);
                avgCosine += unit1.cosineSimilarity(unit2);
            }
            avgCosinePop += (double)(avgCosine / this.population.size());
        }
        return (double)(avgCosinePop / this.population.size());
    }

    /**
     * From the population, draw from the top 10% with 90% chance and from the bottom 90% from 10%
     */

     /*
     TODO
     public Unit sample(Random rand) {
        if (rand.nextInt(100) < 90) {
            return this.population.get_best_p(0.1);
        }
        return this.population.get_worst_p(0.9);
     }

     public Unit get_best_p(double p) {
        Collections.sort(this.population, Collections.reverseOrder());
        ArrayList<Unit> tmp = new ArrayList<Unit>(this.population.subList(0, (int)p*this.population.size()));
     }
    */


     public Unit sample(Random rand) {
        if (rand.nextInt(100) < 95) {
            return this.population.get(rand.nextInt((int)(0.1*this.population.size())));
        } else {
            return this.population.get(((int)(0.1*this.population.size())) + rand.nextInt((int)(this.population.size()*0.9)));
        }
    }

    public ArrayList<Unit> uniform_sample(int k, Random rand) {
        ArrayList<Unit> tmp = new ArrayList<Unit>(this.population);
        Collections.shuffle(tmp, rand);
        return new ArrayList<Unit>(tmp.subList(0, k));
    }

    public ArrayList<Unit> emigrate_fittest(int k) {
        ArrayList<Unit> tmp = new ArrayList<Unit>();
        Collections.sort(this.population, Collections.reverseOrder());
        for (int i = 0; i < k; i++) {
            tmp.add(this.population.get(i));
            this.population.remove(i);
        }
        return tmp;
    }

    public ArrayList<Unit> emigrate_random(int k, Random rand) {
        ArrayList<Unit> tmp = new ArrayList<Unit>();
        for (int i = 0; i < k; i++) {
            tmp.add(this.population.get(rand.nextInt(this.population.size()-1)));
        }
        return tmp;
    }

    public ArrayList<Unit> emigrate_fittest_half(int k, Random rand) {
        ArrayList<Unit> tmp = new ArrayList<Unit>();
        Collections.sort(this.population, Collections.reverseOrder());
        ArrayList<Unit> fittest_half = new ArrayList<Unit>(this.population.subList(0, (int)this.population.size()/2));
        for (int i = 0; i < k; i++) {
            tmp.add(fittest_half.get(rand.nextInt(fittest_half.size()-1)));
        }
        return tmp;
    }

    public ArrayList<Unit> emigrate(int k, Random rand) {
        ArrayList<Unit> tmp = new ArrayList<Unit>();
        Collections.sort(this.population, Collections.reverseOrder());
        for(int i = 0; i < k; i++) {
            int num = rand.nextInt((this.population.size() / 2) - i);
            tmp.add(this.population.get(num));
            this.population.remove(num);
        }
        return tmp;
    }

    public void immigrate(ArrayList<Unit> immigrants) {
        this.population.addAll(immigrants);
    }

    public double getFitnessVariance() {
        double sum = 0;
        for (Unit unit : population) {
            sum += unit.fitness;
        }
        double mean = sum / population.size();
        double diffSq = 0;
        for (Unit unit : population) {
            double diff = unit.fitness - mean;
            diffSq += diff * diff;
        }
        return diffSq / (population.size() - 1);
    }

    public double[] getGenomeVariance() {
        int genSize = population.get(0).size;
        double[] sum = new double[genSize];
        // calculate sum of population for every gene
        for (Unit unit : population) {
            for (int i = 0; i < unit.size; ++i) {
                sum[i] += unit.values[i];
            }
        }

        int popSize = population.size();
        double[] average = new double[genSize];
        // calculate the average value of every gene
        for (int i = 0; i < genSize; ++i) {
            average[i] = sum[i] / popSize;
        }

        double[] diffSq = new double[genSize];
        // calculate the square of the sum of the difference of the gene and the average, for every gene
        for (Unit unit : population) {
            double[] diff = new double[genSize];
            for (int i = 0; i < genSize; ++i) {
                diff[i] = unit.values[i] - average[i];
                diffSq[i] += diff[i] * diff[i];
            }
        }

        double[] var = new double[genSize];
        // divide every value by the size of the population
        for (int i = 0; i < genSize; ++i) {
            var[i] = diffSq[i] / popSize;
        }
        return var;
    }

    public Unit get_ranked_unit(int rank) {
        Collections.sort(this.population, Collections.reverseOrder());
        return this.population.get(rank);
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
        this.reverse();
    }

    public void reverse() {
        Collections.reverse(this.population);
    }

    public void shuffle() {
        Collections.shuffle(this.population);
    }

    public int size() {
        return this.population.size();
    }

    public void remove(Unit unit) {
        this.population.remove(unit);
    }

    public void set(int i, Unit unit) {
        this.population.set(i, unit);
    }

    public String toString() {
        String result = "";

        for (Unit unit: this.population) {
            result += Arrays.toString(unit.values) + ", ";
        }

        return result;
    }
}
