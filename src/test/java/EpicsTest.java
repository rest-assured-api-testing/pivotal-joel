import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Epic;
import entities.Project;
import managers.Endpoint;
import managers.PathParam;
import managers.ProjectManager;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.*;

import static configuration.EnvVariablesPool.dotenv;

public class EpicsTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Epic createdEpic;

    public void createEpic() throws JsonProcessingException {
        Epic epic = new Epic();
        epic.setName("Epic 12-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("/projects/{projectId}/epics")
                .pathParam("projectId", createdProject.getId().toString())
                .body(new ObjectMapper().writeValueAsString(epic));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        createdEpic = apiResponse.getBody(Epic.class);
    }

    public void deleteEpic() {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/projects/{projectId}/epics/{epicId}")
                .pathParam("projectId", createdProject.getId().toString())
                .pathParam("epicId", createdEpic.getId().toString());

        ApiManager.execute(apiRequestBuilder1.build());
    }
    @BeforeSuite
    public void createProject() throws JsonProcessingException {
        createdProject = ProjectManager.create();
    }

    @AfterSuite
    public void deleteProject() throws JsonProcessingException {
        ProjectManager.delete(createdProject.getId().toString());
    }

    @BeforeTest
    public void createBasicRequest() {
        apiRequestBuilder = new ApiRequestBuilder();
        apiRequestBuilder.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"));
    }

    @BeforeMethod(onlyForGroups = "getRequest")
    public void addGetTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.GET);
        createEpic();
    }

    @BeforeMethod(onlyForGroups = {"postRequest","postBadRequest"})
    public void addPostTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.POST);
    }

    @BeforeMethod(onlyForGroups = "putRequest")
    public void addPutTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.PUT);
        createEpic();
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createEpic();
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest"})
    public void cleanCreatedOneByGetRequest() {
        deleteEpic();
    }

    @Test(groups = "getRequest")
    public void getAllEpicsOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.PROJECT_EPICS.getText()))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test(groups = "getRequest")
    public void getAnEpicOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.PROJECT_EPIC.getText()))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        Epic epic = apiResponse.getBody(Epic.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(epic.getKind(), "epic");
    }

    @Test(groups = "postRequest")
    public void createAnEpicToAProjectTest() throws JsonProcessingException {
        Epic epic = new Epic();
        epic.setName("Epic 4-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.PROJECT_EPICS.getText()))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(epic));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdEpic = apiResponse.getBody(Epic.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdEpic.getName(), "Epic 4-P1");
    }

    @Test(groups = "putRequest")
    public void updateAnEpicToAProjectTest() throws JsonProcessingException {
        Epic epic = new Epic();
        epic.setName("Epic 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.PROJECT_EPIC.getText()))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId())
                .body(new ObjectMapper().writeValueAsString(epic));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Epic createdEpic = apiResponse.getBody(Epic.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdEpic.getName(), "Epic 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAnEpicToAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.PROJECT_EPIC.getText()))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = "getRequest")
    public void getAnEpicTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.EPIC.getText()))
                .pathParam(PathParam.EPIC_ID, createdEpic.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        Epic epic = apiResponse.getBody(Epic.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(epic.getKind(), "epic");
    }

    @Test(groups = "getRequest")
    public void DoNotGetAllEpicsOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.PROJECT_EPICS.getText()))
                .pathParam(PathParam.PROJECT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void DoNotGetAnEpicOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.PROJECT_EPIC.getText()))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "postBadRequest")
    public void DoNotCreateAnEpicToAProjectTest() throws JsonProcessingException {
        Epic epic = new Epic();
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.PROJECT_EPICS.getText()))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(epic));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "putRequest")
    public void DoNotUpdateAnEpicToAProjectTest() throws JsonProcessingException {
        Epic epic = new Epic();
        epic.setName("Epic 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.PROJECT_EPIC.getText()))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, "")
                .body(new ObjectMapper().writeValueAsString(epic));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void DoNotDeleteAnEpicToAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.PROJECT_EPIC.getText()))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void DoNotGetAnEpicTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoint.EPIC.getText()))
                .pathParam(PathParam.EPIC_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
