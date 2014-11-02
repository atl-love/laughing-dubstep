/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package dns;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import dns.DNSProtocol.MasterNode;
import dns.DNSProtocol.DNSRequest;
import dns.DNSProtocol.DNSResponse;

public class DNSClientHandler extends SimpleChannelInboundHandler<DNSResponse> {

    private static final Pattern DELIM = Pattern.compile("/");

    // Stateful properties
    private volatile Channel channel;
    private final BlockingQueue<DNSResponse> answer = new LinkedBlockingQueue<DNSResponse>();

    public DNSClientHandler() {
        super(false);
    }

    public String getServerInfo() {
        DNSRequest.Builder rb = DNSRequest.newBuilder();
        rb.setAction(DNSRequest.Action.PUT);
        MasterNode.Builder mb = MasterNode.newBuilder();
        mb.setClusterName("test").setMasterIp("10.0.0.1").setMasterPort(8080);
        rb.setMasterNode(mb.build());
      

        channel.writeAndFlush(rb.build());

        DNSResponse response;
        boolean interrupted = false;
        for (;;) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        
        return response.toString();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DNSResponse response) {
        answer.add(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
