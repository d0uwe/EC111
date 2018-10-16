import org.vu.contest.ContestSubmission;

import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.HashMap;
import java.util.Map;

import structures.Population;
import structures.Unit;
import structures.Recombination;
import structures.Selection;
import structures.Mutation;
import structures.Params;


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
        // evaluations_limit_ = 500;
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));


        // Do sth with property values, e.g. specify relevant settings of your algorithm
        if (isMultimodal) {
            // Do sth
        } else {
            // Do sth else
        }
    }


    public Population baseLineEvo(Population population, Selection selection, Recombination recombination, Mutation mutation) {
        selection.tournament_selection(population, Params.tournament_size, rnd_);
        // selection.select_survivors(population);
        recombination.recombination(population, selection, rnd_);
        mutation.mutate_gaussian_single(population, Params.pop_size, rnd_);
        int curr_pop_size = population.size();
        for (int i = Params.n_survivors; i < curr_pop_size; i++) {
            double new_fitness = (double) evaluation_.evaluate(population.get(i).getValues());
            population.getPopulation().get(i).setFitness(new_fitness);
            Params.evals++;
            if (Params.evals >= evaluations_limit_) {
                break;
            }
        }
        return population;
    }

    public Population diffEvo(Population population, Selection selection, Mutation mutate) {
        Population M = mutate.mutate_differential(population, selection, Params.pop_size, rnd_);
        for (Unit unit: M.getPopulation()) {
            if (Params.evals >= evaluations_limit_) {
                break;
            }
            unit.setFitness((double) evaluation_.evaluate(unit.getValues()));
            Params.evals++;
        }
        population = selection.mu_plus_lambda(population, M);
        int curr_pop_size = population.size();
        return population;
    }

    public void run() {

        // optimal values per evaluation
        String evaluationName = System.getProperty("evaluation");
        if (evaluationName == "BentCigarFunction") {
            Params.Cr = 0.005;
            Params.F = 0.35;
            Params.num_islands = 2;
            Params.pop_size = 200;
            Params.epochs = 40;
        }
        if (evaluationName == "KatsuuraEvaluation") {
            Params.Cr = 0.11;
            Params.F = 0.35;
            Params.pop_size = 400;
            Params.num_islands = 4;
        }

        String evaluation_type = null;
        if (System.getProperty("debug") != null) {
            Params.debug = Boolean.parseBoolean(System.getProperty("debug"));
        }

        if (System.getProperty("log") != null) {
            Params.log = Integer.parseInt(System.getProperty("log")) != 0;
        }

        if (System.getProperty("population") != null) {
            Params.pop_size = Integer.parseInt(System.getProperty("population"));
        }

        if (System.getProperty("F") != null) {
            Params.F = Double.parseDouble(System.getProperty("F"));
        }

        if (System.getProperty("Cr") != null) {
            Params.Cr = Double.parseDouble(System.getProperty("Cr"));
        }

        int evals = 0;
        if (System.getProperty("survp") != null) {
            Params.survivor_percentage = Float.parseFloat(System.getProperty("survp"));
        }

        if (System.getProperty("islands") != null) {
            Params.num_islands = Integer.parseInt(System.getProperty("islands"));
        }

        if (System.getProperty("diffevo") != null) {
            Params.diffevo = Integer.parseInt(System.getProperty("diffevo")) != 0;
        }

        if (System.getProperty("island_migrants") != null) {
            Params.immigrants = Integer.parseInt(System.getProperty("island_migrants"));
        }

        if (System.getProperty("epochs") != null) {
            Params.epochs = Integer.parseInt(System.getProperty("epochs"));
        }


        assert Params.pop_size <= evaluations_limit_;

        int epoch = 0;
        Params.pop_size = (int)Params.pop_size / Params.num_islands;
        ArrayList<Population> islands = new ArrayList<>();
        for(int i = 0; i < Params.num_islands; i++) {
            Population population = new Population(Params.pop_size, rnd_);
            for (Unit unit: population.getPopulation()) {
                if (Params.evals >= evaluations_limit_) {
                    break;
                }
                unit.setFitness((double) evaluation_.evaluate(unit.getValues()));
                Params.evals++;
            }
            islands.add(population);
        }

        Selection selection = new Selection();
        Mutation mutation = new Mutation();
        Recombination recombination = new Recombination();
        Params.update_params();

        while (Params.evals < evaluations_limit_) {

            for (int i = 0; i < islands.size(); i++) {
                Population population = islands.get(i);
                if (Params.diffevo) {
                    population = diffEvo(population, selection, mutation);
                }
                else {
                    population = baseLineEvo(population, selection, recombination, mutation);
                }
                islands.set(i, population);
                if (Params.log) {
                    System.out.println(Params.evals + "," + population.size() + "," + population.averageFitness() + "," + population.getFitnessVariance() + "," +
                     population.bestFitness() + "," + Params.mutation_amount + "," + Params.recombination_amount + "," + i);
                }
            }

            if (Params.num_islands > 0) {
                // Most authors have used epoch lengths of the range 25–150 generations
                // migration on epoch
                if ((epoch % Params.epochs) == 0) {
                    ArrayList<ArrayList<Unit>> fittest_exchanges = new ArrayList<>();
                    for (Population population : islands) {
                        fittest_exchanges.add(population.emigrate_fittest(Params.immigrants));
                    }

                    fittest_exchanges = derange(fittest_exchanges);
                    // Collections.shuffle(fittest_exchanges, rnd_);
                    for (Population population : islands) {
                        int idx = islands.indexOf(population);
                        population.immigrate(fittest_exchanges.get(idx));
                    }
                }
            }
            epoch++;

        }
    }

    public ArrayList<ArrayList<Unit>> derange(ArrayList<ArrayList<Unit>> exchanges) {

        if (exchanges.size() == 1) {
            return exchanges;
        }

        ArrayList<ArrayList<Unit>> result = new ArrayList<ArrayList<Unit>>();
        Map<Integer,ArrayList<Unit>> indexUnitMap = new HashMap<Integer,ArrayList<Unit>>();
        int i = 0;
        for (ArrayList<Unit> exchange : exchanges) {
            indexUnitMap.put(i, exchange);
            i++;
        }

        ArrayList<Integer> keys = new ArrayList<>(indexUnitMap.keySet());
        ArrayList<Integer> new_keys = new ArrayList<>(indexUnitMap.keySet());
        int is_done = 0;
        do {
            int count = 0;
            Collections.shuffle(new_keys, rnd_);
            for (int k = 0; k < new_keys.size(); k++) {
                if (new_keys.get(k) == keys.get(k)) {
                    count++;
                }
            }
            if (count == 0) {
                is_done = 1;
            }
        } while (is_done == 0);

        for (Integer k : new_keys) {
            result.add(indexUnitMap.get(k));
        }
        return result;
    }
}
