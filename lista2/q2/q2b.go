package main

import (
	"fmt"
	"math/rand"
	"time"
)

func main(){
	fmt.Println("gateway =" , gateway(5))
}

func request(c chan int) int{
	rand.Seed(time.Now().UnixNano())
	randNumber := rand.Intn(30)	+ 1
	fmt.Println("Request will sleep ", randNumber, "  seconds.")
	time.Sleep(time.Duration(randNumber) * time.Second)
	c <- randNumber
	
	return randNumber
}

func counterTime(c chan int){
	time.Sleep(16 * time.Second)
	c <- -1
}

func gateway(num_replicas int) int{
	ch0 := make(chan int)
	ch1 := make(chan int)

	for i := 0; i < num_replicas; i++ {
        go request(ch0)
    }

    go counterTime(ch1)
    sum := 0

    for i := 0; i < num_replicas; i++ {
    	select {
			case value := <-ch0:
				sum += value
			case <-ch1:
				return -1
		}    
    }
    return sum
}
