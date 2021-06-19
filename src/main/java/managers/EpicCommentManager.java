package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.EpicComment;

import static configuration.EnvVariablesPool.dotenv;

/**
 * helps to manage the creation and deletion of epic comment.
 */
public class EpicCommentManager {

    /**
     * Creates a epic comment in a pivotal-tracker account.
     */
    public static EpicComment create(String idProject, String idEpic) throws JsonProcessingException {
        EpicComment epicComment = new EpicComment();
        epicComment.setText("A comment 12-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint(dotenv.get(Endpoints.EPIC_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .pathParam(PathParam.EPIC_ID, idEpic)
                .body(new ObjectMapper().writeValueAsString(epicComment));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(EpicComment.class);
    }

    /**
     * Deletes a epic comment in a pivotal-tracker account.
     */
    public static void delete(String idProject, String idEpic, String idComment) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint(dotenv.get(Endpoints.EPIC_COMMENT))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .pathParam(PathParam.EPIC_ID, idEpic)
                .pathParam(PathParam.COMMENT_ID, idComment);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
