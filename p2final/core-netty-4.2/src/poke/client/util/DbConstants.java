/**
 * 
 */
package poke.client.util;

/**
 * @author arun_malik
 *
 */
public interface DbConstants {

	static final String JDBC_DRIVER = "jdbcDriver";
	
	static final String LOCAL_DB_URL = "localDbUrl";
	static final String LOCAL_DB_USER = "localDbUser";
	static final String LOCAL_DB_PASS = "localDbPass";
	
	static final String MAPPER_MAIN_DB_URL = "mapperMainDbUrl"; 
	static final String MAPPER_MAIN_DB_USER = "mapperMainDbUser";
	static final String MAPPER_MAIN_DB_PASS = "mapperMainDbPass";
	
	static final String MAPPER_REPLICATE_DB_URL = "mapperReplicateDbUrl"; 
	static final String MAPPER_REPLICATE_DB_USER = "mapperReplicateDbUser";
	static final String MAPPER_REPLICATE_DB_PASS = "mapperReplicateDbPass";

	static final String DB_PROPERTIES_FILE = "com/lifeForce/storage/db.properties";
}
