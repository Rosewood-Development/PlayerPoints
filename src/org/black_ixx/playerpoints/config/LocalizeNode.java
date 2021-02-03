package org.black_ixx.playerpoints.config;

/**
 * Configuration nodes for the localization messages config.
 * 
 * @author Mitsugaru
 */
public enum LocalizeNode {

    /**
     * General
     */
    PERMISSION_DENY("message.noPermission",
            "&7%tag &cLack permission: &b%extra"),
    CONSOLE_DENY("message.noConsole", "&7%tag &cCannot use command as console"),
    NOT_INTEGER("message.notIntenger", "&7%tag &6%extra &cis not an integer"),
    RELOAD("message.reload", "&7%tag &6Configuration reloaded"),
    BROADCAST("message.broadcast",
            "&7%tag &9Player &a%player &9has &a%amount &9Points"),
    /**
     * Command
     */
    COMMAND_UNKNOWN("message.command.unknown",
            "&7%tag &cUnknown command '%extra'"),
    COMMAND_GIVE("message.command.give",
            "&7%tag &9/points give <name> <points>"),
    COMMAND_GIVEALL("message.command.giveall", "&7%tag &9/points giveall <points>"),
    COMMAND_TAKE("message.command.take",
            "&7%tag &9/points take <name> <points>"),
    COMMAND_LOOK("message.command.look", "&7%tag &9/points look <name>"),
    COMMAND_PAY("message.command.pay", "&7%tag &9/points give <name> <points>"),
    COMMAND_SET("message.command.set", "&7%tag &9/points set <name> <points>"),
    COMMAND_RESET("message.command.reset", "&7%tag &9/points reset <name>"),
    COMMAND_ME("message.command.me", "&7%tag &9/points me"),
    COMMAND_LEAD("message.command.lead", "&7%tag &9/points lead [next|prev|#]"),
    COMMAND_BROADCAST("message.command.broadcast",
            "&7%tag &9/points broadcast <name>"),
    /**
     * Points
     */
    POINTS_SUCCESS("message.points.success",
            "&7%tag &9Player &a%player &9now has &a%amount &9Points"),
    POINTS_SUCCESS_ALL("message.points.successall",
            "&7%tag Gave &a%amount &9Points &7to %player players"),
    POINTS_FAIL("message.points.fail", "&7%tag &cTransaction failed"),
    POINTS_FAIL_ALL("message.points.failall",
            "&7%tag &cFailed to give &a%amount &9Points &cto %player players"),
    POINTS_LOOK("message.points.look",
            "&7%tag &9Player &a%player &9has &a%amount &9Points"),
    POINTS_PAY_SEND("message.points.pay.send",
            "&7%tag &9You have sent &a%amount &9Points to &a%player"),
    POINTS_PAY_RECEIVE("message.points.pay.receive",
            "&7%tag &9You have received &a%amount &9Points from &a%player"),
    POINTS_PAY_INVALID("message.points.pay.invalid",
            "&7%tag &6Cannot pay 0 or negative points."),
    POINTS_LACK("message.points.lack",
            "&7%tag &6You do not have enough Points!"),
    POINTS_RESET("message.points.reset",
            "&7%tag The points of &a%player &9was successfully reset"),
    POINTS_ME("message.points.me", "&7%tag &9You have &a%amount &9Points"),
    /**
     * Help
     */
    HELP_HEADER("message.help.header", "&9======= &7%tag &9======="),
    HELP_ME("message.help.me", "&7/points me &6: Show current points"),
    HELP_GIVE("message.help.give",
            "&7/points give <name> <points> &6: Generate points for given player"),
    HELP_GIVEALL("message.help.giveall",
            "&7/points giveall <points> &6: Generate points for online players"),
    HELP_TAKE("message.help.take",
            "&7/points take <name> <points> &6: Take points from player"),
    HELP_LOOK("message.help.look",
            "&7/points look <name> &6: Lookup player's points"),
    HELP_SET("message.help.set",
            "&7/points set <name> <points> &6: Set player's points to amount"),
    HELP_RESET("message.help.reset",
            "&7/points reset <name> &6: Reset player's points to 0"),
    HELP_PAY("message.help.pay",
            "&7/points pay <name> <points> &6: Send points to given player"),
    HELP_LEAD("message.help.lead",
            "&7/points lead [prev/next/page] &6: Leader board"),
    HELP_BROADCAST("message.help.broadcast",
            "&7/points broadcast <name> &6: Broadcast player's points"),
    HELP_RELOAD("message.help.reload", "&7/points reload &6: Reload config");

    /**
     * Path and default value.
     */
    private final String path;
    private final String def;

    /**
     * Private constructor.
     * 
     * @param path
     *            - Path in config.
     * @param def
     *            - Default value to use.
     */
    LocalizeNode(String path, String def) {
        this.path = path;
        this.def = def;
    }

    /**
     * Get the configuration node path.
     * 
     * @return Config path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the default value.
     * 
     * @return Default value.
     */
    public String getDefaultValue() {
        return def;
    }
}