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

plt.switch_backend('agg')

parser = argparse.ArgumentParser(description='Evolutionary Computing')

DIR = 'log'

# APACHE = 'unzip commons-math3-3.6.1.jar -d commons-math'
JAVA_COPY = 'javac classes/structures/*.java && cp classes/structures/*.class contest/structures/ && jar cf contest.jar -C contest/ .'
JAVA_COMPILE = 'javac -cp contest.jar player111.java'
JAVAC = JAVA_COPY + ' && ' + JAVA_COMPILE

def generate_timestamp():
    return datetime.now().strftime("%Y%m%d-%H%M%S.%f")

class Program():

    frames = []

    def __init__(self):
        if not os.path.exists(DIR):
            os.makedirs(DIR)

    def compile(self):
        p = subprocess.Popen(JAVAC, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        return out.decode('utf-8'), err.decode('utf-8')

    def run(self, arg_dict, evaluation, rand):
        s = ["java"]
        for k, v in arg_dict.items():
            v = 1 if k == 'log' and v == True else 0
            s += ["-D" + k + "=" + str(v)]
        s += ["-jar", "testrun.jar", "-submission=player111", "-evaluation="+evaluation, "-seed="+str(rand)]
        p = subprocess.Popen(s, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        return out.decode("utf-8"), err.decode("utf-8")

    def log(self):
        with open(os.path.join(DIR, generate_timestamp() + '.p'), 'wb') as f:
            pickle.dump(self.frames, f)

class Visualization(Program):

    def __init__(self, args):
        self.DIR = 'figures'
        self.args = args

        if not os.path.exists(DIR):
            os.makedirs(DIR)

    def save(self, evaluation, filename):
        d = os.path.join(self.DIR, evaluation)
        if not os.path.exists(d):
            os.makedirs(d)
        plt.savefig(os.path.join(d, filename + "-" + generate_timestamp() + '.pdf'))

    def plot(self):
        self.plot_variance()
        self.plot_avg()
        self.plot_best()

    def plot_variance(self):
        print("PLOTTING {} FITNESS VARIANCE FRAMES".format(len(self.frames)))
        fig = plt.figure()
        ax = fig.add_subplot(111)
        for i, f in enumerate(self.frames):
            ax.plot(f['eval'], f['fitness_variance'], label='{}, seed: {}'.format(i, f['seed'][0]))
        box = ax.get_position()
        ax.set_position([box.x0, box.y0, box.width * 0.75, box.height])

        # Put a legend to the right of the current axis
        ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))

        ax.set_xlabel("Eval")
        ax.set_ylabel("Fitness variance")
        plt.title("Fitness variance - {}".format(self.frames[0]['evaluation'][0]))
        self.save(self.frames[0]['evaluation'][0], 'fitness_variance')

    def plot_avg(self):
        print("PLOTTING {} FITNESS AVG FRAMES".format(len(self.frames)))
        fig = plt.figure()
        ax = fig.add_subplot(111)
        for i, f in enumerate(self.frames):
            ax.plot(f['eval'], f['fitness_avg'], label='{}, seed: {}'.format(i, f['seed'][0]))
        box = ax.get_position()
        ax.set_position([box.x0, box.y0, box.width * 0.75, box.height])

        # Put a legend to the right of the current axis
        ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
        ax.set_xlabel("Eval")
        ax.set_ylabel("Fitness average")
        plt.title("Fitness average - {}".format(self.frames[0]['evaluation'][0]))
        self.save(self.frames[0]['evaluation'][0], 'fitness_avg')

    def plot_best(self):
        print("PLOTTING {} FITNESS BEST FRAMES".format(len(self.frames)))
        fig = plt.figure()
        ax = fig.add_subplot(111)
        for i, f in enumerate(self.frames):
            ax.plot(f['eval'], f['fitness_best'], label='{}, seed: {}'.format(i, f['seed'][0]))
        # Shrink current axis by 20%
        box = ax.get_position()
        ax.set_position([box.x0, box.y0, box.width * 0.75, box.height])

        # Put a legend to the right of the current axis
        ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
        ax.set_xlabel("Eval")
        ax.set_ylabel("Fitness best")
        plt.title("Fitness best - {}".format(self.frames[0]['evaluation'][0]))
        self.save(self.frames[0]['evaluation'][0], 'fitness_best')


if __name__ == '__main__':
    parser.add_argument('--compile', action='store_true')
    parser.add_argument('--debug', type=int, default=0)
    parser.add_argument('--evaluation', type=str, default='SchaffersEvaluation')
    parser.add_argument('--log', type=bool, default=True)
    parser.add_argument('--plot', action='store_true')
    parser.add_argument('--r', action='store_true')
    parser.add_argument('--m', type=int, default=0)

    args = parser.parse_args()
    program = Program()
    if args.compile:
        out, err = program.compile()
        print(out)
        print(err)

    vis = Visualization(args)
    score_sum = 0
    runtime_sum = 0
    for i in range(0, args.m+1):
        if args.r:
            rand = random.randrange(1, 32767)
        else:
            rand = 1

        out, err = program.run(vars(args), args.evaluation, rand)
        if args.log:
            df = pd.read_csv(StringIO(out))
            df.dropna(inplace=True)
            df['seed'] = rand
            df['evaluation'] = args.evaluation
            program.frames.append(df)
        if args.m:
            out = out.strip().split()
            score_sum += float(out[-3])
            runtime_sum += int(re.sub(r"\D", "", out[-1]))
    if args.m:
        print("Average score: {}".format(score_sum/(args.m+1)))
        print("Average runtime: {}".format(runtime_sum/(args.m+1)))
    else:
        print(out)

    if args.log:
        program.log()
    if args.plot:
        vis.plot()
    print(err)


