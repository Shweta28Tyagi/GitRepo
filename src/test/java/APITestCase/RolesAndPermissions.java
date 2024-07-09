package APITestCase;

import java.io.File;
import java.io.IOException;
import Individual.LoginToken;
import static org.hamcrest.Matchers.equalTo;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class RolesAndPermissions {
    String token;
    int roleId1;
    Faker faker = new Faker();
    String randomRoleName = faker.job().title();
    Response response;
     
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://mytyles.website:3133/api/v1";
        LoginToken.testLoginWithPassword();
        token=LoginToken.authToken;
    }

    @AfterClass
    public void close() {
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    
    @Test
    public void getRoles() throws IOException {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/getRoles")
                .then()
                .body("message", equalTo("Record found successfully"))
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response body of get roles: " + res);
    }
    
    @Test
    public void createRole() throws IOException {
        String file = "{\r\n" +
                      "    \"roleName\": \"" + randomRoleName + "\"\r\n" +
                      "}";

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/createRole")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of create role: " + res);
        this.roleId1 = JsonPath.from(res).get("data.roleId");
       
        int statusCode = response.getStatusCode();
        if (statusCode == 201) {
            response.then().assertThat().body("message", equalTo("Role created successfully"));
        } else if (statusCode == 409) {
            response.then().assertThat().body("message", equalTo("Role name already exists"));
        } else if (statusCode == 403) {
            response.then().assertThat().body("message", equalTo("roleName is not allowed to be empty"));
        }
    }
    
    @Test
    public void editRoleName() throws IOException {
        String file = "{\r\n" +
                      "    \"roleId\": \"" + roleId1 + "\",\r\n" +
                      "    \"roleName\": \"" + randomRoleName + "\"\r\n" +
                      "}";

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/editRoleName")
                .then()
                .body("message", equalTo("Updated successfully"))
                .extract().response();
    	
        String res = response.getBody().asString();
        System.out.println("Response body of edit role name: " + res); 	 	
    }
    
    @Test
    public void deleteRole() throws IOException {
        String file = "{\r\n" +
                      "    \"roleId\": 93,\r\n" +
                      "    \"change_role_id\": \"" + roleId1 + "\"\r\n" +
                      "}";

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/deleteRole")
                .andReturn();
    	
        String res = response.getBody().asString();
        System.out.println("Response body of delete role: " + res);
    	
        if (res.contains("provide valid role id")) {
            Assert.assertEquals(response.getStatusCode(), 401);
            response.then().assertThat().body("message", equalTo("Please provide valid role id"));
        } else if (res.contains("Deleted successfully")) { 
            response.then().assertThat().body("message", equalTo("Deleted successfully"));
        } else if (res.contains("Please Provide Other role")) {
            Assert.assertEquals(response.getStatusCode(), 404);
            response.then().assertThat().body("message", equalTo("Please Provide Other role to update before deleting this Role"));
        }
    }
    
    @Test(dependsOnMethods = "createRole")
    public void getRoleDetail() throws IOException {
        String file = "{\r\n" +
                      "    \"roleId\":\"1\"\r\n" +
                      "}";
        
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/getRoleDetails")
                .then()
                .body("message", equalTo("Record found successfully"))
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response body of get role details: " + res);
    }
    
    @Test
    public void editRole() throws IOException {
        String file = "{\r\n" +
                      "    \"role_name\": \"Principal Marketing Specialist\",\r\n" +
                      "    \"role_id\": \"80\",\r\n" +
                      "    \"permissions\": [\r\n" +
                      "        {\r\n" +
                      "            \"module_id\": 29,\r\n" +
                      "            \"modulePermission\": [\r\n" +
                      "                {\r\n" +
                      "                    \"name\": \"read\",\r\n" +
                      "                    \"displayName\": \"Read\",\r\n" +
                      "                    \"isSelected\": true\r\n" +
                      "                }\r\n" +
                      "            ]\r\n" +
                      "        }\r\n" +
                      "    ]\r\n" +
                      "}";

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/editRole")
                .then()
                .body("message", equalTo("Updated successfully"))
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response body of edit role: " + res);
    }
    
    //Role list
    @Test
    public void RoleList() throws IOException {
    	String[] roleType= {"system", "custom"};
 
        String file = "{\r\n"
        		+ "    \"pageNumber\":\"\",\r\n"
        		+ "    \"pageSize\":\"\",\r\n"
        		+ "    \"role_type\":\"\"\r\n"					//roleType
        		+ "}";

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/roleList")
                .then()
                .body("message", equalTo("Record found successfully"))
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response body of role list: " + res);
    }   
}
