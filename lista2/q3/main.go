package main

import (
	"fmt"
	"time"
	"os"
	"strconv"
)

func main(){
	numberOfThreads, _ := strconv.Atoi(os.Args[1])
	if numberOfThreads == 0 {
		sleep()
	} else {
		gateway(numberOfThreads)
	}
}

func request(c chan int) {
	sleptTime := sleep()
	c <- sleptTime
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

func sleep() int {
	numberOfSecondsToSleep := 1
	time.Sleep(time.Duration(numberOfSecondsToSleep) * time.Second)
	return numberOfSecondsToSleep
}
