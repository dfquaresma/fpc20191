#include <stdio.h>
#include <assert.h>
#include <pthread.h>
#include <semaphore.h>

sem_t semaphore;
pthread_mutex_t mutex;
pthread_t pthreads[5];
int numberOfThreads = 5;
int threadsFinished = 0;
int sum = 0;

void *joiner (void *args) {
    for (int i = 0; i < numberOfThreads; i++) {
        pthread_join(pthreads[i], NULL);
    }
    sem_post(&semaphore);
    pthread_exit(NULL);
}

void *timer (void *args) {
    sleep(16); // Sleeps seconds
    pthread_mutex_lock(&mutex);
    if (threadsFinished < numberOfThreads) {
        sum = -1;
    }
    sem_post(&semaphore);
    pthread_mutex_unlock(&mutex);
    pthread_exit(NULL);
}

void *request (void *args) {
    int random_number = (rand() % 30) + 1; // // Obtain a number between [1 - 30].
    sleep(random_number); // Sleeps seconds

    pthread_mutex_lock(&mutex);
    if (sum != -1) {
        sum += random_number;
    }
    pthread_mutex_unlock(&mutex);
    
    pthread_exit(random_number);
}

int gateway (int num_replicas) {
    for (int i = 0; i < num_replicas; i++) {
        pthread_create (&pthreads[i], NULL, &request, (void*) i);
    }

    pthread_t joinerThread;
    pthread_create(&joinerThread, NULL, &joiner, NULL);
    pthread_t timerThread;
    pthread_create(&timerThread, NULL, &timer, NULL);

    sem_wait(&semaphore);
    pthread_mutex_lock(&mutex);
    int sumValue = sum;
    pthread_mutex_unlock(&mutex);
    return sumValue;
}

int main (int argc, char *argv[]) {
    sem_init(&semaphore, 0, 0);
    pthread_mutex_init(&mutex, NULL);
    printf("gateway=%ld\n", gateway(numberOfThreads));
}