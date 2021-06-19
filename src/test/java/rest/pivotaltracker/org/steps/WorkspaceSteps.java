package rest.pivotaltracker.org.steps;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequest;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;
import entities.WorkSpace;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import managers.Endpoints;
import managers.PathParam;
import org.apache.http.HttpStatus;
import org.testng.Assert;

import static configuration.EnvVariablesPool.dotenv;

public class WorkspaceSteps {
    private ApiRequest apiRequest5 = new ApiRequest();
    private ApiResponse apiResponse5;
    WorkSpace workspace = new WorkSpace();

    private String userToken = dotenv.get(Endpoints.TOKEN);
    private String baseUri = dotenv.get(Endpoints.BASE_URL);

    @Before
    public void createProject() throws JsonProcessingException {
        Project projectTemp = new Project();
        projectTemp.setName("Task List 1");
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setBaseUri(baseUri);
        apiRequest.addHeader("X-TrackerToken", userToken);
        apiRequest.setEndpoint(dotenv.get(Endpoints.WORKSPACES));
        apiRequest.setMethod(ApiMethod.valueOf("POST"));
        apiRequest.setBody(new ObjectMapper().writeValueAsString(projectTemp));
        workspace = ApiManager.executeWithBody(apiRequest).getBody(WorkSpace.class);
    }

    @Given("I build workspace request")
    public void iBuildWorkspaceRequest() {
        apiRequest5.setBaseUri(baseUri);
        apiRequest5.addHeader("X-TrackerToken", userToken);
        apiRequest5.setMethod(ApiMethod.GET);
    }

    @When("I add to workspace request {string}")
    public void iAddToWorkspaceRequest(String endpoint) {
        apiRequest5.setEndpoint(endpoint);
        apiRequest5.addPathParam("workspaceId", workspace.getId().toString());
        apiResponse5 = ApiManager.execute(apiRequest5);
    }

    @Then("the response status code should be {string} to workspace request")
    public void theResponseStatusCodeShouldBeToWorkspaceRequest(String status) {
        Assert.assertEquals(apiResponse5.getStatusCode(), Integer.parseInt(status));
    }

    @After
    public void deleteProject() {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setBaseUri(baseUri);
        apiRequest.addHeader("X-TrackerToken", userToken);
        apiRequest.setEndpoint(dotenv.get(Endpoints.WORKSPACE));
        apiRequest.setMethod(ApiMethod.valueOf("DELETE"));
        apiRequest.addPathParam(PathParam.WORKSPACE_ID, workspace.getId().toString());
        ApiResponse response = ApiManager.execute(apiRequest);
        response.getResponse().then().log().status();
        response.getResponse().then().log().body();
    }
}
