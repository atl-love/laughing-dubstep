package edu.sjsu.cmpe275.p1test.restapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sjsu.cmpe275.p1test.restapi.pojo.ImageFile;

@Service
public class SimpleDataStore {
	
	// Not that this is no the most effect way for storing data but it will do for now
	private Map<String, Map<String, ImageFile>> dataStore;
	private List<String> usedUuids;
	
	@Autowired
	public SimpleDataStore() {
		dataStore = new HashMap<String, Map<String, ImageFile>>();
		usedUuids = new ArrayList<String>();
	}
	
	
	/**
	 * Returns a list of users
	 * @return String[] containing the list of users
	 */
	public String[] getListOfUsers() {
		return dataStore.keySet().toArray(new String[dataStore.size()]);
	}
	
	
	
	/**
	 * Returns a list of UUIDs that this user already has
	 * 
	 * @param user
	 * @return String[] of files
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public String[] getFilesForUser(String user) throws NullPointerException, IllegalArgumentException {
		// Argument validation
		if(user == null) {
			throw new NullPointerException();
		}
		if(user.trim().length() <= 0) {
			throw new IllegalArgumentException("username cannot be empty");
		}
		
		
		// Get User's File
		Map<String, ImageFile> filesForUser = dataStore.get(user);
		if(filesForUser != null) {
			return filesForUser.keySet().toArray(new String[filesForUser.size()]);
		}
		
		// return null if user is not found
		return null;
	}
}
