import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.WorkSpace;
import managers.*;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static configuration.EnvVariablesPool.dotenv;

/**
 * Tests workspace endpoint of a pivotal-tracker account.
 */
public class WorkSpacesTest {
    ApiRequestBuilder apiRequestBuilder;
    WorkSpace createdWorkspace;

    public void createBasicRequest() {
        apiRequestBuilder = new ApiRequestBuilder();
        apiRequestBuilder.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"));
    }

    @BeforeMethod(onlyForGroups = "getRequest")
    public void addGetTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.GET);
        createdWorkspace = WorkSpaceManager.create();
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
        createdWorkspace = WorkSpaceManager.create();
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdWorkspace = WorkSpaceManager.create();
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest"})
    public void cleanCreatedRequirements() {
        WorkSpaceManager.delete(createdWorkspace.getId().toString());
    }

    /**
     * Tests that workspace endpoint gives us all workspaces.
     */
    @Test(groups = "getRequest")
    public void getAllWorkspacesTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.WORKSPACES));

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    /**
     * Tests that workspace endpoint gives us a specific workspace.
     */
    @Test(groups = "getRequest")
    public void getAWorkspaceTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.WORKSPACE))
                .pathParam(PathParam.WORKSPACE_ID, createdWorkspace.getId().toString());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        WorkSpace workSpace = apiResponse.getBody(WorkSpace.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(workSpace.getKind(), "workspace");
        Assert.assertEquals(workSpace.getId(), createdWorkspace.getId());
        Assert.assertEquals(workSpace.getName(), createdWorkspace.getName());
    }

    /**
     * Tests that workspace endpoint creates a workspace.
     */
    @Test(groups = "postRequest")
    public void createAWorkspaceTest() throws JsonProcessingException {
        WorkSpace workspace = new WorkSpace();
        workspace.setName("Test WorkSpace 2");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.WORKSPACES))
                .body(new ObjectMapper().writeValueAsString(workspace));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdWorkspace = apiResponse.getBody(WorkSpace.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdWorkspace.getKind(), "workspace");
        Assert.assertEquals(createdWorkspace.getName(), "Test WorkSpace 2");
    }

    /**
     * Tests that workspace endpoint updates a specific workspace.
     */
    @Test(groups = "putRequest")
    public void updateAWorkspaceTest() throws JsonProcessingException {
        WorkSpace workspace = new WorkSpace();
        workspace.setProject_ids(new ArrayList<Long>());
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.WORKSPACE))
                .body(new ObjectMapper().writeValueAsString(workspace))
                .pathParam(PathParam.WORKSPACE_ID, createdWorkspace.getId());

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        apiResponse.getResponse().then().log().body();
        WorkSpace newWorkspace = apiResponse.getBody(WorkSpace.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(newWorkspace.getName(), createdWorkspace.getName());
    }

    /**
     * Tests that workspace endpoint deletes a specific workspace.
     */
    @Test(groups = "deleteRequest")
    public void deleteAWorkspaceTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.WORKSPACE))
                .pathParam(PathParam.WORKSPACE_ID, createdWorkspace.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);

    }

    /**
     * Tests that workspace endpoint gives us a not found status to respond a wrong url of getting all workspaces.
     */
    @Test(groups = "getRequest")
    public void doNotGetAllWorkspacesTest() {
        apiRequestBuilder.endpoint("/my/workspace");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that workspace endpoint gives us a not found status to respond to a getting request without
     * workspace id.
     */
    @Test(groups = "getRequest")
    public void doNotGetAWorkspaceWithBadUrlTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.WORKSPACE))
                .pathParam(PathParam.WORKSPACE_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that workspace endpoint gives us a bad request status to respond to a creating request without
     * workspace body.
     */
    @Test(groups = "postBadRequest")
    public void doNotCreateAWorkspaceTest() throws JsonProcessingException {
        WorkSpace workspace = new WorkSpace();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.WORKSPACES))
                .body(new ObjectMapper().writeValueAsString(workspace));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Tests that workspace endpoint gives us a not found status to respond to a updating request without
     * a specific workspace id.
     */
    @Test(groups = "putRequest")
    public void doNotUpdateAWorkspaceTest() throws JsonProcessingException {
        WorkSpace workspace = new WorkSpace();
        workspace.setProject_ids(new ArrayList<Long>());
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.WORKSPACE))
                .body(new ObjectMapper().writeValueAsString(workspace))
                .pathParam(PathParam.WORKSPACE_ID, "");

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that workspace endpoint gives us a not found status to respond to a deleting request without
     * a specific workspace id.
     */
    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteAWorkspaceTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.WORKSPACE))
                .pathParam(PathParam.WORKSPACE_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
