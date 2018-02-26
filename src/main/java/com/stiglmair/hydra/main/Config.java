package com.stiglmair.hydra.main;

public class Config {

    public class Logging {
        public String folder = null;
    }

    public class Database {
        public String host = "localhost";
        public int port = 0;
        public String name = "HydraBotDB";
        public String schema = "HydraBotDB";
        public String user = null;
        public String password = null;
    }

    public class Discord {
        public String token = null;
    }

    public class Webapi {
        public String listenAddress = "127.0.0.1";
        public int port = 1337;
    }

    Logging logging = new Logging();
    Database database = new Database();
    Discord discord = new Discord();
    Webapi webapi = new Webapi();
}
