import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.StoryTask;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class StoryTasksTest {
    ApiRequestBuilder apiRequestBuilder;
    StoryTask createdStoryTask;

    public void createStoryTask() throws JsonProcessingException {
        StoryTask storyTask = new StoryTask();
        storyTask.setDescription("Task 1-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("projects/{projectId}/stories/{storyId}/tasks")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .body(new ObjectMapper().writeValueAsString(storyTask));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        createdStoryTask = apiResponse.getBody(StoryTask.class);
    }

    public void deleteStoryTask() {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/projects/{projectId}/stories/{storyId}/tasks/{taskId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .pathParam("taskId", createdStoryTask.getId().toString());

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
        createStoryTask();
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
        createStoryTask();
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createStoryTask();
    }

    @AfterMethod(onlyForGroups = "getRequest")
    public void cleanCreatedOneByGetRequest() {
        deleteStoryTask();
    }

    @AfterMethod(onlyForGroups = "postRequest")
    public void cleanCreatedOneByPostRequest() {
        deleteStoryTask();
    }

    @AfterMethod(onlyForGroups = "putRequest")
    public void cleanCreatedOneByPutRequest() {
        deleteStoryTask();
    }


    @Test(groups = "getRequest")
    public void getAllStoryTasksOfAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}/tasks")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getAStoryTaskOfAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}/tasks/{taskId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .pathParam("taskId", createdStoryTask.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        StoryTask storyTask = apiResponse.getBody(StoryTask.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(storyTask.getKind(), "task");
    }

    @Test(groups = "postRequest")
    public void createAStoryTaskToAProjectTest() throws JsonProcessingException {
        StoryTask storyTask = new StoryTask();
        storyTask.setDescription("Story 4-P1");
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}/tasks")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .body(new ObjectMapper().writeValueAsString(storyTask));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdStoryTask = apiResponse.getBody(StoryTask.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStoryTask.getDescription(), "Story 4-P1");
    }

    @Test(groups = "putRequest")
    public void updateAStoryTaskToAProjectTest() throws JsonProcessingException {
        StoryTask storyTask = new StoryTask();
        storyTask.setDescription("Story 5-P1");
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}/tasks/{taskId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .pathParam("taskId", createdStoryTask.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyTask));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        StoryTask createdStoryTask = apiResponse.getBody(StoryTask.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStoryTask.getDescription(), "Story 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAStoryTaskToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}/tasks/{taskId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .pathParam("taskId", createdStoryTask.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }
}
