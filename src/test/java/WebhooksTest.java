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

/**
 * Tests webhook endpoint of a pivotal-tracker account.
 */
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
        createdWebhook = WebhookManager.create(createdProject.getId().toString());
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
        createdWebhook = WebhookManager.create(createdProject.getId().toString());
    }

    @BeforeMethod(onlyForGroups = "deleteRequest")
    public void addDeleteTypeToRequest() throws JsonProcessingException {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.DELETE);
        createdProject = ProjectManager.create();
        createdWebhook = WebhookManager.create(createdProject.getId().toString());
    }

    @AfterMethod(onlyForGroups = {"getRequest", "postRequest", "putRequest", "deleteBadRequest",
            "deleteRequest", "postBadRequest"})
    public void cleanCreatedRequirements() {
        ProjectManager.delete(createdProject.getId().toString());
    }

    /**
     * Tests that webhook endpoint gives us all webhooks.
     */
    @Test(groups = "getRequest")
    public void getAllWebhooksOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    /**
     * Tests that webhook endpoint gives us a specific webhook.
     */
    @Test(groups = "getRequest")
    public void getAWebhookOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.WEBHOOK_ID, createdWebhook.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());
        Webhook webhook = apiResponse.getBody(Webhook.class);

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(webhook.getKind(), "webhook");
    }

    /**
     * Tests that webhook endpoint creates a webhook.
     */
    @Test(groups= "postRequest")
    public void createAWebhookToAProjectTest() throws JsonProcessingException {
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

    /**
     * Tests that webhook endpoint updates a specific webhook.
     */
    @Test(groups = "putRequest")
    public void updateAWebhookOfAProjectTest() throws JsonProcessingException {
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

    /**
     * Tests that webhook endpoint deletes a specific webhook.
     */
    @Test(groups = "deleteRequest")
    public void deleteAWebhookOfAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.WEBHOOK_ID, createdWebhook.getId());

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    /**
     * Tests that webhook endpoint gives us a not found status to respond a wrong url of getting all webhooks.
     */
    @Test(groups = "getRequest")
    public void doNotGetAllWebhooksOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOKS))
                .pathParam(PathParam.PROJECT_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that webhook endpoint gives us a not found status to respond to a getting request without
     * webhook id.
     */
    @Test(groups = "getRequest")
    public void doNotGetAWebhookOfAProjectTest() {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.WEBHOOK_ID, " ");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Tests that webhook endpoint gives us a bad request status to respond to a creating request without
     * webhook body.
     */
    @Test(groups = "postBadRequest")
    public void doNotCreateAWebhookToAProjectTest() throws JsonProcessingException {
        Webhook webhook = new Webhook();
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOKS))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .body(new ObjectMapper().writeValueAsString(webhook));

        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Tests that webhook endpoint gives us a not found status to respond to a updating request without
     * a specific webhook id.
     */
    @Test(groups = "putRequest")
    public void doNotUpdateAWebhookOfAProjectTest() throws JsonProcessingException {
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

    /**
     * Tests that webhook endpoint gives us a not found status to respond to a deleting request without
     * a specific webhook id.
     */
    @Test(groups = {"deleteRequest", "deleteBadRequest"})
    public void doNotDeleteAWebhookOfAProjectTest() throws JsonProcessingException {
        apiRequestBuilder.endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, createdProject.getId())
                .pathParam(PathParam.WEBHOOK_ID, "");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }
}
