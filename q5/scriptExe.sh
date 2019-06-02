#!/bin/bash
date
set -x

echo "EXPID_START: ${EXPID_START:=0}"
echo "EXPID_END: ${EXPID_END:=4}"
echo "READ_RATE: ${READ_RATE:= 0 0.25 0.5 0.75 1}"
echo "STRUCTURE_OPTION: ${STRUCTURE_OPTION:= concurrentHash synchronizedMap copyOnWriteList synchronizedList}"
echo "NUMBER_OF_THREADS: ${READ_RATE:= 1 128 512}"

for expid in `seq ${EXPID_START} ${EXPID_END}`
do
    for structure_option in ${STRUCTURE_OPTION}
    do
        for number_of_threads in ${NUMBER_OF_THREADS}
        do
            for read_rate in ${READ_RATE}
            do
                java Main ${structure_option} ${number_of_threads} ${read_rate} > exp${expid}-${structure_option}-threads${number_of_threads}-rate${read_rate}.log
            done
        done
    done
done
