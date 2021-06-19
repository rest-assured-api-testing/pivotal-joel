package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.StoryComment;

import static configuration.EnvVariablesPool.dotenv;

/**
 * helps to manage the creation and deletion of story comment.
 */
public class StoryCommentManager {

    /**
     * Creates a story comment in a pivotal-tracker account.
     */
    public static StoryComment create(String idProject, String idStory) throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("A comment 12-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint(dotenv.get(Endpoints.STORY_COMMENTS))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .pathParam(PathParam.STORY_ID, idStory)
                .body(new ObjectMapper().writeValueAsString(storyComment));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(StoryComment.class);
    }

    /**
     * Creates a story comment in a pivotal-tracker account.
     */
    public static void delete(String idProject, String idStory, String idComment) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint(dotenv.get(Endpoints.STORY_COMMENT))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .pathParam(PathParam.STORY_ID, idStory)
                .pathParam(PathParam.COMMENT_ID, idComment);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
