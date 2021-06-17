package configuration;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvVariablesPool {
    public static Dotenv dotenv = Dotenv.configure().filename(".env.develop").ignoreIfMalformed().ignoreIfMissing().load();
}
