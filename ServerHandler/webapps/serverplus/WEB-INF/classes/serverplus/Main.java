package serverplus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.Setter;

public class Main {
	static @Getter @Setter int sessionID = 0;

	static @Getter @Setter CopyOnWriteArrayList<Integer> freeSessions = new CopyOnWriteArrayList<Integer>();
	static @Getter @Setter ConcurrentHashMap<Integer, Session> SessionMap = new ConcurrentHashMap<Integer, Session>();
	static @Getter @Setter ConcurrentHashMap<Integer, Boolean> RunningExperimentMap = new ConcurrentHashMap<Integer, Boolean>();
}
