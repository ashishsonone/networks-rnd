package serverplus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.Setter;

public class Main {
	static @Getter @Setter int sessionID = 0;	//not used

	//It is the pool of free session IDs. Whenever a new session is created, it is assigned its id 
	//from this pool. As session is deleted or expires, the id of that session is added to the pool.
	static @Getter @Setter CopyOnWriteArrayList<Integer> freeSessions = new CopyOnWriteArrayList<Integer>();
	
	//HashMap that stores the current active sessions
	//<key, value> = <session id, Session Object>
	static @Getter @Setter ConcurrentHashMap<Integer, Session> SessionMap = new ConcurrentHashMap<Integer, Session>();
	
	//HashMap that stores the current running experiments
	static @Getter @Setter ConcurrentHashMap<Integer, Boolean> RunningExperimentMap = new ConcurrentHashMap<Integer, Boolean>();
}
