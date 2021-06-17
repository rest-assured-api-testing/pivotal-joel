import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import entities.Analytics;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class AnalyticsTest {
    ApiRequestBuilder apiRequestBuilder;

    @BeforeTest
    public void createBasicRequest() {
        apiRequestBuilder = new ApiRequestBuilder();
        apiRequestBuilder.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"));
    }

    @BeforeMethod(onlyForGroups = "getRequest")
    public void addGetTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.GET);
    }


    @Test(groups = "getRequest")
    public void getAnalyticsOfAIterationsOfAProjectTest() {

        apiRequestBuilder.endpoint("/projects/{projectId}/iterations/{number}/analytics")
                .pathParam("projectId", "2504481")
                .pathParam("number", "1");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Analytics analytics = apiResponse.getBody(Analytics.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(analytics.getKind(), "analytics");
    }

    @Test(groups = "getRequest")
    public void getAnalyticsWithDetailsOfAIterationsOfAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/iterations/{number}/analytics/cycle_time_details")
                .pathParam("projectId", "2504481")
                .pathParam("number", "1");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }
}
