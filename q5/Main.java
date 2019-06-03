import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
	private static double elements_to_fill_structures;

	public static void main(String[] args) {
		String structureName = args[0];
		int numberOfThreads = Integer.parseInt(args[1]);
		double readRate = Double.parseDouble(args[2]);
		
		elements_to_fill_structures = 65536 / numberOfThreads; // 65536 = 2^16
		System.out.printf("is_write_op,time_in_nanoseconds\n");

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
	}

	private static void createThreads(Producer producer, int numberOfThreads) {
		for (int i = 0; i < numberOfThreads; i++) {
			Thread thread = new Thread(producer, "Producer" + i);
			thread.start();
		}
	}

	private static interface Producer extends Runnable {
	}

	private static class MapProducer implements Producer {
		private final double readRate;
		private final Map<Integer, String> map;

		public MapProducer(double readRate, Map<Integer, String> map) {
			this.map = map;
			this.readRate = readRate;
		}

		@Override
		public void run() {
			Random rand = new Random();
			for (int i = 0; i < elements_to_fill_structures; i++) {
				long timeBefore = 0, timeAfter = 0;
				Double randomDouble = rand.nextDouble();
				boolean isPutOp =  randomDouble > this.readRate;
				if (isPutOp) {
					int keyToPut = this.map.size();
					String valueToPut = "Thread " + randomDouble;  
					timeBefore = System.nanoTime();
					this.map.put(keyToPut, valueToPut);
					timeAfter = System.nanoTime();

				} else {
					if (this.map.size() > 0) {
						int keyToGet = rand.nextInt(this.map.size());
						timeBefore = System.nanoTime();
						this.map.get(keyToGet);
						timeAfter = System.nanoTime();
					} 
				}
				System.out.printf("%b,%d\n", isPutOp, (timeAfter - timeBefore));	
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
			for (int i = 0; i < elements_to_fill_structures; i++) {
				long timeBefore = 0, timeAfter = 0;
				Double randomDouble = rand.nextDouble();
				boolean isAddOp = rand.nextDouble() > this.readRate;
				if (isAddOp) {
					String valueToAdd = "Thread " + randomDouble;
					timeBefore = System.nanoTime();
					this.list.add(valueToAdd);
					timeAfter = System.nanoTime();

				} else {
					if(this.list.size() > 0) {
						int indexToGet = rand.nextInt(this.list.size());
						timeBefore = System.nanoTime();
						this.list.get(indexToGet);
						timeAfter = System.nanoTime();
					}
				}
				System.out.printf("%b,%d\n", isAddOp, timeAfter - timeBefore);	
			}
		}
		}
	}


