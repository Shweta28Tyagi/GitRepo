package APITestCase;

import java.io.File;
import java.io.IOException;

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
    String authToken;
    int roleId1;
    Faker faker = new Faker();
    String randomRoleName = faker.job().title();
    Response response;
     
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://mytyles.website:3133/api/v1";
    }

    @AfterClass
    public void close() {
        Assert.assertEquals(response.getStatusCode(), 200);
    }
     
    @Test
    public void login() throws IOException {
        File file = new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\PreLogin\\PasswordLogin.json");
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(file)
                .when()
                .post("/login")
                .andReturn();
        System.out.println("				******** ROLES AND PERMISSIONS ********");

        String res = response.getBody().asString();
        this.authToken = JsonPath.from(res).get("data.token");
        response.then().assertThat().body("message", equalTo("Login successfully"));
    } 
    
    @Test(dependsOnMethods = "login")
    public void getRoles() throws IOException {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/getRoles")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of get roles: " + res);
        response.then().assertThat().body("message", equalTo("Record found successfully"));
    }
    
    @Test(dependsOnMethods = "login")
    public void createRole() throws IOException {
        String file = "{\r\n" +
                      "    \"roleName\": \"" + randomRoleName + "\"\r\n" +
                      "}";

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/createRole")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of create role: " + res);
        this.roleId1 = JsonPath.from(res).get("data.roleId");
        System.out.println("Role Id: " + roleId1);
        
        int statusCode = response.getStatusCode();
        if (statusCode == 201) {
            response.then().assertThat().body("message", equalTo("Role created successfully"));
        } else if (statusCode == 409) {
            response.then().assertThat().body("message", equalTo("Role name already exists"));
        } else if (statusCode == 403) {
            response.then().assertThat().body("message", equalTo("roleName is not allowed to be empty"));
        }
    }
    
    @Test(dependsOnMethods = "login")
    public void editRoleName() throws IOException {
        String file = "{\r\n" +
                      "    \"roleId\": \"" + roleId1 + "\",\r\n" +
                      "    \"roleName\": \"" + randomRoleName + "\"\r\n" +
                      "}";

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/editRoleName")
                .andReturn();
    	
        String res = response.getBody().asString();
        System.out.println("Response body of edit role name: " + res); 	
        response.then().assertThat().body("message", equalTo("Updated successfully"));   	
    }
    
    @Test(dependsOnMethods = "login")
    public void deleteRole() throws IOException {
        String file = "{\r\n" +
                      "    \"roleId\": 93,\r\n" +
                      "    \"change_role_id\": \"" + roleId1 + "\"\r\n" +
                      "}";

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
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
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/getRoleDetails")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of get role details: " + res);
        response.then().assertThat().body("message", equalTo("Record found successfully"));
    }
    
    @Test(dependsOnMethods = "login")
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
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/editRole")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of edit role: " + res);
        response.then().assertThat().body("message", equalTo("Updated successfully"));
    }
    
    //Role list
    @Test(dependsOnMethods = "login")
    public void RoleList() throws IOException {
    	String[] roleType= {"system", "custom"};
 
        String file = "{\r\n"
        		+ "    \"pageNumber\":\"\",\r\n"
        		+ "    \"pageSize\":\"\",\r\n"
        		+ "    \"role_type\":\"\"\r\n"					//roleType
        		+ "}";

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/roleList")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of role list: " + res);
        response.then().assertThat().body("message", equalTo("Record found successfully"));
    }
    
}
