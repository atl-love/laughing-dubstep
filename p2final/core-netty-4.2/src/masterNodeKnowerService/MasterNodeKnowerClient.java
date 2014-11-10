package masterNodeKnowerService;

import poke.server.conf.ServerConf;
import poke.server.conf.NodeDesc;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import masterNodeKnowerService.MasterNodeKnowerProtocol.MasterNode;
import masterNodeKnowerService.MasterNodeKnowerProtocol.DNSResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class MasterNodeKnowerClient {

	private static final String HOST = "127.0.0.1";
    private static final int PORT = 5353;
    private static final int SLEEP = 10;
    private static final int MAX_ERROR_DELAY = 1024;
    private static Logger logger = LoggerFactory.getLogger("MasterNodeKnowerClient");
    private static MasterNodeKnowerClient client = new MasterNodeKnowerClient();
    
    private ServerConf serverConf;
    private ConcurrentLinkedQueue<MasterNode> queue;
    private Bootstrap b;
    private Worker worker;
    
    private MasterNodeKnowerClient(){
    	
    	this.queue = new ConcurrentLinkedQueue<MasterNode>();
    	this.worker = new Worker(this);
    	(new Thread(worker)).start();
    	
    	EventLoopGroup group = new NioEventLoopGroup();
        try {
            b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new MasterNodeKnowerClientInitializer());
        }
        catch (Exception e) {
        	logger.error("Failed to start work groups", e);
        }
        
        
    }
    
    public static MasterNodeKnowerClient getClient(){
    	return client;
    }
    
    public void init(ServerConf serverConf){
    	this.serverConf = serverConf;
    }
    
    // Init must be called first!
    public boolean setLeader(int leader){
    	logger.info("Setting leader to reverse proxy");
    	NodeDesc nd = serverConf.getAdjacent().getAdjacentNodes().get(new Integer(leader));
    	if (nd != null){
    		logger.info("Found master node: " + nd.getHost() + ":" + nd.getPort());
	    	MasterNode.Builder mb = MasterNode.newBuilder();
	    	mb.setClusterName(Integer.toString(nd.getNodeId()));
	    	mb.setMasterIp(nd.getHost());
	    	mb.setMasterPort(nd.getPort());
	    	queue.add(mb.build());
	    	logger.info("Added master to queue");
	    	return true;
    	} else {
    		logger.error("Failed to find master");
    		return false;
    	}
    }

    
    private class Worker implements Runnable {
    	
    	
    	private MasterNodeKnowerClient client;
    	private int delay = 0;
    	

    	
    	public Worker(MasterNodeKnowerClient client){
    		logger.info("Worker constructed");
    		this.client = client;
    	}
    	
    	public void run() {
    		logger.info("Worker running");
    		MasterNode m = null;
    		
    		while (true) {
    			
        		try{
        			
        			Thread.sleep(delay); 
        			
        			m = client.queue.peek();
        			
            		if(m != null){
            			
            			logger.info("Queue length: " + client.queue.size());
            			
            			
            			 // Make a new connection.
                        Channel ch = client.b.connect(HOST, PORT).sync().channel();
                        
                        logger.info("Connect to DNS server");
                        
                        // Get the handler instance to initiate the request.
                        MasterNodeKnowerClientHandler handler = ch.pipeline().get(MasterNodeKnowerClientHandler.class);
                        
                        // Request and get the response.
                        DNSResponse response = handler.putServerInfo(m.getClusterName(), m.getMasterIp(), m.getMasterPort());

                        if (response.getStatus() != DNSResponse.Status.OK){
                        	client.queue.add(m);
                        }
                        
                        // Close the connection.
                        ch.close();
                        
                        logger.info("Finish task");
                        
                        // Remove from queue
                        client.queue.remove(m);
                    
                        // Circuit breaker
                        delay /= 2;
            		} else {
            			// nothing to do
            			delay = SLEEP;
            		}
        		}
        		catch (InterruptedException e){
        			logger.info(e.toString());
        			
        		}
        		catch (Exception e){
        			logger.info(e.toString());
        			
        			// Circuit breaker
        			if (delay == 0){
        				delay = 1;
        			}
        			else if (delay <= MAX_ERROR_DELAY / 2){
        				delay *= 2;
        			}
        		}
        	
    		}
    	}
    }
    

}
