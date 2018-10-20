# EC111
Evolutionary Computing group project

generate results that are shown in the paper:
```
python3 wrapper.py --compile --paper
```

Compile:
```
python3 wrapper.py --compile
```

Create submission file:
```
python3 wrapper.py --submit
```
If you created additional java files, just add them after the player111.java file and include the compiled files after player111.class


show options of wrapper:
```
python3 wrapper.py --help
```

perform gridsearch:
Adapt the parameters that have to be tested on top of gridsearch.py and then:
```
python3 gridsearch.py
```


Test your code on the dummy function by typing:
```
java -jar testrun.jar -submission=player111 -evaluation=SphereEvaluation -seed=1
```

Test your code on one of the contest functions by typing:
```
java -jar testrun.jar -submission=player111 -evaluation=BentCigarFunction -seed=1
```

To investigate the effect of parameters, you can run:
```
java –Dvar1=0.5 -jar testrun.jar -submission=player111 - evaluation=BentCigarFunction -seed=1
```

## To add new classes for compiling:
Unzip the jar file to contest folder
```unzip contest.jar -d contest```

copy any class to the appropriate contest subfolder and create the jar again.
Also overwrite the current contest.jar in main folder
```
jar cf contest.jar -C contest/ .
```

One-liner for Unit.java (assuming you have a `contest` folder with all package structures:
```
javac classes/structures/Unit.java && cp classes/structures/Unit.class contest/structures/Unit.class && jar cf contest.jar -C contest/ .
```

It might be good to delete the folder all together afterwards too.
