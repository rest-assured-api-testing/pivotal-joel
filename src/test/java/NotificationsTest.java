import api.*;
import managers.Endpoints;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

/**
 * Tests notification endpoint of a pivotal-tracker account.
 */
public class NotificationsTest {
    ApiRequestBuilder apiRequestBuilder;

    @BeforeTest
    public void createBasicRequest() {
        apiRequestBuilder = new ApiRequestBuilder()
                .header("X-TrackerToken", dotenv.get(Endpoints.TOKEN))
                .baseUri(dotenv.get("BASE_URL"));
    }

    @BeforeMethod(onlyForGroups = "getRequest")
    public void addGetTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.GET);
    }

    /**
     * Tests that notification endpoint gives us all notifications.
     */
    @Test(groups = "getRequest")
    public void getNotificationsTest() {
        apiRequestBuilder.endpoint("/my/notifications")
                .queryParam("envelope", "true");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }
}
