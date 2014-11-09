/**
 * 
 */
package poke.resources;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.server.conf.NodeDesc;
import poke.server.conf.ServerConf;
import poke.server.queue.ChannelQueue;
import poke.server.queue.PerChannelQueue;
import poke.server.resources.Resource;
import poke.server.resources.ResourceUtil;

import com.google.protobuf.ByteString;
import com.lifeForce.storage.BlobStorage;
import com.lifeForce.storage.BlobStorageProfile;
import com.lifeForce.storage.BlobStorageService;
import com.lifeForce.storage.BlobStorageServiceImplementation;
import com.lifeForce.storage.MapperStorage;
import com.lifeForce.storage.ReplicatedDbServiceImplementation;

import eye.Comm.Header;
import eye.Comm.Payload;
import eye.Comm.PhotoPayload;
import eye.Comm.PhotoPayload.Builder;
import eye.Comm.Request;

/**
 * @author arun_malik
 *
 */
public class MapperResource implements Resource {

	protected static Logger logger = LoggerFactory.getLogger("MapperResource");
	private final String RESPONSE = "response";
	private ServerConf cfg;
	private PerChannelQueue sq;

	/**
	 * @return the sq
	 */
	public PerChannelQueue getSq() {
		return sq;
	}

	/**
	 * @param sq
	 *            the sq to set
	 */
	public void setSq(PerChannelQueue sq) {
		this.sq = sq;
	}

	public ServerConf getCfg() {
		return cfg;
	}

	/**
	 * Set the server configuration information used to initialized the server.
	 * 
	 * @param cfg
	 */
	public void setCfg(ServerConf cfg) {
		this.cfg = cfg;
	}

	@Override
	public Request process(Request request) {
		
		BlobStorageService blobService = new  BlobStorageServiceImplementation();
		MapperStorage mapper = new MapperStorage();
		ReplicatedDbServiceImplementation mapperService = new ReplicatedDbServiceImplementation();
		String nextNodeIp = null;
		int nextNodePort = 0;

		try {

			if (request != null && request.getBody().hasPhotoPayload()) {

				switch (request.getHeader().getPhotoHeader().getRequestType()) {
				case read:
					mapper = mapperService.findNodeIdByUuid(request.getBody()
							.getPhotoPayload().getUuid());

					// image mapping not found in the database
					if (null != mapper) {

						//check forward the reques to the node - where data is saved
						if (mapper.getNodeId() != cfg.getNodeId()) {
							// iterate over cfg and find ip & port for selected
							// node
							for (NodeDesc node : cfg.getAdjacent()
									.getAdjacentNodes().values()) {
								if (mapper.getNodeId() == node.getNodeId()) {

									nextNodeIp = node.getHost();
									nextNodePort = node.getPort();

									eye.Comm.Header.Builder headerBuilder = Header
											.newBuilder(request.getHeader());
									headerBuilder
											.setOriginator(cfg.getNodeId());
									// headerBuilder.setIp(node.getHost());
									// headerBuilder.setPort(node.getPort());
									Request.Builder requestBuilder = Request
											.newBuilder(request);
									requestBuilder.setHeader(headerBuilder
											.build());
									request = requestBuilder.build();

								}
							}

							Request fwdRequest = ResourceUtil
									.buildForwardMessage(request, cfg);

							Bootstrap bootStrap = new Bootstrap();
							NioEventLoopGroup group = new NioEventLoopGroup();
							bootStrap.group(group)
									.channel(NioSocketChannel.class)
									.handler(new ChannelHandler(this.sq));
							bootStrap
									.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
											10000);
							bootStrap.option(ChannelOption.TCP_NODELAY, true);
							bootStrap.option(ChannelOption.SO_KEEPALIVE, true);

							SocketAddress mySocketAddress = new InetSocketAddress(
									nextNodeIp, nextNodePort);
							ChannelFuture futureChannel = bootStrap.connect(
									mySocketAddress).syncUninterruptibly();
							Channel ch = futureChannel.channel();
							ch.writeAndFlush(fwdRequest);

						}else{
							// data is saved in my local db - get image from local Db and return
							try {
								
								BlobStorageProfile blobFound = blobService
										.findByUuid(request.getBody().getPhotoPayload()
												.getUuid());

								Builder photoPayLoadBuilder = PhotoPayload
										.newBuilder(request.getBody().getPhotoPayload());
								eye.Comm.Payload.Builder payLoadBuilder = Payload
										.newBuilder(request.getBody());
								Request.Builder requestBuilder = Request
										.newBuilder(request);
								eye.Comm.Header.Builder headerBuilder = Header
										.newBuilder(request.getHeader());
								headerBuilder.setReplyMsg(RESPONSE);
								if (blobFound != null) {
									photoPayLoadBuilder.setUuid(blobFound.getUuid());
									photoPayLoadBuilder.setName(blobFound.getCaption());
									photoPayLoadBuilder.setData(ByteString
											.copyFrom(blobFound.getImageData()));
								} else {
									photoPayLoadBuilder.setName("Image Not Found");
								}
								payLoadBuilder.setPhotoPayload(photoPayLoadBuilder
										.build());
								requestBuilder.setHeader(headerBuilder.build());
								requestBuilder.setBody(payLoadBuilder.build());
								request = requestBuilder.build();

								//return request;
								
								sq.enqueueResponse(request, null);

								return null;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else {
						// enqueue response to client that image not found
						Builder photoPayLoadBuilder = PhotoPayload
								.newBuilder(request.getBody().getPhotoPayload());
						eye.Comm.Payload.Builder payLoadBuilder = Payload
								.newBuilder(request.getBody());
						Request.Builder requestBuilder = Request
								.newBuilder(request);
						eye.Comm.Header.Builder headerBuilder = Header
								.newBuilder(request.getHeader());
						headerBuilder.setReplyMsg(RESPONSE);
						photoPayLoadBuilder.setName("Image Not Found");
						requestBuilder.setHeader(headerBuilder.build());
						requestBuilder.setBody(payLoadBuilder.build());
						request = requestBuilder.build();
						
						sq.enqueueResponse(request, null);
					}

				default:
					break;
				}
			}
		} catch (Exception ex) {

		} finally {
			mapper = null;
			mapperService = null;
		}
		return null;
	}

	public class ChannelHandler extends ChannelInitializer<Channel> {
		private ChannelQueue sq;

		public ChannelHandler(ChannelQueue sq) {
			this.sq = sq;
		}

		@Override
		protected void initChannel(Channel channel) throws Exception {

			ChannelPipeline pipeline = channel.pipeline();
			pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
					67108864, 0, 4, 0, 4));
			pipeline.addLast("protobufDecoder", new ProtobufDecoder(
					eye.Comm.Request.getDefaultInstance()));
			pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
			pipeline.addLast("protobufEncoder", new ProtobufEncoder());

			pipeline.addLast(new SimpleChannelInboundHandler<eye.Comm.Request>() {
				protected void channelRead0(ChannelHandlerContext ctx,
						eye.Comm.Request msg) throws Exception {
					sq.enqueueResponse(msg, null);
				}
			});

		}

	}

}
