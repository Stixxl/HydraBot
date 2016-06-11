/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;
import com.corbi.robot.main.Main;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;

/**
 *
 * @author Stiglmair
 */
public class EventListener {

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        System.out.println("Discord4j is ready.");
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        InputParser.parseInput(event.getMessage());
    }
}
