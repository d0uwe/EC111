import subprocess
import itertools
import collections
import numpy as np
import progressbar
from multiprocessing import Pool, Queue, Manager, Process

# How many results to print:
np.random.seed(5000)
print_n_best = 100
n_jobs = 1
n_seeds = 10
evaluation = "-evaluation=SchaffersEvaluation"

# (var_name, min, max, stepsize)
pop = ("pop", 100, 1000, 100)
F = ("F", 0.1, 0.9, 0.1)
Cr = ("Cr", 0.1, 0.9, 0.1)
#var2 = ("survp", 0.5, 0.9, 0.05)


# add above variables to a list.
var_list = [pop, F, Cr]


def chunkIt(seq, num):
    avg = len(seq) / float(num)
    out = []
    last = 0.0
    while last < len(seq):
        out.append(seq[int(last):int(last + avg)])
        last += avg
    return out

def getScores(all_combinations, all_var_names, progress_q):
	scores = []
	for combination in all_combinations:
		total_score = 0
		for seed in seeds:
			# create commandline command
			strings = ["java"]
			for number, name in zip(combination, all_var_names):
				strings += ["-D" + name + "=" + str(number)]
			strings += ["-jar", "testrun.jar", "-submission=player111", evaluation, "-seed=" + str(seed)]

			process = subprocess.Popen(strings, stdout=subprocess.PIPE)
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


# create all combinations possible given the settings
seeds = [np.random.randint(100000) for _ in range(n_seeds)]
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

results = pool1.starmap(getScores, zip(chuncked_combs, n_jobs * [all_var_names], n_jobs * [q]))

p.join()
pool1.close()

# concat all results from the different threads
scores = [item for sublist in results for item in sublist]
printScores(scores)
