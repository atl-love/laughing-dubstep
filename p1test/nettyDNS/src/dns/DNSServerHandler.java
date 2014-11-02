package dns;

import java.util.HashMap;
import java.util.Set;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import dns.DNSProtocol.MasterNode;
import dns.DNSProtocol.DNSRequest;
import dns.DNSProtocol.DNSResponse;

public class DNSServerHandler extends SimpleChannelInboundHandler<DNSRequest> {
	
    static final HashMap<String, MasterNode> MASTERNODES = new HashMap<>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DNSRequest request) {
    	if (request.getAction() == DNSRequest.Action.GET){
    		handleGET(ctx, request);
    	}
    	else if (request.getAction() == DNSRequest.Action.PUT) {
    		handlePUT(ctx, request);
    	}
    	else {
    		DNSResponse.Builder rb = DNSResponse.newBuilder();
    		rb.setStatus(DNSResponse.Status.ERROR);
    		rb.setErrorMessage("Not a valid Action");
    		ctx.write(rb.build());
    	}
    }
    
    private void handleGET(ChannelHandlerContext ctx, DNSRequest request){
    	
    	String clusterName = request.getClusterName();
    	
    	if (clusterName == null){ // asking for all clusters
    		DNSResponse.Builder rb = DNSResponse.newBuilder();
            rb.setStatus(DNSResponse.Status.OK);
            Set<String> clusterNames = MASTERNODES.keySet();
            for (String name : clusterNames){
            	rb.addMasterNode(MASTERNODES.get(name));
            }
            ctx.write(rb.build());
    	} 
    	else { // asking for specific cluster
    		MasterNode m = MASTERNODES.get(clusterName);
	    	if (m != null) { // found
	    		DNSResponse.Builder rb = DNSResponse.newBuilder();
	            rb.setStatus(DNSResponse.Status.OK);
	            rb.addMasterNode(m);
	            ctx.write(rb.build());
	    	}
	    	else { // not found
	    		DNSResponse.Builder rb = DNSResponse.newBuilder();
	    		rb.setStatus(DNSResponse.Status.ERROR);
	    		rb.setErrorMessage("Cluster not found");
	    		ctx.write(rb.build());
	    	}
    	}
    }
    
    private void handlePUT(ChannelHandlerContext ctx, DNSRequest request){
    	MasterNode m = request.getMasterNode();
    	MASTERNODES.put(m.getClusterName(), m);
    	DNSResponse.Builder rb = DNSResponse.newBuilder();
        rb.setStatus(DNSResponse.Status.OK);
        rb.addMasterNode(m);
        ctx.write(rb.build());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
