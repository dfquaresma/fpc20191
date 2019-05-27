#include <stdio.h>
#include <assert.h>
#include <pthread.h>

void *request (void *args) {
    int random_number = (rand() % 30) + 1; // // Obtain a number between [1 - 30].
    sleep(random_number); // Sleeps seconds
    pthread_exit(random_number);
}

int gateway (int num_replicas) {
    pthread_t pthreads[num_replicas];
    for (int i = 0; i < num_replicas; i++) {
        pthread_create (&pthreads[i], NULL, &request, (void*) i);
    }
    int sum = 0;
    for (int i = 0; i < num_replicas; i++) {
        int aux;
        pthread_join(pthreads[i], &aux);
        sum += aux;
    }
    return sum;
}

int main (int argc, char *argv[]) {
    printf("gateway=%d\n", gateway(5));
}