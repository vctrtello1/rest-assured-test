package com.victortello.ws.restassuredtest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.restassured.RestAssured;

import java.util.Map;
import java.util.HashMap;

import io.restassured.response.Response;

class UsersServiceEndpointTest {
	
	private final String EMAIL_ADDRESS = "victorhugotello@hotmail.com";
	private final String JSON = "application/json";
	private static String authorizationHeader;
	private static String userId;

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8888;
	}

	@Test
	void testUserLogin() {
		Map<String, String> loginDetails = new HashMap<>();
		loginDetails.put("email", EMAIL_ADDRESS);
		loginDetails.put("password", "puma18ar");


		Response response = given().contentType(JSON).accept(JSON).body(loginDetails).when()
				.post( "/users/login").then().statusCode(200).extract().response();
		
		authorizationHeader = response.header("Authorization");
		userId = response.header("UserID");

		assertNotNull(authorizationHeader);
		assertNotNull(userId);
	}

}
