import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

	public static void main(String[] args) {
		String structureName = args[0];
		int numberOfThreads = Integer.parseInt(args[1]);
		double readRate = Double.parseDouble(s)(args[3]);
		
	    // Memoria inicial da JVM
	    long initialMemory = Runtime.getRuntime().totalMemory();
	    // Tempo de inicio
	    long initialTime = System.nanoTime();
	   
	    Producer producer;	
	    switch (structureName) {
		case "concurrentHash":
			Map<Integer, String> concurrentMap = new ConcurrentHashMap<Integer, String>();
			producer = new MapProducer(readRate, concurrentMap);
			createThreads(producer, numberOfThreads);
			break;

		case "synchronizedMap":
			Map<Integer, String> synchronizedMap =  Collections.synchronizedMap(new HashMap<Integer, String>());
			producer = new MapProducer(readRate, synchronizedMap);
			createThreads(producer, numberOfThreads);
			break;

		case "copyOnWriteList":
			List<String> copyOnWriteList = new CopyOnWriteArrayList<String>();
			producer = new ListProducer(readRate, copyOnWriteList);
			createThreads(producer, numberOfThreads);
			break;

		case "synchronizedList":	
			List<String> synchronizedList = Collections.synchronizedList(new ArrayList<String>());
			producer = new ListProducer(readRate, synchronizedList);
			createThreads(producer, numberOfThreads);
			break;

		default:
			throw new RuntimeException("No valid structure passed");
		}
	    
	    System.out.println("Tempo de execução total: "  
	    		+ (System.nanoTime() - initialTime) / 1e9d + " segundos");

	    System.out.println("Memória gasta: " 
	    		+ (Runtime.getRuntime().totalMemory() - initialMemory));

	}

	private static void createThreads(Producer producer, int numberOfThreads) {
		for (int i = 0; i < numberOfThreads; i++) {
			Thread thread = new Thread(producer, "Producer" + i);
			thread.start();
		}
	}

	private static interface Producer extends Runnable {}

	private static class MapProducer implements Producer {
		private final double readRate;
		private final Map<Integer,String> map;
		private int counter = 0;
		
		public MapProducer(double readRate, Map<Integer, String> map) {
			this.map = map;
			this.readRate = readRate;
		}

		@Override
		public void run() {
			Random rand = new Random();
			for (int i = 0; i < 500; i++) {
				if(rand.nextDouble() > this.readRate){
					this.map.put(counter, "Thread " + counter);
					counter++;
					
				}else{
					if(map.size() > 0)this.map.get(rand.nextInt(map.size()));
				}
				
				
			}
		}
	}

	private static class ListProducer implements Producer {
		private final double readRate;
		private final List<String> list;
		
		public ListProducer(double readRate, List<String> list) {
			this.list = list;
			this.readRate = readRate;
		}

		@Override
		public void run() {
			Random rand = new Random();
			for (int i = 0; i < 500; i++) {
				if (rand.nextDouble() > this.readRate) {
					this.list.add("Thread");

				} else {
					if(list.size() > 0) this.list.get(rand.nextInt(list.size()));
				}
			}
		}
	}
	
}
