#!/bin/bash
date
set -x

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "EXPID_START: ${EXPID_START:=0}"
echo "EXPID_END: ${EXPID_END:=4}"
echo "STRUCTURE_OPTION: ${STRUCTURE_OPTION:= concurrentHash synchronizedMap copyOnWriteList synchronizedList}"
echo "NUMBER_OF_THREADS: ${NUMBER_OF_THREADS:= 1 128 512}"
echo "READ_RATE: ${READ_RATE:= 0 0.25 0.5 0.75 1}"

for expid in `seq ${EXPID_START} ${EXPID_END}`;
do
    echo ${expid}
    for structure_option in ${STRUCTURE_OPTION};
    do
        echo ${structure_option}
        for number_of_threads in ${NUMBER_OF_THREADS};
        do
            echo ${number_of_threads}
            for read_rate in ${READ_RATE};
            do
                echo ${read_rate}
                java Main ${structure_option} ${number_of_threads} ${read_rate}
            done;
        done;
    done;
done
