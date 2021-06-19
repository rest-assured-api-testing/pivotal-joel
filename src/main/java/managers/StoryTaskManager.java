package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.StoryTask;

import static configuration.EnvVariablesPool.dotenv;

public class StoryTaskManager {

    public static StoryTask createStoryTask(String idProject, String idStory) throws JsonProcessingException {
        StoryTask storyTask = new StoryTask();
        storyTask.setDescription("Task 1-S7");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("projects/{projectId}/stories/{storyId}/tasks")
                .pathParam("projectId", idProject)
                .pathParam("storyId", idStory)
                .body(new ObjectMapper().writeValueAsString(storyTask));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(StoryTask.class);
    }

    public static void deleteStoryTask(String idProject, String idStory, String idStoryTask) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/projects/{projectId}/stories/{storyId}/tasks/{taskId}")
                .pathParam("projectId", idProject)
                .pathParam("storyId", idStory)
                .pathParam("taskId", idStoryTask);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
