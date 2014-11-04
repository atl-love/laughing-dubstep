package com.lifeForce.storage;

import java.util.List;



public interface BlobStorageService {
	public BlobStorageProfile createBlobStorage(BlobStorage blob) throws Exception;
	public void deleteBlobStorage(Long blobStorageId) throws Exception;
	
	public BlobStorageProfile findByUuid(String uuid)  throws Exception;
	public List<BlobStorageProfile> findByCreatedBy(String user)  throws Exception;
	
}
