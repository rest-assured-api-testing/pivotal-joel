import api.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;
import managers.Param;
import managers.ProjectManager;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static configuration.EnvVariablesPool.dotenv;

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

    @BeforeMethod(onlyForGroups = "postRequest")
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

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest"})
    public void cleanCreatedOneByGetRequest() {
        ProjectManager.delete(createdProject.getId().toString());
    }


    @Test(groups = "getRequest")
    public void getAllProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_PROJECTS"));

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test(groups = "getRequest")
    public void getAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_PROJECT"))
                .pathParam(Param.PROJECT_ID.getName(), createdProject.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Project project = apiResponse.getBody(Project.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(project.getKind(), "project");
        Assert.assertEquals(project.getId(), createdProject.getId());
        Assert.assertEquals(project.getName(), createdProject.getName());
        Assert.assertEquals(project.getAccount_id(), createdProject.getAccount_id());
        apiResponse.validateBodySchema("schemas/project.json");
    }

    @Test(groups = "getRequest")
    public void getPeopleInProjectTest() {
        apiRequestBuilder.endpoint("/my/people")
                .queryParam("project_id", "2504434");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test(groups = "postRequest")
    public void createAProjectTest() throws JsonProcessingException {
        Project projectToSend = new Project();
        String projectName = "Project 6";
        projectToSend.setName(projectName);
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_PROJECTS"))
                .body(new ObjectMapper().writeValueAsString(projectToSend));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdProject = apiResponse.getBody(Project.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdProject.getKind(), "project");
        Assert.assertEquals(createdProject.getName(), projectName);
        apiResponse.validateBodySchema("schemas/project.json");
    }

    @Test(groups = "putRequest")
    public void updateAProjectTest() throws JsonProcessingException {
        Project projectToSend = new Project();
        String nameToUpdate = "Project 6";
        projectToSend.setName(nameToUpdate);
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_PROJECT"))
                .body(new ObjectMapper().writeValueAsString(projectToSend))
                .pathParam(Param.PROJECT_ID.getName(), createdProject.getId().toString());

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Project project = apiResponse.getBody(Project.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(project.getName(), nameToUpdate);
    }

    @Test(groups = "deleteRequest")
    public void deleteAProject() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_PROJECT"))
                .pathParam(Param.PROJECT_ID.getName(), createdProject.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);

    }
}
