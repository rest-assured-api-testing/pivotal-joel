import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Label;
import entities.Project;
import managers.*;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class LabelsTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Label createdLabel;

    public void createBasicRequest() {
        apiRequestBuilder = new ApiRequestBuilder();
        apiRequestBuilder.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"));
    }

    @BeforeMethod(onlyForGroups = "getRequest")
    public void addGetTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.GET);
        createdProject = ProjectManager.create();
        createdLabel = LabelManager.createStory(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = {"postRequest", "postBadRequest"})
    public void addPostTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.POST);
        createdProject = ProjectManager.create();
    }

    @BeforeMethod(onlyForGroups = "putRequest")
    public void addPutTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.PUT);
        createdProject = ProjectManager.create();
        createdLabel = LabelManager.createStory(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdLabel = LabelManager.createStory(createdProject.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest",
            "deleteRequest", "postBadRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    @Test(groups = "getRequest")
    public void getAllLabelsOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_LABELS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test(groups = "getRequest")
    public void getALabelOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_LABEL))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.LABEL_ID, createdLabel.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Label label = apiResponse.getBody(Label.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(label.getKind(), "label");
    }

    @Test(groups= "postRequest")
    public void createALabelToAProjectTest() throws JsonProcessingException {
        Label label = new Label();
        label.setName("label 4-p1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_LABELS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(label));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdLabel = apiResponse.getBody(Label.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdLabel.getName(), "label 4-p1");
    }

    @Test(groups = "putRequest")
    public void updateALabelToAProjectTest() throws JsonProcessingException {
        Label label = new Label();
        label.setName("label 5-p1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_LABEL))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.LABEL_ID, createdLabel.getId())
                .body(new ObjectMapper().writeValueAsString(label));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Label createdLabel = apiResponse.getBody(Label.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdLabel.getName(), "label 5-p1");
    }

    @Test(groups = "deleteRequest")
    public void deleteALabelToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_LABEL))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.LABEL_ID, createdLabel.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }




    @Test(groups = "getRequest")
    public void doNotGetAllLabelsOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_LABELS))
                .pathParam(PathParam.PROJECT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void doNotGetALabelOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_LABEL))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.LABEL_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "postBadRequest")
    public void doNotCreateALabelToAProjectTest() throws JsonProcessingException {
        Label label = new Label();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_LABELS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(label));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "putRequest")
    public void doNotUpdateALabelToAProjectTest2() throws JsonProcessingException {
        Label label = new Label();
        label.setName("Story 5-P1");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_LABEL))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.LABEL_ID, "")
                .body(new ObjectMapper().writeValueAsString(label));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteALabelToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_LABEL))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.LABEL_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

}
