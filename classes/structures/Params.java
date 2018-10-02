package structures;

import structures.Unit;

public class Params {
    private Params() {}
    
    public static Unit.MutateMode mutate_mode = Unit.MutateMode.GAUSS_SINGLE;
    public static int pop_size = 500;
    public static int min_split = 4;
    public static int max_split = 6;
    public static int n_survivors = (int)(pop_size * 0.5);
    public static int gene_length = 10;
    public static double initial_mutate_sigma = 0.1;
    public static double mutation_step_size = 0.99;
    public static boolean debug = false;
    public static int recombination_amount = (pop_size - n_survivors) / 2;
    public static int mutation_amount = pop_size - n_survivors - recombination_amount;
    public static double recombination_constant = 0.5;
    public static int tournament_size = n_survivors;
    public static double tournament_p = 1.0;
}
