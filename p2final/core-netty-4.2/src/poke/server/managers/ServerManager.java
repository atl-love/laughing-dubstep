package poke.server.managers;

import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.server.conf.ServerConf;

public class ServerManager {

	protected static Logger logger = LoggerFactory.getLogger("serverManager");
	protected static AtomicReference<ServerManager> instance = new AtomicReference<ServerManager>();
	private static ServerConf conf;
	
	public static ServerManager initManager(ServerConf conf) {
		ServerManager.conf = conf;
		instance.compareAndSet(null, new ServerManager());
		return instance.get();
	}

	public static ServerManager getInstance() {
		// TODO throw exception if not initialized!
		return instance.get();
	}
	
	public Integer getNodeId() {
		return this.conf.getNodeId();
	}

}
