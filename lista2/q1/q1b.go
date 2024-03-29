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

func gateway(num_replicas int) int{
	ch0 := make(chan int)
	
	for i := 0; i < num_replicas; i++ {
		go request(ch0)
    }
	
	sum := 0
    for i := 0; i < num_replicas; i++ {
        sum += <- ch0
    }

    return sum
}