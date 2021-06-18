import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.EpicComment;
import entities.StoryComment;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class EpicCommentsTest {
    ApiRequestBuilder apiRequestBuilder;
    EpicComment createdEpicComment;

    public void createEpicComment() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("A comment 12-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("/projects/{projectId}/epics/{epicId}/comments")
                .pathParam("projectId", "2504465")
                .pathParam("epicId", "4789536")
                .body(new ObjectMapper().writeValueAsString(storyComment));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        createdEpicComment = apiResponse.getBody(EpicComment.class);
    }

    public void deleteEpicComment() {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/projects/{projectId}/epics/{epicId}/comments/{commentId}")
                .pathParam("projectId", "2504465")
                .pathParam("epicId", "4789536")
                .pathParam("commentId", createdEpicComment.getId().toString());

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
        createEpicComment();
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
        createEpicComment();
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createEpicComment();
    }

    @AfterMethod(onlyForGroups = "getRequest")
    public void cleanCreatedOneByGetRequest() {
        deleteEpicComment();
    }

    @AfterMethod(onlyForGroups = "postRequest")
    public void cleanCreatedOneByPostRequest() {
        deleteEpicComment();
    }

    @AfterMethod(onlyForGroups = "putRequest")
    public void cleanCreatedOneByPutRequest() {
        deleteEpicComment();
    }


    @Test(groups = "getRequest")
    public void getAllCommentsOfAnEpicTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/epics/{epicId}/comments")
                .pathParam("projectId", "2504465")
                .pathParam("epicId", "4789536");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getACommentOfAnEpicTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/epics/{epicId}/comments/{commentId}")
                .pathParam("projectId", "2504465")
                .pathParam("epicId", "4789536")
                .pathParam("commentId", createdEpicComment.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        EpicComment storyComment = apiResponse.getBody(EpicComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(storyComment.getKind(), "comment");
    }

    @Test(groups = "postRequest")
    public void createACommentOfAnEpicTest() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("Comment 4-P1");
        apiRequestBuilder.endpoint("/projects/{projectId}/epics/{epicId}/comments")
                .pathParam("projectId", "2504465")
                .pathParam("epicId", "4789536")
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdEpicComment = apiResponse.getBody(EpicComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdEpicComment.getText(), "Comment 4-P1");
    }

    @Test(groups = "putRequest")
    public void updateACommentOfAnEpicTest() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("Comment 5-P1");
        apiRequestBuilder.endpoint("/projects/{projectId}/epics/{epicId}/comments/{commentId}")
                .pathParam("projectId", "2504465")
                .pathParam("epicId", "4789536")
                .pathParam("commentId", this.createdEpicComment.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        EpicComment createdStoryComment = apiResponse.getBody(EpicComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(createdStoryComment.getText(), "Comment 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteACommentOfAnEpicTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint("/projects/{projectId}/epics/{epicId}/comments/{commentId}")
                .pathParam("projectId", "2504465")
                .pathParam("epicId", "4789536")
                .pathParam("commentId", createdEpicComment.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }
}
