package managers;

import api.ApiManager;
import api.ApiMethod;
import api.ApiRequestBuilder;
import api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Webhook;

import static configuration.EnvVariablesPool.dotenv;

/**
 * helps to manage the creation and deletion of webhook.
 */
public class WebhookManager {

    /**
     * Creates a webhook in a pivotal-tracker account.
     */
    public static Webhook create(String idProject) throws JsonProcessingException {
        Webhook webhook = new Webhook();
        webhook.setEnabled(true);
        webhook.setWebhook_url("https://pastebin2.com/fred");
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.POST)
                .endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOKS))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .body(new ObjectMapper().writeValueAsString(webhook));
        ApiResponse apiResponse = ApiManager.executeWithBody(apiRequestBuilder1.build());
        return apiResponse.getBody(Webhook.class);
    }

    /**
     * Deletes a webhook in a pivotal-tracker account.
     */
    public static void delete(String idProject, String idWebhook) {
        ApiRequestBuilder apiRequestBuilder1 = new ApiRequestBuilder();
        apiRequestBuilder1.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"))
                .method(ApiMethod.DELETE)
                .endpoint(dotenv.get(Endpoints.PROJECT_WEBHOOK))
                .pathParam(PathParam.PROJECT_ID, idProject)
                .pathParam(PathParam.WEBHOOK_ID, idWebhook);

        ApiManager.execute(apiRequestBuilder1.build());
    }
}
