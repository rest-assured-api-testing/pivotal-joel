package configuration;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvVariablesPool {

    /**
     * Constant that refers path of environment variables file.
     */
    public static Dotenv dotenv = Dotenv.configure().filename(".env.develop").ignoreIfMalformed().ignoreIfMissing().load();
}
