import api.*;
import entities.Account;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static configuration.EnvVariablesPool.dotenv;

public class AccountsTest {
    ApiRequestBuilder apiRequestBuilder;

    public void createBasicRequest() {
        apiRequestBuilder = new ApiRequestBuilder();
        apiRequestBuilder.header("X-TrackerToken", dotenv.get("TOKEN"))
                .baseUri(dotenv.get("BASE_URL"));
    }

    @BeforeMethod(onlyForGroups = "getRequest")
    public void addGetTypeToRequest() {
        createBasicRequest();
        apiRequestBuilder.method(ApiMethod.GET);
    }

    @Test(groups = "getRequest")
    public void getOwnInformationTest() {
        apiRequestBuilder.endpoint("/me");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }

    @Test(groups = "getRequest")
    public void getAAccountTest() {
        apiRequestBuilder.endpoint("/accounts/{accountId}")
                .pathParam("accountId", "1155181");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        Account account = apiResponse.getBody(Account.class);

        Assert.assertEquals(apiResponse.getStatusCode(), 200);
        Assert.assertEquals(account.getKind(), "account");
        apiResponse.validateBodySchema("schemas/account.json");

    }

    @Test(groups = "getRequest")
    public void getAllAccountsTest() {
        apiRequestBuilder.endpoint("/accounts");

        ApiResponse apiResponse = ApiManager.execute(apiRequestBuilder.build());

        apiResponse.getResponse().then().log().body();
        Assert.assertEquals(apiResponse.getStatusCode(), 200);
    }
}
