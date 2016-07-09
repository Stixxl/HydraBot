

                    CREATE TABLE HydraBot.USERS
                    (ID varchar(255) NOT NULL, 
                    GUILD_ID varchar(255) NOT NULL, 
                    UPTIME bigint NOT NULL, 
                    PRIMARY KEY (ID, GUILD_ID))              

                    CREATE TABLE  HydraBot.GAMES
                    (TITLE varchar(255) NOT NULL, 
                    ID varchar(255) not NULL, 
                    GUILD_ID varchar(255) NOT NULL, 
                    TIME_PLAYED bigint NOT NULL, 
                    AMOUNT_PLAYED int NOT NULL, 
                    FOREIGN KEY (ID, GUILD_ID) references  HydraBot.USERS ON DELETE CASCADE,
                    PRIMARY KEY(ID, GUILD_ID, TITLE))

                    CREATE TABLE HydraBot.SOUNDS
                    (name varchar(255) NOT NULL,
                    path varchar(511) NOT NULL,
                    PRIMARY KEY (name))
                    
