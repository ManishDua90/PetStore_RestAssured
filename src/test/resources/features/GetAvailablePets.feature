Feature: Add, delete, update and retrieve pet in the petstore
Description: The purpose of the test is to retrieve all the available pets from the store

Pet Store URL: https://petstore.swagger.io

	Scenario: User is able to retrieve available pets
		Given Petstore is available
		When User retrieves the "available" pets	
		Then "available" pets are retrieved
	
	Scenario: User is able to add a pet in the petstore
		Given Petstore is available
		When User adds a pet to the the store
		| id  | categoryId | categoryName     | name       | photoUrls        | tagId | tagName | status    |
		| 1005 | 1000			 | SampleCatName    | MyPet      | SamplePhotoURL   | 890   | TagName | available |
		Then Pet is added with name "MyPet"
	
	Scenario: User is able to change the pet status already present the petstore
		Given Petstore is available
		When User changes the status of a pet with id "1005" to "sold"
		Then Pet status is changed to "sold" for pet id "1005"
		
	
	Scenario: User is able to delete the pet from the petStore
		Given Petstore is available
		When User deletes the pet with id "1005"
		Then Pet "1001" is deleted