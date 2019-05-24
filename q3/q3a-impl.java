import java.util.Random;

public class Main {

    public static void main(String args[]) {
        System.out.println(gateway(5));
    }

    public static int gateway(int numReplicas) {
        First first = new First();
        for (int i = 0; i < numReplicas; i++) {
            Request request = new Request(first);
            Thread thread = new Thread(request, "request" + i);
            thread.start();
        }

        synchronized (first) {
            try {
                first.wait();
            } catch (InterruptedException e) {}
            return first.get();
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
                Thread.sleep(randomNumber * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            this.first.add(randomNumber);
        }
    }

    public static class First {
        private int value;

        public synchronized void add(int value) {
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

