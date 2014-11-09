/*
 * copyright 2012, gash
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
package poke.resources;

import poke.server.conf.ServerConf;
import poke.server.resources.Resource;

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

public class JobResource implements Resource {

	private final String RESPONSE = "response";
	private ServerConf cfg;

	public ServerConf getCfg() {
		return cfg;
	}

	public void setCfg(ServerConf cfg) {
		this.cfg = cfg;
	}

	@Override
	public Request process(Request request) {

		BlobStorage blob = new BlobStorage();
		BlobStorageService blobService = new BlobStorageServiceImplementation();
		MapperStorage mapper = new MapperStorage();
		ReplicatedDbServiceImplementation mapperService = new ReplicatedDbServiceImplementation();
		try {

			if (request != null && request.getBody().hasPhotoPayload()) {

				switch (request.getHeader().getPhotoHeader().getRequestType()) {
				case write:
					System.out
							.println("****************Inside Write - Job Resource**************");
					final String uuid = java.util.UUID.randomUUID().toString();

					blob.setCaption(request.getBody().getPhotoPayload()
							.getName());
					blob.setContentLength(request.getHeader().getPhotoHeader()
							.getContentLength());
					blob.setImageData(request.getBody().getPhotoPayload()
							.getData().toByteArray());
					blob.setUuid(uuid);
					mapper.setUuid(uuid);
					mapper.setNodeId(cfg.getNodeId());

					try {

						blobService.createBlobStorage(blob);
						mapperService.createMapperStorage(mapper);

						Builder photoPayLoadBuilder = PhotoPayload
								.newBuilder(request.getBody().getPhotoPayload());
						eye.Comm.Payload.Builder payLoadBuilder = Payload
								.newBuilder(request.getBody());
						Request.Builder requestBuilder = Request
								.newBuilder(request);
						eye.Comm.Header.Builder headerBuilder = Header
								.newBuilder(request.getHeader());
						headerBuilder.setReplyMsg(RESPONSE);
						photoPayLoadBuilder.setUuid(uuid);
						payLoadBuilder.setPhotoPayload(photoPayLoadBuilder
								.build());
						requestBuilder.setHeader(headerBuilder.build());
						requestBuilder.setBody(payLoadBuilder.build());
						request = requestBuilder.build();

						return request;

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				case read:
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

						return request;

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				case delete:
					try {

						Boolean success = blobService
								.deleteBlobStorageByUuid(request.getBody()
										.getPhotoPayload().getUuid());

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				default:
					break;

				}
			}
		} catch (Exception ex) {

			eye.Comm.Header.Builder headerBuilder = Header.newBuilder(request
					.getHeader());
			headerBuilder.setReplyMsg(RESPONSE);
			Request.Builder requestBuilder = Request.newBuilder(request);
			requestBuilder.setHeader(headerBuilder.build());
			request = requestBuilder.build();

		} finally {

		}
		return request;
	}
}
