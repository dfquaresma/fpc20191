#!/bin/bash
date
set -x

GREEN='\033[0;32m'
NC='\033[0m'

echo "NUMBER_OF_THREADS: ${NUMBER_OF_THREADS:=0 1 2 4 8 16 32 64 128 256 512 1024 2048}" # 4096 8192 16384 32768}, after 4096, java throw java.lang.OutOfMemoryError.
echo "NUMBER_OF_EXECUTION: ${NUMBER_OF_EXECUTION:=10}"

# Compiling the stuff
javac Main.java
mkdir -p results/

# Running for Java
for number_of_threads in ${NUMBER_OF_THREADS};
do
    echo "number_of_threads,memory_size_in_K" >> "results/java.csv"
    for expid in `seq 1 ${NUMBER_OF_EXECUTION}`;
    do
        echo -e "${GREEN}EXPERIMENT RUNNING: Java ${number_of_threads} threads${NC}"
        java Main ${number_of_threads} &
	    sleep 1
        echo -n "${number_of_threads}," >> "results/java.csv"
        ps v --pid=$! | tail -n 1 | awk -F " " '{ print $8 }' >> "results/java.csv"
    done;
done;

# Running for Go
for number_of_goroutines in ${NUMBER_OF_THREADS};
do
    echo "number_of_goroutines,memory_size_in_K" >> "results/go.csv"
    for expid in `seq 1 ${NUMBER_OF_EXECUTION}`;
    do
        echo -e "${GREEN}EXPERIMENT RUNNING: Go ${number_of_goroutines} goroutines${NC}"
        go run main.go ${number_of_goroutines} &
	    sleep 1
        echo -n "${number_of_goroutines}," >> "results/go.csv"
        ps v --pid=$! | tail -n 1 | awk -F " " '{ print $8 }' >> "results/go.csv"
    done;
done;

# Cleaning the stuff
rm -rf *.class
