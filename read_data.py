import pandas as pd
import matplotlib.pyplot as plt
import os

FILENAME = 'data.csv'
DIR = 'figures'

if not os.path.exists(DIR):
    os.makedirs(DIR)

# read csv, drop rows with NaN values.
df = pd.read_csv(FILENAME)
df.dropna(inplace=True)

print(df.columns)

fitness_var = df.plot(x='eval', y='fitness_variance').get_figure()
avg_fitness = df.plot(x='eval', y='avg_fitness').get_figure()

fitness_var.savefig(os.path.join(DIR, 'fitness_var.pdf'))
avg_fitness.savefig(os.path.join(DIR, 'avg_fitness.pdf'))


