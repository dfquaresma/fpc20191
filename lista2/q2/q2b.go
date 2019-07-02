package main

import (
	"fmt"
	"math/rand"
	"time"
)

func main(){
	fmt.Println(gateway(5))
}

func request(c chan int) int{
	rand.Seed(time.Now().UnixNano())
	randNumber := rand.Intn(30)	+ 1
	fmt.Println("Request will sleep ", randNumber, "  seconds.")
	time.Sleep(time.Duration(randNumber) * time.Second)
	c <- randNumber
	
	return randNumber
}

//função que dorme por 16 segundos e depois adiciona um valor ao canal
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
    soma := 0

    for i := 0; i < num_replicas; i++ {
    	select {
			case valor := <-ch0: //a cada interação do for ou adicionamos um valor a soma ou verificamos se já se passaram os 16s
				soma += valor
			case <-ch1: //se existir um valor disponivel no canal significa que já se passaram 16s
				return -1
		}    
    }
    return soma
}
