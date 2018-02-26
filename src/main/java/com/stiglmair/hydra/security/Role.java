package com.stiglmair.hydra.security;

import com.stiglmair.hydra.main.Main;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

/**
 * Class that maps discord roles to roles for this bot and handles Authorization.
 *
 * @author PogChamp
 */
public class Role {

    public enum ROLE {
        ADMIN, MEMBER, ANY
    }

    /**
     * gets the internal role for this bot derived from the roles in discord
     * itself
     *
     * @param user IUser for which Role is needed
     * @param guild guild from which request came
     * @return a ROLE that symbolises the Permissions for this bot
     */
    public static ROLE getRole(IUser user, IGuild guild) {
        ROLE finalRole = ROLE.ANY;
        for (IRole role : user.getRolesForGuild(guild)) {
            String name = role.getName().toUpperCase();
            if (name.equals("JOHANN SCHMIDT") || name.equals("HEAD'S COUNCIL")) {//TODO make bot independent of name of roles and instead dependent on permissions
                finalRole = ROLE.ADMIN; // no higher Role possible; therefore we can break;
                break;
            } else if (name.equals("MARVEL FANBOIS")) {
                finalRole = ROLE.MEMBER;
            }
        }
        return finalRole;
    }

    /**
     * authorizes an given user for a given action; will log result
     * and prints out a failure message, if access was denied
     * @param user IUser which requires Authorization
     * @param guild IGuild for which Authorization is required
     * @param requiredRole Level of Authority that is required @see com.corbi.robot.actions.Chat#sendMessage(sx.blah.discord.handle.obj.IChannel, java.lang.String)
     * @return true if user is authorized; false otherwise
     */
    public static boolean authorize(IUser user, IGuild guild, ROLE requiredRole) {
        if (requiredRole.equals(getRole(user, guild))) {
            Main.logger.info("Authorized access for user with ID: {}", user.getLongID());
            return true;
        } else {
            Main.logger.warn("Unauthorized access for user with ID: {}", user.getLongID());
            return false;
        }
    }
}
