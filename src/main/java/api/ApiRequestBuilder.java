package api;

import managers.Param;
import managers.Param2;

public class ApiRequestBuilder implements IBuilder {
    ApiRequest apiRequest = new ApiRequest();

    public ApiRequestBuilder baseUri(String baseUri) {
        apiRequest.setBaseUri(baseUri);
        return this;
    }

    public ApiRequestBuilder endpoint(String endpoint) {
        apiRequest.setEndpoint(endpoint);
        return this;
    }

    public ApiRequestBuilder body(String body) {
        apiRequest.setBody(body);
        return this;
    }

    public ApiRequestBuilder token(String token) {
        apiRequest.setToken(token);
        return this;
    }

    public ApiRequestBuilder method(Enum<ApiMethod> method) {
        apiRequest.setMethod(method);
        return this;
    }

    public ApiRequestBuilder header(final String header, final String value) {
        apiRequest.addHeader(header, value);
        return this;
    }

    public ApiRequestBuilder queryParam(final String param, final String value) {
        apiRequest.addQueryParam(param, value);
        return this;
    }

    public ApiRequestBuilder pathParam(final String param, final String value) {
        apiRequest.addPathParam(param, value);
        return this;
    }

    public ApiRequestBuilder pathParam(String param, final Long value) {
        return pathParam(param, value.toString());
    }

    @Override
    public ApiRequest build() {
        return apiRequest;
    }
}
