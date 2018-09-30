import subprocess
import itertools
import collections
import numpy as np
import progressbar
from multiprocessing import Pool, Queue, Manager, Process

# How many results to print:
print_n_best = 10
n_jobs = 4
evaluation = "-evaluation=SphereEvaluation"

# (var_name, min, max, stepsize)
pop = ("pop", 50, 200, 2)
var2 = ("var2", 2, 20, 10)

# add above variables to a list.
var_list = [pop, var2]


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
		# create commandline command
		strings = ["java"]
		for number, name in zip(combination, all_var_names):
			strings += ["-D" + name + "=" + str(number)]
		strings += ["-jar", "testrun.jar", "-submission=player111", evaluation, "-seed=1"]

		process = subprocess.Popen(strings, stdout=subprocess.PIPE)
		out, err = process.communicate()
		score = float(str(out).split(":")[1].split("\\n")[0])
		scores += [(combination, score)]
		progress_q.put(1)
	progress_q.put(0)
	return scores

def printScores(scores):
	scores.sort(key=lambda x: x[1])
	scores = scores[::-1]
	print(scores[:10])
	for score in scores[:print_n_best]:
		for var, value in zip(all_var_names, score[0]):
			print(var,":", value, end=" ")
		print("\tscored:", score[1])

def progressBar(max_value, queue):
	bar = progressbar.ProgressBar(max_value=len(all_combinations)).start()
	finished = 0
	while finished < n_jobs:
		if queue.get() == 1:
			bar += 1
		else:
			finished += 1


# create all combinations possible given the settings
all_var_names = [x[0] for x in var_list]
all_lists = [np.arange(x[1], x[2], x[3]) for x in var_list]
all_combinations = list(itertools.product(*all_lists))
chuncked_combs = chunkIt(all_combinations, n_jobs)

# multiprocess gridsearch and have a seperate thread for the progress bar.
pool1 = Pool(processes = n_jobs)
m = Manager()
q = m.Queue()
p = Process(target=progressBar, args=(len(all_combinations), q,))
p.start()

results = pool1.starmap(getScores, zip(chuncked_combs, n_jobs * [all_var_names], n_jobs*[q]))

p.join()
pool1.close()

# concat all results from the different threads
scores = [item for sublist in results for item in sublist]
printScores(scores)
