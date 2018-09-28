package structures;

import structures.Unit;

public class Params {
    public static Unit.MutateMode mutate_mode = Unit.MutateMode.GAUSS_SINGLE;
    public static int pop_size = 100;
    public static int min_split = 4;
    public static int max_split = 6;
    public static int n_survivors = pop_size / 10;
    public static int gene_length = 10;
    public static double initial_mutate_sigma = 1;
    public static double mutation_step_size = 0.99;
    public static boolean debug = true;
    public static int recombination_amount = n_survivors / 2;
    public static int mutation_amount = n_survivors - recombination_amount;
    public static double recombination_constant = 0.5;
    public Params() {}
}
