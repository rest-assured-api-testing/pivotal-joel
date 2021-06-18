import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Epic;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class EpicsTest {
    ApiRequestBuilder apiRequestBuilder;
    Epic createdEpic;

    public void createEpic() throws JsonProcessingException {
        Epic epic = new Epic();
        epic.setName("Epic 12-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("/projects/{projectId}/epics")
                .pathParam("projectId", "2504465")
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
                .pathParam("projectId", "2504465")
                .pathParam("epicId", createdEpic.getId().toString());

        ApiManager.execute(apiRequestBuilder1.build());
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

    @BeforeMethod(onlyForGroups = "postRequest")
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

    @AfterMethod(onlyForGroups = "getRequest")
    public void cleanCreatedOneByGetRequest() {
        deleteEpic();
    }

    @AfterMethod(onlyForGroups = "postRequest")
    public void cleanCreatedOneByPostRequest() {
        deleteEpic();
    }

    @AfterMethod(onlyForGroups = "putRequest")
    public void cleanCreatedOneByPutRequest() {
        deleteEpic();
    }


    @Test(groups = "getRequest")
    public void getAllEpicsOfAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/epics")
                .pathParam("projectId", "2504465");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getAnEpicOfAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/epics/{epicId}")
                .pathParam("projectId", "2504465")
                .pathParam("epicId", createdEpic.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        Epic epic = apiResponse.getBody(Epic.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(epic.getKind(), "epic");
    }

    @Test(groups = "postRequest")
    public void createAnEpicToAProjectTest() throws JsonProcessingException {
        Epic epic = new Epic();
        epic.setName("Epic 4-P1");
        apiRequestBuilder.endpoint("/projects/{projectId}/epics")
                .pathParam("projectId", "2504465")
                .body(new ObjectMapper().writeValueAsString(epic));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdEpic = apiResponse.getBody(Epic.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdEpic.getName(), "Epic 4-P1");
    }

    @Test(groups = "putRequest")
    public void updateAnEpicToAProjectTest() throws JsonProcessingException {
        Epic epic = new Epic();
        epic.setName("Epic 5-P1");
        apiRequestBuilder.endpoint("/projects/{projectId}/epics/{epicId}")
                .pathParam("projectId", "2504465")
                .pathParam("epicId", createdEpic.getId().toString())
                .body(new ObjectMapper().writeValueAsString(epic));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Epic createdEpic = apiResponse.getBody(Epic.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdEpic.getName(), "Epic 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAnEpicToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint("/projects/{projectId}/epics/{epicId}")
                .pathParam("projectId", "2504465")
                .pathParam("epicId", createdEpic.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }

    @Test(groups = "getRequest")
    public void getAnEpicTest() {
        apiRequestBuilder.endpoint("/epics/{epicId}")
                .pathParam("epicId", createdEpic.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        Epic epic = apiResponse.getBody(Epic.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(epic.getKind(), "epic");
    }
}
