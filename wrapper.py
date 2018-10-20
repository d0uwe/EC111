import subprocess
import argparse
import os
import pandas as pd
from io import StringIO
import random
import re
import matplotlib.pyplot as plt
from datetime import datetime
import pickle
# import requests

# Gridsearch
import itertools
import collections
import numpy as np
import progressbar
from multiprocessing import Pool, Queue, Manager, Process

np.random.seed(5000)
print_n_best = 100
n_jobs = 8
n_seeds = 50
seeds = [np.random.randint(100000) for _ in range(n_seeds)]
evaluation="-evaluation=BentCigarFunction"
# (var_name, min, max, stepsize)
pop = ("pop", 100, 1000, 100)
#F = ("F", 0.1, 0.9, 0.1)
Cr = ("Cr", 0.1, 0.2, 0.01)
#var2 = ("survp", 0.5, 0.9, 0.05)
var_list = [pop, Cr]

plt.switch_backend('agg')

parser = argparse.ArgumentParser(description='Evolutionary Computing')

DIR = 'log'

# APACHE = 'unzip commons-math3-3.6.1.jar -d commons-math'
JAVA_COPY = 'javac classes/structures/*.java && cp classes/structures/*.class contest/structures/ && jar cf contest.jar -C contest/ .'
CONCAT_JARS = 'mkdir -p lib && (cd lib; unzip -uo ../contest.jar) && (cd lib; unzip -uo ../commons-math3-3.6.1.jar)'
CONCAT_JARS_2 = 'rm contest.jar && jar -cvf contest.jar -C lib .'
JAVA_COMPILE = 'javac -cp contest.jar player111.java'
# JAVAC = JAVA_COPY + ' && ' + CONCAT_JARS + ' && ' + CONCAT_JARS_2 + ' && ' + JAVA_COMPILE
JAVAC = JAVA_COPY + ' && ' + JAVA_COMPILE


# JAVA_SUBMISSION = 'rm -f submission.jar && ' + CONCAT_JARS + ' && ' + 'jar cmf MainClass.txt submission.jar player111.class lib'
JAVA_SUBMISSION = 'rm -f submission.jar && cp -r contest/structures . && jar cmf MainClass.txt submission.jar player111.class structures && rm -rf structures'

def chunkIt(seq, num):
    avg = len(seq) / float(num)
    out = []
    last = 0.0
    while last < len(seq):
        out.append(seq[int(last):int(last + avg)])
        last += avg
    return out

def getScores(all_combinations, all_var_names, progress_q, process):
    scores = []
    DIR = './process-' + str(process)
    if not os.path.exists(DIR):
        os.makedirs(DIR)
    for combination in all_combinations:
        total_score = 0
        for seed in seeds:
            # create commandline command
            strings = ["java"]
            for number, name in zip(combination, all_var_names):
                strings += ["-D" + name + "=" + str(number)]
            strings += ["-jar", "../testrun.jar", "-submission=player111", evaluation, "-seed=" + str(seed)]

            process = subprocess.Popen(strings, stdout=subprocess.PIPE, cwd=DIR)
            out, err = process.communicate()
            score = float(str(out).split(":")[1].split("\\n")[0])
            total_score += score
            progress_q.put(1)

        scores += [(combination, total_score / n_seeds)]
    progress_q.put(0)
    return scores

def printScores(scores):
    scores.sort(key=lambda x: x[1])
    scores = scores[::-1]
    print()
    for score in scores[:print_n_best]:
        for var, value in zip(all_var_names, score[0]):
            print(var,":", value, end=" ")
        print("\tscored:", score[1])

def progressBar(max_value, queue):
    bar = progressbar.ProgressBar(max_value=max_value).start()
    finished = 0
    while finished < n_jobs:
        if queue.get() == 1:
            bar += 1
        else:
            finished += 1

def gridsearch():
    # create all combinations possible given the settings
    all_var_names = [x[0] for x in var_list]
    all_lists = [np.arange(x[1], x[2], x[3]) for x in var_list]
    all_combinations = list(itertools.product(*all_lists))
    chuncked_combs = chunkIt(all_combinations, n_jobs)

    # multiprocess gridsearch and have a seperate thread for the progress bar.
    pool1 = Pool(processes = n_jobs)
    m = Manager()
    q = m.Queue()
    p = Process(target=progressBar, args=(len(all_combinations) * n_seeds, q,))
    p.start()


    program = Program()
    process_list = np.arange(n_jobs)
    results = pool1.starmap(getScores, zip(chuncked_combs, n_jobs * [all_var_names], n_jobs * [q], process_list))

    p.join()
    pool1.close()

    # concat all results from the different threads
    scores = [item for sublist in results for item in sublist]
    printScores(scores)



def generate_timestamp():
    return datetime.now().strftime("%Y%m%d-%H%M%S.%f")

class Program():

    frames = []

    def __init__(self):
        if not os.path.exists(DIR):
            os.makedirs(DIR)

    def submit(self):
        p = subprocess.Popen(JAVA_SUBMISSION, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        return out.decode('utf-8'), err.decode('utf-8')


    def compile(self):
        p = subprocess.Popen(JAVAC, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        return out.decode('utf-8'), err.decode('utf-8')

    def run(self, arg_dict, evaluation, rand):
        s = ["java"]

        if arg_dict['defaults'] == False:
            for k, v in arg_dict.items():
                if k == 'log' and v == True:
                    v = 1
                if k == 'islands' and v == True:
                    v = 1
                s += ["-D" + k + "=" + str(v)]
        s += ["-jar", "testrun.jar", "-submission=player111", "-evaluation="+evaluation, "-seed="+str(rand)]
        if arg_dict['nosec'] == True:
            s += ["-nosec"]

        # print(' '.join(s))
        p = subprocess.Popen(s, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        return out.decode("utf-8"), err.decode("utf-8")

    def log(self):
        pass
        #with open(os.path.join(DIR, generate_timestamp() + '.p'), 'wb') as f:
        #    pickle.dump(self.frames, f)

class Visualization(Program):

    def __init__(self, args):
        self.DIR = 'figures'
        self.args = args
        self.fig = plt.figure(frameon=False)
        if not os.path.exists(DIR):
            os.makedirs(DIR)

    def save(self, evaluation, filename):
        d = os.path.join(self.DIR, evaluation)
        if not os.path.exists(d):
            os.makedirs(d)
        savedir = os.path.join(d, filename + "-" + generate_timestamp() + '.pdf')
        self.fig.savefig(savedir, frameon=False)
        print("SAVED FILE: {}".format(savedir))

    def plot(self):
        '''
        self.plot_variance()
        self.plot_avg()
        self.plot_best()
        '''
        plt.title(self.make_title())
        plt.xticks([])
        plt.yticks([])
        plt.axis('off')

        self.plot_islands()
        self.plot_islands_variance()
        self.plot_sigma_avg()
        self.plot_euclidean_avg()
        self.plot_cosine_avg()
        self.plot_text()
        self.save(self.frames[0]['evaluation'][0], 'plots')
        # self.fig.show()

    def make_desc(self):
        s = ''
        for k,v in vars(self.args).items():
            s += '{}: {}\n'.format(k, v)
        return s

    def make_title(self):
        return "{}  - Pop: {}".format(self.frames[0]['evaluation'][0], self.frames[0]['pop_size'][0])

    def plot_variance(self):
        print("PLOTTING {} FITNESS VARIANCE FRAMES".format(len(self.frames)))
        ax = self.fig.add_subplot(611)
        for i, f in enumerate(self.frames):
            ax.plot(f['eval'], f['fitness_variance'], label='{}, seed: {}'.format(i, f['seed'][0]))
        ax.set_ylabel("Fitness variance")

    def plot_avg(self):
        print("PLOTTING {} FITNESS AVG FRAMES".format(len(self.frames)))
        ax = self.fig.add_subplot(612)
        for i, f in enumerate(self.frames):
            ax.plot(f['eval'], f['fitness_avg'], label='{}, seed: {}'.format(i, f['seed'][0]))
        ax.set_ylabel("Fitness average")

    def plot_best(self):
        print("PLOTTING {} FITNESS BEST FRAMES".format(len(self.frames)))
        ax = self.fig.add_subplot(613)
        for i, f in enumerate(self.frames):
            ax.plot(f['eval'], f['fitness_best'], label='{}, seed: {}'.format(i, f['seed'][0]))
        ax.set_ylabel("Fitness best")

    def plot_islands(self):
        ax = self.fig.add_subplot(611)
        for i, f in enumerate(self.frames):
            gb = f.groupby(['island'])
            for t, group in gb:
                # for row, data in group.iterrows():
                ax.plot(group['eval'], group['fitness_best'], label='Island: {}'.format(t))
        ax.set_ylabel("Fitness best")

    def plot_islands_variance(self):
        ax = self.fig.add_subplot(612)
        for i, f in enumerate(self.frames):
            gb = f.groupby(['island'])
            for t, group in gb:
                # for row, data in group.iterrows():
                ax.plot(group['eval'], group['fitness_variance'], label='Island: {}'.format(t))
        ax.set_ylabel("Fitness variance")

    def plot_sigma_avg(self):
        ax = self.fig.add_subplot(613)
        for i, f in enumerate(self.frames):
            gb = f.groupby(['island'])
            for t, group in gb:
                # for row, data in group.iterrows():
                ax.plot(group['eval'], group['sigma_avg'], label='Island: {}'.format(t))
        ax.set_ylabel("Sigma avg")

    def plot_euclidean_avg(self):
        ax = self.fig.add_subplot(614)
        for i, f in enumerate(self.frames):
            gb = f.groupby(['island'])
            for t, group in gb:
                # for row, data in group.iterrows():
                ax.plot(group['eval'], group['euclidean_avg'], label='Island: {}'.format(t))
        ax.set_ylabel("Euclidean avg")

    def plot_cosine_avg(self):
        ax = self.fig.add_subplot(615)
        for i, f in enumerate(self.frames):
            gb = f.groupby(['island'])
            for t, group in gb:
                # for row, data in group.iterrows():
                ax.plot(group['eval'], group['cosine_avg'], label='Island: {}'.format(t))
        ax.set_ylabel("Cosine Similarity avg")


    def plot_text(self):
        ax = self.fig.add_subplot(616)
        ax.figure.set_size_inches(10, 30)
        ax.text(0,0,self.make_desc())
        ax.set_xticks([])
        ax.set_yticks([])


    def plot_all(self, frames):
        for idx, f in enumerate(frames):
            fig, ax1 = plt.subplots()
            color = 'tab:red'
            ax1.set_xlabel('eval (t)')
            ax1.set_ylabel('best fitness score', color=color)
            ax1.set_ylim(0, 10)
            ax1.tick_params(axis='y', labelcolor=color)
            ax1.plot(f[3], f[4], color=color)

            ax2 = ax1.twinx()
            color = 'tab:blue'
            ax2.set_ylabel('cosine similarity', color=color)
            ax2.set_ylim(0, 1)
            ax2.tick_params(axis='y', labelcolor=color)
            ax2.plot(f[3], f[5], color=color)

            title = '{}\n{}{}'.format(f[0], 'DE / ' if f[1] else 'Baseline / ', 'Island Model / i=' + str(f[2]) if f[2] > 1 else 'Non-parallel')
            plt.figtext(.5,.9,title, fontsize=16, ha='center')
            fig.savefig('paper-{}-{}.pdf'.format(f[0], idx))




def set_args(args):
    if args.evaluation == 'BentCigarFunction':
        # Diff evo
        if args.diffevo:
            args.Cr = 0.11
            args.F = 1.0
            args.population = 50
            args.survp = 0.75
            args.sigma = 0.05
            args.expfactor = 5.0
        # Baseline
        else:
            args.population = 30
            args.survp = 0.5
            args.sigma = 0.1
            args.expfactor = 4.0
        # Islands
        if args.islands > 1:
            args.population = 60


    elif args.evaluation == 'SchaffersEvaluation':
        # Diff evo
        if args.diffevo:
            args.Cr = 0.11
            args.F = 0.4
            args.population = 200
            args.survp = 0.8
            args.sigma = 0.1
            args.expfactor = 5.0
        # Baseline
        else:
            args.population = 200
            args.survp = 0.8
            args.sigma = 0.1
            args.expfactor = 4.0
        # Islands
        if args.islands > 1:
            args.islands = 2
            args.population = 200

    elif args.evaluation == 'KatsuuraEvaluation':
       # Diff evo
        if args.diffevo:
            args.Cr = 0.11
            args.F = 1.0
            args.population = 50
            args.survp = 0.999
            args.sigma = 0.005
            args.expfactor = 4.0
        # Baseline
        else:
            args.population = 50
            args.survp = 0.999
            args.sigma = 0.005
            args.expfactor = 4.0
        # Islands
        if args.islands > 1:
            args.population = 4000
            args.islands = 100
            args.immigrants = 5
            args.epochs = 70
    return args


def run(args, program, vis_eval):
    vis = Visualization(args)

    best_fitness = []
    cosines = []
    evals = []

    score_sum = 0
    scores = []
    runtime_sum = 0

    test_seeds = [1150, 6950, 6756, 2301, 3279, 114, 4089, 61, 6797, 19]
    if args.t:
        args.m = 10

    repeats = args.m
    if args.t:
        repeats = 10
    for i in range(0, repeats):
        if args.r:
            rand = random.randrange(1, 32767)
        elif args.t:
            rand = test_seeds[i]
        else:
            rand = 1

        out, err = program.run(vars(args), args.evaluation, rand)
        if args.log:
            df = pd.read_csv(StringIO(out))
            df.dropna(inplace=True)
            df['seed'] = rand
            df['evaluation'] = args.evaluation
            program.frames.append(df)
            evals.append(df['eval'].tolist())
            best_fitness.append(df['fitness_best'].tolist())
            cosines.append(df['cosine_avg'].tolist())
        if args.m:
            out = out.strip().split()
            scores += [float(out[-3])]
            score_sum += float(out[-3])
            runtime_sum += int(re.sub(r"\D", "", out[-1]))

    if args.m:
        print("Average score: {}".format(score_sum/(args.m)))
        print("Average runtime: {}".format(runtime_sum/(args.m)))
    if args.t or args.m:
        print("std: {}".format(np.std(scores)))

    if args.log:
        program.log()
    if args.plot:
        vis.plot()
    print(err)

    evals_avg = list(np.average(np.array(evals).astype(np.int), axis=0))
    best_avg = list(np.average(np.array(best_fitness).astype(np.float), axis=0))
    cosines_avg = list(np.average(np.array(cosines).astype(np.float), axis=0))
    return [args.evaluation, args.diffevo, args.islands, evals_avg, best_avg, cosines_avg]



if __name__ == '__main__':
    parser.add_argument('--islands', type=int, default=1)
    parser.add_argument('--diffevo', type=int, default=0)
    parser.add_argument('--defaults', action='store_true')
    parser.add_argument('--compile', action='store_true')
    parser.add_argument('--submit', action='store_true')
    parser.add_argument('--debug', type=int, default=0)
    parser.add_argument('--evaluation', type=str, default='SchaffersEvaluation')
    parser.add_argument('--log', type=int, default=0)
    parser.add_argument('--plot', action='store_true')
    parser.add_argument('--r', action='store_true')
    parser.add_argument('--m', type=int, default=1)
    parser.add_argument('--population', type=int, default=400)
    parser.add_argument('--survp', type=float, default=0.8)
    parser.add_argument('--immigrants', type=int, default=5)
    parser.add_argument('--epochs', type=int, default=70)
    parser.add_argument('--Cr', type=float, default=0.11)
    parser.add_argument('--F', type=float, default=0.4)
    parser.add_argument('--nosec', action='store_true')
    parser.add_argument('--sigma', type=float, default=0.1)
    parser.add_argument('--expfactor', type=float, default=4.0)
    parser.add_argument('--t', action='store_true')
    parser.add_argument('--gridsearch', action='store_true')
    parser.add_argument('--paper', action='store_true')


    program = Program()
    args = parser.parse_args()
    if args.paper:
        args.log = 1
        args.t = 1
    vis_eval = Visualization(args)
    if args.compile or args.submit or args.gridsearch:
        print("### COMPILING ###")
        out, err = program.compile()
        if err:
            print(err)
            exit(1)
        print(out)

    if args.submit:
        print("### Creating submission.jar ###")
        out, err = program.submit()
        if err:
            print(err)
            exit(1)
        print(out)
        exit(0)

    if args.gridsearch:
        gridsearch()
        exit(0)

    all_frames = []
    for i in range(0,12):
        if i == 0:
            args.evaluation = 'BentCigarFunction'
            args.diffevo = 0
            args.islands = 1
        if i == 1:
            args.evaluation = 'BentCigarFunction'
            args.diffevo = 1
            args.islands = 1
        if i == 2:
            args.evaluation = 'BentCigarFunction'
            args.diffevo = 0
            args.islands = 2
        if i == 3:
            args.evaluation = 'BentCigarFunction'
            args.diffevo = 1
            args.islands = 2
        if i == 4:
            args.evaluation = 'SchaffersEvaluation'
            args.diffevo = 0
            args.islands = 1
        if i == 5:
            args.evaluation = 'SchaffersEvaluation'
            args.diffevo = 1
            args.islands = 1
        if i == 6:
            args.evaluation = 'SchaffersEvaluation'
            args.diffevo = 0
            args.islands = 2
        if i == 7:
            args.evaluation = 'SchaffersEvaluation'
            args.diffevo = 1
            args.islands = 2
        if i == 8:
            break
            args.evaluation = 'KatsuuraEvaluation'
            args.diffevo = 0
            args.islands = 1
        if i == 9:
            args.evaluation = 'KatsuuraEvaluation'
            args.diffevo = 1
            args.islands = 1
        if i == 10:
            args.evaluation = 'KatsuuraEvaluation'
            args.diffevo = 0
            args.islands = 2
        if i == 11:
            args.evaluation = 'KatsuuraEvaluation'
            args.diffevo = 1
            args.islands = 2

        print('### EVAL: {}, DE: {}, IM: {}'.format(args.evaluation, args.diffevo, args.islands))
        args = set_args(args)
        results = run(args, program, vis_eval)
        all_frames.append(results)
    vis_eval.plot_all(all_frames)
    with open('research_dataframes', 'wb') as f:
        pickle.dump(all_frames, f)
