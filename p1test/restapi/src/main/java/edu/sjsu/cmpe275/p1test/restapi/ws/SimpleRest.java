package edu.sjsu.cmpe275.p1test.restapi.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.p1test.restapi.service.SimpleDataStore;

@RestController
public class SimpleRest {
	
	@Autowired
	private SimpleDataStore dataServices;
	
	@RequestMapping(value = "/api", produces = "application/json")
	public String[] getListOfUsers() {
		return dataServices.getListOfUsers();
	}
	

}
