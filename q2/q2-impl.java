
public class Main {

    public static void main(String args[]) {
        Channel channel = new ConcurrentChannel(5);
        Producer producer = new Producer(channel);
        Consumer consumer = new Consumer(channel);

        Thread producerThread = new Thread(producer, "Producer");
        Thread consumerThread = new Thread(consumer, "Consumer");
        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();

        } catch (InterruptedException e) {}
    }

    public static class Producer implements Runnable {
        private final Channel channel;

        public Producer(Channel channel) {
            this.channel = channel;
        }

        public void run() {
            int count = 0;
            while (true) {
                this.channel.putMessage(Integer.toString(++count));
                System.out.println("Producer put message " + count + ", time stamp: " + System.nanoTime());
            }
        }
    }
    
    public static class Consumer implements Runnable {
        private final Channel channel;

        public Consumer(Channel channel) {
            this.channel = channel;
        }

        public void run() {
            while (true) {
                String msg = this.channel.takeMessage();
                System.out.println("Consumer take message " + msg + ", time stamp: " + System.nanoTime());
            }
        }
    }

    public interface Channel {
        public void putMessage(String message);
        public String takeMessage();
    }
    
    public static class ConcurrentChannel implements Channel {
        private ChannelData data;

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
                    return this.data.get();
                
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

            private void put(String message) {
                if (isFull()) {
                    throw new RuntimeException("ChannelData full and put called!");
                }
                this.data[this.tail] = message;
                this.size++;
                this.tail = (this.tail + 1) % this.maxSize;
            }
    
            private String get() {
                if (isEmpty()) {
                    throw new RuntimeException("ChannelData empty and get called!");
                }
                int aux = this.head;
                this.head = (this.head + 1) % this.maxSize;
                this.size--;
                return this.data[aux];
            }

            private boolean isEmpty() {
                return this.size == 0;
            } 
    
            private boolean isFull() {
                return this.size == this.maxSize;
            } 
        }
    }
}