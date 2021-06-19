import api.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Tests project endpoint of a pivotal-tracker account.
 */
public class ProjectsTest {
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

    @BeforeMethod(onlyForGroups = {"postRequest", "postBadRequest"})
    public void addPostTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.POST);
    }

    @BeforeMethod(onlyForGroups = "putRequest")
    public void addPutTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.PUT);
        createdProject = ProjectManager.create();
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    /**
     * Tests that project endpoint gives us all projects.
     */
    @Test(groups = "getRequest")
    public void getAllProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECTS));

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    /**
     * Tests that project endpoint gives us a specific project.
     */
    @Test(groups = "getRequest")
    public void getAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Project project = apiResponse.getBody(Project.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(project.getKind(), "project");
        Assert.assertEquals(project.getId(), createdProject.getId());
        Assert.assertEquals(project.getName(), createdProject.getName());
        Assert.assertEquals(project.getAccount_id(), createdProject.getAccount_id());
        apiResponse.validateBodySchema("schemas/project.json");
    }

    /**
     * Tests that project people endpoint give us project people information.
     */
    @Test(groups = "getRequest")
    public void getPeopleInProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PEOPLE))
                .queryParam("project_id", "2504434");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    /**
     * Tests that project endpoint creates a project.
     */
    @Test(groups = "postRequest")
    public void createAProjectTest() throws JsonProcessingException {
        Project projectToSend = new Project();
        String projectName = "Project 6";
        projectToSend.setName(projectName);
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECTS))
                .body(new ObjectMapper().writeValueAsString(projectToSend));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdProject = apiResponse.getBody(Project.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdProject.getKind(), "project");
        Assert.assertEquals(createdProject.getName(), projectName);
        apiResponse.validateBodySchema("schemas/project.json");
    }

    /**
     * Tests that project endpoint updates a specific project.
     */
    @Test(groups = "putRequest")
    public void updateAProjectTest() throws JsonProcessingException {
        Project projectToSend = new Project();
        String nameToUpdate = "Project 6";
        projectToSend.setName(nameToUpdate);
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT))
                .body(new ObjectMapper().writeValueAsString(projectToSend))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString());

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Project project = apiResponse.getBody(Project.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(project.getName(), nameToUpdate);
    }

    /**
     * Tests that project endpoint deletes a specific project.
     */
    @Test(groups = "deleteRequest")
    public void deleteAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);

    }

    /**
     * Tests that project endpoint gives us a not found status to respond a wrong url of getting all projects.
     */
    @Test(groups = "getRequest")
    public void doNotGetAllProjectTest() {
        apiRequestBuilder.endpoint("/project");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that project endpoint gives us a not found status to respond to a getting request without
     * project id.
     */
    @Test(groups = "getRequest")
    public void doNotGetAProjectWithBadUrlTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT))
                .pathParam(PathParam.PROJECT_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that project endpoint gives us a not found status to respond to a getting request with
     * a project id that does not exist.
     */
    @Test(groups = "getRequest")
    public void doNotGetAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT))
                .pathParam(PathParam.PROJECT_ID, "1");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_FORBIDDEN);
    }

    /**
     * Tests that project endpoint gives us a bad request status to respond to a creating request without
     * project body.
     */
    @Test(groups = "postBadRequest")
    public void doNotCreateAProjectTest() throws JsonProcessingException {
        Project projectToSend = new Project();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECTS))
                .body(new ObjectMapper().writeValueAsString(projectToSend));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Tests that project endpoint gives us a not found status to respond to a updating request without
     * a specific project id.
     */
    @Test(groups = "putRequest")
    public void doNotUpdateAProjectTest() throws JsonProcessingException {
        Project projectToSend = new Project();
        String nameToUpdate = "Project 6";
        projectToSend.setName(nameToUpdate);
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT))
                .body(new ObjectMapper().writeValueAsString(projectToSend))
                .pathParam(PathParam.PROJECT_ID, "");

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
    /**
     * Tests that project endpoint gives us a not found status to respond to a deleting request without
     * a specific project id.
     */
    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT))
                .pathParam(PathParam.PROJECT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
