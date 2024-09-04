package MSNagile;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ManageSubscription {
    Response response;
    String authToken;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://be.eazyagile.com";
    }

    @AfterClass
    public void close() {
        Assert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK status code but received: " + response.getStatusCode());
    }

    @Test
    public void Login() throws IOException {
        String file = "{\r\n"
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
                .statusCode(200)
                .body("message", equalTo("Loggedin Successfully"))
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response of Login : " + res);
        this.authToken = JsonPath.from(res).get("token");
        Assert.assertNotNull(authToken, "Auth token should not be null after successful login.");
    }

    @Test(dependsOnMethods = "Login")
    public void GetActivePlan() throws IOException {

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/auth/getActivePlan/ss1")
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully"))
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response of Get active plan : " + res);

        String planName = JsonPath.from(res).get("data.planName");
       // System.out.println(planName);
        Assert.assertNotNull(planName, "Active plan name should not be null.");
        // You can add more assertions related to the plan's properties.
    }

    @Test(dependsOnMethods = "Login")
    public void GetConsumedStorage() throws IOException {

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/auth/getConsumedStorage/ss1")
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully"))
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response of Get consumed storage : " + res);

        int storageUsed = JsonPath.from(res).getInt("data.sizeInKb");
        Assert.assertTrue(storageUsed >= 0, "Storage used should be a non-negative integer.");
    }

    @Test(dependsOnMethods = "Login")
    public void GetPaymentHistory() throws IOException {

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/auth/getPaymentHistory/ss1")
                .then()
                .statusCode(200)
                .body("message", equalTo("No Record Found!"))
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response of get payment history : " + res);
    }

    @Test(dependsOnMethods = "Login")
    public void SubscribePlan() throws IOException {

        String payload = "{\r\n"
                + "    \"url\": \"ss1\",\r\n"
                + "    \"plan\": \"free\"\r\n"
                + "}";

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(payload)
                .when()
                .post("/auth/subscribedPlan")
                .then()
                .statusCode(200)
                .extract().response();

        String res = response.getBody().asString();
        System.out.println("Response of subscribed plan : " + res);

        if (res.contains("You already in same plan")) {
            System.out.println("User already subscribed to this plan.");
            Assert.assertTrue(res.contains("You already in same plan"), "Expected 'already in same plan' message but found: " + res);
        } else {
            System.out.println("Plan Subscribed Successfully");
            Assert.assertTrue(res.contains("Plan Subscribed Successfully"), "Expected plan subscription success but found: " + res);
        }
    }
}
