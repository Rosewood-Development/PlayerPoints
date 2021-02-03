package org.black_ixx.playerpoints.permissions;

import org.bukkit.command.CommandSender;

/**
 * Modified version to handle permissions. Removed Vault handling. Allows for
 * checking if a given CommandSender has a permission node by using the
 * PermissionNode enumeration.
 * 
 * @author Mitsugaru
 * 
 */
public class PermissionHandler {

    /**
     * Check if the CommandSender has the specified permission node.
     * 
     * @param sender
     *            who sent
     * @param node
     *            to check
     * @return True if sender has permission, else false. If sender is OP, then
     *         return true always.
     */
    public static boolean has(CommandSender sender, PermissionNode node) {
        if(sender.isOp()) {
            // Because sometimes ops might not have permission, even when set to
            // default to op
            return true;
        }
        return sender.hasPermission(node.getNode());
    }
}