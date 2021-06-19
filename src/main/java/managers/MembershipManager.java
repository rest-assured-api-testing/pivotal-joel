package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Membership;
import entities.ProjectMembership;

import static configuration.EnvVariablesPool.dotenv;

public class MembershipManager {

    public static Membership createStoryTask(String idProject) throws JsonProcessingException {
        ProjectMembership projectMembership = new ProjectMembership();
        projectMembership.setEmail("marek15@sith.mil");
        projectMembership.setRole("member");
        ApiRequestBuilder apiRequestBuilder = new ApiRequestBuilder();
        apiRequestBuilder.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIPS))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .body(new ObjectMapper().writeValueAsString(projectMembership));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        return apiResponse.getBody(Membership.class);
    }

    public static void deleteStoryTask(String idProject, String idMember) {
        ApiRequestBuilder apiRequestBuilder = new ApiRequestBuilder();
        apiRequestBuilder.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint(dotenv.get(Endpoints.PROJECT_MEMBERSHIP))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .pathParam(PathParam.MEMBER_ID, idMember);

        ApiManager.execute(apiRequestBuilder.build());
    }
}
