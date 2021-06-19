package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Label;

import static configuration.EnvVariablesPool.dotenv;

public class LabelManager {
    public static Label createStory(String idProject) throws JsonProcessingException {
        Label label = new Label();
        label.setName("Story 7-P1");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("/projects/{projectId}/labels")
                .pathParam("projectId", idProject)
                .body(new ObjectMapper().writeValueAsString(label));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(Label.class);
    }

    public static void deleteStory(String idProject, String idLabel) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/projects/{projectId}/labels/{labelId}")
                .pathParam("projectId", idProject)
                .pathParam("labelId", idLabel);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
