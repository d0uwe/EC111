#!/bin/bash

# print "help" if h flag is specified, or if no arguments are given
if [[ "$1" == "-h" || "$1" == "" ]]
then
    echo "Usage: $0 [mode] [options]"
    echo "modes:"
    echo "  compile       compile the project"
    echo "  sphere        run code on \"sphere\" function"
    echo "  bent          run code on \"bent cigar\" function"
    echo ""
    echo "options:"
    echo "  d | debug     print debug messages"
    exit 0
fi

# parse the debug flag
if [[ "$2" == "d" || "$2" == "debug" ]]
then
    D=true
else
    D=false
fi

if [[ "$2" == "r" || "$2" == "random" || "$3" == "r" || "$3" == "random" ]]
then
    SEED=$RANDOM
else
    SEED=1
fi

# execute commands based on specified function
if [ "$1" == "compile" ]
then
    javac classes/structures/*.java && cp classes/structures/*.class contest/structures/ && jar cf contest.jar -C contest/ .
    javac -cp contest.jar player111.java
elif [ "$1" == "sphere" ]
then
    java -Ddebug=$D -jar testrun.jar -submission=player111 -evaluation=SphereEvaluation -seed=$SEED
elif [ "$1" == "bent" ]
then
    java -Ddebug=$D -jar testrun.jar -submission=player111 -evaluation=BentCigarFunction -seed=$SEED
elif [ "$1" == "katsuura" ]
then
    java -Ddebug=$D -jar testrun.jar -submission=player111 -evaluation=KatsuuraEvaluation -seed=$SEED
elif [ "$1" == "schaffers" ]
then
    java -Ddebug=$D -jar testrun.jar -submission=player111 -evaluation=SchaffersEvaluation -seed=$SEED
fi
