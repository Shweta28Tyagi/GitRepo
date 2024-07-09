package APITestCase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import Individual.LoginToken;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ManageProfile {
    private String token;
    private String Doj;
    Response response;
    String number;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://mytyles.website:3133/api/v1";
        LoginToken.testLoginWithPassword();
	    token = LoginToken.authToken;
    }
    @AfterClass
    public void close()
    {
    	Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void testGetUserProfile() {
        String requestBody = "{ \"id\": 1 }";
   
         response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/getUserProfile")
                .then()
                .body("message", equalTo("Record found successfully"))
                .extract().response();

        this.Doj = response.jsonPath().getString("data.date_of_joining");
        this.number=response.jsonPath().getString("data.phone_number");
       // System.out.println(number);
        System.out.println("Response body of User Profile : "+response.getBody().asString());
        Assert.assertNotNull(Doj, "Date of Joining should not be null");
    }

    @Test
    public void testUploadProfileImage() throws IOException {
        String filePath = "C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageUsers\\tile1.jpg";
        File file = Paths.get(filePath).toFile();

         response = given()
                .header("Authorization", "Bearer " + token)
                .multiPart("id", 1)
                .multiPart("file", file, "image/jpeg")
                .when()
                .post("/uploadProfileImage")
                .then()
                .body("message",equalTo("Added successfully"))
                .extract().response();

        JSONObject responseBody = new JSONObject(response.getBody().asString());
        System.out.println("Response body of upload profile image: " + responseBody.toString(4));
    }

    @Test
    public void testRemoveProfileImage() {
        String requestBody = "{ \"id\": 1 }";

         response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/RemoveProfileImage")
                .then()
                .body("message", equalTo("Profile Image removed successfully."))
                .extract().response();

        System.out.println("Response body of remove profile image: " + response.getBody().asString());
    }

    @Test(dependsOnMethods = "testGetUserProfile")
    public void testUpdateUserProfile() throws ParseException {
        Faker faker = new Faker();
        String randomFirstName = faker.name().firstName();
        String randomLastName = faker.name().lastName();
        String email = faker.internet().emailAddress();      
        
     // Remove any extraneous characters from Doj
        String cleanDoj = Doj.replaceAll("[\\[\\]]", "");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dojDate = dateFormat.parse(cleanDoj);
        String formattedDoj = dateFormat.format(dojDate);
        //System.out.println(formattedDoj);
        
        String requestBody = String.format("{\n" +
                "    \"id\": 1,\n" +
                "    \"first_name\": \"%s\",\n" +
                "    \"last_name\": \"%s\",\n" +
                "    \"user_email\": \"%s\",\n" +
                "    \"date_of_birth\": \"2013-01-01\",\n" +
                "    \"date_of_joining\": \"%s\",\n" +
                "    \"phone_number\": \"%s\",\n" +
                "    \"change_password\": {}\n" +
                "}", randomFirstName, randomLastName, email,formattedDoj,number);

         response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/updateUserProfile")
                .then()
                .body("message", equalTo("User Updated Successfully"))
                .extract().response();

        System.out.println("Response body of edit user profile: " + response.getBody().asString());
    }

    @Test
    public void testChangePassword() {
        Faker faker = new Faker();
        String randomFirstName = faker.name().firstName();
        String randomLastName = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String newPassword = generateUniquePassword();

        String requestBody = String.format("{\n" +
                "    \"id\": 1,\n" +
                "    \"first_name\": \"%s\",\n" +
                "    \"last_name\": \"%s\",\n" +
                "    \"user_email\": \"%s\",\n" +
                "    \"date_of_birth\": \"\",\n" +
                "    \"date_of_joining\": \"\",\n" +
                "    \"change_password\": {\n" +
                "        \"new_password\": \"%s\",\n" +
                "        \"confirm_password\": \"%s\"\n" +
                "    }\n" +
                "}", randomFirstName, randomLastName, email, newPassword, newPassword);

         response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/updateUserProfile")
                .then()
                .body("message", equalTo("User Updated Successfully"))
                .extract().response();

        System.out.println("Response body of change password: " + response.getBody().asString());
    }

    private String generateUniquePassword() {
        String basePassword = "Test" + "@";
        Random random = new Random();
        int randomNumber = 100 + random.nextInt(900); // Generate a random number between 100 and 999
        return basePassword + randomNumber;
    }
}




