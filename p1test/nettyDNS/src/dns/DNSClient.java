package dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Sends a list of continent/city pairs to a {@link ClusterInfoServer} to
 * get the local times of the specified cities.
 */
public final class DNSClient {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8463"));
    static final List<String> CITIES = Arrays.asList(System.getProperty(
            "cities", "Asia/Seoul,Europe/Berlin,America/Los_Angeles").split(","));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new DNSClientInitializer(sslCtx));

            // Make a new connection.
            Channel ch = b.connect(HOST, PORT).sync().channel();

            // Get the handler instance to initiate the request.
            DNSClientHandler handler = ch.pipeline().get(DNSClientHandler.class);

            // Request and get the response.
            String response = handler.getServerInfo();

            // Close the connection.
            ch.close();

            // Print the response at last but not least.
            
                System.out.format(response);
            
        } finally {
            group.shutdownGracefully();
        }
    }
}
