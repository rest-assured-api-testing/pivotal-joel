package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Story;
import entities.StoryComment;

import static configuration.EnvVariablesPool.dotenv;

public class StoryCommentManager {

    public static StoryComment createStoryComment(String idProject, String idStory) throws JsonProcessingException {
        StoryComment storyComment = new StoryComment();
        storyComment.setText("A comment 12-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("/projects/{projectId}/stories/{storyId}/comments")
                .pathParam("projectId", idProject)
                .pathParam("storyId", idStory)
                .body(new ObjectMapper().writeValueAsString(storyComment));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(StoryComment.class);
    }

    public static void deleteStoryComment(String idProject, String idStory, String idComment) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/projects/{projectId}/stories/{storyId}/comments/{commentId}")
                .pathParam("projectId", idProject)
                .pathParam("storyId", idStory)
                .pathParam("commentId", idComment);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
