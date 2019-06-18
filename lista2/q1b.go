package main

import (
	"fmt"
	"math/rand"
	"time"
)

func main(){
	num_replicas := 10
	c := make(chan int)
	gateway(num_replicas, c)
	soma := 0
	for i := 0; i < num_replicas; i++ {
        soma += <- c
    }
    fmt.Println(soma)
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

func gateway(num_replicas int, c chan int){
	for i := 0; i < num_replicas; i++ {
		//time.Sleep(1000) //para que o seed mude
        go request(c)
    }
}