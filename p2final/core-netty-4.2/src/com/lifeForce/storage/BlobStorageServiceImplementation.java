package com.lifeForce.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlobStorageServiceImplementation implements BlobStorageService {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/cmpe275";
	static final String USER = "root";
	static final String PASS = "";

	Connection conn = null;
	Statement stmt = null;

	public BlobStorageServiceImplementation() throws ClassNotFoundException,
			SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
	}

	public BlobStorageProfile createBlobStorage(BlobStorage blob)
			throws Exception {

		stmt = conn.createStatement();
		PreparedStatement ps = null;

		try {

			conn.setAutoCommit(false);
			String sql = "INSERT INTO blobStorage (`uuid`,`caption`,`img`,`contentLength`,`createdBy`,`createdDate`,`lastModifiedBy`,`lastModifiedDate`) VALUES(?,?,?,?,?,?,?,?)";

			ps = conn.prepareStatement(sql);
			ps.setString(1, blob.getUuid());
			ps.setString(2, blob.getCaption());
			ps.setBytes(3, blob.getImageData());
			ps.setLong(4, blob.getContentLength());
			ps.setString(5, blob.getCreatedBy());
			ps.setDate(6, (Date) blob.getCreatedDate());
			ps.setString(7, blob.getLastModifiedBy());
			ps.setDate(8, (Date) blob.getLastModifiedDate());

			ps.executeUpdate();
			conn.commit();

		} finally {
			ps.close();
		}
		
		return findByUuid(blob.getUuid());
	}

	public void deleteBlobStorage(Long blobStorageId) throws Exception {

		PreparedStatement ps = null;
		BlobStorage blob = null;

		try {

			String sql = "DELETE FROM blobStorage where blobStorage.blobStorageId = ?;";

			ps = conn.prepareStatement(sql);
			ps.setLong(1, blobStorageId);

			ResultSet rs = ps.executeQuery();
			
		} finally {
			ps.close();
			blob = null;
		}
	}

	public BlobStorageProfile findByUuid(String uuid) throws Exception {

		PreparedStatement ps = null;
		BlobStorage blob = null;

		try {

			String sqlSelect = "SELECT * FROM blobStorage where blobStorage.uuid = ?;";

			ps = conn.prepareStatement(sqlSelect);
			ps.setString(1, uuid);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				blob = new BlobStorage();
				blob.setBlobStorageId(rs.getLong("blobStorageId"));
				blob.setUuid(rs.getString("uuid"));
				blob.setCaption(rs.getString("caption"));
				blob.setContentLength(rs.getInt("contentLength"));
				blob.setImageData(rs.getBytes("img"));
				blob.setCreatedBy(rs.getString("createdBy"));
				blob.setCreatedBy(rs.getString("lastModifiedBy"));
				blob.setCreatedDate(rs.getDate("createdDate"));
				blob.setLastModifiedDate(rs.getDate("lastModifiedDate"));
			}

			return new BlobStorageProfile(blob);
		} finally {
			ps.close();
			blob = null;
		}
	}

	public List<BlobStorageProfile> findByCreatedBy(String createdBy)
			throws Exception {
		 
		PreparedStatement ps = null;
		BlobStorage blob = null;
		List<BlobStorageProfile> blobs = new ArrayList<BlobStorageProfile>();

		try {

			String sqlSelect = "SELECT * FROM blobStorage where blobStorage.createdBy = ?;";

			ps = conn.prepareStatement(sqlSelect);
			ps.setString(1, createdBy);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				blob = new BlobStorage();
				blob.setBlobStorageId(rs.getLong("blobStorageId"));
				blob.setUuid(rs.getString("uuid"));
				blob.setCaption(rs.getString("caption"));
				blob.setContentLength(rs.getInt("contentLength"));
				blob.setImageData(rs.getBytes("img"));
				blob.setCreatedBy(rs.getString("createdBy"));
				blob.setCreatedBy(rs.getString("lastModifiedBy"));
				blob.setCreatedDate(rs.getDate("createdDate"));
				blob.setLastModifiedDate(rs.getDate("lastModifiedDate"));
				
				blobs.add(new BlobStorageProfile(blob));
			}

			return blobs;
		} finally {
			ps.close();
			blob = null;
			blobs=null;
		}
	}

}
