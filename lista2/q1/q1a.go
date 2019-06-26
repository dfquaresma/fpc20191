package main

import (
	"fmt"
	"math/rand"
	"time"
)

func main(){
	fmt.Println(gateway(10))	
}

func request(c chan int) int{
	rand.Seed(time.Now().UnixNano())
	randNumber := rand.Intn(30)	+ 1
	//fmt.Println("A função irá dormir por", randNumber, "segundos.")
	time.Sleep(time.Duration(randNumber) * time.Second)
	//fmt.Println(randNumber, "terminou.")
	c <- randNumber

	return randNumber
}

func gateway(num_replicas int) int{
	ch0 := make(chan int)
	
	for i := 0; i < num_replicas; i++ {
		//time.Sleep(1000) //para que o seed mude 
        go request(ch0)
    }

    return <-ch0
}