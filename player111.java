import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.ArrayList;


public class player111 implements ContestSubmission {
    Random rnd_;
    ContestEvaluation evaluation_;
    private int evaluations_limit_;

    public player111() {
        rnd_ = new Random();
    }

    public void setSeed(long seed) {
        // Set seed of algortihms random process
        rnd_.setSeed(seed);
    }

    public void setEvaluation(ContestEvaluation evaluation) {
        // Set evaluation problem used in the run
        evaluation_ = evaluation;

        // Get evaluation properties
        Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        // Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }
    }

    private ArrayList<ArrayList<Double>> init_population (int pop_size, Random rand) {
        ArrayList<ArrayList<Double>> population = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < pop_size; i++) {
            ArrayList<Double> child = new ArrayList<Double>();
            for (int j = 0; j < 10; j++) {
                child.add((rand.nextDouble() - 0.5) * 10);
            }
            population.add(child);
        }
        return population;
    }

    public void run() {
        // Run your algorithm here

        Random rand = new Random();
        int evals = 0;
        int pop_size = 150;
        // init population
        ArrayList<ArrayList<Double>> population = init_population(pop_size, rand);

        // calculate fitness
        while(evals<evaluations_limit_) {
            // Select parents
            // Apply crossover / mutation operators
            double child[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
            // Check fitness of unknown fuction
            Double fitness = (double) evaluation_.evaluate(child);
            evals++;
            // Select survivors
        }
    }
}
