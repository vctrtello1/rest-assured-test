package com.victortello.ws.restassuredtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsersServiceEndpointTest {

	private final String EMAIL_ADDRESS = "victorhugotello@hotmail.com";
	private final String JSON = "application/json";
	private static String authorizationHeader;
	private static String userId;
	private static List<Map<String, String>> addresses;

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8888;
	}

	@Test
	@Order(1)
	void testUserLogin() {
		Map<String, String> loginDetails = new HashMap<>();
		loginDetails.put("email", EMAIL_ADDRESS);
		loginDetails.put("password", "puma18ar");

		Response response = given().contentType(JSON).accept(JSON).body(loginDetails).when().post("/users/login").then()
				.statusCode(200).extract().response();

		authorizationHeader = response.header("Authorization");
		userId = response.header("UserID");

		assertNotNull(authorizationHeader);
		assertNotNull(userId);
	}

	@Test
	@Order(2)
	final void testGetUserDetails() {

		Response response = given().pathParam("id", userId).header("Authorization", authorizationHeader).accept(JSON)
				.when().get("/users/{id}").then().statusCode(200).contentType(JSON).extract().response();

		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		addresses = response.jsonPath().getList("addresses");
		String addressId = addresses.get(0).get("addressId");

		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(lastName);
		assertEquals(EMAIL_ADDRESS, userEmail);

		assertTrue(addresses.size() == 2);
		assertTrue(addressId.length() == 30);

	}

	@Test
	@Order(3)
	final void testUpdateUserDetails() {
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "hugo");
		userDetails.put("lastName", "miramontes");

		Response response = given().contentType(JSON).accept(JSON).header("Authorization", authorizationHeader)
				.pathParam("id", userId).body(userDetails).when().put("/users/{id}").then().statusCode(200)
				.contentType(JSON).extract().response();

		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");

		List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");

		assertEquals("Serge", firstName);
		assertEquals("Kargopolov", lastName);
		assertNotNull(storedAddresses);
		assertTrue(addresses.size() == storedAddresses.size());
		assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
	}

	@Test
	@Order(4)
	final void deleteUserDetailsTest() {
		Response response = given().header("Authorization", authorizationHeader).accept(JSON).pathParam("id", userId)
				.when().delete("/users/{id}").then().statusCode(200).contentType(JSON).extract().response();

		String operationResult = response.jsonPath().getString("operationResult");
		assertEquals("SUCCESS", operationResult);

	}

}
