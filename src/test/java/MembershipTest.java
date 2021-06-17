import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Membership;
import entities.Person;
import entities.Project;
import entities.ProjectMembership;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class MembershipTest {
    ApiRequestBuilder apiRequestBuilder;

    @BeforeTest
    public void createBasicRequest() {
        apiRequestBuilder = new ApiRequestBuilder();
        apiRequestBuilder.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"));
    }

    @BeforeMethod(onlyForGroups = "getRequest")
    public void addGetTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.GET);
    }

    @BeforeMethod(onlyForGroups = "postRequest")
    public void addPostTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.POST);
    }

    @BeforeMethod(onlyForGroups = "putRequest")
    public void addPutTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.PUT);
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
    }

    @Test(groups = "getRequest")
    public void getAllMembersOfAProjectTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/memberships")
                .pathParam("projectId", "2504465");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        apiResponse.getResponse().then().log().body();
    }

    @Test(groups = "getRequest")
    public void getAMemberTest() {
        apiRequestBuilder.endpoint("/projects/{projectId}/memberships/{memberId}")
                .pathParam("projectId", "2504465")
                .pathParam("memberId", "10933893");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Membership membership = apiResponse.getBody(Membership.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(membership.getKind(), "project_membership");
        apiResponse.validateBodySchema("schemas/membership.json");
    }

    @Test(groups = "postRequest")
    public void addAMemberToAProjectTest() throws JsonProcessingException {
        ProjectMembership projectMembership = new ProjectMembership();
        projectMembership.setEmail("marek13@sith.mil");
        projectMembership.setRole("member");
        apiRequestBuilder.endpoint("/projects/{projectId}/memberships")
                .pathParam("projectId", "2504465")
                .body(new ObjectMapper().writeValueAsString(projectMembership));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        apiResponse.getResponse().then().log().body();
    }

    @Test(groups = "putRequest")
    public void updateAMemberOfAProjectTest() throws JsonProcessingException {
        ProjectMembership projectMembership = new ProjectMembership();
        projectMembership.setRole("viewer");
        apiRequestBuilder.endpoint("/projects/{projectId}/memberships/{memberId}")
                .pathParam("projectId", "2504465")
                .pathParam("memberId", "10935515")
                .body(new ObjectMapper().writeValueAsString(projectMembership));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        apiResponse.getResponse().then().log().body();
    }

    @Test(groups = "deleteRequest")
    public void deleteAMemberOfAProject() {
        apiRequestBuilder.endpoint("/projects/{projectId}/memberships/{memberId}")
                .pathParam("projectId", "2505060")
                .pathParam("memberId", "10935515");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 204);
    }
}
