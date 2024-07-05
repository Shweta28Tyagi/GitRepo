package APITestCase;

import java.io.File;
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

public class Dashboard {
	String authToken;
	Response response;
	String[] toplimit= {"5","10","20","30","40","50"};
	String[] filterData= {"This Week","Today","This Month","This Year"};
    
	
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
				    System.out.println("        ******** DASHBOARD ********");

	        String res = response.getBody().asString();
	        this.authToken = JsonPath.from(res).get("data.token");
	        response.then().assertThat().body("message", equalTo("Login successfully"));
	    } 
	 
	 //GET DASHBOARD DATA
	 @Test(dependsOnMethods = "Login")
	    public void GetDashboardData() throws IOException {
	        String file="{\r\n"
	        		+ "    \"filter\":\""+filterData[3]+"\" \r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/getDashboardData")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of get dashboard data :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	 
	 //Get Quotation Analytics
	 @Test(dependsOnMethods = "Login")
	    public void GetQuotationAnalytics() throws IOException {
		 String[] sort= {"salesExecutiveDesc", "salesExecutiveAsc", "convertedQuoteDesc", "convertedQuoteAsc", "totalDesc", "totalAsc", "quoteNumberDesc", "quoteNumberAsc", "salesExecutiveNcDesc", "salesExecutiveNcAsc", "customerNameNcDesc", "customerNameNcAsc", "amountNcDesc", "amountNcAsc"};
		 String file="{\r\n"
	        		+ "    \"year\": 2024,\r\n"
	        		+ "    \"month\": 6,\r\n"
	        		+ "    \"user_id\": \"\",\r\n"							//sales executive id
	        		+ "    \"top_ten\": "+toplimit[1]+",\r\n"
	        		+ "    \"sort\": \"\"\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/getQuotationAnalytics")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of get quotation analytics :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully."));
	    }
	 
	 //Export Quotation Analytics
	 @Test(dependsOnMethods = "Login")
	    public void ExportQuotationAnalytics() throws IOException {
	        String file="{\r\n"
	        		+ "    \"year\": 2024,\r\n"
	        		+ "    \"month\": 6\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/exportQuotationAnalytics")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of export quotation analytics :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	 
	 //Export quotation sales executive 
	 @Test(dependsOnMethods = "Login")
	    public void ExportSalesExecutiveQuotationAnalytics() throws IOException {
	        String file="{\r\n"
	        		+ "    \"year\": 2024,\r\n"
	        		+ "    \"month\": 6,\r\n"
	        		+ "    \"user_id\": \"\"\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/exportSalesExecutiveAnalytics")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of export sales executive quotation analytics :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	 
	 //Export not yet converted quotes QUOTATION
	 @Test(dependsOnMethods = "Login")
	    public void ExportNotYetConvertedQuotation() throws IOException {
	        String file="{\r\n"
	        		+ "    \"year\": 2024,\r\n"
	        		+ "    \"month\": 6,\r\n"
	        		+ "    \"limit\": "+toplimit[1]+"\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/exportNotConvertedSalesExecutiveAnalytics")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of export not yet converted quotation :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	 
	 //Lead Analytics Report
	 @Test(dependsOnMethods = "Login")
	    public void GetLeadAnalaytics() throws IOException {
	        String file="{\r\n"
	        		+ "    \"year\": 2024\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/getLeadAnalytics")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of lead analytics :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully."));
	    }
	 
	 //Get overdue task  lead analytics
	 @Test(dependsOnMethods = "Login")
	    public void GetOverDueTask() throws IOException {
		 String[] sortValues= {"salesExecutiveDesc", "salesExecutiveAsc", "taskDesc", "taskAsc", "overDueTaskDesc", "overDueTaskAsc"};
		 
	     String file="{\r\n"
	        		+ "    \"user_id\": \"\",\r\n"
	        		+ "    \"sort\": \""+sortValues[1]+"\"\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/getOverDueTask")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of leads overdue tasks :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully."));
	    }
	 
	 //Lead Conversion
	 @Test(dependsOnMethods = "Login")
	    public void LeadConversionReport() throws IOException {
	        String file="{\r\n"
	        		+ "    \"month\": 6\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/leadConversation")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of lead conversion report :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	 
	 //Export lead generation by sources
	 @Test(dependsOnMethods = "Login")
	    public void ExportLeadGenerationSources() throws IOException {
	        String file="{\r\n"
	        		+ "    \"year\": 2024\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/exportLeadGenerationBySources")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of export lead generation by sources :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	 
	 //Export Lead overdue tasks
	 @Test(dependsOnMethods = "Login")
	    public void ExportLeadOverDueTask() throws IOException {
		 String[] sortValue= {"salesExecutiveDesc", "salesExecutiveAsc", "taskDesc", "taskAsc", "overDueTaskDesc", "overDueTaskAsc"};
		 
		 String file="{\r\n"
	        		+ "    \"user_id\": \"\",\r\n"
	        		+ "    \"sort\": \""+sortValue[1]+"\"\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/exportOverdueTasks")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of export lead overdue tasks :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	 
	 //Export lead conversion report
	 @Test(dependsOnMethods = "Login")
	    public void ExportLeadConversionReport() throws IOException {
	        String file="{\r\n"
	        		+ "    \"year\": 2024,\r\n"
	        		+ "    \"month\": 6\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/exportLeadConversionReport")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of export lead conversion :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	
	 //Stock Check Analytics
	 @Test(dependsOnMethods = "Login")
	    public void StockCheckAnalytics() throws IOException {
		 String[] sortValue= {"vendorDesc", "vendorAsc", "avgQuickDesc", "avgQuickAsc", "avgTimeDesc", "avgTimeAsc", "strikeRateDesc", "strikeRateAsc", "vendorNamesPrDesc", "vendorNamesSrDesc", "vendorNamesPrAsc", "vendorNamesSrAsc"};
		 
		 String file="{\r\n"
	        		+ "    \"filter\": \""+filterData[3]+"\",\r\n"
	        		+ "    \"sort\": \"\",\r\n"
	        		+ "    \"quick_response_limit\": "+toplimit[1]+",\r\n"
	        		+ "    \"poor_response_limit\": "+toplimit[1]+",\r\n"
	        		+ "    \"strike_rate_limit\": "+toplimit[1]+"\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/stockCheckAnalytics")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of stock check analytics :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	 
	 //Export get Vendor quick response
	 @Test(dependsOnMethods = "Login")
	    public void ExportVendorQuickResponse() throws IOException {
		 String[] sortValue= {"vendorDesc", "vendorAsc", "avgQuickDesc", "avgQuickAsc"};
		 
	        String file="{\r\n"
	        		+ "    \"filter\": \""+filterData[3]+"\",\r\n"
	        		+ "    \"sort\": \""+sortValue[1]+"\",\r\n"
	        		+ "    \"limit\": "+toplimit[1]+"\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/exportVendorQuickResponses")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of export vendor quick response :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	 
	 //Export vendor poor response
	 @Test(dependsOnMethods = "Login")
	    public void ExportVendorPoorResponse() throws IOException {
		 String[] sortValue= {"vendorNamesPrDesc", "vendorNamesPrAsc", "avgTimeDesc", "avgTimeAsc"};
	     
		 String file="{\r\n"
	        		+ "    \"filter\": \""+filterData[3]+"\",\r\n"
	        		+ "    \"sort\": \""+sortValue[1]+"\",\r\n"
	        		+ "    \"limit\": "+toplimit[1]+"\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/exportVendorPoorResponses")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of export vendor poor response :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
	 
	 //Export vendor strike rate
	 @Test(dependsOnMethods = "Login")
	    public void ExportVendorStrikeRate() throws IOException {
		 String[] sortValue= {"vendorNameSrDesc", "vendorNameSrAsc", "strikeRateDesc", "strikeRateAsc"};
	        String file="{\r\n"
	        		+ "    \"filter\": \""+filterData[3]+"\",\r\n"
	        		+ "    \"sort\": \""+sortValue[1]+"\",\r\n"
	        		+ "    \"limit\": "+toplimit[1]+"\r\n"
	        		+ "}";
	        
	         response = RestAssured.given()
	                .contentType(ContentType.JSON)
	                .header("Authorization", "Bearer " + authToken)
	                .body(file)
	                .when()
	                .post("/exportVendorStrikeRate")
	                .andReturn();

	        String res = response.getBody().asString();
	        System.out.println("Response body of export vendor strike rate :"+res);
	        response.then().assertThat().body("message", equalTo("Record found successfully"));
	    }
}
