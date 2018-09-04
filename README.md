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