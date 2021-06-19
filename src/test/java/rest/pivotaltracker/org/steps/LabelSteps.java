package rest.pivotaltracker.org.steps;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequest;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import entities.Label;
import entities.Project;
import entities.Story;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import managers.Endpoints;
import managers.LabelManager;
import managers.ProjectManager;
import managers.StoryManager;
import org.testng.Assert;

import static configuration.EnvVariablesPool.dotenv;

public class LabelSteps {
    private ApiRequest apiRequest4 = new ApiRequest();
    private ApiResponse apiResponse4;
    Project project4;
    Label label4;


    private String userToken = dotenv.get(Endpoints.TOKEN);
    private String baseUri = dotenv.get(Endpoints.BASE_URL);

    @Before
    public void createEpicRequirements() throws JsonProcessingException, InterruptedException {
        project4 = ProjectManager.create("Test Project 4");
        label4 = LabelManager.create(project4.getId().toString());
        System.out.println("====================================");
        System.out.println("created project4 : " + project4.getId());
        System.out.println("created epic4 :" + label4.getId());
        System.out.println("====================================");
    }

    @Given("I build label request")
    public void iBuildLabelRequest() {
        apiRequest4.setBaseUri(baseUri);
        apiRequest4.addHeader("X-TrackerToken", userToken);
        apiRequest4.setMethod(ApiMethod.GET);
    }

    @When("I add to label request {string}")
    public void iAddToLabelRequest(String endpoint) {
        apiRequest4.setEndpoint("/projects/{projectId}/" + endpoint);
        apiRequest4.addPathParam("projectId", project4.getId().toString());
        apiRequest4.addPathParam("labelId", label4.getId().toString());
        apiResponse4 = ApiManager.execute(apiRequest4);
        apiResponse4.getResponse().then().log().body();
    }

    @Then("the response status code should be {string} to label request")
    public void theResponseStatusCodeShouldBeToLabelRequest(String status) {
        Assert.assertEquals(apiResponse4.getStatusCode(), Integer.parseInt(status));
    }

    @After
    public void deleteEpicRequirements() {
        ProjectManager.delete(project4.getId().toString());
    }
}
