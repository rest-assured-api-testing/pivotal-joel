import api.ApiManager;
import api.ApiMethod;
import api.ApiRequest;
import api.ApiResponse;
import entities.Project;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Projects {
    ApiRequest apiRequest = new ApiRequest();


    public void createBasicRequest() {
        apiRequest = new ApiRequest();
        apiRequest.addHeader("X-TrackerToken", "7a6b2ec9fe77808a9d9286d18bd91e95");
        apiRequest.setBaseUri("https://www.pivotaltracker.com/services/v5");
    }

    @BeforeMethod(onlyForGroups = "getProjects")
    public void createProjects() {
        createBasicRequest();
        apiRequest.setMethod(ApiMethod.GET);
    }

    @Test(groups = "getProjects")
    public void getAllProjectTest() {
        apiRequest.setEndpoint("/projects");

        ApiResponse apiResponse = new ApiResponse(ApiManager.execute(apiRequest));

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getProjects")
    public void getAProjectTest() {
        apiRequest.setEndpoint("/projects/{projectId}");
        apiRequest.addPathParam("projectId", "2504481");

        ApiResponse apiResponse = new ApiResponse(ApiManager.execute(apiRequest));
        Project project = apiResponse.getBody(Project.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(project.getKind(), "project");
        apiResponse.validateBodySchema("schemas/project.json");
    }
}
