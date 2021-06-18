package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Epic;

import static configuration.EnvVariablesPool.dotenv;

public class EpicManager {

    public static Epic createEpic(String idProject) throws JsonProcessingException {
        Epic epic = new Epic();
        epic.setName("Epic 12-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("/projects/{projectId}/epics")
                .pathParam("projectId", idProject)
                .body(new ObjectMapper().writeValueAsString(epic));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(Epic.class);
    }

    public static void deleteEpic(String idProject, String idEpic) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/projects/{projectId}/epics/{epicId}")
                .pathParam("projectId", idProject)
                .pathParam("epicId", idEpic);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
