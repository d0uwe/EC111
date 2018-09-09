package structures;

import java.util.Comparator;

public class Unit implements Comparable<Unit> {
    double[] values;
    double fitness = -1;
    int size = 10;

    public Unit() {
        values = new double[size];
    }

    public Unit(int value_size) {
        values = new double[value_size];
        size = value_size;
    }

    // copy constructor
    public Unit(Unit unit) {
        values = unit.values;
        fitness = unit.fitness;
    }

    public double[] getValues() { return values; }
    public double   getValue(int loc) { return values[loc]; }
    public double   getFitness() { return fitness; }
    public int      getSize() { return size; }


    public void setValues(double[] new_values) { values = new_values; }
    public void setValue(int location, double new_value) { values[location] = new_value; }
    public void setFitness(double new_fitness) { fitness = new_fitness; }

    // @Override
    public int compareTo(Unit a) {
        return fitness > a.fitness ? 1 : fitness < a.fitness ? -1 : 0;
    }
}
