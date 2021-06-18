import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;
import entities.Story;
import entities.StoryComment;
import managers.Endpoints;
import managers.PathParam;
import managers.ProjectManager;
import managers.StoryManager;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.*;
import static configuration.EnvVariablesPool.dotenv;

public class StoryCommentsTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Story createdStory;
    StoryComment createdStoryComment;


    public void createStoryComment() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("A comment 12-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("/projects/{projectId}/stories/{storyId}/comments")
                .pathParam("projectId", createdProject.getId().toString())
                .pathParam("storyId", createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyComment));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        createdStoryComment = apiResponse.getBody(StoryComment.class);
    }

    public void deleteStoryComment() {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/projects/{projectId}/stories/{storyId}/comments/{commentId}")
                .pathParam("projectId", createdProject.getId().toString())
                .pathParam("storyId", createdStory.getId().toString())
                .pathParam("commentId", createdStoryComment.getId().toString());

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
        createStoryComment();
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
        createStoryComment();
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createStoryComment();
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest"})
    public void cleanCreatedOneByGetRequest() {
        deleteStoryComment();
    }

    @Test(groups = "getRequest")
    public void getAllCommentsOfAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test(groups = "getRequest")
    public void getACommentOfAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.COMMENT_ID, createdStoryComment.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        StoryComment storyComment = apiResponse.getBody(StoryComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(storyComment.getKind(), "comment");
    }

    @Test(groups = "postRequest")
    public void createACommentOfAStoryTest() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("Comment 4-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
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
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.COMMENT_ID, this.createdStoryComment.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        StoryComment createdStoryComment = apiResponse.getBody(StoryComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdStoryComment.getText(), "Comment 5-P1");
    }

    @Test(groups = "deleteRequest")
    public void deleteACommentOfAStoryTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.COMMENT_ID, createdStoryComment.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    //*******************************************

    @Test(groups = "getRequest")
    public void getAllCommentsOfAStoryTest2() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void getACommentOfAStoryTest2() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.COMMENT_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "postBadRequest")
    public void createACommentOfAStoryTest2() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "putRequest")
    public void updateACommentOfAStoryTest2() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("Comment 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.COMMENT_ID, "")
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void deleteACommentOfAStoryTest2() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.COMMENT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
