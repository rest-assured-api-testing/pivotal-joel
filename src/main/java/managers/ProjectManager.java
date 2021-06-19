package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;

import static configuration.EnvVariablesPool.dotenv;

/**
 * helps to manage the creation and deletion of project.
 */
public class ProjectManager {

    /**
     * Creates a project with any name in a pivotal-tracker account.
     */
    public static Project create(String name) throws JsonProcessingException {
        Project project = new Project();
        project.setName(name);
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint(dotenv.get(Endpoints.PROJECTS))
                .body(new ObjectMapper().writeValueAsString(project));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(Project.class);
    }

    /**
     * Creates a project with a same name in a pivotal-tracker account.
     */
    public static Project create() throws JsonProcessingException {
        return create("Test Project 1");
    }

    /**
     * Deletes a project in a pivotal-tracker account.
     */
    public static void delete(String idProject) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint(dotenv.get(Endpoints.PROJECT))
                .pathParam(PathParam.PROJECT_ID, idProject);

        ApiManager.execute(apiRequestBuilder1.build());
    }

}
