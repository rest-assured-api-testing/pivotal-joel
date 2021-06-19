import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Iteration;
import entities.IterationOverride;
import entities.Project;
import managers.Endpoints;
import managers.PathParam;
import managers.ProjectManager;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

/**
 * Tests iteration endpoint of a pivotal-tracker account.
 */
public class IterationsTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;

    public void createBasicRequest() {
        apiRequestBuilder = new ApiRequestBuilder();
        apiRequestBuilder.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"));
    }

    @BeforeMethod(onlyForGroups = "getRequest")
    public void addGetTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.GET);
        createdProject = ProjectManager.create();
    }

    @BeforeMethod(onlyForGroups = "putRequest")
    public void addPutTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.PUT);
        createdProject = ProjectManager.create();
    }
    @AfterMethod(onlyForGroups = {"getRequest", "putRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    /**
     * Tests that iteration endpoint gives us all iterations.
     */
    @Test(groups = "getRequest")
    public void getAllIterationsOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_ITERATIONS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .queryParam("limit", "10")
                .queryParam("offset", "1");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    /**
     * Tests that project iteration endpoint gives us a specific project iteration.
     */
    @Test(groups = "getRequest")
    public void getAIterationsOfAProjectTest() {

        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_ITERATION_NUMBERS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.NUMBER, "1");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Iteration iteration = apiResponse.getBody(Iteration.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(iteration.getNumber(), 1);
    }

    /**
     * Tests that project iteration endpoint updates a specific project iteration.
     */
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

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(iterationOverride.getLength(), 5);
        Assert.assertEquals(iterationOverride.getTeam_strength(), 0.7f);
    }
}
