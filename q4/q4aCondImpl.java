import java.util.Random;

public class Main {

    public static void main(String args[]) {
        System.out.println("gateway=" + gateway(5));
    }

    public static int gateway(int numReplicas) {
        First first = new First();
        for (int i = 0; i < numReplicas; i++) {
            Request request = new Request(first);
            Thread thread = new Thread(request, "request" + i);
            thread.start();
        }

        Timer timer = new Timer(first);
        Thread timerThread = new Thread(timer, "timer");
        timerThread.start();
    
        synchronized (first) {
            try {
                first.wait();
            } catch (InterruptedException e) {}
            return first.get();
        }
    }

    public static class Timer extends Request implements Runnable {
        public Timer(First first) {
          super(first);
        }
        @Override
        public void run() {
            try {
                Thread.sleep(8 * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            super.first.set(-1);
        }
    }

    public static class Request implements Runnable {
        private First first;
        private int randomNumber;

        public Request (First first) {
            this.first = first;
            this.randomNumber = (new Random()).nextInt(30) + 1; // Obtain a number between [1 - 30]. 
        }

        public void run() {
            try {
                Thread.sleep(this.randomNumber * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            this.first.set(this.randomNumber);
        }
    }

    public static class First {
        private int value;

        public synchronized void set(int value) {
            if (this.value == 0) {
                this.value = value;
                this.notifyAll();
            }
        }

        public synchronized int get() {
            return this.value;
        }
    }  

}

