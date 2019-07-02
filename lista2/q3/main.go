package main

import (
	"fmt"
	"time"
	"os"
	"strconv"
)

func main(){
	numberOfThreads, _ := strconv.Atoi(os.Args[1])
	gateway(numberOfThreads)
	fmt.Println("Finished")
}

func request(c chan int) {
	numberOfSecondsToSleep := 4
	time.Sleep(numberOfSecondsToSleep)
	c <- numberOfSecondsToSleep
}

func gateway(num_replicas int) {
	ch0 := make(chan int)
	
	for i := 0; i < num_replicas; i++ {
		go request(ch0)
    }
	
    for i := 0; i < num_replicas; i++ {
        <- ch0
    }
}
