package APITestCase;

import static org.hamcrest.Matchers.equalTo;
import Individual.LoginToken;
import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ManageInquiry {
	Response response;
	String[] dateType = { "all", "today", "yesterday", "this_week", "last_week", "this_month", "last_month",
			"this_year", "custom" };
	String selectedDateType = dateType[7];
	String[] enquiryStatus = { "New inquiry", "Old inquiry" };
	String[] inquiryAction = { "Available", "Not Available" };
	String token;

	@BeforeClass
	public void setup() {
		RestAssured.baseURI = "https://mytyles.website:3133/api/v1";
		LoginToken.testLoginWithPassword();
	     token = LoginToken.authToken;
	}

	@AfterClass
	public void close() {
		Assert.assertEquals(response.getStatusCode(), 200);
	}
    
    //Get all inquiry with filters
    @Test
    public void GetVendorInquiry() throws IOException
    {
       String[] inquiryTab= {"new_inquiry","responded_inquiry","order_placed_inquiry"};   
       String selectedValue=inquiryTab[1];
       
       String[] sort= {"createdDateDesc","createdDateAsc","productNameAsc","productNameDesc","enquiryAsc","enquiryDesc","quantityDesc","quantityAsc","enquiryStatusAsc","enquiryStatusDesc"};
       String selectedSortValue=sort[0];
       
       String file = "{\r\n"
    		    + "    \"pageNumber\": 1,\r\n"
    		    + "    \"pageSize\": 10,\r\n"
    		    + "    \"enquiry_tab\": \"" + selectedValue + "\",\r\n"
    		    + "    \"search\": \"\",\r\n"
    		    + "    \"sort\": \"" + selectedSortValue + "\",\r\n"
    		    + "    \"dateRange\": {\r\n"
    		    + "        \"type\": \"" + selectedDateType + "\",\r\n"
    		    + "        \"startDate\": \"\",\r\n"
    		    + "        \"endDate\": \"\"\r\n"
    		    + "    },\r\n"
    		    + "    \"action\": \"\",\r\n"
    		    + "    \"quantity\": \"\",\r\n"
    		    + "    \"enquiry_status\": \"\"  \r\n"        						// For new inquiry tab only
    		    + "}";
       

       response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/getVendorQuotations")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of get vendor inquiry :"+res);
        
        response.then().assertThat().body("message", equalTo("Record found successfully."));
    } 
    
  //Update Inquiry status
    @Test
    public void UpdateInquiryStatus() throws IOException
    {
       String[] inquiryTab= {"Not Available","Available Multiple Batches","Available"};   
       String selectedValue=inquiryTab[2];
       
        String file="{\r\n"
        		+ "    \"id\": 118,\r\n"
        		+ "    \"stock_check_status\": \""+selectedValue+"\",\r\n"
        		+ "    \"not_available_quantity\": \"\",\r\n"
        		+ "    \"not_available_in_days\": \"\"\r\n"
        		+ "}";
        
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/updateQuotationProductStatus")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of update inquiry status :"+res);
        
        response.then().assertThat().body("message", equalTo("Updated successfully."));  
    } 
    
    //EXPORT
    @Test
    public void ExportInquiry() throws IOException
    {
       String[] inquiryTab= {"new_inquiry", "order_placed_inquiry", "responded_inquiry"};
       String selectedValue=inquiryTab[0];
      
       String file="{\r\n"
       		+ "    \"dateRange\": {\r\n"
       		+ "        \"type\": \""+selectedDateType+"\",\r\n"
       		+ "        \"startDate\": \"\",\r\n"
       		+ "        \"endDate\": \"\"\r\n"
       		+ "    },\r\n"
       		+ "    \"action\": \"\",\r\n"						//inquiryAction
       		+ "    \"quantity\": \"1\",\r\n"
       		+ "    \"enquiry_status\": \"\",\r\n"				//enquiryStatus
       		+ "    \"enquiry_tab\": \""+selectedValue+"\",\r\n"
       		+ "    \"search\": \"\"\r\n"
       		+ "}";
        
         response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/exportVendorQuotations")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of export inquiry :"+res);
        
        response.then().assertThat().body("message", equalTo("Record found successfully."));
    } 
}
