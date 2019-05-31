
public class Main {

    public static void main(String args[]) {
        int channelSize = 3;
        Channel channel = new ConcurrentChannel(channelSize);

        int numberOfConsumerThreads = 2;
        int numberOfProducerThreads = 2;
        int totalNumberOfThreads = numberOfConsumerThreads + numberOfProducerThreads;
        Thread[] threads = new Thread[totalNumberOfThreads];
        for (int i = 0; i < numberOfConsumerThreads; i++) {
            Consumer consumer = new Consumer(i, channel);
            Thread consumerThread = new Thread(consumer, "Consumer" + i);
            threads[i] = consumerThread;
        }
        for (int i = numberOfConsumerThreads; i < totalNumberOfThreads; i++) {
            Producer producer = new Producer(i, channel);
            Thread producerThread = new Thread(producer, "Producer" + i);
            threads[i] = producerThread;
        }        
        for (int i = 0; i < totalNumberOfThreads; i++) {
            threads[i].start();
        }

        try {
            for (int i = 0; i < totalNumberOfThreads; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {}
    }

    public static class Producer implements Runnable {
        private final Channel channel;
        private final int id;

        public Producer(int id, Channel channel) {
            this.channel = channel;
            this.id = id;
        }

        public void run() {
            int count = 0;
            while (count++ < 5) {
                this.channel.putMessage(this.id + " " + Integer.toString(count));
            }
        }
    }
    
    public static class Consumer implements Runnable {
        private final Channel channel;
        private final int id;
        
        public Consumer(int id, Channel channel) {
            this.channel = channel;
            this.id = id;
        }
        
        public void run() {
            int count = 0;
            while (count++ < 5) {
                String msg = this.channel.takeMessage();
            }
        }
    }
    
    public interface Channel {
        public void putMessage(String message);
        public String takeMessage();
    }
    
    public static class ConcurrentChannel implements Channel {
        private final ChannelData data;
        
        public ConcurrentChannel(int maxSize) {
            this.data = new ChannelData(maxSize);
        }
        
        public void putMessage(String message) {
            synchronized (this.data) {
                while (this.data.isFull()) {
                    try {
                        this.data.wait();
                    } catch (InterruptedException e) { }
                }
                
                try {
                    this.data.put(message);
                    System.out.println("Put message " + message + ", timestamp: " + System.nanoTime());
                    
                } catch (Exception e) {
                    throw e;
                    
                } finally {
                    this.data.notifyAll();
                }
            }
        }
        
        public String takeMessage() {
            synchronized (this.data) {
                while (this.data.isEmpty()) {
                    try {
                        this.data.wait();
                    } catch (InterruptedException e) { }
                }
                
                try {
                    String message = this.data.get();
                    System.out.println("Take message " + message + ", timestamp: " + System.nanoTime());
                    return message;

                } catch (Exception e) {
                    throw e;
    
                } finally {
                    this.data.notifyAll();
                }
            }
        }

        private class ChannelData {
            private String[] data;
            private int head, tail, size, maxSize;
            
            public ChannelData(int maxSize) {
                this.maxSize = maxSize;
                this.data = new String[this.maxSize];
                this.head = this.tail = this.size = 0;
            }

            private synchronized void put(String message) {
                if (isFull()) {
                    throw new RuntimeException("ChannelData full and put called!");
                }
                this.data[this.tail] = message;
                this.size++;
                this.tail = (this.tail + 1) % this.maxSize;
            }
    
            private synchronized String get() {
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