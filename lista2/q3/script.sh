#!/bin/bash
date
set -x

GREEN='\033[0;32m'
NC='\033[0m'

echo "NUMBER_OF_THREADS: ${NUMBER_OF_THREADS:=0 1 128 1024}" # 2^0, 2^7, 2^10

# Compiling the stuff
javac Main.java
mkdir -p results/

# Running for Java
for number_of_threads in ${NUMBER_OF_THREADS};
do
    echo "memory_size_in_K"  >> results/java-${number_of_threads}-${expid}.csv
    for expid in `seq 1 100`;
    do
        echo -e "${GREEN}EXPERIMENT RUNNING: Java ${number_of_threads} threads${NC}"
        java Main ${number_of_threads} &
        pmap $! | tail -n 1 | awk '/[0-9]K/{print $2}' | sed 's/\K//g' >> results/java-${number_of_threads}-${expid}.csv
    done;
done;

# Running for Go
for number_of_goroutines in ${NUMBER_OF_THREADS};
do
    echo "memory_size_in_K" >> results/go-${number_of_goroutines}-${expid}.csv
    for expid in `seq 1 100`;
    do
        echo -e "${GREEN}EXPERIMENT RUNNING: Go ${number_of_goroutines} goroutines${NC}"
        go run main.go ${number_of_goroutines} &
        pmap $! | tail -n 1 | awk '/[0-9]K/{print $2}' | sed 's/\K//g' >> results/go-${number_of_goroutines}-${expid}.csv
    done;
done;

# Cleaning the stuff
rm -rf *.class
