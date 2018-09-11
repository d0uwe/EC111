#!/bin/bash
if [ "$1" == "compile" ]
then
    javac classes/structures/*.java && cp classes/structures/*.class contest/structures/ && jar cf contest.jar -C contest/ .
elif [ "$1" == "sphere" ]
then
    java -jar testrun.jar -submission=player111 -evaluation=SphereEvaluation -seed=1
elif [ "$1" == "bent" ]
then
    java -jar testrun.jar -submission=player111 -evaluation=BentCigarFunction -seed=1
fi
