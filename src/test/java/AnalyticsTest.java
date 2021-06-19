import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import entities.Analytics;
import managers.PathParam;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

/**
 * Tests analytics endpoint of a pivotal-tracker account.
 */
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

    /**
     * Tests that iteration analytics endpoint gives us all iteration analytics.
     */
    @Test(groups = "getRequest")
    public void getAnalyticsOfAIterationsOfAProjectTest() {

        apiRequestBuilder.endpoint("/projects/{projectId}/iterations/{number}/analytics")
                .pathParam(PathParam.PROJECT_ID, "2504481")
                .pathParam(PathParam.NUMBER, "1");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Analytics analytics = apiResponse.getBody(Analytics.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(analytics.getKind(), "analytics");
    }

    /**
     * Tests that iteration analytics cycle endpoint gives us all iteration analytics cycle.
     */
    @Test(groups = "getRequest")
    public void getAnalyticsWithDetailsOfAIterationsOfAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/iterations/{number}/analytics/cycle_time_details")
                .pathParam(PathParam.PROJECT_ID, "2504481")
                .pathParam(PathParam.NUMBER, "1");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }
}
