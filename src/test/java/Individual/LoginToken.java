package Individual;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class LoginToken 
{
	public static String authToken;
	
   @BeforeClass
   public void setup() {
       RestAssured.baseURI = "https://mytyles.website:3133/api/v1";
   }
   
   // Test case for login with password
   @Test
   public static void testLoginWithPassword() {
       String file ="{\r\n"
       		+ "     \"phone_number\": \"9999999999\",\r\n"
       		+ "     \"password\": \"Test@123\",\r\n"
       		+ "     \"login_type\": \"password\"\r\n"
       		+ "}";
        Response response = given()
               .contentType(ContentType.JSON)
               .body(file)
               .when()
               .post("/login")
               .then()
               .statusCode(200)
               .body("message", equalTo("Login successfully"))
               .extract().response();
        
       // private static String authToken;
       String res = response.getBody().asString();
       System.out.println("Response of Login with Password : "+res);
       authToken = JsonPath.from(res).get("data.token");
       System.out.println(authToken);
   }
}


