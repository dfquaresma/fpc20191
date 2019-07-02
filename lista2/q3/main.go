package main

import (
	"fmt"
	"time"
	"os"
)

func main(){
	numberOfThreads := str(os.Args[1])
	gateway(numberOfThreads)
	fmt.Println("Finished")
}

func request(c chan int) {
	numberOfSecondsToSleep := 4
	time.Sleep(numberOfSecondsToSleep * time.Second)
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
