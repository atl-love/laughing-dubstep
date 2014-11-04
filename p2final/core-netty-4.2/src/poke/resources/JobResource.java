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

import com.lifeForce.storage.BlobStorage;

import poke.server.resources.Resource;
import eye.Comm.Request;

public class JobResource implements Resource {

	@Override
	public Request process(Request request) {
		BlobStorage blob = new BlobStorage();

		
		if(request != null && request.getBody().hasPhotoPayload()) {
			switch(request.getHeader().getPhotoHeader().getRequestType()){
			
			case write:
					
					System.out.println("****************Inside Write - Job Resource**************");
					blob.setCaption(request.getBody().getPhotoPayload().getName());
					blob.setContentLength(request.getHeader().getPhotoHeader().getContentLength());
					blob.setImageData(request.getBody().getPhotoPayload().getData().toByteArray());
					blob.setUuid(java.util.UUID.randomUUID().toString());
					
			case read:
				
			case delete:
				
			
			default:
				break;
				
			}
		}
		return null;
	}

}
