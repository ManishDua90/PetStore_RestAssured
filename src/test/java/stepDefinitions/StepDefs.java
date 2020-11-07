package stepDefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;

import groovy.util.logging.Log;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class StepDefs {
	private static final String BASE_URL = "https://petstore.swagger.io";
	private static final String PET_URL_STRING = "/v2/pet/";
	static Logger log = Logger.getLogger(Log.class.getClass());
	String log4jConfPath = "log4j.properties";

	public StepDefs()
	{
		PropertyConfigurator.configure(log4jConfPath);
	}

	private static Response response;
	List<String> pets = new ArrayList<>();

	@Given("^Petstore is available$")
	public void petStoreIsAvailable() {
		log.info("Checking if Petstore is available..");
		RequestSpecification request = setBaseURI();
		request.header("Content-Type", "application/json");
		response = request.get();
		Assert.assertTrue(response.getStatusCode() == 200);
		log.info("Petstore is available");
	}

	@When("^User retrieves the \"(.*)\" pets$")
	public void listOfPets(String status) {
		log.info("Retrieving all " + status + " pets..");
		pets.clear();
		RequestSpecification request = setBaseURI();
		response = request.get(PET_URL_STRING + "findByStatus?status=" + status);
		pets = response.jsonPath().getList("$");

	}

	@When("^User adds a pet to the the store$")
	public void addPet(List<Map<String, String>> petInput) {
		
		log.info("Adding a new pet to the store.");

		String id = petInput.get(0).get("id");
		String categoryId = petInput.get(0).get("categoryId");
		String categoryName = petInput.get(0).get("categoryName");
		String name = petInput.get(0).get("name");
		String photoURL = petInput.get(0).get("photoUrls");
		String tagId = petInput.get(0).get("tagId");
		String tagName = petInput.get(0).get("tagName");
		String status = petInput.get(0).get("status");

		RequestSpecification request = setBaseURI();
		request.header("Content-Type", "application/json");

		String body = createBody(id, categoryId, categoryName, name, photoURL, tagId, tagName, status);

		response = request.body(body).post(PET_URL_STRING);

		response = request.get(PET_URL_STRING + "findByStatus?status=" + status);
		pets = response.jsonPath().getList("name");

	}

	@When("^User changes the status of a pet with id \"(.*)\" to \"(.*)\"$")
	public void changeStatus(int id, String status) {
		log.info("Changing status to " + status + " for pet id " + id);
		RequestSpecification request = setBaseURI();
		request.queryParam("id", id);
		request.queryParam("status", status);
		response = request.post(PET_URL_STRING + id);
	}

	@When("^User deletes the pet with id \"(.*)\"$")
	public void deletePet(int id) {
		log.info("Deleting pet with id " + id);
		RequestSpecification request = setBaseURI();
		response = request.delete(PET_URL_STRING + id);
		
	}

	@Then("^\"(.*)\" pets are retrieved$")
	public void IsPetAvailable(String status) {
		Assert.assertTrue(pets.size() > 0);
	}

	@Then("^Pet is added with name \"(.*)\"$")
	public void IsPetAdded(String name) {
		boolean flag = false;
		for (int i = 0; i < pets.size(); i++) {
			try {
				if (pets.get(i).equals(name))
					flag = true;
			} catch (NullPointerException e) {
			}
		}
		Assert.assertTrue(flag);
		log.info("Pet with name " + name + " added to the pet store");
	}

	@Then("^Pet status is changed to \"(.*)\" for pet id \"(.*)\"$")
	public void isStatusChanged(String status, int id) {
		RequestSpecification request = setBaseURI();
		request.header("Content-Type", "application/json");
		response = request.get(PET_URL_STRING + id);
		String responseStatus = response.jsonPath().get("status");
		Assert.assertTrue(responseStatus.contentEquals(status));
		log.info("Pet status is changed to " + status + " for pet id " + id);
	}

	@Then("^Pet \"(.*)\" is deleted$")
	public void isPetDeleted(int id) {
		boolean flag = false;
		RequestSpecification request = setBaseURI();
		request.header("Content-Type", "application/json");
		response = request.get(PET_URL_STRING + id);
		String responseStatus = response.jsonPath().get("status");
		if (responseStatus == null)
			flag = true;
		Assert.assertTrue(flag);
		log.info("Deleted pet id " + id + " from petstore");

	}

	private RequestSpecification setBaseURI() {
		log.info("Setting base URI..");
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		return request;
	}

	private String createBody(String id, String categoryId, 
			String categoryName, String name, String photoURL, 
			String tagId, String tagName, String status)
	{
		String body = "{\"id\":" + id + ",\"category\":{\"id\":" + categoryId + ",\"name\":\"" + categoryName
				+ "\"},\"name\":\"" + name + "\",\"photoUrls\":[\"" + photoURL + "\"],\"tags\":[{\"id\":" + tagId
				+ ",\"name\":\"" + tagName + "\"}],\"status\":\"" + status + "\"}";
		return body;
	}
}
