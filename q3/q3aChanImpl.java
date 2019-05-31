import java.util.Random;

public class Main {

    public static void main(String args[]) {
        System.out.println("gateway=" + gateway(5));
    }

    public static int gateway(int numReplicas) {
        Channel channel = new ConcurrentChannel(numReplicas);
        Request request = new Request(channel);
        for (int i = 0; i < numReplicas; i++) {
            Thread thread = new Thread(request, "request" + i);
            thread.start();
        }

        return channel.takeMessage();
    }

    public static class Request implements Runnable {
        private Channel channel;

        public Request (Channel channel) {
            this.channel = channel;
        }

        public void run() {
            int randomNumber = (new Random()).nextInt(30) + 1; // Obtain a number between [1 - 30].
            try {
                System.out.printf("Request will sleep %d seconds.\n", randomNumber);
                Thread.sleep(randomNumber * 1000); // Thread.sleep sleeps milliseconds
            } catch (InterruptedException e) {}
            this.channel.putMessage(randomNumber);
        }
    }
    

    // Aproveitado da segunda questão
    public interface Channel {
        public void putMessage(int message);
        public int takeMessage();
    }
    
    public static class ConcurrentChannel implements Channel {
        private final ChannelData data;
        
        public ConcurrentChannel(int maxSize) {
            this.data = new ChannelData(maxSize);
        }
        
        public void putMessage(int message) {
            synchronized (this.data) {
                while (this.data.isFull()) {
                    try {
                        this.data.wait();
                    } catch (InterruptedException e) { }
                }
                
                try {
                    this.data.put(message);
                    
                } catch (Exception e) {
                    throw e;
                    
                } finally {
                    this.data.notifyAll();
                }
            }
        }
        
        public int takeMessage() {
            synchronized (this.data) {
                while (this.data.isEmpty()) {
                    try {
                        this.data.wait();
                    } catch (InterruptedException e) { }
                }
                
                try {
                    return this.data.get();

                } catch (Exception e) {
                    throw e;
    
                } finally {
                    this.data.notifyAll();
                }
            }
        }

        private class ChannelData {
            private int[] data;
            private int head, tail, size, maxSize;
            
            public ChannelData(int maxSize) {
                this.maxSize = maxSize;
                this.data = new int[this.maxSize];
                this.head = this.tail = this.size = 0;
            }

            private synchronized void put(int message) {
                if (isFull()) {
                    throw new RuntimeException("ChannelData full and put called!");
                }
                this.data[this.tail] = message;
                this.size++;
                this.tail = (this.tail + 1) % this.maxSize;
            }
    
            private synchronized int get() {
                if (isEmpty()) {
                    throw new RuntimeException("ChannelData empty and get called!");
                }
                int aux = this.head;
                this.head = (this.head + 1) % this.maxSize;
                this.size--;
                return this.data[aux];
            }

            private synchronized boolean isEmpty() {
                return this.size == 0;
            } 
    
            private synchronized boolean isFull() {
                return this.size == this.maxSize;
            } 
        }
    }
}

