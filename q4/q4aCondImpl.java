import java.util.Random;

public class Main {

    public static void main(String args[]) {
        System.out.println("gateway=" + gateway(5));
    }

    public static int gateway(int numReplicas) {
        First first = new First();
        Request request = new Request(first);
        for (int i = 0; i < numReplicas; i++) {
            Thread thread = new Thread(request, "request" + i);
            thread.start();
        }

        Timer timer = new Timer(first);
        Thread timerThread = new Thread(timer, "timer");
        timerThread.start();
    
        synchronized (first) {
            while (!first.isSet()) {
                try {
                    first.wait();
                } catch (InterruptedException e) {}
            }
            return first.getValue();
        }
    }

    public static class Timer implements Runnable {
        private First first;

        public Timer(First first) {
            this.first = first;
        }
        
        @Override
        public void run() {
            try {
                System.out.println("Timer will sleep 8 seconds.");
                Thread.sleep(8 * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            this.first.setValue(-1);
        }
    }

    public static class Request implements Runnable {
        private First first;

        public Request (First first) {
            this.first = first; 
        }

        public void run() {
            int randomNumber = (new Random()).nextInt(30) + 1; // Obtain a number between [1 - 30].
            try {
                System.out.println("Request will sleep " + randomNumber + " seconds.");
                Thread.sleep(randomNumber * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            this.first.setValue(randomNumber);
        }
    }

    public static class First {
        private boolean set;
        private int value;

        public synchronized void setValue(int value) {
            if (!this.set) {
                this.set = true;
                this.value = value;
                this.notifyAll();
            }
        }

        public synchronized int getValue() {
            return this.value;
        }

        public synchronized boolean isSet() {
            return this.set;
        }
    }  

}

