/*
 * copyright 2014, gash
 * 
 * Gash licenses this file to you under the Apache License,
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
package poke.server.management.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eye.Comm.LeaderElection;
import eye.Comm.LeaderElection.ElectAction;
import eye.Comm.Management;
import eye.Comm.MgmtHeader;

/**
 * Similar to the ClientCommand, the Management Client provides a external API
 * hook to the communication and messaging for the management network.
 * 
 * @author gash
 * 
 */
public class MgmtClientCommand {
	protected static Logger logger = LoggerFactory.getLogger("client");

	private String host;
	private int port;
	private MgmtConnection comm;

	public MgmtClientCommand(String host, int port) {
		this.host = host;
		this.port = port;

		init();
	}

	private void init() {
		comm = new MgmtConnection(host, port);
	}

	/**
	 * add an application-level listener to receive messages from the server (as
	 * in replies to requests).
	 * 
	 * @param listener
	 */
	public void addListener(MgmtListener listener) {
		comm.addListener(listener);
	}

	public void startElection(String ip, int port) {

		LeaderElection.Builder leb = LeaderElection.newBuilder();
		leb.setElectId("demo." + System.currentTimeMillis());
		leb.setExpires(System.currentTimeMillis() + 2 * 60 * 1000);
		leb.setDesc("election demo");
		leb.setAction(ElectAction.DECLAREELECTION);

		MgmtHeader.Builder mhdr = MgmtHeader.newBuilder();
		mhdr.setTime(System.currentTimeMillis());
		mhdr.setOriginator(9999);
		mhdr.setToNode(0);
		
		Management.Builder mb = Management.newBuilder();
		mb.setHeader(mhdr.build());
		mb.setElection(leb.build());

		try {
			Management m = mb.build();
			comm.sendMessage(m);
		} catch (Exception e) {
			logger.warn("Unable to deliver mgmt message, startElection()");
			e.printStackTrace();
		}
	}
}
