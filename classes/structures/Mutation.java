package structures;

import structures.Unit;
import java.util.Random;

public class Mutation {
    double upper_bound;
    double lower_bound;

    public Mutation(double upper_bound, double lower_bound) {
        upper_bound = upper_bound;
        lower_bound = lower_bound;
    }

    public Mutation() {
        upper_bound =  5.0;
        lower_bound = -5.0;
    }

    public Unit mutate_uniform(Unit unit) {
        Random rand = new Random();
        Unit new_unit = new Unit(unit);
        int unit_size = new_unit.getSize();

        for (int i = 0; i < unit_size; i++) {
            new_unit.setValue(i, (rand.nextDouble() - 0.5) * 10);
        }

        return new_unit;
    }

}
