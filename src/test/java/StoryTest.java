import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;
import entities.Story;
import managers.*;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class StoryTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Story createdStory;

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
    }

    @BeforeMethod(onlyForGroups = {"postRequest", "postBadRequest"})
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
        createdStory = StoryManager.createStory(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdStory = StoryManager.createStory(createdProject.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest",
            "deleteRequest", "postBadRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    @Test(groups = "getRequest")
    public void getAllStoriesOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORIES))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getAStoryOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(Param.STORY_ID.getText(), createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Story story = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(story.getKind(), "story");
    }

    @Test(groups = "postRequest")
    public void createAStoryToAProjectTest() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 4-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORIES))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdStory = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStory.getName(), "Story 4-P1");
    }

    @Test(groups = "putRequest")
    public void updateAStoryToAProjectTest() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(Param.STORY_ID.getText(), createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Story createdStory = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStory.getName(), "Story 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAStoryToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(Param.STORY_ID.getText(), createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }

    @Test(groups = "getRequest")
    public void getAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Story story = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(story.getKind(), "story");
    }

    @Test(groups = "putRequest")
    public void updateAStoryTest() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 6-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Story createdStory = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStory.getName(), "Story 6-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }

    @Test(groups = "getRequest")
    public void doNotGetAllStoriesOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORIES))
                .pathParam(PathParam.PROJECT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void doNotGetAStoryOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(Param.STORY_ID.getText(), " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "postBadRequest")
    public void doNotCreateAStoryToAProjectTest() throws JsonProcessingException {
        Story story = new Story();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORIES))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "putRequest")
    public void doNotUpdateAStoryToAProjectTest2() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(Param.STORY_ID.getText(), "")
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteAStoryToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(Param.STORY_ID.getText(), "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void doNotGetAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "putRequest")
    public void doNotUpdateAStoryTest() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 6-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, " ")
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
