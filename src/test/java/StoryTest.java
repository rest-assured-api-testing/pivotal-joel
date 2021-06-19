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

/**
 * Tests story endpoint of a pivotal-tracker account.
 */
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
        createdStory = StoryManager.create(createdProject.getId().toString());
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
        createdStory = StoryManager.create(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdStory = StoryManager.create(createdProject.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest",
            "deleteRequest", "postBadRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    /**
     * Tests that project story endpoint gives us all stories.
     */
    @Test(groups = "getRequest")
    public void getAllStoriesOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORIES))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    /**
     * Tests that project story endpoint gives us a specific story.
     */
    @Test(groups = "getRequest")
    public void getAStoryOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.STORY_ID, createdStory.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Story story = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(story.getKind(), "story");
    }

    /**
     * Tests that project story endpoint creates a story.
     */
    @Test(groups = "postRequest")
    public void createAStoryToAProjectTest() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 4-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORIES))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdStory = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdStory.getName(), "Story 4-P1");
    }

    /**
     * Tests that project story endpoint updates a specific story.
     */
    @Test(groups = "putRequest")
    public void updateAStoryOfAProjectTest() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.STORY_ID, createdStory.getId())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Story createdStory = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdStory.getName(), "Story 5-P1");
    }

    /**
     * Tests that project story endpoint deletes a specific story.
     */
    @Test(groups = "deleteRequest")
    public void deleteAStoryOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.STORY_ID, createdStory.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    /**
     * Tests that story endpoint gives us a specific story.
     */
    @Test(groups = "getRequest")
    public void getAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Story story = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(story.getKind(), "story");
    }

    /**
     * Tests that story endpoint updates a specific story.
     */
    @Test(groups = "putRequest")
    public void updateAStoryTest() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 6-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, createdStory.getId().toString())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Story createdStory = apiResponse.getBody(Story.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdStory.getName(), "Story 6-P1");
    }

    /**
     * Tests that story endpoint deletes a specific story.
     */
    @Test(groups = "deleteRequest")
    public void deleteAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, createdStory.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    /**
     * Tests that project story endpoint gives us a not found status to respond a wrong url of getting all stories.
     */
    @Test(groups = "getRequest")
    public void doNotGetAllStoriesOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORIES))
                .pathParam(PathParam.PROJECT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that project story endpoint gives us a bad request status to respond to a getting request without
     * story id.
     */
    @Test(groups = "getRequest")
    public void doNotGetAStoryOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.STORY_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Tests that project story endpoint gives us a bad request status to respond to a creating request without
     * story body.
     */
    @Test(groups = "postBadRequest")
    public void doNotCreateAStoryToAProjectTest() throws JsonProcessingException {
        Story story = new Story();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORIES))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Tests that project story endpoint gives us a not found status to respond to a updating request without
     * a specific story id.
     */
    @Test(groups = "putRequest")
    public void doNotUpdateAStoryOfAProjectTest2() throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.STORY_ID, "")
                .body(new ObjectMapper().writeValueAsString(story));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that project story endpoint gives us a not found status to respond to a deleting request without
     * a specific story id.
     */
    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteAStoryOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_STORY))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.STORY_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that story endpoint gives us a not found status to respond to a getting request without
     * story id.
     */
    @Test(groups = "getRequest")
    public void doNotGetAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that story endpoint gives us a not found status to respond to a updating request without
     * a specific story id.
     */
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

    /**
     * Tests that story endpoint gives us a not found status to respond to a deleting request without
     * a specific story id.
     */
    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteAStoryTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
