package rest.pivotaltracker.org.steps;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequest;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import entities.Epic;
import entities.Project;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import managers.Endpoints;
import managers.EpicManager;
import managers.ProjectManager;
import org.testng.Assert;

import static configuration.EnvVariablesPool.dotenv;

public class EpicSteps {
    private ApiRequest apiRequest2 = new ApiRequest();
    private ApiResponse apiResponse2;
    Project project2;
    Epic epic2;

    private String userToken = dotenv.get(Endpoints.TOKEN);
    private String baseUri = dotenv.get(Endpoints.BASE_URL);

    @Before
    public void createEpicRequirements() throws JsonProcessingException {
        project2 = ProjectManager.create("Test Project 3");
        epic2 = EpicManager.create(project2.getId().toString());
    }

    @Given("I build epic request")
    public void iBuildEpicRequest() throws JsonProcessingException {
        apiRequest2.setBaseUri(baseUri);
        apiRequest2.addHeader("X-TrackerToken", userToken);
        apiRequest2.setMethod(ApiMethod.GET);
    }

    @When("I add {string} to epic request")
    public void iAddToEpicRequest(String endpoint) {
        apiRequest2.setEndpoint("/projects/{projectId}/" + endpoint);
        apiRequest2.addPathParam("projectId", project2.getId().toString());
        apiRequest2.addPathParam("epicId", epic2.getId().toString());
        apiResponse2 = ApiManager.execute(apiRequest2);
        apiResponse2.getResponse().then().log().body();
    }

    @Then("the response status code to epic request should be {string}")
    public void theResponseStatusCodeToEpicRequestShouldBe(String status) {
        Assert.assertEquals(apiResponse2.getStatusCode(), Integer.parseInt(status));

    }

    @After
    public void deleteEpicRequirements() {
        ProjectManager.delete(project2.getId().toString());
    }
}
