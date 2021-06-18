package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;

import static configuration.EnvVariablesPool.dotenv;

public class ProjectManager {
    public static Project create() throws JsonProcessingException {
        Project project = new Project();
        project.setName("Test Project");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint("/projects")
                .body(new ObjectMapper().writeValueAsString(project));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(Project.class);
    }

    public static void delete(String idProject) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint("/projects/{projectId}")
                .pathParam("projectId", idProject);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
