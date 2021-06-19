package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.WorkSpace;

import static configuration.EnvVariablesPool.dotenv;

public class WorkSpaceManager {
    public static WorkSpace create() throws JsonProcessingException {
        WorkSpace workspace = new WorkSpace();
        workspace.setName("Test WorkSpace 1");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint(dotenv.get(Endpoints.WORKSPACES))
                .body(new ObjectMapper().writeValueAsString(workspace));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(WorkSpace.class);
    }

    public static void delete(String idProject) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint(dotenv.get(Endpoints.WORKSPACE))
                .pathParam(PathParam.WORKSPACE_ID, idProject);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
