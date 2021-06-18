import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Iteration;
import entities.IterationOverride;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class IterationsTest {
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

    @BeforeMethod(onlyForGroups = "putRequest")
    public void addPutTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.PUT);
    }

    @Test(groups = "getRequest")
    public void getAllIterationsOfAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/iterations")
                .pathParam("projectId", "2504481")
                .queryParam("limit", "10")
                .queryParam("offset", "1");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getAIterationsOfAProjectTest() {

        apiRequestBuilder.endpoint("/projects/{projectId}/iterations/{number}")
                .pathParam("projectId", "2504481")
                .pathParam("number", "1");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Iteration iteration = apiResponse.getBody(Iteration.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(iteration.getNumber(), 1);
    }

    @Test(groups = "putRequest")
    public void updateAIterationOverrideOfAProjectTest() throws JsonProcessingException {
        IterationOverride iterationOverrideToSend = new IterationOverride();
        iterationOverrideToSend.setLength(5);
        iterationOverrideToSend.setTeam_strength(0.7f);
        apiRequestBuilder.endpoint("/projects/{projectId}/iteration_overrides/{number}")
                .pathParam("projectId", "2504481")
                .pathParam("number", "1")
                .body(new ObjectMapper().writeValueAsString(iterationOverrideToSend));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        IterationOverride iterationOverride = apiResponse.getBody(IterationOverride.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(iterationOverride.getLength(), 5);
        Assert.assertEquals(iterationOverride.getTeam_strength(), 0.7f);
    }
}
