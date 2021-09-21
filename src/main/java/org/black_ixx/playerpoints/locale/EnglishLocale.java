package org.black_ixx.playerpoints.locale;

import dev.rosewood.rosegarden.locale.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "Esophose";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<String, Object>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&7[<g:#E8A230:#ECD32D>PlayerPoints&7] ");

            this.put("#1", "Currency");
            this.put("currency-singular", "Point");
            this.put("currency-plural", "Points");
            this.put("currency-separator", ",");

            this.put("#2", "Misc");
            this.put("no-permission", "&cYou don't have permission for that!");
            this.put("no-console", "&cOnly players may execute this command.");
            this.put("invalid-amount", "&cAmount must be a positive whole number.");
            this.put("unknown-player", "&cPlayer could not be found: &b%player%");
            this.put("unknown-command", "&cUnknown command: &b%input%");
            this.put("votifier-voted", "&eThanks for voting on %service%! &b%amount% &ehas been added to your balance.");

            this.put("#3", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eUse &b/points help &efor command information.");

            this.put("#4", "Help Command");
            this.put("command-help-description", "&8 - &d/points help &7- Displays the help menu... You have arrived");
            this.put("command-help-title", "&eAvailable Commands:");

            this.put("#5", "Give Command");
            this.put("command-give-description", "&8 - &d/points give &7- Give a player points");
            this.put("command-give-usage", "&cUsage: &e/points give <player> <amount>");
            this.put("command-give-success", "&b%player% &awas given &b%amount% &e%currency%.");
            this.put("command-give-received", "&eYou have received &b%amount% &e%currency%.");

            this.put("#6", "Give All Command");
            this.put("command-giveall-description", "&8 - &d/points giveall &7- Gives all online players points");
            this.put("command-giveall-usage", "&cUsage: &e/points giveall <amount> [*]");
            this.put("command-giveall-success", "&aGave &b%amount% &a%currency% to all online players.");

            this.put("#7", "Take Command");
            this.put("command-take-description", "&8 - &d/points take &7- Take points from a player");
            this.put("command-take-usage", "&cUsage: &e/points take <player> <amount>");
            this.put("command-take-success", "&aTook &b%amount% &a%currency% from &b%player%&a.");
            this.put("command-take-lacking-funds", "&b%player% &cdoes not have enough %currency% for that, so their balance was set to 0.");

            this.put("#8", "Look Command");
            this.put("command-look-description", "&8 - &d/points look &7- View a player's points");
            this.put("command-look-usage", "&cUsage: &e/points look <player>");
            this.put("command-look-success", "&b%player% &ehas &b%amount% &e%currency%.");

            this.put("#9", "Pay Command");
            this.put("command-pay-description", "&8 - &d/points pay &7- Pay a player");
            this.put("command-pay-usage", "&cUsage: &e/points pay <player> <amount>");
            this.put("command-pay-sent", "&aYou paid &b%player% %amount% &a%currency%.");
            this.put("command-pay-received", "&eYou were paid &b%amount% &e%currency% by &b%player%&e.");
            this.put("command-pay-lacking-funds", "&cYou do not have enough %currency% for that.");

            this.put("#10", "Set Command");
            this.put("command-set-description", "&8 - &d/points set &7- Set a player's points");
            this.put("command-set-usage", "&cUsage: &e/points set <player> <amount>");
            this.put("command-set-success", "&aSet the %currency% of &b%player% &ato &b%amount%&a.");

            this.put("#11", "Reset Command");
            this.put("command-reset-description", "&8 - &d/points reset &7- Reset a player's points");
            this.put("command-reset-usage", "&cUsage: &e/points reset <player>");
            this.put("command-reset-success", "&aReset the %currency% for &b%player%&a.");

            this.put("#12", "Me Command");
            this.put("command-me-description", "&8 - &d/points me &7- View your points");
            this.put("command-me-usage", "&cUsage: &d/points me");
            this.put("command-me-success", "&eYou have &b%amount% &e%currency%.");

            this.put("#13", "Lead Command");
            this.put("command-lead-description", "&8 - &d/points lead &7- View the leaderboard");
            this.put("command-lead-usage", "&cUsage: &e/points lead [next|prev|#]");
            this.put("command-lead-title", "&eLeaderboard &7(Page #%page%/%pages%)");
            this.put("command-lead-entry", "&b%position%). &e%player% &7- &6%amount% %currency%");

            this.put("#14", "Broadcast Command");
            this.put("command-broadcast-description", "&8 - &d/points broadcast &7- Broadcast a player's points");
            this.put("command-broadcast-usage", "&cUsage: &e/points broadcast <player>");
            this.put("command-broadcast-message", "&b%player% &ehas &b%amount% &e%currency%.");

            this.put("#15", "Reload Command");
            this.put("command-reload-description", "&8 - &d/points reload &7- Reloads the plugin");
            this.put("command-reload-usage", "&cUsage: &e/points reload");
            this.put("command-reload-success", "&aConfiguration and locale files were reloaded.");

            this.put("#16", "Export Command");
            this.put("command-export-description", "&8 - &d/points export &7- Exports the data to storage.yml");
            this.put("command-export-usage", "&cUsage: &e/points export");
            this.put("command-export-success", "&aSave data has been exported to storage.yml.");
            this.put("command-export-warning", "&cNotice: A storage.yml file already exists. If you would like to overwrite it, use &b/points export confirm&c.");

            this.put("#17", "Import Command");
            this.put("command-import-description", "&8 - &d/points import &7- Imports the data from storage.yml");
            this.put("command-import-usage", "&cUsage: &e/points import");
            this.put("command-import-success", "&aSave data has been imported from storage.yml.");
            this.put("command-import-no-backup", "&cUnable to import, storage.yml does not exist. You can generate one with &b/points export &cand use it to transfer data between database types.");
            this.put("command-import-warning", "&cNotice: This operation will delete all data from the active database and replace it with the contents of storage.yml. " +
                    "&cThe currently active database type is &b&o&l%type%&c. " +
                    "&cIf you are absolutely sure about this, use &b/points import confirm&c.");

            this.put("#18", "Convert Command");
            this.put("command-convert-description", "&8 - &d/points convert &7- Loads currency data from another plugin");
            this.put("command-convert-usage", "&cUsage: &e/points convert <plugin>");
            this.put("command-convert-invalid", "&b%plugin% &cis not a convertible currency plugin name.");
            this.put("command-convert-success", "&aCurrency data from &b%plugin% &ahas been converted.");
            this.put("command-convert-failure", "&cAn error occurred while attempting to convert the data. " +
                    "Please check your console and report any errors to the PlayerPoints plugin author.");
            this.put("command-convert-warning", "&cNotice: This operation will delete all data from the active database and replace it with the data from &b%plugin%&c. " +
                    "&cIf you are absolutely sure about this, use &b/points convert %plugin% confirm&c.");

            this.put("#19", "Import Legacy Command");
            this.put("command-importlegacy-description", "&8 - &d/points importlegacy &7- Import a legacy table");
            this.put("command-importlegacy-usage", "&cUsage: &e/points importlegacy <table>");
            this.put("command-importlegacy-success", "&aSuccessfully imported legacy data from &b%table%&a.");
            this.put("command-importlegacy-failure", "&cFailed to import legacy data from &b%table%&c. Does the table exist?");
            this.put("command-importlegacy-only-mysql", "&cThis command is only available when you have MySQL enabled.");

            this.put("#20", "Version Command");
            this.put("command-version-description", "&8 - &d/points version &7- Display the version info for PlayerPoints");
        }};
    }

}
