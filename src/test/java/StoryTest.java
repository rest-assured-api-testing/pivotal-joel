import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Story;
import managers.Param;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class StoryTest {
    ApiRequestBuilder apiRequestBuilder;
    Story createdStory;

    public void createStory() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 7-P1");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("/projects/{projectId}/stories")
                .pathParam("projectId", "2504465")
                .body(new ObjectMapper().writeValueAsString(story));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        createdStory = apiResponse.getBody(Story.class);
    }

    public void deleteStory() {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/stories/{storyId}")
                .pathParam("storyId", createdStory.getId().toString());

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
        createStory();
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
        createStory();
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createStory();
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest"})
    public void cleanCreatedOneByGetRequest() {
        deleteStory();
    }

    @Test(groups = "getRequest")
    public void getAllStoriesOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORIES_PROJECT"))
                .pathParam("projectId", "2504481");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getAStoryOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY_PROJECT"))
                .pathParam("projectId", "2504465")
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
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORIES_PROJECT"))
                .pathParam("projectId", "2504465")
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
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY_PROJECT"))
                .pathParam("projectId", "2504465")
                .pathParam(Param.STORY_ID.getText(), createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Story createdStory = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStory.getName(), "Story 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAStoryToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY_PROJECT"))
                .pathParam("projectId", "2504465")
                .pathParam(Param.STORY_ID.getText(), createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }

    @Test(groups = "getRequest")
    public void getAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY"))
                .pathParam("storyId", createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Story story = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(story.getKind(), "story");
    }

    @Test(groups = "putRequest")
    public void updateAStoryTest() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 6-P1");
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY"))
                .pathParam("storyId", createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Story createdStory = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStory.getName(), "Story 6-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY"))
                .pathParam("storyId", createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }

    @Test(groups = "getRequest")
    public void doNotGetAllStoriesOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORIES_PROJECT"))
                .pathParam("projectId", "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void DoNotGetAStoryOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY_PROJECT"))
                .pathParam("projectId", "2504465")
                .pathParam(Param.STORY_ID.getText(), " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "postBadRequest")
    public void DoNotCreateAStoryToAProjectTest() throws JsonProcessingException {
        Story story = new Story();
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORIES_PROJECT"))
                .pathParam("projectId", "2504465")
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "putRequest")
    public void DoNotUpdateAStoryToAProjectTest2() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 5-P1");
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY_PROJECT"))
                .pathParam("projectId", "2504465")
                .pathParam(Param.STORY_ID.getText(), "")
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void DoNotDeleteAStoryToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY_PROJECT"))
                .pathParam("projectId", "2504465")
                .pathParam(Param.STORY_ID.getText(), "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void doNotGetAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY"))
                .pathParam(Param.STORY_ID.getText(), " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "putRequest")
    public void doNotUpdateAStoryTest() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 6-P1");
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY"))
                .pathParam(Param.STORY_ID.getText(), " ")
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get("ENDPOINT_STORY"))
                .pathParam(Param.STORY_ID.getText(), "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
