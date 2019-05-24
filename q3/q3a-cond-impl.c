#include <stdio.h>
#include <assert.h>
#include <pthread.h>

pthread_mutex_t mutex;
pthread_cond_t first;
int firstToWake;

void* request (void* args) {
    int random_number = (rand() % 30) + 1; // // Obtain a number between [1 - 30].
    sleep(random_number); // Sleeps seconds
    pthread_mutex_lock(&mutex);
    pthread_cond_signal(&first);
    firstToWake = random_number;
    pthread_mutex_unlock(&mutex);
    pthread_exit(random_number);
}

int gateway (int num_replicas) {
    pthread_t pthreads[num_replicas];
    for (int i = 0; i < num_replicas; i++) {
        pthread_create (&pthreads[i], NULL, &request, (void*) i);
    }
    int valueToReturn;
    pthread_mutex_lock(&mutex);
    pthread_cond_wait(&first, &mutex);
    valueToReturn = firstToWake;
    pthread_mutex_unlock(&mutex);
    return valueToReturn;
}

int main (int argc, char *argv[]) {
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&first, NULL);
    printf("gateway=%ld\n", gateway(5));
}
