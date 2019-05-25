import java.util.Random;

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

        Timer timer = new Timer(numReplicas, sum);
        Thread timerThread = new Thread(timer, "timer");
        timerThread.start();
        Joiner joiner = new Joiner(threads, sum);
        Thread joinerThread = new Thread(joiner, "joiner");
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
                for (int i = 0; i < this.threads.length; i++) {
                    this.threads[i].join();
                }
                synchronized (this.sum) {
                  this.sum.notifyAll();
                }
            } catch (InterruptedException e) {}
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
                Thread.sleep(16 * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            synchronized(this.sum) {
              if (this.sum.getAdds() < this.threadNumber) {
                this.sum.set(-1);
                this.sum.notifyAll();
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

