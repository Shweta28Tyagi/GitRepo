package MSNagile;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ManageUsersAndGroups {
	Response response;
	static String authToken;
	static Faker faker=new Faker();
	static String randomText = faker.lorem().characters(5, true, true);
	
	 @BeforeClass
	   public void setup() {
	       RestAssured.baseURI = "https://be.eazyagile.com";
	      
	   }

	   @AfterClass
	   public void close()
	   {
	 	  Assert.assertEquals(response.getStatusCode(), 200);
	   }
	
	@Test
	   public void Login() throws IOException {
	       String file="{\r\n"
	       		+ "  \"url\": \"ss1\",\r\n"
	       		+ "  \"email\": \"shweta.tyagi+9@devstringx.com\",\r\n"
	       		+ "  \"password\": \"Shweta@123\",\r\n"
	       		+ "  \"gToken\": \"\",\r\n"
	       		+ "  \"type\": \"self\"\r\n"
	       		+ "}";
		  
	        response = given()
	               .contentType(ContentType.JSON)
	               .body(file)
	               .when()
	               .post("/login")
	               .then()
	               .body("message", equalTo("Loggedin Successfully"))
	               .extract().response();
	        
	       String res = response.getBody().asString();
	       System.out.println("Response of Login : "+res);
	       this.authToken = JsonPath.from(res).get("token");	       
	    }
	
	//Get all users
	 @Test(dependsOnMethods="Login")
	   public void GetAllUsers()
	   {	   
				  
		  response = given()
				  .contentType(ContentType.JSON)
	              .header("Authorization", "Bearer " + authToken)
	              .when()
	              .get("/auth/getAllUsers/ss1/null/1/25/null/0/null");
		  
		  String res=response.getBody().asString();
		  System.out.println("Response body of get all users : "+res); 
	   } 
	 
	 //Get all groups
	 @Test(dependsOnMethods="Login")
	   public void GetAllGroups()
	   {	   
				  
		  response = given()
				  .contentType(ContentType.JSON)
	              .header("Authorization", "Bearer " + authToken)
	              .when()
	              .get("/auth/getAllGroups/ss1/null/1/25/0");
		  
		  String res=response.getBody().asString();
		  System.out.println("Response body of get all groups : "+res); 
	   } 
	 
	 //Get roles
	 @Test(dependsOnMethods="Login")
	 public void GetAllRoles()
	 {
		 response = given()
				  .contentType(ContentType.JSON)
	              .header("Authorization", "Bearer " + authToken)
	              .when()
	              .get("/auth/getAllRoles/ss1/null/null/null");
		  
		  String res=response.getBody().asString();
		  System.out.println("Response body of get all roles : "+res); 
	 }
	 							
	 									//  ####### GROUP #######
	 //Create Group
	 @Test(dependsOnMethods="Login")
	 public void CreateGroup()
	 {
		 String payload="{\r\n"
		 		+ "    \"url\": \"ss1\",\r\n"
		 		+ "    \"groupName\": \""+randomText+" \",\r\n"
		 		+ "    \"usersarray\": \"\",\r\n"
		 		+ "    \"projectId\": \"null\"\r\n"
		 		+ "}";
		 
		 response=given()
				 .contentType(ContentType.JSON)
				 .headers("Authorization","Bearer "+authToken)
				 .body(payload)
				 .when()
				 .post("/auth/createGroup");
		 
		 String res=response.getBody().asString();
		 System.out.println("Response of create group : "+res);				 
	 }
	 
	 //Update group
	 @Test(dependsOnMethods="Login")
	 public void UpdateGroup()
	 {
		 String payload="{\r\n"
		 		+ "    \"url\": \"ss1\",\r\n"
		 		+ "    \"projectId\": \"null\",\r\n"
		 		+ "    \"groupId\": \"1e77bc51082549c6c21310ca7529a8e7\",\r\n"
		 		+ "    \"groupName\": \"new one group\",\r\n"
		 		+ "    \"usersarray\": \"\"\r\n"
		 		+ "}";
		 
		 response=given()
				 .contentType(ContentType.JSON)
				 .headers("Authorization","Bearer "+authToken)
				 .body(payload)
				 .when()
				 .post("/auth/editGroup");
		 
		 String res=response.getBody().asString();
		 System.out.println("Response of update group : "+res);				 
	 }
	 
	 //Delete Group
	 @Test(dependsOnMethods="Login")
	 public void DeleteGroup()
	 {
		 String payload="{\r\n"
		 		+ "    \"url\": \"ss1\",\r\n"
		 		+ "    \"groupId\": \"1e77bc51082549c6c21310ca7529a8e7\",\r\n"
		 		+ "    \"projectId\": \"null\"\r\n"
		 		+ "}";
		 
		 response=given()
				 .contentType(ContentType.JSON)
				 .headers("Authorization","Bearer "+authToken)
				 .body(payload)
				 .when()
				 .post("/auth/deleteGroup");
		 
		 String res=response.getBody().asString();
		 System.out.println("Response of delete group : "+res);				 
	 }
	 
	 								//	########  USER #######
	 //INVITE USER
	 @Test(dependsOnMethods="Login")
	 public void InviteUser() {

	     // Step 1: Send the invite request
	     String payload = "{\r\n"
	         + "    \"projectId\": \"null\",\r\n"
	         + "    \"url\": \"ss1\",\r\n"
	         + "    \"email\": \"shweta.tyagi+019@devstringx.com\"\r\n"
	         + "}";

	     response = given()
	         .contentType(ContentType.JSON)
	         .headers("Authorization", "Bearer " + authToken)
	         .body(payload)
	         .when()
	         .post("/auth/inviteMember");

	     String res = response.getBody().asString();
	    // System.out.println("Response of invite member: " + res);

	     // Step 2: Check if the response indicates that the user already exists
	     if (res.contains("User already exist in this organization.")) {
	         System.out.println("User already exists in the organization with this email");
	     } else {
	         // Handle other responses or successful invite
	         System.out.println("User invitation response: " + res);
	     }
	 }
	 
	 //Deactivate User
	 @Test(dependsOnMethods="Login")
	    public void GetAllUsersAndDeactivateIfActive() {
	        
	        // Step 1: Get all users
		 response = given()
	              .contentType(ContentType.JSON)
	              .header("Authorization", "Bearer " + authToken)
	              .when()
	              .get("/auth/getAllUsers/ss1/null/1/25/null/0/null");

	    String res = response.getBody().asString();
	    System.out.println("Response body of get all users : " + res); 
	    
	    // Extract user status and userId
	    List<Map<String, Object>> users = response.jsonPath().getList("users");
	    
	    for (Map<String, Object> user : users) {
	        String userId = (String) user.get("userId");
	       
	        String status = (String) user.get("status");
	        System.out.println(status);

	        // Check if the user status is active (assuming active status is "1")
	        if (status.equals("1")) {
	            
	            // Step 2: Deactivate the user
	            response = given()
	                       .header("Authorization", "Bearer " + authToken)
	                       .basePath("/auth/updateUserStatus")
	                       .multiPart("projectId", "null") 
	                       .multiPart("url", "ss1")
	                       .multiPart("userId", userId)
	                       .multiPart("status", 0) // 0 indicates deactivation
	                       .header("Accept", "*/*")
	                       .header("Content-Type", "multipart/form-data")
	                       .when()
	                       .post();
	            
	            String deactivateRes = response.getBody().asString();
	            System.out.println("Response body of deactivate user : " + deactivateRes); 

	            // Optional: Assert the deactivation was successful
	            response.then().statusCode(200)
	                    .body("message", equalTo("User deactivated successfully")); // Adjust based on actual API response
	        }
	    }
	 }
	 
	 //Assign Group To User
	 @Test(dependsOnMethods="Login")
	 public void AssignGroupToUser() 
	 {
		 String payload ="{\r\n"
		 		+ "  \"url\": \"ss1\",\r\n"
		 		+ "  \"projectId\": \"null\",\r\n"
		 		+ "  \"userId\": \"65e024961352f13a93e2a644099b76f4\",\r\n"
		 		+ "  \"groupJson\": \"[{\\\"groupId\\\":\\\"93f1be1e65ee14a56b6b355f7a388b5f\\\",\\\"groupName\\\":\\\"one\\\",\\\"isSelected\\\":\\\"true\\\"}]\"\r\n"
		 		+ "}";
		 
		 response = given()
				 .contentType(ContentType.JSON)
                 .header("Authorization", "Bearer " + authToken)
                 .body(payload)
                 .when()
                 .post("/auth/assignGroupsToUser");

	     String res = response.getBody().asString();
	     System.out.println("Response of assign group to user: " + res);
	 }
}

