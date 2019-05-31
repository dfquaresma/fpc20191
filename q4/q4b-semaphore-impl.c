#include <stdio.h>
#include <assert.h>
#include <pthread.h>
#include <semaphore.h>
#include <time.h>

sem_t semaphore;
pthread_mutex_t mutex;
int numberOfThreads = 5;
int sleep_sum = 0;

void *joiner (pthread_t *pthreads[]) {
    int joiner_sum = 0;
    printf("Joiner waiting...\n");
    for (int i = 0; i < numberOfThreads; i++) {
        int aux = 0;
        pthread_join(*pthreads[i], &aux);    
        printf("Thread %d finished. It slept %d seconds.\n", i, aux);
        joiner_sum += aux;        
    }

    pthread_mutex_lock(&mutex);
    if (sleep_sum != -1) {
        sleep_sum = joiner_sum;
    }
    sem_post(&semaphore);
    pthread_mutex_unlock(&mutex);
    
    pthread_exit(NULL);
}

void *timer (int *number_of_threads_running) {
    printf("Timer will sleep 16 seconds.\n");
    sleep(16); // Sleeps seconds

    pthread_mutex_lock(&mutex);
    if (*number_of_threads_running > 0) {
        sleep_sum = -1;
    }
    sem_post(&semaphore);
    pthread_mutex_unlock(&mutex);

    pthread_exit(NULL);
}

void *request (int *number_of_threads_running) {
    int random_number = (rand() % 30) + 1; // // Obtain a number between [1 - 30].
    printf("Request will sleep %d seconds\n", random_number);
    sleep(random_number); // Sleeps seconds  
    
    pthread_mutex_lock(&mutex);
    *number_of_threads_running = *number_of_threads_running - 1; // Short -- does not work...
    pthread_mutex_unlock(&mutex);  
    
    pthread_exit(random_number);
}

int gateway (int num_replicas) {
    pthread_t *pthreads[5];
    int *number_of_threads_running = num_replicas;
    for (int i = 0; i < num_replicas; i++) {
        pthread_create (&pthreads[i], NULL, &request, &number_of_threads_running);
    }

    pthread_t joinerThread;
    pthread_create(&joinerThread, NULL, &joiner, &pthreads);
    pthread_t timerThread;
    //pthread_create(&timerThread, NULL, &timer, &number_of_threads_running);

    sem_wait(&semaphore);
    pthread_mutex_lock(&mutex);
    int sumValue = sleep_sum;
    pthread_mutex_unlock(&mutex);
    return sumValue;
}

int main (int argc, char *argv[]) {
    srand ( time(NULL) );
    sem_init(&semaphore, 0, 0);
    pthread_mutex_init(&mutex, NULL);
    printf("gateway=%d\n", gateway(numberOfThreads));
}