package APITestCase;

import java.io.File;
import java.util.Iterator;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.hamcrest.Matcher;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.github.javafaker.Faker;

import APITestCase.Login;

public class ManageUser {
	String authToken;
	private static final Faker faker=new Faker();
	 String randomFirstName = faker.name().firstName();
	 int userId;
	 int id;
	 int getUserId;
	 List<Integer>  roleID;
	 String branchName;
	 Iterator<Integer> iterator;
	 private String payload;
	 Response response;
	 
	 @BeforeClass
	   public void setup() {
	       RestAssured.baseURI = "https://mytyles.website:3133/api/v1";
	   }
	 @AfterClass
     public void close()
     {
   	  Assert.assertEquals(response.getStatusCode(), 200);
     }

    @Test
    public void Login() throws IOException
    {
        File file = new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\PreLogin\\PasswordLogin.json");

         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(file)
                .when()
                .post("/login")
                .andReturn();
        System.out.println("        ******** MANAGE USER ********");

        String res = response.getBody().asString();
        this.authToken = JsonPath.from(res).get("data.token");
        response.then().assertThat().body("message", equalTo("Login successfully"));
    } 
    
   // Generating random phone number
    private String generateRandomIndianPhoneNumber() {
        // Ensure it generates a 10-digit number
        return faker.number().digits(10); // Start with '9' to ensure a valid 10-digit Indian number
    }
    public static Date generateRandomDateOfBirth(int minAge, int maxAge) {
        return faker.date().birthday(minAge, maxAge);
    }  
    public static Date generateDateOfJoining(Date dateOfBirth, int minYearsGap) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        calendar.add(Calendar.YEAR, minYearsGap);
        return calendar.getTime();
    }
    
    @Test(dependsOnMethods="Login")
   public void AddUser()  {
    	 String randomFirstName = faker.name().firstName();
         String randomLastName = faker.name().lastName();
         String randomPhoneNumber = generateRandomIndianPhoneNumber();
         String email=faker.internet().emailAddress();
         
         Date dob = generateRandomDateOfBirth(18, 60);
         Date doj = generateDateOfJoining(dob, 16);
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
         String formattedDob = dateFormat.format(dob);
        // System.out.println("Random Date of Birth: " + formattedDob);
         String formattedDoj=dateFormat.format(doj);
        // System.out.println("Date of Joining :"+ formattedDoj);
    	String  payload="{\r\n"
    			+ "    \"first_name\": \""+randomFirstName+"\",\r\n"
    			+ "    \"last_name\": \""+randomLastName+"\",\r\n"
    			+ "    \"user_email\": \""+email+"\",\r\n"
    			+ "    \"phone_number\": \""+randomPhoneNumber+"\",\r\n"
    			+ "    \"date_of_birth\": \""+formattedDob+"\",\r\n"
    			+ "    \"date_of_joining\": \""+formattedDoj+"\",\r\n"
    			+ "    \"role\": \"6\",\r\n"
    			+ "    \"branch\": \"\",\r\n"
    			+ "    \"vendor_company_name\": \"\",\r\n"
    			+ "    \"vendor_company_admin_id\": \"\"\r\n"
    			+ "}";
       
        response = RestAssured.given()
               .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
               .body(payload)
               .when()
               .post("/createUser")
               .then()
               .body("message", equalTo("User Added Successfully"))
               .extract().response();
       
       String res1 = response.getBody().asString();
       System.out.println("Response body of Add User : " + res1);

       // Extract user_id from response
       this.userId = JsonPath.from(res1).get("data.user_id");
       //System.out.println("After adding user id :" + userId);
   }

    	//UPDATE USER
   @Test(dependsOnMethods = "AddUser")
   public void UpdateUser() throws IOException {       
       String randomFirstName = faker.name().firstName();
       String randomLastName = faker.name().lastName();
       String randomPhoneNumber = generateRandomIndianPhoneNumber();
       
       String payload = "{\r\n"
               + "    \"id\":\"123\",\r\n"
               + "    \"first_name\":\"" + randomFirstName + "\",\r\n"
               + "    \"last_name\":\"" + randomLastName + "\",\r\n"
               + "    \"user_email\":\"\",\r\n"
               + "    \"phone_number\":\"" + randomPhoneNumber + "\",\r\n"
               + "    \"date_of_birth\":\"\",\r\n"
               + "    \"date_of_joining\":\"\",\r\n"
               + "    \"role\":\"5\",\r\n"
               + "    \"vendor_company_name\":\"\",\r\n"
               + "    \"branch\":\"2\",\r\n"
               + "    \"profile_image\":\"\"\r\n"
               + "}";

        response = RestAssured.given()
               .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
               .body(payload)
               .when()
               .post("/updateUser")
               .then()
               .body("message", equalTo("User Updated Successfully"))
               .extract().response();

       String exp = response.getBody().asString();
       System.out.println("Response body of Update User: " + exp);     
   }
   
   @Test(dependsOnMethods = "Login")
   public void GetUser() throws IOException {
       //Sorting data
       String[] sort= {"userPhoneDesc", "userPhoneAsc","userRoleAsc","userRoleDesc", "userNameAsc","userNameDesc","createdDateAsc","createdDateDesc","userStatusAsc","userStatusDesc"};
       String sortValue=sort[0];
       
       String[] user_status = {"active", "inactive", "added"};        
       String selectedStatus=user_status[0];
       
       String file="{\r\n"
       		+ "    \"pageSize\":\"\",\r\n"
       		+ "    \"role\":\"\",\r\n"
       		+ "    \"user_status\":\"\",\r\n"
       		+ "    \"search\":\"\",\r\n"
       		+ "    \"sort\":\"\"\r\n"
       		+ "}";
       
        response = RestAssured.given()
               .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
               .body(file)
               .when()
               .post("/getUsers")
               .andReturn();

       String responseBody = response.getBody().asString();
       System.out.println("Response body of get User: " + responseBody);

       JsonPath jsonPath = response.jsonPath();
       
          List<Integer> ids = jsonPath.getList("data.records.id");
          List<String> Name= jsonPath.getList("data.records.first_name");
          Assert.assertTrue(Name!=null);
          Assert.assertNotNull("user_status");
}

   //User count
	@Test(dependsOnMethods = "Login")
    public void UsersCount() throws IOException
    {
        File file = new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageUsers\\getUser.json");

         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
                .body(file)
                .when()
                .post("/getUsers")
                .andReturn();

        String exp = response.getBody().asString();
        int count = JsonPath.from(exp).get("data.count");
        System.out.println("Total Users : "+exp);
        
        String contentType = response.getContentType();
        response.then().assertThat().body("message", equalTo("Record found successfully"));
    }
   
    // UPLOAD IMAGE
    @Test
    public void UploadUserProfileImage() throws IOException {
        // Define the file to be uploaded
        String filePath = "C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageUsers\\Image.jpg";
        File file = Paths.get(filePath).toFile();
        byte[] fileContent = FileUtils.readFileToByteArray(file);

        // Construct the multipart form data request
         response = given()
                .basePath("/uploadProfileImage")
                .multiPart("id", 369) // Replace with actual id
                .multiPart("file", file, "image/jpeg")
                .header("Accept", "*/*")
                .header("Content-Type", "multipart/form-data")
                .when()
                .post()
                .andReturn();

        // Parse the response body as JSON
        JSONObject responseBody = new JSONObject(response.getBody().asString());
        System.out.println("Response body of upload user profile : " + responseBody.toString(4));
        
        response.then().assertThat().body("message", equalTo("Added successfully"));
    }
   
    //EXPORT USERS
    @Test(dependsOnMethods = "Login")
    public void ExportUser() throws IOException
    {
        String[] status= {"active","inactive","added"};
        String selectedStatus=status[0];
        
        String file="{\r\n"
        		+ "    \"role\":\"\",\r\n"
        		+ "    \"user_status\":\"\",\r\n"
        		+ "    \"search\":\"\"\r\n"
        		+ "}";

         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
                .body(file)
                .when()
                .post("/exportUsers")
                .then()
                .body("message", equalTo("Record found successfully"))
                .extract().response();
        
        String exp = response.getBody().asString();
        System.out.println("Response body of Export User : " + exp);
    }
    
    //ROLE LIST
    @Test(dependsOnMethods = "Login")
    public void RoleList() throws IOException
    {
        File file = new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageUsers\\roleList.json");

         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
                .body(file)
                .when()
                .post("/roleList")
                .then()
                .body("message", equalTo("Record found successfully"))
                .extract().response();
        
        String exp = response.getBody().asString();
        System.out.println("Response body of role list : " + exp);        
        JsonPath jsonPath = response.jsonPath();
        // Fetch Role IDs
           this.roleID = jsonPath.getList("data.records.roleId");
        
    }
    
    //GENERATE UNIQUE PASSWORD
    private String generateUniquePassword() {
        String basePassword = randomFirstName +"@0";
        Random random = new Random();
        int randomNumber = 100 + random.nextInt(900); // Generate a random number between 100 and 999
        return basePassword + randomNumber;
    }
    
    //RESET PASSWORD
    @Test(dependsOnMethods = "Login")
    public void ResetPassword() throws IOException
    {
        String newPassword = generateUniquePassword();
       
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
                .body("{\r\n"
                        + "    \"phone_number\":\"9999999999\",\r\n"
                        + "    \"new_password\":\"" + newPassword + "\",\r\n"
                        + "    \"confirm_password\":\"" + newPassword + "\"\r\n"
                        + "}")
                .when()
                .post("/resetPassword")
                .then()
                .body("message", equalTo("Password changed successfully"))
                .extract().response();
        
        String exp = response.getBody().asString();
        System.out.println("Response body of reset password : " + exp);
    }
    
    //RESEND PASSWORD
    @Test(dependsOnMethods = "Login")
    public void ResendPassword() throws IOException
    {
        String file="{\r\n"
        		+ "    \"id\":223\r\n"
        		+ "}";
        
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
                .body(file)
                .when()
                .post("/resendPassword")
                .then()
                .body("message", equalTo("Password sent successfully"))
                .extract().response();
        
        String exp = response.getBody().asString();
        System.out.println("Response body of Resend password : " + exp);
    }
    
    //UPDATE USER STATUS
    @Test(dependsOnMethods = "Login")
    public void UpdateUserStatus() throws IOException
    {
        String[] statuses = {"active", "inactive", "deactivated"};        
        String selectedStatus=statuses[0];
            
        String requestBody="{\r\n"
        		+ "    \"id\":\"180\",\r\n"
        		+ "    \"user_status\":\"" + selectedStatus + "\"\r\n"
        		+ "}";
        
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
                .body(requestBody)
                .when()
                .post("/updateStatus")
                .then()
                .extract().response();
        
        String exp = response.getBody().asString();
        System.out.println("Response body of Update User Status : " + exp);
        
     // Conditional validation based on the selected status
        if (selectedStatus.equals("active")) {
            response.then().assertThat().body("message", equalTo("Activated Successfully"));
        } else if (selectedStatus.equals("inactive")) {
            response.then().assertThat().body("message", equalTo("Deactivated Successfully"));
        } else if (selectedStatus.equals("deactivated")) {
            response.then().assertThat().body("message", equalTo("Deleted Successfully"));
        }else {
            Assert.fail("Unknown status: " + selectedStatus);
        }
    }
    
    //GET BRANCH NAME
    @Test(dependsOnMethods = "Login")
    public void GetBranches() throws IOException
    {
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken) 
                .when()
                .post("/getBranchName")
                .then()
        		.body("message", equalTo("Record found successfully"))
        		.extract().response();
        
        String res = response.getBody().asString();
        System.out.println("Response body of branches : " + res);
        
        JsonPath jsonPath = response.jsonPath();
        
        // Fetch the list of IDs
        List<Integer> ids = jsonPath.getList("data.id");
    }     
    
    //GET VENDOR COMPANY NAMES
    @Test(dependsOnMethods = "Login")
    public void GetVendorCompany() throws IOException
    {     
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
                .when()
                .post("/getVendorCompanyNames")
                .then()
                .body("message", equalTo("Record found successfully"))
                .extract().response();
        
        String exp = response.getBody().asString();
        System.out.println("Response body of Vendor company : " + exp);

        JsonPath jsonPath = response.jsonPath();
        
        // Fetch the list of IDs
           List<Integer> ids = jsonPath.getList("data.vendor_company_admin_id");
           // Fetch vendor company names
           List<String> vendor = jsonPath.getList("data.vendor_company_name");
           }
    }





