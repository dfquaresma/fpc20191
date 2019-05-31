import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String args[]) {
        System.out.println("gateway=" + gateway(5));
    }

    public static int gateway(int numReplicas) {
        First first = new First();
        Semaphore semaphore = new Semaphore(0);
        for (int i = 0; i < numReplicas; i++) {
            Request request = new Request(semaphore, first);
            Thread thread = new Thread(request, "request" + i);
            thread.start();
        }

        Timer timer = new Timer(semaphore, first);
        Thread timerThread = new Thread(timer, "timer");
        timerThread.start();
    
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {}
        return first.get();
    }


    public static class Timer implements Runnable {
        private Semaphore semaphore;
        private First first;

        public Timer(Semaphore semaphore, First first) {
            this.semaphore = semaphore;
            this.first = first;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(8 * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            synchronized(this.first) {
                this.first.set(-1);
                this.semaphore.release();
            }
        }
    }

    public static class Request implements Runnable {
        private Semaphore semaphore;
        private First first;
        private int randomNumber;

        public Request (Semaphore semaphore, First first) {
            this.semaphore = semaphore;
            this.first = first;
            this.randomNumber = (new Random()).nextInt(30) + 1; // Obtain a number between [1 - 30]. 
        }

        public void run() {
            try {
                Thread.sleep(this.randomNumber * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            this.first.set(this.randomNumber);
            this.semaphore.release();
        }
    }

    public static class First {
        private int value;
        private boolean set;

        public synchronized void set(int value) {
            if (!this.set) {
                this.set = true;
                this.value = value;
            }
        }

        public synchronized int get() {
            return this.value;
        }
    }  

}

