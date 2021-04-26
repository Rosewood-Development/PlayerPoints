package org.black_ixx.playerpoints.permissions;

import org.bukkit.permissions.Permissible;

/**
 * Enumeration of permission nodes used through the plugin. Allows for a
 * centralized location and combats typos.
 *
 * @author Mitsugaru
 */
public enum PermissionNode {

    GIVE("give"),
    GIVEALL("giveall"),
    TAKE("take"),
    LOOK("look"),
    PAY("pay"),
    SET("set"),
    RESET("reset"),
    ME("me"),
    LEAD("lead"),
    RELOAD("reload"),
    BROADCAST("broadcast");

    /**
     * Permission prefix.
     */
    private static final String prefix = "playerpoints.";

    /**
     * Individual node path.
     */
    private final String node;

    /**
     * Private constructor.
     *
     * @param node - Specific node.
     */
    PermissionNode(String node) {
        this.node = prefix + '.' + node;
    }

    /**
     * Checks if a Permissible entity has permission
     *
     * @param permissible The entity to check
     * @return true if has permission, otherwise false
     */
    public boolean check(Permissible permissible) {
        return permissible.hasPermission(prefix + this.node);
    }

}
