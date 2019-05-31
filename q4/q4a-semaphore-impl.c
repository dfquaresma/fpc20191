#include <stdio.h>
#include <assert.h>
#include <pthread.h>
#include <semaphore.h>
#include <time.h>

sem_t semaphore;
pthread_mutex_t mutex;
int numberOfThreads = 5;

void *timer (int *first_to_wake_value) {
    printf("Timer will sleep 8 seconds.\n");
    sleep(8); // Sleeps seconds

    pthread_mutex_lock(&mutex);
    if (*first_to_wake_value == 0) {
        *first_to_wake_value = -1;
    }
    sem_post(&semaphore);
    pthread_mutex_unlock(&mutex);
    
    pthread_exit(NULL);
}

void *request (int *first_to_wake_value) {
    int random_number = (rand() % 30) + 1; // // Obtain a number between [1 - 30].
    printf("Request will sleep %d seconds\n", random_number);
    sleep(random_number); // Sleeps seconds

    pthread_mutex_lock(&mutex);
    if (*first_to_wake_value == 0) {
        *first_to_wake_value = random_number;
    }
    sem_post(&semaphore);
    pthread_mutex_unlock(&mutex);

    pthread_exit(random_number);
}

int gateway (int num_replicas) {
    int *first_to_wake_value = 0;
    pthread_t pthreads[num_replicas];
    for (int i = 0; i < num_replicas; i++) {
        pthread_create(&pthreads[i], NULL, &request, &first_to_wake_value);
    }
    pthread_t timerThread;
    //pthread_create(&timerThread, NULL, &timer, &first_to_wake_value);

    sem_wait(&semaphore);
    pthread_mutex_lock(&mutex);
    int valueToReturn = *first_to_wake_value;
    pthread_mutex_unlock(&mutex);
    return valueToReturn;
}

int main (int argc, char *argv[]) {
    srand ( time(NULL) );
    sem_init(&semaphore, 0, 0);
    pthread_mutex_init(&mutex, NULL);
    printf("gateway=%d\n", gateway(numberOfThreads));
}
