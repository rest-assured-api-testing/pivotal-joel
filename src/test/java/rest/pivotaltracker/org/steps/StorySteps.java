package rest.pivotaltracker.org.steps;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequest;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import entities.Epic;
import entities.Project;
import entities.Story;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import managers.Endpoints;
import managers.EpicManager;
import managers.ProjectManager;
import managers.StoryManager;
import org.testng.Assert;

import static configuration.EnvVariablesPool.dotenv;

public class StorySteps {
    private ApiRequest apiRequest3 = new ApiRequest();
    private ApiResponse apiResponse3;
    Project project3;
    Story story3;

    private String userToken = dotenv.get(Endpoints.TOKEN);
    private String baseUri = dotenv.get(Endpoints.BASE_URL);

    @Before
    public void createEpicRequirements() throws JsonProcessingException, InterruptedException {
        project3 = ProjectManager.create("Test Project 2");
        story3 = StoryManager.create(project3.getId().toString());
    }
    @Given("I build story request")
    public void iBuildStoryRequest() {
        apiRequest3.setBaseUri(baseUri);
        apiRequest3.addHeader("X-TrackerToken", userToken);
        apiRequest3.setMethod(ApiMethod.GET);
    }

    @When("I add to story request {string}")
    public void iAddToEpicRequest(String endpoint) {
        apiRequest3.setEndpoint("/projects/{projectId}/" + endpoint);
        apiRequest3.addPathParam("projectId", project3.getId().toString());
        apiRequest3.addPathParam("storyId", story3.getId().toString());
        apiResponse3 = ApiManager.execute(apiRequest3);
        apiResponse3.getResponse().then().log().body();
    }

    @Then("the response status code should be {string} to story request")
    public void theResponseStatusCodeShouldBeToEpicRequest(String status) {
        Assert.assertEquals(apiResponse3.getStatusCode(), Integer.parseInt(status));
    }

    @After
    public void deleteEpicRequirements() {
        ProjectManager.delete(project3.getId().toString());
    }
}
