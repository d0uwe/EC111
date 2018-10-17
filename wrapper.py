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

        print(' '.join(s))
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
        ax = self.fig.add_subplot(411)
        for i, f in enumerate(self.frames):
            gb = f.groupby(['island'])
            for t, group in gb:
                # for row, data in group.iterrows():
                ax.plot(group['eval'], group['fitness_best'], label='Island: {}'.format(t))
        ax.set_ylabel("Fitness best")

    def plot_islands_variance(self):
        ax = self.fig.add_subplot(412)
        for i, f in enumerate(self.frames):
            gb = f.groupby(['island'])
            for t, group in gb:
                # for row, data in group.iterrows():
                ax.plot(group['eval'], group['fitness_variance'], label='Island: {}'.format(t))
        ax.set_ylabel("Fitness variance")

    def plot_sigma_avg(self):
        ax = self.fig.add_subplot(413)
        for i, f in enumerate(self.frames):
            gb = f.groupby(['island'])
            for t, group in gb:
                # for row, data in group.iterrows():
                ax.plot(group['eval'], group['sigma_avg'], label='Island: {}'.format(t))
        ax.set_ylabel("Sigma avg")

    def plot_text(self):
        ax = self.fig.add_subplot(414)
        ax.figure.set_size_inches(10, 30)
        ax.text(0,0,self.make_desc())
        ax.set_xticks([])
        ax.set_yticks([])

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
    parser.add_argument('--t', action='store_true')



    args = parser.parse_args()
    program = Program()
    if args.compile or args.submit:
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

    vis = Visualization(args)
    score_sum = 0
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
        print(out)
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
        print("Average score: {}".format(score_sum/(args.m)))
        print("Average runtime: {}".format(runtime_sum/(args.m)))
    if args.log:
        program.log()
    if args.plot:
        vis.plot()
    print(err)
