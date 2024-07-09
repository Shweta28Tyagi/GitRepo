package APITestCase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import Individual.LoginToken;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import Individual.LoginToken;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ManageInventory {
    String token;
    int productId;
    Faker faker = new Faker();
    int randomCode = 10000000 + faker.number().numberBetween(0, 90000000); // Ensures the number is 6 digits
    String randomSKU = faker.lorem().characters(15, true, true);
    String randomProductName = faker.commerce().productName();
    List<Integer> vendorCompanyId;
    Response response;

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

    @Test(dependsOnMethods = "GetProductDetails")
    public void AddInventory() throws IOException {
        String[] uom = {"Sq.ft", "Set", "Piece", "Kg"};
        String selectedUom = uom[1];
        
     // Check if vendorCompanyId is null or empty
        if (vendorCompanyId == null || vendorCompanyId.isEmpty()) {
            throw new RuntimeException("vendorCompanyId list is empty or null");
        }
        int vendor_Company = vendorCompanyId.get(1); // Vendor company access via id

        Map<String, Object> file = new HashMap<>();
        file.put("product_name", randomProductName);
        file.put("product_sku", randomSKU);
        file.put("brand_id", 1);
        file.put("unit_of_measurement", selectedUom);
        file.put("country_of_manufacture", 1);
        file.put("product_uses", new int[]{1});
        file.put("product_category", new int[]{1});
        file.put("product_code", randomCode);
        file.put("product_hsn_code", 1220);
        file.put("product_packing", "new");
        file.put("vendor_company_name", 2);
        file.put("product_size", 1);
        file.put("product_finish", 1);
        file.put("product_material_type", 1);
        file.put("product_weight", "");
        file.put("product_quality", 1);
        file.put("web_url", "");
        file.put("final_price", 1200);
        file.put("mrp", 2000);
        file.put("discount", "");
        file.put("gst_rate", 1);
        file.put("purchase_rate", "");
        file.put("inventory", "10");
        file.put("coverage", "");
        file.put("delivery_time", "");
        file.put("product_images", "");
        file.put("product_image_previews", "");
        
        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/addProductInventory")
                .then()
                .body("message", equalTo("Product Added Successfully"))
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response body of add product: " + res);
        // Extract productId from response
        this.productId = JsonPath.from(res).get("data.product_id");
    }

    @Test
    public void GetProductDetails() throws IOException {
    	String[] date= {"brand", "country_of_manufacture", "uses", "category", "finish", "material", "quality", "delivery_time", "vendor_company_name", "product_size"};
        
    	Map<String, Object> file = new HashMap<>();
        file.put("dropdown_data", date[0]);
        file.put("search", "");

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/getProductDetails")
                .then()
                .body("message", equalTo("Record found successfully"))
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response body of vendor data: " + res);
        
        // Extract vendorCompanyId from response
        this.vendorCompanyId = JsonPath.from(res).getList("data.records.id", Integer.class);
        
        // Debugging statement to verify extraction
        System.out.println("Extracted vendorCompanyId: " + this.vendorCompanyId);

        // Check if vendorCompanyId is null or empty
        if (this.vendorCompanyId == null || this.vendorCompanyId.isEmpty()) {
            throw new RuntimeException("vendorCompanyId list is empty or null");
        }
    }

    @Test(dependsOnMethods = "GetProductDetails")
    public void GetInventory() throws IOException {
        int vendor_Company = vendorCompanyId.get(1); // Vendor company access via id
        
        Map<String, Object> file = new HashMap<>();
        file.put("pageNumber", 1);
        file.put("pageSize", 10);
        file.put("search", "");
        file.put("sort", "");
        file.put("product_status",  new int[]{});
        file.put("brand",new int[]{});
        file.put("product_material_type", new int[]{});
        file.put("product_quality", new int[]{});
        file.put("product_finish", new int[]{});
        file.put("product_category", new int[]{});
        file.put("product_uses", new int[]{});
        file.put("country_of_manufacture", new int[]{});
        file.put("vendor_company_name", new int[]{vendor_Company});
        file.put("unit_of_measurement",  new int[]{});
        file.put("gst_rate", new int[]{});

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/getProductsInventory")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of product list: " + res);
        response.then().assertThat().body("message", equalTo("Record found successfully"));
    }

    @Test(dependsOnMethods = "AddInventory")
    public void AddProductImage() throws IOException {
        String filePath = "C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageInventory\\tile1.jpg";
        File file = Paths.get(filePath).toFile();
        byte[] fileContent = FileUtils.readFileToByteArray(file);

        String filepath1 = "C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageInventory\\Image.jpg";
        File file1 = Paths.get(filepath1).toFile();
        byte[] fileContent1 = FileUtils.readFileToByteArray(file1);

        response = given()
                .header("Authorization", "Bearer " + token)
                .basePath("/addUploadMultipleImage")
                .multiPart("id", productId) // Replace with actual id
                .multiPart("file", file, "image/jpeg")
                .multiPart("file", file1, "image/jpeg")
                .header("Accept", "*/*")
                .header("Content-Type", "multipart/form-data")
                .when()
                .post();

        String res = response.getBody().asString();
        System.out.println("Response body of add product image: " + res);
    }

    @Test
    public void UpdateProduct() throws IOException {    	
    	 Map<String, Object> file = new HashMap<>();
         file.put("id", "187");
         file.put("product_name", randomProductName);
         file.put("product_sku", randomSKU);
         file.put("brand_id", new int[]{1});
         file.put("unit_of_measurement", new int[]{1});
         file.put("country_of_manufacture", new int[]{1});
         file.put("product_uses", new int[]{1});
         file.put("product_category", "");
         file.put("product_code", randomCode);
         file.put("product_hsn_code", 1220);
         file.put("product_packing", "");
         file.put("vendor_company_name", new int[]{2});
         file.put("product_size", new int[]{1});
         file.put("product_finish", new int[]{1});
         file.put("product_material_type", new int[]{1});
         file.put("product_weight", "");
         file.put("product_quality", new int[]{1});
         file.put("web_url", "");
         file.put("final_price", 1200);
         file.put("mrp", 2000);
         file.put("discount", "");
         file.put("gst_rate", new int[]{1});
         file.put("purchase_rate", "");
         file.put("inventory", "10");
         file.put("coverage", "");
         file.put("delivery_time", "");
         file.put("product_images", "");
         file.put("product_image_previews", "");

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(file)
                .when()
                .post("/updateProductsInventory")
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of updated product: " + res);
    }
    
   // @Test(dependsOnMethods = "AddInventory")
    public void UpdateProductImage() throws IOException {
        String filePath = "C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\ManageInventory\\Image.jpg";
        File file = Paths.get(filePath).toFile();
        byte[] fileContent = FileUtils.readFileToByteArray(file);

        response = given()
                .header("Authorization", "Bearer " + token)
                .basePath("/updateUploadMultipleImage")
                .multiPart("id", productId) // Replace with actual id
                .multiPart("file", file, "image/jpeg")
                .header("Accept", "*/*")
                .header("Content-Type", "multipart/form-data")
                .when()
                .post()
                .andReturn();

        String res = response.getBody().asString();
        System.out.println("Response body of updated product image: " + res);
    }
}
