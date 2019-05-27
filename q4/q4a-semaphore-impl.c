#include <stdio.h>
#include <assert.h>
#include <pthread.h>
#include <semaphore.h>

sem_t semaphore;
pthread_mutex_t mutex;
int numberOfThreads = 5;
int firstToWake = 0;

void *timer (void *args) {
    sleep(8); // Sleeps seconds
    pthread_mutex_lock(&mutex);
    if (firstToWake == 0) {
        firstToWake = -1;
    }
    sem_post(&semaphore);
    pthread_mutex_unlock(&mutex);
    pthread_exit(NULL);
}

void *request (void *args) {
    int random_number = (rand() % 30) + 1; // // Obtain a number between [1 - 30].
    sleep(random_number); // Sleeps seconds

    pthread_mutex_lock(&mutex);
    if (firstToWake == 0) {
        firstToWake = random_number;
    }
    sem_post(&semaphore);
    printf("REQ: %d", random_number);
    pthread_mutex_unlock(&mutex);
    pthread_exit(random_number);
}

int gateway (int num_replicas) {
    pthread_t pthreads[num_replicas];
    for (int i = 0; i < num_replicas; i++) {
        pthread_create(&pthreads[i], NULL, &request, (void*) i);
    }
    
    pthread_t timerThread;
    pthread_create(&timerThread, NULL, &timer, NULL);

    sem_wait(&semaphore);
    pthread_mutex_lock(&mutex);
    int firstValue = firstToWake;
    pthread_mutex_unlock(&mutex);
    return firstValue;
}

int main (int argc, char *argv[]) {
    sem_init(&semaphore, 0, 0);
    pthread_mutex_init(&mutex, NULL);
    printf("gateway=%d\n", gateway(numberOfThreads));
}
