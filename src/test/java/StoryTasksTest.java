import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;
import entities.Story;
import entities.StoryTask;
import managers.*;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.*;

import static configuration.EnvVariablesPool.dotenv;

public class StoryTasksTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Story createdStory;
    StoryTask createdStoryTask;
    
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
        createdStory = StoryManager.createStory(createdProject.getId().toString());
        createdStoryTask = StoryTaskManager.createStoryTask(createdProject.getId().toString(),
                createdStory.getId().toString());
    }

    @BeforeMethod(onlyForGroups = {"postRequest", "postBadRequest"})
    public void addPostTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.POST);
        createdProject = ProjectManager.create();
        createdStory = StoryManager.createStory(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "putRequest")
    public void addPutTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.PUT);
        createdProject = ProjectManager.create();
        createdStory = StoryManager.createStory(createdProject.getId().toString());
        createdStoryTask = StoryTaskManager.createStoryTask(createdProject.getId().toString(),
                createdStory.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdStory = StoryManager.createStory(createdProject.getId().toString());
        createdStoryTask = StoryTaskManager.createStoryTask(createdProject.getId().toString(),
                createdStory.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest",
            "deleteRequest", "postBadRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    @Test(groups = "getRequest")
    public void getAllStoryTasksOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
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

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
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

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdStoryTask.getDescription(), "Story 4-P1");
    }

    @Test(groups = "putRequest")
    public void updateAStoryTaskOfAProjectTest() throws JsonProcessingException {
        StoryTask storyTask = new StoryTask();
        storyTask.setDescription("Story 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.TASK_ID, createdStoryTask.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyTask));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        StoryTask createdStoryTask = apiResponse.getBody(StoryTask.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdStoryTask.getDescription(), "Story 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAStoryTaskOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.TASK_ID, createdStoryTask.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = "getRequest")
    public void doNotGetAllStoryTasksOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void doNotGetAStoryTaskOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.TASK_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "postBadRequest")
    public void doNotCreateAStoryTaskToAProjectTest() throws JsonProcessingException {
        StoryTask storyTask = new StoryTask();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyTask));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "putRequest")
    public void doNotUpdateAStoryTaskOfAProjectTest() throws JsonProcessingException {
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
    public void doNotDeleteAStoryTaskOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_TASK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.TASK_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

}
