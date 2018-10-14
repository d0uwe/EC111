import subprocess
import argparse
import os
import pandas as pd
from io import StringIO
import random
import re

parser = argparse.ArgumentParser(description='Evolutionary Computing')

DIR = 'output'

# APACHE = 'unzip commons-math3-3.6.1.jar -d commons-math'
JAVA_COPY = 'javac classes/structures/*.java && cp classes/structures/*.class contest/structures/ && jar cf contest.jar -C contest/ .'
JAVA_COMPILE = 'javac -cp contest.jar player111.java'
JAVAC = JAVA_COPY + ' && ' + JAVA_COMPILE


class Program():

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
            s += ["-D" + k + "=" + str(v)]
        s += ["-jar", "testrun.jar", "-submission=player111", "-evaluation="+evaluation, "-seed="+str(rand)]
        p = subprocess.Popen(s, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        return out.decode("utf-8"), err.decode("utf-8")


if __name__ == '__main__':
    parser.add_argument('--compile', action='store_true')
    parser.add_argument('--debug', type=int, default=0)
    parser.add_argument('--evaluation', type=str, default='BentCigarFunction')
    parser.add_argument('--csv', type=int, default=0)
    parser.add_argument('--r', action='store_true')
    parser.add_argument('--m', type=int, default=1)

    args = parser.parse_args()

    program = Program()
    if args.compile:
        out, err = program.compile()

    if args.r:
        rand = random.randrange(1, 32767)
    else:
        rand = 1

    experiments = []
    score_sum = 0
    runtime_sum = 0
    for i in range(0, args.m):
        out, err = program.run(vars(args), args.evaluation, rand)
        out = out.strip().split()
        score_sum += float(out[1])
        runtime_sum += int(re.sub(r"\D", "", out[3]))
        if args.csv:
            df = pd.read_csv(StringIO(out))
            experiments.append(df)

    print("Average score: {}".format(score_sum/args.m))
    print("Average runtime: {}".format(runtime_sum/args.m))
    print(err)



