package structures;

import structures.Unit;

public class Params {
    private Params() {}
    public static Unit.MutateMode mutate_mode = Unit.MutateMode.GAUSS_SINGLE;
    public static int pop_size = 900;
    public static int min_split = 4;
    public static int max_split = 6;
    public static float survivor_percentage = 0.8f;
    public static int n_survivors = (int)(pop_size * survivor_percentage);
    public static int gene_length = 10;
    public static double initial_mutate_sigma = 0.1;
    public static double mutation_step_size = 0.99;
    public static boolean debug = false;
    public static boolean log = true;
    public static int recombination_amount = (pop_size - n_survivors) / 2;
    public static int mutation_amount = pop_size - n_survivors - recombination_amount;
    public static double recombination_constant = 0.5;
    public static int tournament_size = 2; //n_survivors;
    public static double tournament_p = 1.0;
    public static double F = 0.4;
    public static double Cr = 0.11;
    public static int evals = 0;
    public static int mutantSize = 0;
    // public static int mu = 5;
    // public static int lambda = 10;

    public static int num_islands = 4;
    public static int island_exchange_gens = 5;
    public static int immigrants = 5;

    public static boolean csv = true;
    public static boolean use_islands = false;


    public static void update_params(){
        n_survivors = (int)(pop_size * survivor_percentage);
        recombination_amount = (pop_size - n_survivors) / 2;
        mutation_amount = pop_size - n_survivors - recombination_amount;
    }

}


