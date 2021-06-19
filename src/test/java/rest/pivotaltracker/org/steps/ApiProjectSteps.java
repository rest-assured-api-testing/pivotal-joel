package rest.pivotaltracker.org.steps;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequest;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;
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

public class ApiProjectSteps {
    private ApiRequest apiRequest1 = new ApiRequest();
    private ApiResponse apiResponse1;
    Project project1 = new Project();

    private String userToken = dotenv.get(Endpoints.TOKEN);
    private String baseUri = dotenv.get(Endpoints.BASE_URL);

    @Before
    public void createProject() throws JsonProcessingException {
        Project projectTemp = new Project();
        projectTemp.setName("Task List 1");
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setBaseUri(baseUri);
        apiRequest.addHeader("X-TrackerToken", userToken);
        apiRequest.setEndpoint(dotenv.get(Endpoints.PROJECTS));
        apiRequest.setMethod(ApiMethod.valueOf("POST"));
        apiRequest.setBody(new ObjectMapper().writeValueAsString(projectTemp));
        project1 = ApiManager.executeWithBody(apiRequest).getBody(Project.class);
    }

    @Given("I build {string} request")
    public void iBuildRequest(String method) throws JsonProcessingException {
        apiRequest1.setBaseUri(baseUri);
        apiRequest1.addHeader("X-TrackerToken", userToken);
        apiRequest1.setMethod(ApiMethod.valueOf(method));
    }

    @When("I execute {string} request")
    public void iExecuteRequest(String endpoint) {
        apiRequest1.setEndpoint(endpoint);
        apiRequest1.addPathParam("projectId", project1.getId().toString());
        apiResponse1 = ApiManager.execute(apiRequest1);
    }

    @Then("the response status code should be {string}")
    public void theResponseStatusCodeShouldBe(String arg0) {
        Assert.assertEquals(apiResponse1.getStatusCode(), HttpStatus.SC_OK);
        apiResponse1.getResponse().then().log().body();
    }

    @After
    public void deleteProject() {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setBaseUri(baseUri);
        apiRequest.addHeader("X-TrackerToken", userToken);
        apiRequest.setEndpoint(dotenv.get(Endpoints.PROJECT));
        apiRequest.setMethod(ApiMethod.valueOf("DELETE"));
        apiRequest.addPathParam(PathParam.PROJECT_ID, project1.getId().toString());
        ApiResponse response = ApiManager.execute(apiRequest);
        response.getResponse().then().log().status();
        response.getResponse().then().log().body();
    }
}
