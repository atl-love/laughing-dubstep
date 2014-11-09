/**
 * 
 */
package com.lifeForce.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import poke.client.util.DbConstants;

/**
 * @author arun_malik
 *
 */
public class DbConfigurations {

	private static String jdbcDriver ;
	
	private static String localDbUrl ;
	private static String localDbUser ;
	private static String localDbPass ;
	
	private static String mapperMainDbUrl ; 
	private static String mapperMainDbUser ;
	private static String mapperMainDbPass ;
	
	private static String mapperReplicatedDbUrl ; 
	private static String mapperReplicatedDbUser ;
	private static String mapperReplicatedDbPass ;
	
	
	public static void Initialize() throws DatabaseException{
		
		Properties props = null;
		
		try {
				props = new Properties();
				ClassLoader loader = Thread.currentThread().getContextClassLoader();  
				InputStream stream = loader.getResourceAsStream(DbConstants.DB_PROPERTIES_FILE);
				if(stream !=null){
					
					
				props.load(stream);
			
				}else{
					throw new DatabaseException("Cannot load / find  "+DbConstants.DB_PROPERTIES_FILE+" file.", new FileNotFoundException());
				}
			} catch (IOException e) {
				throw new DatabaseException("Cannot load Stream from "+DbConstants.DB_PROPERTIES_FILE+" file.", e);
			}
		
			DbConfigurations.setJdbcDriver(props.getProperty(DbConstants.JDBC_DRIVER)); 
			
			DbConfigurations.setLocalDbUrl(props.getProperty(DbConstants.LOCAL_DB_URL)); 
			DbConfigurations.setLocalDbUser(props.getProperty(DbConstants.LOCAL_DB_USER)); 
			DbConfigurations.setLocalDbPass(props.getProperty(DbConstants.LOCAL_DB_PASS)); 
			
			
			DbConfigurations.setMapperMainDbUrl(props.getProperty(DbConstants.MAPPER_MAIN_DB_URL)); 
			DbConfigurations.setMapperMainDbUser(props.getProperty(DbConstants.MAPPER_MAIN_DB_USER)); 
			DbConfigurations.setMapperMainDbPass(props.getProperty(DbConstants.MAPPER_MAIN_DB_PASS)); 
			
			DbConfigurations.setMapperReplicatedDbUrl(props.getProperty(DbConstants.MAPPER_REPLICATE_DB_URL)); 
			DbConfigurations.setMapperReplicatedDbUser(props.getProperty(DbConstants.MAPPER_REPLICATE_DB_USER)); 
			DbConfigurations.setMapperReplicatedDbPass(props.getProperty(DbConstants.MAPPER_REPLICATE_DB_PASS)); 
			
		
	}

	/**
	 * @return the jdbcDriver
	 */
	public static String getJdbcDriver() {
		return jdbcDriver;
	}

	/**
	 * @param jdbcDriver the jdbcDriver to set
	 */
	public static void setJdbcDriver(String jdbcDriver) {
		DbConfigurations.jdbcDriver = jdbcDriver;
	}

	/**
	 * @return the localDbUrl
	 */
	public static String getLocalDbUrl() {
		return localDbUrl;
	}

	/**
	 * @param localDbUrl the localDbUrl to set
	 */
	public static void setLocalDbUrl(String localDbUrl) {
		DbConfigurations.localDbUrl = localDbUrl;
	}

	/**
	 * @return the localDbUser
	 */
	public static String getLocalDbUser() {
		return localDbUser;
	}

	/**
	 * @param localDbUser the localDbUser to set
	 */
	public static void setLocalDbUser(String localDbUser) {
		DbConfigurations.localDbUser = localDbUser;
	}

	/**
	 * @return the localDbPass
	 */
	public static String getLocalDbPass() {
		return localDbPass;
	}

	/**
	 * @param localDbPass the localDbPass to set
	 */
	public static void setLocalDbPass(String localDbPass) {
		DbConfigurations.localDbPass = localDbPass;
	}

	/**
	 * @return the mapperMainDbUrl
	 */
	public static String getMapperMainDbUrl() {
		return mapperMainDbUrl;
	}

	/**
	 * @param mapperMainDbUrl the mapperMainDbUrl to set
	 */
	public static void setMapperMainDbUrl(String mapperMainDbUrl) {
		DbConfigurations.mapperMainDbUrl = mapperMainDbUrl;
	}

	/**
	 * @return the mapperMainDbUser
	 */
	public static String getMapperMainDbUser() {
		return mapperMainDbUser;
	}

	/**
	 * @param mapperMainDbUser the mapperMainDbUser to set
	 */
	public static void setMapperMainDbUser(String mapperMainDbUser) {
		DbConfigurations.mapperMainDbUser = mapperMainDbUser;
	}

	/**
	 * @return the mapperMainDbPass
	 */
	public static String getMapperMainDbPass() {
		return mapperMainDbPass;
	}

	/**
	 * @param mapperMainDbPass the mapperMainDbPass to set
	 */
	public static void setMapperMainDbPass(String mapperMainDbPass) {
		DbConfigurations.mapperMainDbPass = mapperMainDbPass;
	}
	

	/**
	 * @return the mapperReplicatedDbUrl
	 */
	public static String getMapperReplicatedDbUrl() {
		return mapperReplicatedDbUrl;
	}

	/**
	 * @param mapperReplicatedDbUrl the mapperReplicatedDbUrl to set
	 */
	public static void setMapperReplicatedDbUrl(String mapperReplicatedDbUrl) {
		DbConfigurations.mapperReplicatedDbUrl = mapperReplicatedDbUrl;
	}

	/**
	 * @return the mapperReplicatedDbUser
	 */
	public static String getMapperReplicatedDbUser() {
		return mapperReplicatedDbUser;
	}

	/**
	 * @param mapperReplicatedDbUser the mapperReplicatedDbUser to set
	 */
	public static void setMapperReplicatedDbUser(String mapperReplicatedDbUser) {
		DbConfigurations.mapperReplicatedDbUser = mapperReplicatedDbUser;
	}

	/**
	 * @return the mapperReplicatedDbPass
	 */
	public static String getMapperReplicatedDbPass() {
		return mapperReplicatedDbPass;
	}

	/**
	 * @param mapperReplicatedDbPass the mapperReplicatedDbPass to set
	 */
	public static void setMapperReplicatedDbPass(String mapperReplicatedDbPass) {
		DbConfigurations.mapperReplicatedDbPass = mapperReplicatedDbPass;
	}

	
}
