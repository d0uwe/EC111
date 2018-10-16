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
JAVA_COMPILE = 'javac -cp contest.jar player111.java'
JAVAC = JAVA_COPY + ' && ' + JAVA_COMPILE

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
        '''
        url = 'http://mac360.few.vu.nl:8080/EC_BB_ASSIGNMENT/submit.html'
        files = {'file': (open('submission.jar','rb'), 'application/java-archive')}
        values = {'teamcode': '9PcFxEM=', 'contest': '/Users/eccomp/EC_BB_ASSIGNMENT', 'submit': 'Submit'}
        r = requests.post(url, files=files, data=values)
        print(r)
        '''
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

        if not os.path.exists(DIR):
            os.makedirs(DIR)

    def save(self, evaluation, filename):
        d = os.path.join(self.DIR, evaluation)
        if not os.path.exists(d):
            os.makedirs(d)
        savedir = os.path.join(d, filename + "-" + generate_timestamp() + '.pdf')
        plt.savefig(savedir)
        print("SAVED FILE: {}".format(savedir))

    def plot(self):
        self.plot_variance()
        self.plot_avg()
        self.plot_best()
        self.plot_islands()
        self.plot_islands_variance()


    def make_desc(self):
        s = ''
        for k,v in vars(self.args).items():
            s += '{}: {}\n'.format(k, v)
        return s

    def make_title(self):
        return "{}  - Pop: {}".format(self.frames[0]['evaluation'][0], self.frames[0]['pop_size'][0])

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
        plt.title(self.make_title())
        plt.figtext(0,0, self.make_desc())
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
        plt.title(self.make_title())
        plt.figtext(0,0, self.make_desc())
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
        plt.title(self.make_title())
        plt.figtext(0,0, self.make_desc())
        self.save(self.frames[0]['evaluation'][0], 'fitness_best')

    def plot_islands(self):
        fig = plt.figure()
        ax = fig.add_subplot(111)
        for i, f in enumerate(self.frames):
            gb = f.groupby(['island'])
            for t, group in gb:
                # for row, data in group.iterrows():
                ax.plot(group['eval'], group['fitness_best'], label='Island: {}'.format(t))
        # Shrink current axis by 20%
        box = ax.get_position()
        ax.set_position([box.x0, box.y0, box.width * 0.75, box.height])

        # Put a legend to the right of the current axis
        ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
        ax.set_xlabel("Eval")
        ax.set_ylabel("Fitness best")
        plt.title(self.make_title())
        plt.figtext(0,0, self.make_desc())
        self.save(self.frames[0]['evaluation'][0], 'fitness_islands')

    def plot_islands_variance(self):
        fig = plt.figure()
        ax = fig.add_subplot(111)
        for i, f in enumerate(self.frames):
            gb = f.groupby(['island'])
            for t, group in gb:
                # for row, data in group.iterrows():
                ax.plot(group['eval'], group['fitness_variance'], label='Island: {}'.format(t))
        # Shrink current axis by 20%
        box = ax.get_position()
        ax.set_position([box.x0, box.y0, box.width * 0.75, box.height])

        # Put a legend to the right of the current axis
        ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
        ax.set_xlabel("Eval")
        ax.set_ylabel("Fitness variance")
        plt.title(self.make_title())
        plt.figtext(0,0, self.make_desc())
        self.save(self.frames[0]['evaluation'][0], 'fitness_islands_variance')

if __name__ == '__main__':
    parser.add_argument('--defaults', action='store_true')
    parser.add_argument('--compile', action='store_true')
    parser.add_argument('--submit', action='store_true')
    parser.add_argument('--debug', type=int, default=0)
    parser.add_argument('--evaluation', type=str, default='SchaffersEvaluation')
    parser.add_argument('--log', type=int, default=1)
    parser.add_argument('--plot', action='store_true')
    parser.add_argument('--r', action='store_true')
    parser.add_argument('--m', type=int, default=0)
    parser.add_argument('--population', type=int, default=200)
    parser.add_argument('--survp', type=float, default=0.8)
    parser.add_argument('--islands', type=int, default=2)
    parser.add_argument('--immigrants', type=int, default=5)
    parser.add_argument('--epochs', type=int, default=70)
    parser.add_argument('--Cr', type=float, default=0.11)
    parser.add_argument('--F', type=float, default=0.4)

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
    for i in range(0, args.m+1):
        if args.r:
            rand = random.randrange(1, 32767)
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
        print("Average score: {}".format(score_sum/(args.m+1)))
        print("Average runtime: {}".format(runtime_sum/(args.m+1)))
    else:
        print(out)
    if args.log:
        program.log()
    if args.plot:
        vis.plot()
    print(err)


