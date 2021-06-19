package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Story;

import static configuration.EnvVariablesPool.dotenv;

public class StoryManager {
    public static Story createStory(String idProject) throws JsonProcessingException {
        Story story = new Story();
        story.setName("Story 7-P1");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint(dotenv.get(Endpoints.PROJECT_STORIES))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .body(new ObjectMapper().writeValueAsString(story));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(Story.class);
    }

    public static void deleteStory(String idStory) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint(dotenv.get(Endpoints.STORY))
                .pathParam(PathParam.STORY_ID, idStory);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
