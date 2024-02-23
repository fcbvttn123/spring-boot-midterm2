package ca.sheridancollege.vutran.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.sheridancollege.vutran.models.AuthenticationRequest;
import ca.sheridancollege.vutran.models.AuthenticationResponse;

@Service
public class AuthenticationService {
	
	final private String USERNAME = "simon.hood@sheridancollege.ca";
	final private String PASSWORD = "$2a$10$pWPXXn0JtcDzD4xRhtHZcOB0eIdY26.yQPa/g5.Bjmq23uYV50r2m";
	
	private String token = null;
	
	private String getBody() {
		// create an AuthenticationRequest instance based on credentials
		AuthenticationRequest credentials = new AuthenticationRequest(USERNAME, PASSWORD);
		// return a JSONified version
		return JSONify(credentials);
	}
	
	// convert AuthenticationRequest credentials to JSON
	private String JSONify(final AuthenticationRequest userInfo) {
		String toReturn = null;
		try {
			toReturn = new ObjectMapper().writeValueAsString(userInfo);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		if (token != null) {
			String authToken = "Bearer " + token;
			headers.set("Authorization", authToken);
		}
		return headers;
	}
	
	private void authenticate(RestTemplate restTemplate) {
		// create headers and body specifying that it is JSON request and include
		// JSONifed details
		HttpHeaders authenticationHeaders = getHeaders();
		String authenticationBody = getBody();
		HttpEntity<String> authenticationEntity = new HttpEntity<String>(authenticationBody, authenticationHeaders);
		// Authenticate User and get JWT
		ResponseEntity<AuthenticationResponse> authenticationResponse = restTemplate.exchange(
				"http://localhost:50000/api/v1/auth/authenticate", HttpMethod.POST, authenticationEntity,
				AuthenticationResponse.class);
		// if the authentication is successful, get the JWT token from the response
		if (authenticationResponse.getStatusCode().equals(HttpStatus.OK))
			token = authenticationResponse.getBody().getToken();
	}
	
	// make an HTTP request in the type of our choosing to our RESTful service
	// with JWT inside the header but with nothing in the request body
	// (good for all GETs and DELETEs specifically!)
	public <T> ResponseEntity<T> standardRequest(RestTemplate restTemplate, String url, HttpMethod methodType,
			Class<T> returnType) {
		if (token == null)
			authenticate(restTemplate);
		HttpHeaders headers = getHeaders();
		HttpEntity<String> request = new HttpEntity<>("", headers);
		return (ResponseEntity<T>) restTemplate.exchange(url, methodType, request, returnType);
	}
	
	// make an HTTP POST request to our RESTful service
	// with JWT in the header and an Object we wish to store via RESTful in the body
	public <T> ResponseEntity<T> postRequest(RestTemplate restTemplate, String url, Object objectToPost,
			Class<T> returnType) {
		if (token == null)
			authenticate(restTemplate);
		HttpHeaders headers = getHeaders();
		HttpEntity<Object> request = new HttpEntity<>(objectToPost, headers);
		return (ResponseEntity<T>) restTemplate.postForEntity(url, request, returnType);
	}
	
	// make an HTTP PUT request to our RESTful service
	// with JWT in the header and an Object we wish to update/replace via RESTful in
	// the body
	public void putRequest(RestTemplate restTemplate, String url, Object objectToPut) {
		if (token == null)
			authenticate(restTemplate);
		HttpHeaders headers = getHeaders();
		HttpEntity<Object> request = new HttpEntity<>(objectToPut, headers);
		restTemplate.put(url, request, objectToPut);
	}

}
