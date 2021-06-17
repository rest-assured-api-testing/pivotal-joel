import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Story;
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

    @BeforeMethod(onlyForGroups = "postRequest")
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

    @AfterMethod(onlyForGroups = "getRequest")
    public void cleanCreatedOneByGetRequest() {
        deleteStory();
    }

    @AfterMethod(onlyForGroups = "postRequest")
    public void cleanCreatedOneByPostRequest() {
        deleteStory();
    }

    @AfterMethod(onlyForGroups = "putRequest")
    public void cleanCreatedOneByPutRequest() {
        deleteStory();
    }
    

    @Test(groups = "getRequest")
    public void getAllStoriesOfAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/stories")
                .pathParam("projectId", "2504481");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getAStoryOfAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Story story = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(story.getKind(), "story");
    }

    @Test(groups = "postRequest")
    public void createAStoryToAProjectTest() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 4-P1");
        apiRequestBuilder.endpoint("/projects/{projectId}/stories")
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
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Story createdStory = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStory.getName(), "Story 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAStoryToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }

    @Test(groups = "getRequest")
    public void getAStoryTest() {
        apiRequestBuilder.endpoint("/stories/{storyId}")
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
        apiRequestBuilder.endpoint("/stories/{storyId}")
                .pathParam("storyId", createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Story createdStory = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStory.getName(), "Story 6-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteAStoryTest() {
        apiRequestBuilder.endpoint("/stories/{storyId}")
                .pathParam("storyId", createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }
}
