package structures;

import structures.Unit;

public class Params {
    public static Unit.MutateMode mutate_mode = Unit.MutateMode.UNIFORM;
    public static int pop_size = 150;
    public static int min_split = 4;
    public static int max_split = 6;
    public static int n_survivors = pop_size / 2;
    public static double initial_mutate_sigma = 3;

    public Params() {}
}