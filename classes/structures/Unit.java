package structures;

import java.util.Comparator;

public class Unit implements Comparable<Unit> {
    double[] values;
    double fitness = 0; // Or -1?
    int size = 10;
    double[] sigmas;
    MutateMode mutate_mode;

    public enum MutateMode {
        GAUSS_SINGLE,
        GAUSS_MULTI,
        UNIFORM
    }

    public Unit(MutateMode mode) {
        values = new double[size];
        sigmas = new double[size]; // can be used fully or just the first element depending on mutation strategy
        for (int i = 0; i < sigmas.length; i++) {
            sigmas[i] = Params.initial_mutate_sigma;
        }
        mutate_mode = mode;
    }

    public Unit(MutateMode mode, int value_size) {
        values = new double[value_size];
        size = value_size;
        mutate_mode = mode;
        sigmas = new double[size];
        for (int i = 0; i < sigmas.length; i++) {
            sigmas[i] = Params.initial_mutate_sigma;
        }
    }

    // copy constructor
    public Unit(Unit unit) {
        values = unit.values;
        fitness = unit.fitness;
        size = unit.size;
        sigmas = unit.sigmas;
    }

    public double[] getValues() { return values; }
    public double   getValue(int loc) { return values[loc]; }
    public double   getFitness() { return fitness; }
    public int      getSize() { return size; }
    public double   getSigma(int loc) { return sigmas[loc]; }


    public void setValues(double[] new_values) { values = new_values; }
    public void setValue(int loc, double new_value) { values[loc] = new_value; }
    public void setFitness(double new_fitness) { fitness = new_fitness; }
    public void setSigma(int loc, double new_sigma) { sigmas[loc] = new_sigma; }

    // @Override
    public int compareTo(Unit a) {
        return fitness > a.fitness ? 1 : fitness < a.fitness ? -1 : 0;
    }
}
