package APITestCase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ManageQuotation {
	String authToken;
	Faker faker=new Faker();
	String randomName = faker.name().fullName();
    String randomPhoneNumber = generateRandomIndianPhoneNumber();
    String email=faker.internet().emailAddress();
    String secondaryPhone=generateRandomIndianPhoneNumber();
    String randomText = faker.lorem().characters(15, true, true);
    int leadId;
    static Response response;
	String Address1 = faker.address().streetAddress();
    String Address2 = faker.address().streetAddress();
    
 // Generate 6 digit pincode
    private String generateSixDigitPincode() {
        return faker.number().digits(6); // Ensure it generates a 6-digit pincode
    } 
    
    @BeforeClass
    public void setup()
    {
    	RestAssured.baseURI = "https://mytyles.website:3133/api/v1";
    }
    @AfterClass
    public static void close() {
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
        System.out.println("        ******** MANAGE QUOTATION ********");

        String res = response.getBody().asString();
        this.authToken = JsonPath.from(res).get("data.token");
        response.then().assertThat().body("message", equalTo("Login successfully"));
    } 
	
	// Add quotation
	@Test(dependsOnMethods="Login")
    public void AddQuotation() throws IOException
    {
        File file = new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageQuotation\\addQuote.json");

         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/createQuotation")
                .andReturn();

        String res = response.getBody().asString();
        response.then().assertThat().body("message", equalTo("Added successfully"));
    } 
	
	//Get Quotations
	@Test(dependsOnMethods="Login")
    public void GetQuotation() throws IOException
    {
        String file="{\r\n"
        		+ "    \"pageNumber\": 1,\r\n"
        		+ "    \"pageSize\": 10,\r\n"
        		+ "    \"search\": \"\",\r\n"
        		+ "    \"sort\": \"\"\r\n"
        		+ "}";
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/getQuotations")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of get quotations :"+res);
        response.then().assertThat().body("message", equalTo("Record found successfully."));
    } 
	
	//Update Quotations
	@Test(dependsOnMethods="Login")
    public void UpdateQuotation() throws IOException
    {
        File file=new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageQuotation\\updateQuote.json");
        
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/updateQuotation")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of update quotation :"+res);
        response.then().assertThat().body("message", equalTo("Updated successfully."));
    } 
	
	//Export
	@Test(dependsOnMethods="Login")
    public void ExportQuotation() throws IOException
    {
        String file ="{\r\n"
        		+ "    \"search\": \"\"\r\n"
        		+ "}";
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/exportQuotations")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of export quotation :"+res);
        response.then().assertThat().body("message", equalTo("Record found successfully"));
    } 
	
	//Delete Quotation
	@Test(dependsOnMethods="Login")
    public void DeleteQuotation() throws IOException
    {
        String file ="{\r\n"
        		+ "    \"id\": 67\r\n"
        		+ "}";
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/deleteQuotation")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of delete quotation :"+res);
       
        if (res.contains("Please provide valid id.")) {
            // Handle case where the quotation is already deleted
            Assert.assertEquals(response.getStatusCode(), 403);
            response.then().assertThat().body("message", equalTo("Please provide valid id."));
        } else {
            response.then().assertThat().body("message", equalTo("Quotation deleted successfully."));
        }
    } 
	
	//Quotation Detail
	@Test(dependsOnMethods="Login")
    public void QuotationDetail() throws IOException
    {
        String file ="{\r\n"
        		+ "	    \"pageNumber\": 1,\r\n"
        		+ "	    \"pageSize\": \"\",\r\n"
        		+ "	    \"id\": \"75\"\r\n"
        		+ "	}";
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/getQuotations")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of quotation detail :"+res);
        response.then().assertThat().body("message", equalTo("Record found successfully."));
    } 
	
	//Download quotation
	@Test(dependsOnMethods="Login")
    public void DownloadQuotation() throws IOException
    {
        String file ="{\r\n"
        		+ "    \"quotation_id\": \"75\"\r\n"
        		+ "}";
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/downloadQuote")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of download quote :"+res);
        response.then().assertThat().body("message", equalTo("Record found successfully."));
    } 
	
	//Convert to Order
	@Test(dependsOnMethods="Login")
    public void ConvertToOrder() throws IOException
    {
        String filePath = "C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageQuotation\\Image.png";
        File file = Paths.get(filePath).toFile();
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        
        String filePath1 = "C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageQuotation\\Screenshot (26).png";
        File file1 = Paths.get(filePath1).toFile();
        byte[] fileContent1 = FileUtils.readFileToByteArray(file1);
        
         response = given()
                .header("Authorization", "Bearer " + authToken)
                .basePath("/orderDispatched")
                .multiPart("id", 57) 
                .multiPart("comment", "New comment") 
                .multiPart("links", "abc.com") 
                .multiPart("proof", file, "image/png") 
                .multiPart("delivery_type", "Pick Up") 
                .multiPart("delivery_date",2024-06-13)
                .multiPart("sales_person",50)
                .multiPart("direct_ready_for_pickup",true)
                .multiPart("quote_order_amount",450)
                .multiPart("customer_phone_number","2345678910")
                .multiPart("sale_order_no",10056)
               .multiPart("instructions", file1, "image/png")
                .multiPart("order_type","shortage_order")
                .header("Accept", "*/*")
                .header("Content-Type", "multipart/form-data")
                .when()
                .post();

        String res = response.getBody().asString();
        System.out.println("Response body of convert to order :"+res);
        response.then().assertThat().body("message", equalTo("Order converted successfully"));
    } 
	
	// Generating random phone number
    private String generateRandomIndianPhoneNumber() {
        // Ensure it generates a 10-digit number
        return "9" + faker.number().digits(9); // Start with '9' to ensure a valid 10-digit Indian number
    }
   
   //ADD LEAD
    @Test(dependsOnMethods = "Login")
    public void AddLead() throws IOException
    {   
 	   String pinCode = generateSixDigitPincode();
 	   String[] lead_type= {"Call","Walkin","Website Signups"};
 	   String[] lead_source= {"Website","Google Business","Offline","Instagram","Friend Referral","Web Signups","Zopim Chat","Interior/Architect Ref"};

    	 Map<String, Object> billingAddress = new HashMap<>();
         billingAddress.put("address_line_1", "");
         billingAddress.put("address_line_2", "");
         billingAddress.put("city", "");
         billingAddress.put("state", "");
         billingAddress.put("pincode", "");
         billingAddress.put("landmark", "");		
         billingAddress.put("gst_number", "");
         
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
                .header("Authorization", "Bearer " + authToken) // Pass authToken in the header
                .body(data)
                .when()
                .post("/createLead")
                .andReturn();
        String exp = response.getBody().asString();
        System.out.println("Response body of add lead : " + exp);
        this.leadId = JsonPath.from(exp).get("data.lead_id");
        System.out.println("Added lead Id : " + leadId);
    }
      
    @Test(dependsOnMethods="AddLead")
    public void AddBillingAddress() throws IOException
    {
        RestAssured.baseURI = "https://mytyles.website:3133/api/v1";
        Response getResponse = given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/getBillingAddress/" + leadId)
                .andReturn();

        if (getResponse.getStatusCode() == 403 && getResponse.getBody().asString().contains("Please add billing address")) { //No address found
        	String pinCode = generateSixDigitPincode();
            
            Map<String, Object> addressPayload = new HashMap<>();
            addressPayload.put("lead_id", leadId);
            addressPayload.put("address_line_1", Address1);
            addressPayload.put("address_line_2", Address2);
            addressPayload.put("gst_number", "");
            addressPayload.put("state_id", 1);
            addressPayload.put("city_id", 5);
            addressPayload.put("pincode", pinCode);
            
            Response postResponse = given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + authToken)
                    .body(addressPayload)
                    .when()
                    .post("/addBillingAddress")
                    .andReturn();

            String res = postResponse.getBody().asString();
            System.out.println("Response body of add billing address: " + res);
            Assert.assertEquals(postResponse.getStatusCode(), 200);
            postResponse.then().assertThat().body("message", equalTo("Address added successfully."));
        } else {
            System.out.println("Address already added to this lead");
        }
    }
    
	//Update billing address
	@Test(dependsOnMethods="Login")
    public void UpdateBillingAddress() throws IOException
    {
		String pinCode = generateSixDigitPincode();
		
		Map<String, Object> data = new HashMap<>();
        data.put("lead_id", 137);
        data.put("address_line_1", Address1);
        data.put("address_line_2", Address2);
        data.put("gst_number", "");
        data.put("state_id", 1);
        data.put("city_id", 4);
        data.put("pincode", pinCode);
        
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(data)
                .when()
                .post("/updateBillingAddress")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of update billing address :"+res);
        response.then().assertThat().body("message", equalTo("Address updated successfully."));
    } 
	
	//Add Shipping Address
	@Test(dependsOnMethods="Login")
    public void AddShippingAddress() throws IOException
    {
		String pinCode = generateSixDigitPincode();
		
		Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("id", "");
        shippingAddress.put("address_line_1", Address1);
        shippingAddress.put("address_line_2", Address2);
        shippingAddress.put("state_id", 1);
        shippingAddress.put("city_id", 4);
        shippingAddress.put("pincode", pinCode);
        shippingAddress.put("gst_number", "");
        shippingAddress.put("site_in_charge_mobile_number", "");
        shippingAddress.put("landmark", "");
        
        // Create a list of shipping addresses
        List<Map<String, Object>> shippingAddresses = new ArrayList<>();
        shippingAddresses.add(shippingAddress);

        // Create the outer map
        Map<String, Object> payload = new HashMap<>();
        payload.put("lead_id", 61);
        payload.put("shipping_addresses", shippingAddresses);
        
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(payload)
                .when()
                .post("/addMultipleShippingAddresses")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of add shipping address :"+res);
        response.then().assertThat().body("message", equalTo("Address added successfully."));
    }
	
	//Update Shipping address
	@Test(dependsOnMethods="Login")
    public void UpdateShippingAddress() throws IOException
    {
		String pinCode = generateSixDigitPincode();
		
		Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("id", "12");
        shippingAddress.put("address_line_1", Address1);
        shippingAddress.put("address_line_2", Address2);
        shippingAddress.put("state_id", 1);
        shippingAddress.put("city_id", 4);
        shippingAddress.put("pincode", pinCode);
        shippingAddress.put("gst_number", "");
        shippingAddress.put("site_in_charge_mobile_number", "");
        shippingAddress.put("landmark", "");

        // Create a list of shipping addresses
        List<Map<String, Object>> shippingAddresses = new ArrayList<>();
        shippingAddresses.add(shippingAddress);

        // Create the outer map
        Map<String, Object> payload = new HashMap<>();
        payload.put("lead_id", 61);
        payload.put("shipping_addresses", shippingAddresses);

	        // Send the POST request and get the response
	        Response response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(payload)
	                .when()
	                .post("/addMultipleShippingAddresses")
	                .andReturn();

	        // Print the response body
	        String res = response.getBody().asString();
	        System.out.println("Response body of update shipping address: " + res);
        response.then().assertThat().body("message", equalTo("Address added successfully."));
    }   
	
	//RECHECK STOCK
	//@Test(dependsOnMethods="Login")
    public void RecheckStock() throws IOException
    {
        String file ="{\r\n"
        		+ "    \"quotation_id\": \"88\",\r\n"
        		+ "    \"id\": [\r\n"
        		+ "        \"123\"\r\n"
        		+ "    ]\r\n"
        		+ "}";
        
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(file)
                .when()
                .post("/reStockCheck")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of recheck stock :"+res);
        response.then().assertThat().body("message", equalTo("Quotation status updated successfully."));
    }  
}	
