import java.util.Random;

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

        Timer timer = new Timer(numReplicas, sum);
        Thread timerThread = new Thread(timer, "timer");
        Joiner joiner = new Joiner(threads, sum);
        Thread joinerThread = new Thread(joiner, "joiner");

        timerThread.start();        
        for(Thread thread: threads){
            thread.start();
        }
        joinerThread.start();

        synchronized (sum) {
          try {
            sum.wait();
          } catch (InterruptedException e) {}
          return sum.getValue();
        }
    }
    
    public static class Joiner implements Runnable {
        private Thread[] threads;
        private Sum sum;

        public Joiner (Thread[] threads, Sum sum) {
            this.threads = threads;
            this.sum = sum;
        }

        public void run() {
            try {
                System.out.println("Joiner waiting...");
                for (int i = 0; i < this.threads.length; i++) {
                    this.threads[i].join();
                    System.out.printf("Thread %s finished.\n", this.threads[i].getName());
                }
            } catch (InterruptedException e) {}
            synchronized (this.sum) {
                this.sum.notifyAll();
            }
        }
    }

    public static class Timer implements Runnable {
        private int threadNumber;
        private Sum sum;

        public Timer(int threadNumber, Sum sum) {
            this.threadNumber = threadNumber;
            this.sum = sum;
        }

        @Override
        public void run() {
            try {
                System.out.println("Timer will sleep 16 seconds.");
                Thread.sleep(16 * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            synchronized(this.sum) {
                if (this.sum.getAddsCount() < this.threadNumber) {
                    this.sum.setTimeout();
                }
                this.sum.notifyAll();
            }
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
                System.out.printf("Request will sleep %d seconds.\n", randomNumber);
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
