package structures;

import java.lang.reflect.Field;

import structures.Unit;

import java.lang.reflect.*;

public class Params {
    private Params() {}
    public static Unit.MutateMode mutate_mode = Unit.MutateMode.GAUSS_SINGLE;
    public static int pop_size = 200;
    public static float survivor_percentage = 0.8f;
    public static int n_survivors = (int)(pop_size * survivor_percentage);
    public static int gene_length = 10;
    public static int total_evals = 10000;
    public static double initial_mutate_sigma = 0.1;
    public static double final_min_sigma = 0; // 0.1;
    public static double mutation_step_size = 0.99;
    public static boolean debug = false;
    public static boolean log = false;
    public static int recombination_amount = (pop_size - n_survivors) / 2;
    public static int mutation_amount = pop_size - n_survivors - recombination_amount;
    public static double recombination_constant = 1.0;
    public static int tournament_size = 2; //n_survivors;
    public static double tournament_p = 1.0;
    public static double F = 0.4;
    public static double Cr = 0.11;
    public static int evals = 0;
    public static int mutantSize = 0;
    // public static int mu = 5;
    // public static int lambda = 10;

    public static int num_islands = 1;
    public static int island_exchange_gens = 5;
    public static int immigrants = 5;
    public static int epochs = 70;

    public static double mutationRate = 1.0;

    public static boolean csv = true;
    public static boolean diffevo = false;

    public static boolean mutatePopulation = false;

    public static void update_params(){
        n_survivors = (int)(pop_size * survivor_percentage);
        recombination_amount = (pop_size - n_survivors) / 2;
        mutation_amount = pop_size - n_survivors - recombination_amount;
    }

    public static void dump() {
        System.out.println("\n\n\n[START PARAMS]");
        Field[] params = Params.class.getFields();
        for (Field param : params) {
            if (Modifier.isStatic(param.getModifiers())) {
                Type type = param.getType();
                try {
                    if (type == int.class) {
                        System.out.println(param.getName() + ": " + param.getInt(null));
                    } else if (type == boolean.class) {
                        System.out.println(param.getName() + ": " + param.getBoolean(null));
                    } else if (type == double.class) {
                        System.out.println(param.getName() + ": " + param.getDouble(null));
                    } else if (type == float.class) {
                        System.out.println(param.getName() + ": " + param.getFloat(null));
                    }
                } catch(Exception e) {
                    // ...
                }
            }
        }

        System.out.println("[END PARAMS]\n\n\n");
    }
}


