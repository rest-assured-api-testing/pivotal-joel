package configuration;

import io.github.cdimascio.dotenv.Dotenv;

public class FilePath {
    public static Dotenv dotenv = Dotenv.configure().filename(".env.develop").ignoreIfMalformed().ignoreIfMissing().load();
}
