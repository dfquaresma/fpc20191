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

        Joiner joiner = new Joiner(threads, sum);
        Thread joinerThread = new Thread(joiner, "joiner");
        joinerThread.start();
        Timer timer = new Timer(sum);
        Thread timerThread = new Thread(timer, "timer");
        timerThread.start();

        synchronized (sum) {
            try {
                sum.wait();
            } catch (InterruptedException e) {}
            return sum.get();
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

    public static class Timer extends Request implements Runnable {
        public Timer(Sum sum) {
            super(sum);
        }
        @Override
        public void run() {
            try {
                Thread.sleep(16 * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            super.sum.set(-1);
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
        private int value;

        public synchronized void set(int value) {
            if (this.value == 0) {
                this.value = value;
                this.notifyAll();
            }
        }

        public synchronized void add(int value) {
            this.value += value;
        }

        public synchronized int get() {
            return this.value;
        }
    }  

}

