import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Membership;
import entities.Project;
import entities.ProjectMembership;
import managers.Endpoints;
import managers.MembershipManager;
import managers.PathParam;
import managers.ProjectManager;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class MembershipTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Membership createdMembership;

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
        createdMembership = MembershipManager.createStoryTask(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "postRequest")
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
        createdMembership = MembershipManager.createStoryTask(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdMembership = MembershipManager.createStoryTask(createdProject.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteRequest"})
    public void cleanCreatedOneByGetRequest() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    @Test(groups = "getRequest")
    public void getAllMembersOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIPS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        apiResponse.getResponse().then().log().body();
    }

    @Test(groups = "getRequest")
    public void getAMemberTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIP))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.MEMBER_ID, createdMembership.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Membership membership = apiResponse.getBody(Membership.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(membership.getKind(), "project_membership");
    }

    @Test(groups = "postRequest")
    public void addAMemberToAProjectTest() throws JsonProcessingException {
        ProjectMembership projectMembership = new ProjectMembership();
        projectMembership.setEmail("marek19@sith.mil");
        projectMembership.setRole("member");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIPS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(projectMembership));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        apiResponse.getResponse().then().log().body();
    }

    @Test(groups = "putRequest")
    public void updateAMemberOfAProjectTest() throws JsonProcessingException {
        ProjectMembership projectMembership = new ProjectMembership();
        projectMembership.setRole("viewer");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIP))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.MEMBER_ID, createdMembership.getId())
                .body(new ObjectMapper().writeValueAsString(projectMembership));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        apiResponse.getResponse().then().log().body();
    }

    @Test(groups = "deleteRequest")
    public void deleteAMemberOfAProject() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIP))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.MEMBER_ID, createdMembership.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = "getRequest")
    public void doNotGetAllMembersOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIPS))
                .pathParam(PathParam.PROJECT_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void doNotGetAMemberTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIP))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.MEMBER_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "postRequest")
    public void DoNotAddAMemberToAProjectTest() throws JsonProcessingException {
        ProjectMembership projectMembership = new ProjectMembership();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIPS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(projectMembership));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "putRequest")
    public void doNotUpdateAMemberOfAProjectTest() throws JsonProcessingException {
        ProjectMembership projectMembership = new ProjectMembership();
        projectMembership.setRole("viewer");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIP))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.MEMBER_ID, "")
                .body(new ObjectMapper().writeValueAsString(projectMembership));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "deleteRequest")
    public void doNotDeleteAMemberOfAProject() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIP))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.MEMBER_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
