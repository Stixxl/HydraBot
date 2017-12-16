package com.stiglmair.hydra.main;
import com.moandjiezana.toml.Toml;
import com.stiglmair.hydra.utilities.UtilityMethods;
import java.io.File;
import java.io.IOException;

public class Config {

  public class Logging {
    public String folder = null;
  }

  public class Database {
    public String user = null;
    public String password = null;
  }

  public class Discord {
    public String token = null;
  }

  public class Webapi {
    public int port = 1337;
  }

  Logging logging = new Logging();
  Database database = new Database();
  Discord discord = new Discord();
  Webapi webapi = new Webapi();

  public void read(String filename) throws IOException {;
    Toml config = new Toml().read(new File(UtilityMethods.generatePath(filename)));
    logging.folder = config.getString("logging.folder");
    database.user = config.getString("database.user");
    database.password = config.getString("database.password");
    discord.token = config.getString("discord.token");
    webapi.port = Math.toIntExact(config.getLong("webapi.port", 1337l));
  }

}
