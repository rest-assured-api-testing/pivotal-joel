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

/**
 * Tests story comment endpoint of a pivotal-tracker account.
 */
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
        createdStory = StoryManager.create(createdProject.getId().toString());
        createdStoryComment = StoryCommentManager.create(createdProject.getId().toString(),
                createdStory.getId().toString());
    }

    @BeforeMethod(onlyForGroups = {"postRequest", "postBadRequest"})
    public void addPostTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.POST);
        createdProject = ProjectManager.create();
        createdStory = StoryManager.create(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "putRequest")
    public void addPutTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.PUT);
        createdProject = ProjectManager.create();
        createdStory = StoryManager.create(createdProject.getId().toString());
        createdStoryComment = StoryCommentManager.create(createdProject.getId().toString(),
                createdStory.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdStory = StoryManager.create(createdProject.getId().toString());
        createdStoryComment = StoryCommentManager.create(createdProject.getId().toString(),
                createdStory.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest",
            "deleteRequest", "postBadRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    /**
     * Tests that story comment endpoint gives us all story comments.
     */
    @Test(groups = "getRequest")
    public void getAllCommentsOfAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    /**
     * Tests that story comment endpoint gives us a specific story comment.
     */
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

    /**
     * Tests that story comment endpoint creates a story comment.
     */
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

    /**
     * Tests that story comment endpoint updates a specific story comment.
     */
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

    /**
     * Tests that story comment endpoint deletes a specific story comment.
     */
    @Test(groups = "deleteRequest")
    public void deleteACommentOfAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.COMMENT_ID, createdStoryComment.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    /**
     * Tests that story comment endpoint gives us a not found status to respond a wrong url of getting
     * all story comments.
     */
    @Test(groups = "getRequest")
    public void doNotGetAllCommentsOfAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that story comment endpoint gives us a not found status to respond to a getting request without
     * story comment id.
     */
    @Test(groups = "getRequest")
    public void doNotGetACommentOfAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.COMMENT_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that story comment endpoint gives us a bad request status to respond to a creating request
     * without story comment body.
     */
    @Test(groups = "postBadRequest")
    public void doNotCreateACommentOfAStoryTest() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Tests that story story comment endpoint gives us a not found status to respond to a updating
     * request without a specific story story comment id.
     */
    @Test(groups = "putRequest")
    public void doNotUpdateACommentOfAStoryTest() throws JsonProcessingException {
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

    /**
     * Tests that story comment endpoint gives us a not found status to respond to a deleting request
     * without a specific story comment id.
     */
    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteACommentOfAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId().toString())
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .pathParam(PathParam.COMMENT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
