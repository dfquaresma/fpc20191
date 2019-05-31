import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String args[]) {
        System.out.println("gateway=" + gateway(5));
    }

    public static int gateway(int numReplicas) {
        Sum sum = new Sum();
        Thread[] threads = new Thread[numReplicas];
        Request request = new Request(sum);
        for (int i = 0; i < numReplicas; i++) {
            Thread thread = new Thread(request, "request" + i);
            threads[i] = thread;
        }
        
        Semaphore semaphore = new Semaphore(0);
        Joiner joiner = new Joiner(threads, semaphore);
        Thread joinerThread = new Thread(joiner, "joiner");
        Timer timer = new Timer(numReplicas, semaphore, sum);
        Thread timerThread = new Thread(timer, "timer");
        
        timerThread.start();        
        for(Thread thread: threads){
            thread.start();
        }
        joinerThread.start();

        try {
            semaphore.acquire();      
        } catch (InterruptedException e) {}
        return sum.getValue();
    }
    
    public static class Joiner implements Runnable {
        private Thread[] threads;
        private Semaphore semaphore;

        public Joiner (Thread[] threads, Semaphore semaphore) {
            this.threads = threads;
            this.semaphore = semaphore;
        }

        public void run() {
            try {
                System.out.println("Joiner waititng...");
                for (int i = 0; i < this.threads.length; i++) {                
                    this.threads[i].join();
                    System.out.println("Thread " + this.threads[i].getName() + " finished.");
                }
            } catch (InterruptedException e) {}
            this.semaphore.release();
        }
    }

    public static class Timer implements Runnable {
        private Semaphore semaphore;
        private int threadNumber;
        private Sum sum;

        public Timer(int threadNumber, Semaphore semaphore, Sum sum) {
            this.threadNumber = threadNumber;
            this.semaphore = semaphore;
            this.sum = sum;
        }

        @Override
        public void run() {
            try {
                System.out.println("Timer will sleep 16 seconds.");
                Thread.sleep(16 * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            if (this.sum.getAddsCount() < this.threadNumber) {
              this.sum.setTimeout();
            }
            this.semaphore.release();
        }
    }

    public static class Request implements Runnable {
        private Sum sum;

        public Request (Sum sum) {
            this.sum = sum; 
        }

        public void run() {
            int randomNumber = (new Random()).nextInt(30) + 1; // Obtain a number between [1 - 30].
            try {
                System.out.println("Request will sleep " + randomNumber + " seconds.");
                Thread.sleep(randomNumber * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            this.sum.add(randomNumber);
        }
    }

    public static class Sum {
        private int value, addsCount;
        private boolean timeout;

        public synchronized void add(int value) {
            if (!this.timeout) {
                this.addsCount++;
                this.value += value;
            }
        }

        public synchronized void setTimeout() {
            this.timeout = true;
            this.value = -1;
        }

        public synchronized int getAddsCount() {
            return this.addsCount;
        }

        public synchronized int getValue() {
            return this.value;
        }
    }  

}

