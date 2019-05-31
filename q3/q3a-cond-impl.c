#include <stdio.h>
#include <assert.h>
#include <pthread.h>
#include <stdbool.h>
#include <time.h>

pthread_mutex_t mutex;
pthread_cond_t cond;
int first_to_wake_value = 0;

void *request (void *args) {
    srand ( time(NULL) );
    int random_number = (rand() % 30) + 1; // // Obtain a number between [1 - 30].
    printf("Request will sleep %d seconds\n", random_number);
    sleep(random_number); // Sleeps seconds

    pthread_mutex_lock(&mutex);
    if (first_to_wake_value == 0) {
        first_to_wake_value = random_number;
    }
    pthread_cond_signal(&cond);
    pthread_mutex_unlock(&mutex);
    
    pthread_exit(random_number);
}

int gateway (int num_replicas) {
    pthread_t pthreads[num_replicas];
    for (int i = 0; i < num_replicas; i++) {
        pthread_create(&pthreads[i], NULL, &request, (void*) i);
    }
    int valueToReturn;
    pthread_mutex_lock(&mutex);
    while (first_to_wake_value == 0) {
        pthread_cond_wait(&cond, &mutex);
    }
    valueToReturn = first_to_wake_value;
    pthread_mutex_unlock(&mutex);
    return valueToReturn;
}

int main (int argc, char *argv[]) {
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&cond, NULL);
    printf("gateway=%d\n", gateway(5));
}
