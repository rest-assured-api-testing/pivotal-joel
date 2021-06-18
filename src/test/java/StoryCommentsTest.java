import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.StoryComment;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class StoryCommentsTest {
    ApiRequestBuilder apiRequestBuilder;
    StoryComment createdStoryComment;


    public void createStory() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("A comment 12-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("/projects/{projectId}/stories/{storyId}/comments")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .body(new ObjectMapper().writeValueAsString(storyComment));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        createdStoryComment = apiResponse.getBody(StoryComment.class);
    }

    public void deleteStory() {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/projects/{projectId}/stories/{storyId}/comments/{commentId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .pathParam("commentId", createdStoryComment.getId().toString());

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
    public void getAllCommentsOfAStoryTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}/comments")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getACommentOfAStoryTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}/comments/{commentId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .pathParam("commentId", createdStoryComment.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        StoryComment storyComment = apiResponse.getBody(StoryComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(storyComment.getKind(), "comment");
    }

    @Test(groups = "postRequest")
    public void createACommentOfAStoryTest() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("Comment 4-P1");
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}/comments")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdStoryComment = apiResponse.getBody(StoryComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStoryComment.getText(), "Comment 4-P1");
    }

    @Test(groups = "putRequest")
    public void updateACommentOfAStoryTest() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("Comment 5-P1");
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}/comments/{commentId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .pathParam("commentId", this.createdStoryComment.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        StoryComment createdStoryComment = apiResponse.getBody(StoryComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStoryComment.getText(), "Comment 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteACommentOfAStoryTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint("/projects/{projectId}/stories/{storyId}/comments/{commentId}")
                .pathParam("projectId", "2504465")
                .pathParam("storyId", "178547585")
                .pathParam("commentId", createdStoryComment.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }
}
