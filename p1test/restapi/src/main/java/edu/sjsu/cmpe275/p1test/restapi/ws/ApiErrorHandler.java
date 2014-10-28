package edu.sjsu.cmpe275.p1test.restapi.ws;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import edu.sjsu.cmpe275.p1test.restapi.service.StandardApiException;

@ControllerAdvice
public class ApiErrorHandler {
	
	
	@ExceptionHandler({StandardApiException.class})
	public ResponseEntity<Map<String, String>> handleRandomTestError(StandardApiException ex) {
		Map<String, String> exception = new HashMap<String, String>();
		
		exception.put("type", ex.getClass().getName());
		exception.put("msg", ex.getMessage());
		exception.put("isError", "true");
		
		return new ResponseEntity<Map<String,String>>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
