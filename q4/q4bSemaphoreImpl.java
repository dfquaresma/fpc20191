import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String args[]) {
        System.out.println("gateway=" + gateway(5));
    }

    public static int gateway(int numReplicas) {
        Sum sum = new Sum();
        Thread[] threads = new Thread[numReplicas];
        for (int i = 0; i < numReplicas; i++) {
            Request request = new Request(sum);
            Thread thread = new Thread(request, "request" + i);
            thread.start();
            threads[i] = thread;
        }
        
        Semaphore semaphore = new Semaphore(0);
        Joiner joiner = new Joiner(threads, semaphore);
        Thread joinerThread = new Thread(joiner, "joiner");
        joinerThread.start();
        Timer timer = new Timer(numReplicas, semaphore, sum);
        Thread timerThread = new Thread(timer, "timer");
        timerThread.start();

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
                for (int i = 0; i < this.threads.length; i++) {
                    this.threads[i].join();
                }
                this.semaphore.release();
            } catch (InterruptedException e) {}
        }
    }

    public static class Timer implements Runnable {
        private int threadNumber;
        private Semaphore semaphore;
        private Sum sum;

        public Timer(int threadNumber, Semaphore semaphore, Sum sum) {
            this.threadNumber = threadNumber;
            this.semaphore = semaphore;
            this.sum = sum;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(16 * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            synchronized(this.sum) {
                if (this.sum.getAdds() < this.threadNumber) {
                  this.sum.set(-1);
                  this.semaphore.release();
                }
            }
        }
    }

    public static class Request implements Runnable {
        private Sum sum;
        private int randomNumber;

        public Request (Sum sum) {
            this.sum = sum;
            this.randomNumber = (new Random()).nextInt(30) + 1; // Obtain a number between [1 - 30]. 
        }

        public void run() {
            try {
                Thread.sleep(this.randomNumber * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            this.sum.add(this.randomNumber);
        }
    }

    public static class Sum {
        private int value, adds;
        private boolean timeout;

        public synchronized void add(int value) {
            if (!this.timeout) {
              this.value += value;
              this.adds++;
            }
        }

        public synchronized void set(int value) {
            this.timeout = true;
            this.value = value;
        }

        public int getAdds() {
            return this.adds;
        }

        public int getValue() {
            return this.value;
        }
    }  

}

