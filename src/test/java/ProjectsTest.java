import api.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static configuration.EnvVariablesPool.dotenv;

public class ProjectsTest {
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

    @BeforeMethod(onlyForGroups = "postRequest")
    public void addPostTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.POST);
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
    }

    @Test(groups = "getRequest")
    public void getAllProjectTest() {
        apiRequestBuilder.endpoint("/projects");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}")
                .pathParam("projectId", "2504481");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Project project = apiResponse.getBody(Project.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(project.getKind(), "project");
        apiResponse.validateBodySchema("schemas/project.json");
    }

    @Test(groups = "getRequest")
    public void getPeopleInProjectTest() {
        apiRequestBuilder.endpoint("/my/people")
                .queryParam("project_id", "2504434");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "postRequest")
    public void createAProjectTest() throws JsonProcessingException {
        Project projectToSend = new Project();
        projectToSend.setName("Project 6");
        apiRequestBuilder.endpoint("/projects")
                .body(new ObjectMapper().writeValueAsString(projectToSend));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Project project = apiResponse.getBody(Project.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(project.getKind(), "project");
        apiResponse.validateBodySchema("schemas/project.json");
    }

    @Test(groups = "deleteRequest")
    public void deleteAProject() {
        apiRequestBuilder.endpoint("/projects/{projectId}")
                .pathParam("projectId", "2505060");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);

    }
}
