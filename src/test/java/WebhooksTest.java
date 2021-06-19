import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Project;
import entities.Webhook;
import managers.*;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class WebhooksTest {
    ApiRequestBuilder apiRequestBuilder;
    Project createdProject;
    Webhook createdWebhook;

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
        createdWebhook = WebhookManager.createStory(createdProject.getId().toString());
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
        createdWebhook = WebhookManager.createStory(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdWebhook = WebhookManager.createStory(createdProject.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest",
            "deleteRequest", "postBadRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    @Test(groups = "getRequest")
    public void getAllLabelsOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test(groups = "getRequest")
    public void getALabelOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.WEBHOOK_ID, createdWebhook.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Webhook webhook = apiResponse.getBody(Webhook.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(webhook.getKind(), "webhook");
    }

    @Test(groups= "postRequest")
    public void createALabelToAProjectTest() throws JsonProcessingException {
        Webhook webhook = new Webhook();
        webhook.setEnabled(true);
        webhook.setWebhook_url("https://pastebin2.com/fred");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(webhook));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        createdWebhook = apiResponse.getBody(Webhook.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdWebhook.isEnabled(), true);
        Assert.assertEquals(createdWebhook.getWebhook_url(), "https://pastebin2.com/fred");

    }

    @Test(groups = "putRequest")
    public void updateALabelToAProjectTest() throws JsonProcessingException {
        Webhook webhook = new Webhook();
        webhook.setWebhook_url("https://pastebin3.com/fred");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.WEBHOOK_ID, createdWebhook.getId())
                .body(new ObjectMapper().writeValueAsString(webhook));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());
        Webhook createdWebhook = apiResponse.getBody(Webhook.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(createdWebhook.getWebhook_url(), "https://pastebin3.com/fred");
    }

    @Test(groups = "deleteRequest")
    public void deleteALabelToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.WEBHOOK_ID, createdWebhook.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }




    @Test(groups = "getRequest")
    public void doNotGetAllLabelsOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOKS))
                .pathParam(PathParam.PROJECT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "getRequest")
    public void doNotGetALabelOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.WEBHOOK_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = "postBadRequest")
    public void doNotCreateALabelToAProjectTest() throws JsonProcessingException {
        Webhook webhook = new Webhook();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(webhook));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(groups = "putRequest")
    public void doNotUpdateALabelToAProjectTest2() throws JsonProcessingException {
        Webhook webhook = new Webhook();
        webhook.setEnabled(true);
        webhook.setWebhook_url("https://pastebin2.com/fred");
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.WEBHOOK_ID, "")
                .body(new ObjectMapper().writeValueAsString(webhook));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteALabelToAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.WEBHOOK_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
