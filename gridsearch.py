import subprocess
import itertools
import collections
import numpy as np
import progressbar

pop_max = 200
pop_min = 50
pop_step = 20
pop_list = np.arange(pop_min, pop_max, pop_step)

var2_max = 20
var2_min = 2
var2_step = 10
var2_list = np.arange(var2_min, var2_max, var2_step)

all_var_names = ["pop", "var2"]
all_lists = [pop_list, var2_list]
all_combinations = list(itertools.product(*all_lists))


bar = progressbar.ProgressBar(max_value=len(all_combinations)).start()
scores = []
for combination in all_combinations:
	strings = ["java"]
	for number, name in zip(combination, all_var_names):
		strings += ["-D" + name + "=" + str(number)]
	strings += ["-jar", "testrun.jar", "-submission=player111", "-evaluation=SphereEvaluation", "-seed=1"]

	process = subprocess.Popen(strings, stdout=subprocess.PIPE)
	out, err = process.communicate()
	score = float(str(out).split(":")[1].split("\\n")[0])
	scores += [(combination, score)]
	bar+=1


scores += [("potato", 6)]
scores += [("potato2", 7)]

scores.sort(key=lambda x: x[1])
scores = scores[::-1]
print(scores[:10])
