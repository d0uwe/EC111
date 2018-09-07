import java.util.Comparator;

public class Unit implements Comparable<Unit> {
    double[] values;
    double fitness = -1;

    public Unit() {
        values = new double[10];
    }

    public Unit(int value_size) {
        values = new double[value_size];
    }

    // @Override
    public int compareTo(Unit a) {
        return fitness > a.fitness ? 1 : fitness < a.fitness ? -1 : 0;
    }
}