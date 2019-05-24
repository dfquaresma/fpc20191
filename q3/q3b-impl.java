import java.util.Random;

public class Main {

    public static void main(String args[]) {
        System.out.println(gateway(5));
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

        try {
            for (int i = 0; i < numReplicas; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {}
        return sum.get();
    }

    public static class Request implements Runnable {
        private Sum sum;

        public Request (Sum sum) {
            this.sum = sum;
        }

        public void run() {
            int randomNumber = (new Random()).nextInt(30) + 1; // Obtain a number between [1 - 30].
            try {
                Thread.sleep(randomNumber * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            this.sum.add(randomNumber);
        }
    }

    public static class Sum {
        private int value;

        public synchronized void add(int value) {
            this.value += value;
        }

        public synchronized int get() {
            return this.value;
        }
    }  

}

