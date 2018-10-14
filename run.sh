#!/bin/bash

# print "help" if h flag is specified, or if no arguments are given
if [[ "$1" == "-h" || "$1" == "" ]]
then
    echo "Usage: $0 [mode] [options]"
    echo "modes:"
    echo "  compile         compile the project"
    echo "  sphere          run code on \"sphere\" function"
    echo "  bent            run code on \"bent cigar\" function"
    echo ""
    echo "options:"
    echo "  FLAG            POS    EXPLANATION"
    echo "  d | debug       2      print debug messages"
    echo "  r | random      2|3    generates a random seed"
    echo "  m | multiple    2&3    runs N times and averages"
    echo "                         enter N after the flag (with a space)"
    exit 0
fi

# parse the debug flag
if [[ "$2" == "d" || "$2" == "debug" ]]
then
    D=true
else
    D=false
fi

if [[ "$2" == "csv" ]]
then
    CSV=true
else
    CSV=false
fi

if [[ "$2" == "r" || "$2" == "random" || "$3" == "r" || "$3" == "random" ]]
then
    RND=""
else
    RND="nonempty"
fi

if [[ "$2" == "m" || "$2" == "multiple" ]]
then
    N="$3"
else
    N=1
fi

# store a command in $Q to execute later
if [ "$1" == "compile" ]
then
    javac classes/structures/*.java && cp classes/structures/*.class contest/structures/ && jar cf contest.jar -C contest/ .
    javac -cp contest.jar player111.java
    exit 0
elif [ "$1" == "sphere" ]
then
    QUERY="java -Ddebug=$D -Dcsv=$CSV -jar testrun.jar -submission=player111 -evaluation=SphereEvaluation -seed="
elif [ "$1" == "bent" ]
then
    QUERY="java -Ddebug=$D -Dcsv=$CSV  -jar testrun.jar -submission=player111 -evaluation=BentCigarFunction -seed="
elif [ "$1" == "katsuura" ]
then
    QUERY="java -Ddebug=$D -Dcsv=$CSV -jar testrun.jar -submission=player111 -evaluation=KatsuuraEvaluation -seed="
elif [ "$1" == "schaffers" ]
then
    QUERY="java -Ddebug=$D -Dcsv=$CSV -jar testrun.jar -submission=player111 -evaluation=SchaffersEvaluation -seed="
fi

# execute query $Q $N times
SUM=0
for (( i=0; i < $N; i++ ))
do
    # check if seed has to be random, or 1
    if [[ $RND ]] && [[ "$N" -le "1" ]]
    then
        OUTPUT=$QUERY"1"

        if [[ $CSV = "true" ]]
        then
            echo csv
            SUM=$($OUTPUT | tee 'data.csv' | awk '/^Score:/{print $2}' | (read TMP && echo $TMP+$SUM | bc))
        else
            echo notcsv
            SUM=$($OUTPUT | tee /dev/tty | awk '/^Score:/{print $2}' | (read TMP && echo $TMP+$SUM | bc))
        fi
        # echo "1"
        # TMP=$($QUERY"1" | awk '/^Score:/{print $2}') && SUM=$(echo $TMP+$SUM | bc)
    else
        # echo "2"
        SUM=$($QUERY$RANDOM | tee /dev/tty | awk '/^Score:/{print $2}' | (read TMP && echo $TMP+$SUM | bc))
        # TMP=$($QUERY$RANDOM | awk '/^Score:/{print $2}') && SUM=$(echo $TMP+$SUM | bc)
    fi
done
echo "average score: " $(echo "$SUM/$N" | bc -l)
