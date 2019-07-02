import java.util.Random;

public class Main {

    public static void main(String args[]) {
        int numberOfThreads = Integer.parseInt(args[0]);
        if (numberOfThreads == 0) {
            sleep();
        } else {
            gateway(numberOfThreads);
        }
        System.out.println("Finished");
    }

    public static void gateway(int numReplicas) {
        Request request = new Request();
        Thread[] threads = new Thread[numReplicas];
        for (int i = 0; i < numReplicas; i++) {
            Thread thread = new Thread(request, "request" + i);
            thread.start();
            threads[i] = thread;
        }

        try {
            for (int i = 0; i < numReplicas; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {}
    }

    public static class Request implements Runnable {
        public void run() {
            sleep();
        }
    }

    public static void sleep() {
        try {
            int numberOfSecondsToSleep = 4; 
            Thread.sleep(numberOfSecondsToSleep * 1000); // Thread.sleep sleeps milliseconds.
        } catch (InterruptedException e) {}
    }
}
