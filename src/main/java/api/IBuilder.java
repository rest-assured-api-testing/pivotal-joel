package api;

/**
 * Helps to build an ApiRequest entity.
 */
public interface IBuilder {
    <T> T build();
}
