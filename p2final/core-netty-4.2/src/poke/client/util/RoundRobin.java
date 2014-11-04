/**
 * 
 */
package poke.client.util;

import poke.server.managers.HeartbeatManager;
import poke.server.managers.ServerManager;

/**
 * @author arun_malik
 *
 */
public class RoundRobin {
	
	private static Integer value =-1;
	private static Integer lastUsedNode=-1;

	/**
	 * @return the lastUsedNode
	 */
	public static Integer getLastUsedNode() {
		return lastUsedNode;
	}

	public static Integer getNodeIdToServeRequest(){
		// increment the value -- initial 0
		value++;
		
		// get aliveNodes
		int aliveNodes = HeartbeatManager.getInstance().getAliveNodes();
		
		
		while(true){
			
			//check whether node is alive or value is my NodeId
			if(ServerManager.getInstance().getNodeId() == value || HeartbeatManager.getInstance().isNodeAlive(value)){
				
				lastUsedNode = value;
				return value;
			}else {
				// if node is not alive -- check node
				value = value == aliveNodes ? 0 : value++;
			}
		} 
	}
}
