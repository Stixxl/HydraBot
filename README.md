# HydraBot

[![Build Status](https://www.jenkins.stiglmair.com/buildStatus/icon?job=HydraBot_CI_git)](https://www.jenkins.stiglmair.com/job/HydraBot_CI_git/)


Hello everybody,
The HydraBot is a small handcrafted bot designed for our own little server.
Any suggestions for improvements are greatly appreciated and will be answered swiftly.
In case you want to use this bot simply contact me and ill make sure it will be deployed.

Current Features:
  - Audioplayback with some dope ass audio
  - Audioplayback via http queries
  - Help Command, which displays possible actions
  - Smartass Tips
  - Ranking of users via their time spent online

Future Features:
  - Use of the League of Legends API
  - Integration Tests
  - Admin Commands
  - Token for REST Usage

To add the bot to your Discord server:
  - Initialize the Postgres Database with `psql --username=HydraBot --dbname=HydraBotDB --file=scripts/initdb.sql`
  - Create a new bot in the [Discord Apps Panel](https://discordapp.com/developers/applications/me)
  - Click the "Create a Bot User" button in the bot's dashboard
  - Reveal the "Token" under "Bot > App Bot User" and save it in `config.toml`
    as the `discord.token` key
  - Build and start the Hydra Bot
  - Visit https://discordapi.com/permissions.html and mark the following
    permissions:
      - Text Permissions: Read Messages
      - Text Permissions: Send Messages
      - Voice Permissions: View Channel
      - Voice Permissions: Connect
      - Voice Permissions: Speak
  - Insert the "Client ID" under "App Details" into the permissions builder
  - Visit the Link displayed in the permissions builder to add the bot to your server
