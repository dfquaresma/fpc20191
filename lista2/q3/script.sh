#!/bin/bash
date
set -x

GREEN='\033[0;32m'
NC='\033[0m'

echo "NUMBER_OF_THREADS: ${NUMBER_OF_THREADS:=0 1 2 4 8 16 32 64 128 256 512 1024 2048 4096 8192 16384 32768}"
echo "NUMBER_OF_EXECUTION: ${NUMBER_OF_EXECUTION:=5}"

# Compiling the stuff
javac Main.java
mkdir -p results/

# Running for Java
for number_of_threads in ${NUMBER_OF_THREADS};
do
    echo "memory_size_in_K" >> results/java-${number_of_threads}.csv
    for expid in `seq 1 ${NUMBER_OF_EXECUTION}`;
    do
        echo -e "${GREEN}EXPERIMENT RUNNING: Java ${number_of_threads} threads${NC}"
        java Main ${number_of_threads} &
	sleep 1
        ps v --pid=$! | tail -n 1 | awk -F " " '{ print $8 }' >> results/java-${number_of_threads}.csv
    done;
done;

# Running for Go
for number_of_goroutines in ${NUMBER_OF_THREADS};
do
    echo "memory_size_in_K" >> results/go-${number_of_goroutines}.csv
    for expid in `seq 1 ${NUMBER_OF_EXECUTION}`;
    do
        echo -e "${GREEN}EXPERIMENT RUNNING: Go ${number_of_goroutines} goroutines${NC}"
        go run main.go ${number_of_goroutines} &
	sleep 1
        ps v --pid=$! | tail -n 1 | awk -F " " '{ print $8 }' >> results/go-${number_of_goroutines}.csv
    done;
done;

# Cleaning the stuff
rm -rf *.class
