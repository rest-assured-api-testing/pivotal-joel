import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;
import entities.Story;
import entities.StoryTask;
import managers.Endpoints;
import managers.PathParam;
import managers.ProjectManager;
import managers.StoryManager;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.*;

import static configuration.EnvVariablesPool.dotenv;

public class StoryTasksTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Story createdStory;
    StoryTask createdStoryTask;

    public void createStoryTask() throws JsonProcessingException {
        StoryTask storyTask = new StoryTask();
        storyTask.setDescription("Task 1-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("projects/{projectId}/stories/{storyId}/tasks")
                .pathParam("projectId", createdProject.getId().toString())
                .pathParam("storyId", createdStory.getId().toString())
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
                .pathParam("projectId", createdProject.getId().toString())
                .pathParam("storyId", createdStory.getId().toString())
                .pathParam("taskId", createdStoryTask.getId().toString());

        ApiManager.execute(apiRequestBuilder1.build());
    }

    @BeforeSuite
    public void createProject() throws JsonProcessingException {
        createdProject = ProjectManager.create();
        createdStory = StoryManager.createStory(createdProject.getId().toString());
    }

    @AfterSuite
    public void deleteProject() throws JsonProcessingException {
        ProjectManager.delete(createdProject.getId().toString());
        StoryManager.deleteStory(createdStory.getId().toString());
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

    @BeforeMethod(onlyForGroups = {"postRequest", "postBadRequest"})
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

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest"})
    public void cleanCreatedOneByGetRequest() {
        deleteStoryTask();
    }

    @Test(groups = "getRequest")
    public void getAllStoryTasksOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getAStoryTaskOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.TASK_ID, createdStoryTask.getId().toString());

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
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
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
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.TASK_ID, createdStoryTask.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyTask));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        StoryTask createdStoryTask = apiResponse.getBody(StoryTask.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStoryTask.getDescription(), "Story 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAStoryTaskToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.TASK_ID, createdStoryTask.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }

    @Test(groups = "getRequest")
    public void getAllStoryTasksOfAProjectTest2() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void getAStoryTaskOfAProjectTest2() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.TASK_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "postBadRequest")
    public void createAStoryTaskToAProjectTest2() throws JsonProcessingException {
        StoryTask storyTask = new StoryTask();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyTask));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "putRequest")
    public void updateAStoryTaskToAProjectTest2() throws JsonProcessingException {
        StoryTask storyTask = new StoryTask();
        storyTask.setDescription("Story 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.TASK_ID, "")
                .body(new ObjectMapper().writeValueAsString(storyTask));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void deleteAStoryTaskToAProjectTest2() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.TASK_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

}
