import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;
import entities.Story;
import entities.StoryComment;
import managers.*;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.*;
import static configuration.EnvVariablesPool.dotenv;

public class StoryCommentsTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Story createdStory;
    StoryComment createdStoryComment;

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
        createdProject = ProjectManager.create();
        createdStory = StoryManager.createStory(createdProject.getId().toString());
        createdStoryComment = StoryCommentManager.createStoryComment(createdProject.getId().toString(),
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
        createdStoryComment = StoryCommentManager.createStoryComment(createdProject.getId().toString(),
                createdStory.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdStory = StoryManager.createStory(createdProject.getId().toString());
        createdStoryComment = StoryCommentManager.createStoryComment(createdProject.getId().toString(),
                createdStory.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest",
            "deleteRequest", "postBadRequest"})
    public void cleanCreatedOneByGetRequest() {
        ProjectManager.delete(createdProject.getId().toString());
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

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
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
