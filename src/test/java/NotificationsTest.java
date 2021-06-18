import api.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class NotificationsTest {
    ApiRequestBuilder apiRequestBuilder;

    @BeforeTest
    public void createBasicRequest() {
        apiRequestBuilder = new ApiRequestBuilder()
                .header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"));
    }

    @BeforeMethod(onlyForGroups = "getRequest")
    public void addGetTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.GET);
    }

    @Test(groups = "getRequest")
    public void getNotificationsTest() {
        apiRequestBuilder.endpoint("/my/notifications")
                .queryParam("envelope", "true");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }
}
