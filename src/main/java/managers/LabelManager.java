package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Label;

import static configuration.EnvVariablesPool.dotenv;

/**
 * helps to manage the creation and deletion of label.
 */
public class LabelManager {

    /**
     * Creates a label in a pivotal-tracker account.
     */
    public static Label create(String idProject) throws JsonProcessingException {
        Label label = new Label();
        label.setName("Story 7-P1");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint(dotenv.get(Endpoints.PROJECT_LABELS))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .body(new ObjectMapper().writeValueAsString(label));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(Label.class);
    }

    /**
     * Deletes a label in a pivotal-tracker account.
     */
    public static void delete(String idProject, String idLabel) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint(dotenv.get(Endpoints.PROJECT_LABEL))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .pathParam(PathParam.LABEL_ID, idLabel);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
