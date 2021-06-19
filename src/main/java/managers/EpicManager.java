package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Epic;

import static configuration.EnvVariablesPool.dotenv;

/**
 * helps to manage the creation and deletion of epic.
 */
public class EpicManager {

    /**
     * Creates a epic in a pivotal-tracker account.
     */
    public static Epic create(String idProject) throws JsonProcessingException {
        Epic epic = new Epic();
        epic.setName("Epic 12-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint(dotenv.get(Endpoints.PROJECT_EPICS))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .body(new ObjectMapper().writeValueAsString(epic));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(Epic.class);
    }

    /**
     * Deletes a epic in a pivotal-tracker account.
     */
    public static void delete(String idProject, String idEpic) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint(dotenv.get(Endpoints.PROJECT_EPIC))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .pathParam(PathParam.EPIC_ID, idEpic);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
