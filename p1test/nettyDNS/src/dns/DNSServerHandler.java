package dns;

import java.util.HashMap;

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
    	MasterNode m = MASTERNODES.get(request.getClusterName());
    	if (m == null) {
    		DNSResponse.Builder rb = DNSResponse.newBuilder();
    		rb.setStatus(DNSResponse.Status.ERROR);
    		rb.setErrorMessage("Cluster not found");
    		ctx.write(rb.build());
    	}
    	else {
    		DNSResponse.Builder rb = DNSResponse.newBuilder();
            rb.setStatus(DNSResponse.Status.OK);
            rb.addMasterNode(m);
            ctx.write(rb.build());
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
