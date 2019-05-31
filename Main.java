import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

	static ConcurrentHash hashMap = new ConcurrentHash(new ConcurrentHashMap<String, String>());
	static SyncronizedMap syncronizedMap = new SyncronizedMap(Collections.synchronizedMap(new HashMap<String, String>()));
	static CopyOnWriteList copyWriteList = new CopyOnWriteList(new CopyOnWriteArrayList<String>());
	static SyncronizedList syncronizedList = new SyncronizedList(Collections.synchronizedList(new ArrayList<String>()));

	public static void main(String[] args) {

		switch (args[1]) {
		case "concurrentHash":
			for (int i = 0; i < Integer.parseInt(args[2]); i++) {
				Thread thread = new Thread(hashMap, "ConcurrentHashMap");
				thread.start();
			}
		case "syncronizedMap":
			for (int i = 0; i < Integer.parseInt(args[2]); i++) {
				Thread thread = new Thread(syncronizedMap, "ConcurrentHashMap");
				thread.start();
			}
		case "copyOnWriteList":
			for (int i = 0; i < Integer.parseInt(args[2]); i++) {
				Thread thread = new Thread(copyWriteList, "Estrutura");
				thread.start();
			}
		case "syncronizedList":
			for (int i = 0; i < Integer.parseInt(args[2]); i++) {
				Thread thread = new Thread(syncronizedList, "Estrutura");
				thread.start();
			}
		}
	}

	public static class ConcurrentHash implements Runnable {
		ConcurrentHashMap<String, String> estrutura;

		public ConcurrentHash(ConcurrentHashMap<String, String> estrutura) {
			this.estrutura = estrutura;
		}

		@Override
		public void run() {
			estrutura.put("Thread" , "Thread");
		}
	}

	public static class SyncronizedMap implements Runnable {

		Map<String, String> estrutura;

		public SyncronizedMap(Map<String, String> estrutura) {
			this.estrutura = estrutura;
		}

		@Override
		public void run() {
			estrutura.put("Thread" , "Thread");
		}
	}

	public static class CopyOnWriteList implements Runnable {

		CopyOnWriteArrayList<String> estrutura;

		public CopyOnWriteList(CopyOnWriteArrayList<String> estrutura) {
			this.estrutura = estrutura;
		}

		@Override
		public void run() {
			estrutura.add("Thread");
		}
	}

	public static class SyncronizedList implements Runnable {

		List<String> estrutura;

		public SyncronizedList(List<String> estrutura) {
			this.estrutura = estrutura;
		}

		@Override
		public void run() {
			estrutura.add("Thread");
		}
	}
}
