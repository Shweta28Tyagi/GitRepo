package MSNagile;

import static io.restassured.RestAssured.given;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ManageOrganization {
	Response response;
	static String authToken;
	
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
	
	 @Test(dependsOnMethods="Login")
	   public void GetOrgDetail()
	   {	   
				  
		  response = given()
				  .contentType(ContentType.JSON)
	              .header("Authorization", "Bearer " + authToken)
	              .when()
	              .get("/auth/getOrgDetail/ss1");
		  
		  String res=response.getBody().asString();
		  System.out.println("Response body of get org detail : "+res); 
	   }   
	   
	   //Get tax id types
	   @Test(dependsOnMethods="Login")
	   public void GetTaxIdTypes()
	   {	   
				  
		  response = given()
				  .contentType(ContentType.JSON)
	              .header("Authorization", "Bearer " + authToken)
	              .when()
	              .get("/auth/getTaxIdTypes/ss1");
		  
		  String res=response.getBody().asString();
		  System.out.println("Response body of get tax id types : "+res); 
	   } 
	   
	   //Update organization
	   @Test(dependsOnMethods="Login")
	   public void UpdateOrg()
	   {	
		  
		  response = given()
	               .header("Authorization", "Bearer " + authToken)
	               .basePath("/auth/updateOrg")
	               .multiPart("url", "ss1") 
	               .multiPart("contactPerson", "Naman")
	               .multiPart("orgName", "Naman pvt ltd")
	               .multiPart("orgWebsite", "Web app")
	               .multiPart("logo", "null")
	               .multiPart("taxId", 1)
	               .multiPart("shortlogo", "null")
	               .multiPart("taxDetail", "tax1")
	               .header("Accept", "*/*")
	               .header("Content-Type", "multipart/form-data")
	               .when()
	               .post();
		  
		  String res=response.getBody().asString();
		  System.out.println("Response body of update org : "+res); 
	   } 
}


