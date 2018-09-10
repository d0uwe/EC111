# EC111
Evolutionary Computing group project

Compile:
```
javac -cp contest.jar player111.java
```

Create submission file:
```
jar cmf MainClass.txt submission.jar player111.class
```
If you created additional java files, just add them after the player111.java file and include the compiled files after player111.class



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
