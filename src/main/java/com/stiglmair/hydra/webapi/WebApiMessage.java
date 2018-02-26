package com.stiglmair.hydra.webapi;

import com.stiglmair.hydra.main.Main;
import com.vdurmont.emoji.Emoji;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceState;
import sx.blah.discord.util.MessageTokenizer;
import sx.blah.discord.util.cache.LongMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * This implements a potentially incomplete version of the IMessage interface.
 * The goal is that an IMessage can be created from the information provided to the
 * web interface. This IMessage is then used to execute the commands passed to the web interface.
 */
public class WebApiMessage implements IMessage {

    private final IUser user;

    /**
     * Creates the corresponding WebApiMessage object for a user.
     * @param user The user.
     */
    public WebApiMessage(IUser user) {
        this.user = user;
    }

    @Override
    public String getContent() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IChannel getChannel() {
        return user.getOrCreatePMChannel();
    }

    @Override
    public IUser getAuthor() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public LocalDateTime getTimestamp() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public List<IUser> getMentions() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public List<IRole> getRoleMentions() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public List<IChannel> getChannelMentions() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public List<Attachment> getAttachments() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public List<IEmbed> getEmbeds() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IMessage reply(String content) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IMessage reply(String content, EmbedObject embed) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IMessage edit(String content) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IMessage edit(String content, EmbedObject embed) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IMessage edit(EmbedObject embed) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public boolean mentionsEveryone() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public boolean mentionsHere() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public void delete() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public Optional<LocalDateTime> getEditedTimestamp() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public boolean isPinned() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IGuild getGuild() {
        LongMap<IVoiceState> voiceStates = user.getVoiceStates();
        Optional<IVoiceState> voiceStateOpt = voiceStates.values().stream().findFirst();
        if (voiceStates.values().size() > 1) {
            Main.logger.warn("User has more than one IVoiceState. Choosing the first one.");
        }
        return voiceStateOpt.map(IVoiceState::getGuild).orElse(null);
    }

    @Override
    public String getFormattedContent() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public List<IReaction> getReactions() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IReaction getReactionByIEmoji(IEmoji emoji) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IReaction getReactionByUnicode(String name) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IReaction getReactionByUnicode(Emoji emoji) {
        throw new RuntimeException("Method not implemented.");
    }


    @Override
    public void removeAllReactions() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public void addReaction(IReaction reaction) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public void addReaction(IEmoji emoji) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public void addReaction(String emoji) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public void addReaction(Emoji emoji) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public void removeReaction(IUser user, IReaction reaction) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public void removeReaction(IReaction reaction) {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public MessageTokenizer tokenize() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public boolean isDeleted() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public long getWebhookLongID() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IDiscordClient getClient() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IShard getShard() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IMessage copy() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public long getLongID() {
        throw new RuntimeException("Method not implemented.");
    }

    @Override
    public IReaction getReactionByEmoji(IEmoji iemoji) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IReaction getReactionByID(long l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IReaction getReactionByEmoji(ReactionEmoji re) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addReaction(ReactionEmoji re) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeReaction(IUser iuser, ReactionEmoji re) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeReaction(IUser iuser, IEmoji iemoji) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeReaction(IUser iuser, Emoji emoji) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeReaction(IUser iuser, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Type getType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSystemMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
