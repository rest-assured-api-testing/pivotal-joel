import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Epic;
import entities.Project;
import managers.*;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.*;

import static configuration.EnvVariablesPool.dotenv;

public class EpicsTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Epic createdEpic;

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
        createdEpic = EpicManager.createEpic(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = {"postRequest","postBadRequest"})
    public void addPostTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.POST);
        createdProject = ProjectManager.create();
    }

    @BeforeMethod(onlyForGroups = "putRequest")
    public void addPutTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.PUT);
        createdProject = ProjectManager.create();
        createdEpic = EpicManager.createEpic(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdEpic = EpicManager.createEpic(createdProject.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest",
            "deleteRequest", "postBadRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    @Test(groups = "getRequest")
    public void getAllEpicsOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_EPICS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test(groups = "getRequest")
    public void getAnEpicOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_EPIC))
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
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_EPICS))
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
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_EPIC))
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
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_EPIC))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = "getRequest")
    public void getAnEpicTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC))
                .pathParam(PathParam.EPIC_ID, createdEpic.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        Epic epic = apiResponse.getBody(Epic.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(epic.getKind(), "epic");
    }

    @Test(groups = "getRequest")
    public void doNotGetAllEpicsOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_EPICS))
                .pathParam(PathParam.PROJECT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void doNotGetAnEpicOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_EPIC))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "postBadRequest")
    public void doNotCreateAnEpicToAProjectTest() throws JsonProcessingException {
        Epic epic = new Epic();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_EPICS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(epic));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "putRequest")
    public void doNotUpdateAnEpicToAProjectTest() throws JsonProcessingException {
        Epic epic = new Epic();
        epic.setName("Epic 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_EPIC))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, "")
                .body(new ObjectMapper().writeValueAsString(epic));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteAnEpicToAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_EPIC))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void doNotGetAnEpicTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC))
                .pathParam(PathParam.EPIC_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
