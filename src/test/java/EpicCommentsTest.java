import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.*;
import managers.*;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.*;

import static configuration.EnvVariablesPool.dotenv;

/**
 * Tests epic comment endpoint of a pivotal-tracker account.
 */
public class EpicCommentsTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Epic createdEpic;
    EpicComment createdEpicComment;

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
        createdEpic = EpicManager.create(createdProject.getId().toString());
        createdEpicComment = EpicCommentManager.create(createdProject.getId().toString(),
                createdEpic.getId().toString());
    }

    @BeforeMethod(onlyForGroups = {"postRequest", "postBadRequest"})
    public void addPostTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.POST);
        createdProject = ProjectManager.create();
        createdEpic = EpicManager.create(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "putRequest")
    public void addPutTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.PUT);
        createdProject = ProjectManager.create();
        createdEpic = EpicManager.create(createdProject.getId().toString());
        createdEpicComment = EpicCommentManager.create(createdProject.getId().toString(),
                createdEpic.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdEpic = EpicManager.create(createdProject.getId().toString());
        createdEpicComment = EpicCommentManager.create(createdProject.getId().toString(),
                createdEpic.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest",
            "deleteRequest", "postBadRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    /**
     * Tests that epic comment endpoint gives us all epic comments.
     */
    @Test(groups = "getRequest")
    public void getAllCommentsOfAnEpicTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    /**
     * Tests that epic comment endpoint gives us a specific epic comment.
     */
    @Test(groups = "getRequest")
    public void getACommentOfAnEpicTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId())
                .pathParam(PathParam.COMMENT_ID, createdEpicComment.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        EpicComment storyComment = apiResponse.getBody(EpicComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(storyComment.getKind(), "comment");
    }

    /**
     * Tests that epic comment endpoint creates a epic comment.
     */
    @Test(groups = "postRequest")
    public void createACommentOfAnEpicTest() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("Comment 4-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId())
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdEpicComment = apiResponse.getBody(EpicComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdEpicComment.getText(), "Comment 4-P1");
    }

    /**
     * Tests that epic comment endpoint updates a specific epic comment.
     */
    @Test(groups = "putRequest")
    public void updateACommentOfAnEpicTest() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("Comment 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId())
                .pathParam(PathParam.COMMENT_ID, this.createdEpicComment.getId().toString())
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        EpicComment createdStoryComment = apiResponse.getBody(EpicComment.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdStoryComment.getText(), "Comment 5-P1");
    }

    /**
     * Tests that epic comment endpoint deletes a specific epic comment.
     */
    @Test(groups = "deleteRequest")
    public void deleteACommentOfAnEpicTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId())
                .pathParam(PathParam.COMMENT_ID, createdEpicComment.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    /**
     * Tests that epic comment endpoint gives us a not found status to respond a wrong url of
     * getting all epic comments.
     */
    @Test(groups = "getRequest")
    public void doNotGetAllCommentsOfAnEpicTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that epic comment endpoint gives us a not found status to respond to a getting request
     * without epic comment id.
     */
    @Test(groups = "getRequest")
    public void doNotGetACommentOfAnEpicTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId())
                .pathParam(PathParam.COMMENT_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that epic comment endpoint gives us a bad request status to respond to a creating request
     * without epic comment body.
     */
    @Test(groups = "postBadRequest")
    public void doNotCreateACommentOfAnEpicTest() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId())
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Tests that epic comment endpoint gives us a not found status to respond to a updating request
     * without a specific epic comment id.
     */
    @Test(groups = "putRequest")
    public void doNotUpdateACommentOfAnEpicTest() throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("Comment 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId())
                .pathParam(PathParam.COMMENT_ID, "")
                .body(new ObjectMapper().writeValueAsString(storyComment));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that epic comment endpoint gives us a not found status to respond to a deleting request
     * without a specific epic comment id.
     */
    @Test(groups = "deleteRequest")
    public void doNotDeleteACommentOfAnEpicTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.EPIC_COMMENT))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.EPIC_ID, createdEpic.getId())
                .pathParam(PathParam.COMMENT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
