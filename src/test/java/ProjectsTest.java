import api.*;
import entities.Project;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static configuration.EnvVariablesPool.dotenv;

public class ProjectsTest {
    ApiRequestBuilder apiRequestBuilder;

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
}
