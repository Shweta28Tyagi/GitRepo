package APITestCase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import Individual.LoginToken;
import org.apache.commons.io.FileUtils;  //IMAGE UPLOAD
import org.hamcrest.Matchers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import Individual.LoginToken;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ManageLead {
	String token;
	Faker faker=new Faker();
	String randomName = faker.name().fullName();
    String randomPhoneNumber = generateRandomIndianPhoneNumber();
    String email=faker.internet().emailAddress();
    String secondaryPhone=generateRandomIndianPhoneNumber();
    String randomText = faker.lorem().characters(15, true, true);
    int leadId;
    static Response response;
    int lead_owner;
    String streetAddress = faker.address().streetAddress();
    String streetAddress1 = faker.address().streetAddress();
    String city = faker.address().city();
    String state = faker.address().state();
    String country = faker.address().country();
    String[] lead_type= {"Call","Walkin","Website Signups"};
	String[] lead_source= {"Website","Google Business","Offline","Instagram","Friend Referral","Web Signups","Zopim Chat","Interior/Architect Ref"};

    @BeforeClass
    public void setup()
    {
    	RestAssured.baseURI = "https://mytyles.website:3133/api/v1";
    	LoginToken.testLoginWithPassword();
	    token = LoginToken.authToken;
    }
    @AfterClass
    public static void close() {
    	Assert.assertEquals(response.getStatusCode(), 200);
    }
      
    // Generating random phone number
     private String generateRandomIndianPhoneNumber() {
         // Ensure it generates a 10-digit number
         return "8" + faker.number().digits(9); // Start with '9' to ensure a valid 10-digit Indian number
     }
     
     //Get lead owners list
     @Test
     public void GetLeadOwner() throws IOException
     {
       	 Map<String, Object> data = new HashMap<>();
         data.put("pageSize", "");
         data.put("role", 6);
         data.put("user_status", "active");
         data.put("search", "");
         data.put("sort", "");
     
          response = RestAssured.given()
                 .contentType(ContentType.JSON)
                 .header("Authorization", "Bearer " + token) // Pass token in the header
                 .body(data)
                 .when()
                 .post("/getUsers")
                 .andReturn();
         String exp = response.getBody().asString();
         System.out.println("Response body of get lead : " + exp);

         List<Integer> SalesExecutiveId = JsonPath.from(exp).get("data.records.id");
         System.out.println("Sales excutives : "+SalesExecutiveId);
         this.lead_owner=SalesExecutiveId.get(0);
         
         response.then().assertThat().body("message", equalTo("Record found successfully"));
     }
     
     // Generate 6 digit pincode
     private String generateSixDigitPincode() {
         return faker.number().digits(6); // Ensure it generates a 6-digit pincode
     }
    
    //ADD LEAD
    @Test
    public void AddLead() throws IOException
    {   
 	   String pinCode = generateSixDigitPincode();
    	 Map<String, Object> billingAddress = new HashMap<>();
         billingAddress.put("address_line_1", streetAddress);
         billingAddress.put("address_line_2", streetAddress1);
         billingAddress.put("city", 4);
         billingAddress.put("state", 1);
         billingAddress.put("pincode", pinCode);
         billingAddress.put("landmark", "new");		
         billingAddress.put("gst_number", "12WERTY1234WEZ2");
         
         Map<String, Object> data = new HashMap<>();
         data.put("full_name", randomName);
         data.put("primary_phone", randomPhoneNumber);
         data.put("secondary_phone", secondaryPhone);
         data.put("email", "");
         data.put("lead_type", lead_type[0]);
         data.put("lead_source", lead_source[0]);
         data.put("notes", randomText);        
         data.put("lead_owner_id", 2);  
         data.put("requirements", new int[]{1});
         data.put("billing_address", billingAddress);
        
    	response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token) // Pass token in the header
                .body(data)
                .when()
                .post("/createLead")
                .andReturn();
        String exp = response.getBody().asString();
        System.out.println("Response body of add lead : " + exp);
        this.leadId = JsonPath.from(exp).get("data.lead_id");
        System.out.println("Added lead Id : " + leadId);
    }
         
    // GET LEADS
   @Test
    public void GetLeads() throws IOException
    {
        File file = new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageLeads\\GetLeads.json");

         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token) // Pass token in the header
                .body(file)
                .when()
                .post("/getLeads")
                .then()
                .body("message",equalTo("Record found successfully."))
                .extract().response();
        String exp = response.getBody().asString();
        System.out.println("Response body of get lead : " + exp);
        int count = JsonPath.from(exp).get("data.count");      
    }
	
   //UPDATE LEAD
   @Test
   public void UpdateLead() throws IOException
   {
	   String pinCode = generateSixDigitPincode();
	   String randomPhoneNumber = generateRandomIndianPhoneNumber();
	   
       Map<String, Object> billingAddress = new HashMap<>();
       billingAddress.put("address_line_1", streetAddress);
       billingAddress.put("address_line_2", streetAddress1);
       billingAddress.put("city_id", 4);
       billingAddress.put("state_id", 1);
       billingAddress.put("pincode", pinCode);
       billingAddress.put("landmark", "new");		
       billingAddress.put("gst_number", "12WERTY1234WEZ2");
       
       Map<String, Object> data = new HashMap<>();
       data.put("id", 215);
       data.put("full_name", randomName);
       data.put("primary_phone", randomPhoneNumber);
       data.put("secondary_phone", "");
       data.put("email", "");
       data.put("lead_type", lead_type[0]);
       data.put("lead_source", lead_source[0]);
       data.put("notes", randomText);        
       data.put("lead_owner_id", 2);  
       data.put("requirements", new int[]{1});
       data.put("billing_address", billingAddress);
       
        response = RestAssured.given()
               .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token) // Pass token in the header
               .body(data)
               .when()
               .post("/updateLead")
               .then()
               .body("message",equalTo("Lead updated successfully."))
               .extract().response();
        
       String exp = response.getBody().asString();  
       System.out.println("Respons ebody of update lead : "+exp);
   }
   
   //ADD ATTACHMENTS
   @Test
   public void UploadLeadAttachment() throws IOException
   {
	   String filePath = "C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageUsers\\tile1.jpg";
       File file = Paths.get(filePath).toFile();
       byte[] fileContent = FileUtils.readFileToByteArray(file);

       // Print the image URL from Faker (similar to your original console log)
       String imageUrl = new com.github.javafaker.Faker().internet().avatar();
       System.out.println("Generated image URL: " + imageUrl);

       // Construct the multipart form data request
        response = given()
               .header("Authorization", "Bearer " + token)
               .basePath("/addUploadMultipleAttachments")
               .multiPart("id", 104) // Replace with actual id
               .multiPart("file", file, "image/jpeg")
               .header("Accept", "*/*")
               .header("Content-Type", "multipart/form-data")
               .when()
               .post();

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body: " + responseBody.toString(4));  
   }
   
   //EXPORT LEADS
   @Test
   public void ExportLeads() throws IOException
   {
	   File file = new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageLeads\\exportLead.json");
	   
        response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(file)
               .when()
               .post("/exportLeads");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body: " + responseBody.toString(4));
   }
   
   //Add task
   @Test
   public void AddTaskForLead() throws IOException
   {
	   Map<String, Object> payload = new HashMap<>();
	   payload.put("lead_id", 185);
       payload.put("subject", "Follow up with " + faker.name().fullName());
       payload.put("follow_up_date", new SimpleDateFormat("yyyy-MM-dd").format(faker.date().future(10, TimeUnit.DAYS)));
       payload.put("follow_up_time", new SimpleDateFormat("HH:mm").format(new Date()));
       payload.put("task_details", faker.lorem().sentence());
       payload.put("reminder", faker.bool().bool());
	   
        response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(payload)
               .when()
               .post("/createLeadTask");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body: " + responseBody);
   }
   
   //GET LEAD TASK
   @Test
   public void GetLeadTask() throws IOException
   {	  
	   File file = new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageLeads\\getLeadTask.json");
	   
        response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(file)
               .when()
               .post("/getLeadTask");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of all lead task : " + responseBody);
   }
   
   //GET LEAD HISTORY
   @Test
   public void GetLeadHistory() throws IOException
   {
	   Map<String, Object> payload = new HashMap<>();
       payload.put("lead_id", 23);
	   
	   response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(payload)
               .when()
               .post("/getLeadHistory");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of all lead task : " + responseBody);
   }
   
   //ADD LEAD ACTIVITY
   @Test
   public void AddLeadActivity() throws IOException
   {
	   Map<String, Object> payload = new HashMap<>();
       payload.put("lead_id", 37);
       payload.put("activity_type", "Phone Call Positive");
       payload.put("notes", randomText);

	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(payload)
               .when()
               .post("/createLeadActivity");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of add lead activity : " + responseBody);
   }
   
   //GET ACTIVITY
   @Test
   public void GetLeadActivity() throws IOException
   {
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .post("/getActivityType");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of get lead activities : " + responseBody);
   }
   
   //CITIES
   @Test
   public void GetCity() throws IOException
   {
	   Map<String, Object> payload = new HashMap<>();
       payload.put("state_id", 5);
	 
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(payload)
               .when()
               .post("/getCities");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of get cities : " + responseBody);
   }
   
   //ALL STATES
   @Test
   public void GetStates() throws IOException
   {
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .post("/getStates");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of get states : " + responseBody);
   }
   
   //GET LEAD DETAILS
   @Test
   public void GetLeadDetails() throws IOException
   {
	   Map<String, Object> payload = new HashMap<>();
       payload.put("id", 63);
	   
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(payload)
               .when()
               .post("/getLeadDetails");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of lead details : " + responseBody);
   }
   
   //CHECK LEAD NUMBERS
   @Test
   public void CheckLeadNumbers() throws IOException
   {
	   Map<String, Object> payload = new HashMap<>();
       payload.put("primary_phone", "9891022072");
       payload.put("secondary_phone", "");
	   
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(payload)
               .when()
               .post("/checkLeadNumbers");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of checking lead numbers : " + responseBody);
   }
   
   //UPDATE LEAD STAGE
   @Test(dependsOnMethods="AddLead")
   public void UpdateLeadStage() throws IOException
   {
	   String[] stages= {"New Lead","Follow Up","Showroom Visit","Quotation","Order Placed","General Enquiry","Lost","Invalid"};
	   
	   Map<String, Object> payload = new HashMap<>();
       payload.put("id", leadId);
       payload.put("lead_stage", stages[1]);
       payload.put("comment", "");
	   
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(payload)
               .when()
               .post("/updateLeadStage");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of update lead stage : " + responseBody);
   }
    
   //GET LEAD STAGE  
   @Test
   public void GetLeadStage() throws IOException
   {
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .post("/getLeadStage");

       // Parse the response body as JSON
       String res = response.getBody().asString();
       System.out.println("Response body of get lead stage : " + res);
   }
   
   //GET REQUIREMENTS
   @Test
   public void GetRequirement() throws IOException
   {
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .post("/getRequirements");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of get requirements : " + responseBody);
   }
   
   //MARK LEAD AS STAR
   @Test
   public void MarkLeadAsStar() throws IOException
   {
	   String file="{\r\n"
	   		+ "\"lead_id\": 101,\r\n"
	   		+ "\"is_star_marked\":false \r\n"
	   		+ "}";
	   
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(file)
               .when()
               .post("/markLeadAsStar");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of mark lead as star : " + responseBody);
   }
   
   //GET LEAD TASK BY ID
   @Test
   public void GetTaskByLeadId() throws IOException
   {
	   String file="{\r\n"
	   		+ "    \"lead_id\":101\r\n"
	   		+ "}";
	   
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(file)
               .when()
               .post("/getLeadTaskByLeadId");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of  get lead task by id : " + responseBody);
   }
   
   //Mark Task as Completed
   @Test
   public void MarkTaskAsCompleted() throws IOException
   {
	   String file="{\r\n"
	   		+ "    \"task_id\":78\r\n"
	   		+ "}\r\n"
	   		+ "";
	   
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(file)
               .when()
               .post("/markTaskAsCompleted");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body of mark task as completed : " + responseBody);
   }
   
   //RESCHEDULE TASK
   @Test
   public void RescheduleTask() throws IOException
   {
	   Map<String, Object> payload = new HashMap<>();
       payload.put("task_id", faker.number().randomDigit());
       payload.put("subject", "Follow up with " + faker.name().fullName());
       payload.put("follow_up_date", new SimpleDateFormat("yyyy-MM-dd").format(faker.date().future(10, TimeUnit.DAYS)));
       payload.put("follow_up_time", new SimpleDateFormat("HH:mm").format(new Date()));
       payload.put("task_details", faker.lorem().sentence());
       payload.put("reminder", faker.bool().bool());
	   
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(payload)
               .when()
               .post("/rescheduleTask");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body reschedule task : " + responseBody);
   }
   
   //DELETE LEAD ATTACHMENT
   @Test
   public void DeleteAttachment() throws IOException
   {
	   String file="{\r\n"
	   		+ "    \"id\":[1]\r\n"
	   		+ "}";
	   
	    response = given()
    		   .contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + token)
               .body(file)
               .when()
               .post("/deleteAttachments");

       // Parse the response body as JSON
       JSONObject responseBody = new JSONObject(response.getBody().asString());
       System.out.println("Response body delete attachment : " + responseBody);

       int statusCode = response.getStatusCode();
       if (statusCode == 200) {
           System.out.println("Test passed.");
       } else {
           System.out.println("Test failed with status code: " + statusCode);
       }
   }
}
