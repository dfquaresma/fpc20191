import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

	public static void main(String[] args) {
		String structureName = args[1];
		int numberOfThreads = Integer.parseInt(args[2]);
		int readRate = Integer.parseInt(args[3]);
		
		Producer producer;
		switch (structureName) {
		case "concurrentHash":
			Map concurrentMap = new ConcurrentHashMap<String, String>();
			producer = new MapProducer(readRate, concurrentMap);
			createThreads(producer, numberOfThreads);
			break;

		case "synchronizedMap":
			Map synchronizedMap =  Collections.synchronizedMap(new HashMap<String, String>());
			producer = new MapProducer(readRate, synchronizedMap);
			createThreads(producer, numberOfThreads);
			break;

		case "copyOnWriteList":
			List copyOnWriteList = new CopyOnWriteArrayList<String>();
			producer = new ListProducer(readRate, copyOnWriteList);
			createThreads(producer, numberOfThreads);
			break;

		case "synchronizedList":	
			List synchronizedList = Collections.synchronizedList(new ArrayList<String>());
			producer = new ListProducer(readRate, synchronizedList);
			createThreads(producer, numberOfThreads);
			break;

		default:
			throw new RuntimeException("No valid structure passed");
		}
	}

	private static void createThreads(Producer producer, int numberOfThreads) {
		for (int i = 0; i < numberOfThreads; i++) {
			Thread thread = new Thread(producer, "Estrutura");
			thread.start();
		}
	}

	private static interface Producer extends Runnable {}

	private static class MapProducer implements Producer {
		private final int readRate;
		private final Map map;
		
		public MapProducer(int readRate, Map map) {
			this.map = map;
			this.readRate = readRate;
		}

		@Override
		public void run() {
		}
	}

	private static class ListProducer implements Producer {
		private final int readRate;
		private final List list;
		
		public ListProducer(int readRate, List list) {
			this.list = list;
			this.readRate = readRate;
		}

		@Override
		public void run() {
		}
	}
	
}
