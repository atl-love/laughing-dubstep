package masterNodeKnowerService;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import masterNodeKnowerService.MasterNodeKnowerProtocol.MasterNode;
import masterNodeKnowerService.MasterNodeKnowerProtocol.DNSRequest;
import masterNodeKnowerService.MasterNodeKnowerProtocol.DNSResponse;

public class MasterNodeKnowerClientHandler extends SimpleChannelInboundHandler<DNSResponse> {

	private static int WAIT = 1200;
	private static TimeUnit TIMEUNIT = TimeUnit.MILLISECONDS;
	
    // Stateful properties
    private volatile Channel channel;
    private final BlockingQueue<DNSResponse> answer = new LinkedBlockingQueue<DNSResponse>();

    public MasterNodeKnowerClientHandler() {
        super(false);
    }

    public String getServers() {
        DNSRequest.Builder rb = DNSRequest.newBuilder();
        rb.setAction(DNSRequest.Action.GET);
      
        channel.writeAndFlush(rb.build());

        DNSResponse response = waitForResponse();

        return response.toString();
    }
    
    public String getServer(String clusterName) {
        DNSRequest.Builder rb = DNSRequest.newBuilder();
        rb.setAction(DNSRequest.Action.GET);
        rb.setClusterName(clusterName);

        channel.writeAndFlush(rb.build());

        DNSResponse response = waitForResponse();

        return response.toString();
    }
    
    public DNSResponse putServerInfo(String clusterName, String ip, int port) {
        DNSRequest.Builder rb = DNSRequest.newBuilder();
        rb.setAction(DNSRequest.Action.PUT);
        
        MasterNode.Builder mb = MasterNode.newBuilder();
        mb.setClusterName(clusterName).setMasterIp(ip).setMasterPort(port);
        rb.setMasterNode(mb.build());
      
        channel.writeAndFlush(rb.build());

        DNSResponse response = waitForResponse();

        return response;
    }
    
    private DNSResponse waitForResponse(){
    	DNSResponse response;
       
        try {
        	System.out.println("wait.");
            response = answer.poll(WAIT, TIMEUNIT);
            System.out.println("unblocked.");
            return response;
        } catch (InterruptedException ignore) {
        
        }
        
        return null;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DNSResponse response) {
    	System.out.println("channelRead0 reading");
        answer.add(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
