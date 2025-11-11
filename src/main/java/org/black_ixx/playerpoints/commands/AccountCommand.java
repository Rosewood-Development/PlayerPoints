package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Collections;
import java.util.UUID;
import java.util.regex.Pattern;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.commands.arguments.StringSuggestingArgumentHandler;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.util.PointsUtils;

public class AccountCommand extends BasePointsCommand {

    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^\\*?\\w{3,16}$");

    public AccountCommand(PlayerPoints playerPoints) {
        super(playerPoints);
    }

    public static class CreateCommand extends BasePointsCommand {

        public CreateCommand(PlayerPoints playerPoints) {
            super(playerPoints);
        }

        @RoseExecutable
        public void execute(CommandContext context, String accountName) {
            DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
            String prefixedAccountName = accountName.startsWith("*") ? accountName : "*" + accountName;

            if (!ACCOUNT_PATTERN.matcher(accountName).matches()) {
                this.localeManager.sendCommandMessage(context.getSender(), "command-account-create-name-invalid");
                return;
            }

            this.rosePlugin.getScheduler().runTaskAsync(() -> {
                if (dataManager.lookupCachedUUID(prefixedAccountName) != null) {
                    this.localeManager.sendCommandMessage(context.getSender(), "command-account-create-exists", StringPlaceholders.of("account", accountName));
                    return;
                }

                dataManager.createNonPlayerAccount(prefixedAccountName);
                this.localeManager.sendCommandMessage(context.getSender(), "command-account-create-success", StringPlaceholders.of("account", prefixedAccountName));
            });
        }

        @Override
        protected CommandInfo createCommandInfo() {
            return CommandInfo.builder("create")
                    .arguments(ArgumentsDefinition.builder()
                            .required("target", new StringSuggestingArgumentHandler(x -> Collections.singletonList("*NewAccountName")))
                            .build())
                    .build();
        }

    }

    public static class DeleteCommand extends BasePointsCommand {

        public DeleteCommand(PlayerPoints playerPoints) {
            super(playerPoints);
        }

        @RoseExecutable
        public void execute(CommandContext context, String accountName, String confirm) {
            DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
            UUID accountID = dataManager.lookupCachedUUID(accountName);
            if (accountID == null) {
                this.localeManager.sendCommandMessage(context.getSender(), "command-account-delete-does-not-exist", StringPlaceholders.of("account", accountName));
                return;
            }

            if (confirm == null) {
                this.localeManager.sendCommandMessage(context.getSender(), "command-account-delete-warning", StringPlaceholders.of("account", accountName));
                return;
            }

            this.rosePlugin.getScheduler().runTaskAsync(() -> {
                dataManager.deleteAccount(accountID);
                this.localeManager.sendCommandMessage(context.getSender(), "command-account-delete-success", StringPlaceholders.of("account", accountName));
            });
        }

        @Override
        protected CommandInfo createCommandInfo() {
            return CommandInfo.builder("delete")
                    .arguments(ArgumentsDefinition.builder()
                            .required("target", new StringSuggestingArgumentHandler(PointsUtils::getPlayerTabComplete))
                            .optional("confirm", ArgumentHandlers.forValues(String.class, "confirm"))
                            .build())
                    .build();
        }

    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("account")
                .descriptionKey("command-account-description")
                .permission("playerpoints.account")
                .arguments(ArgumentsDefinition.builder()
                        .requiredSub("operation", new CreateCommand(this.playerPoints), new DeleteCommand(this.playerPoints)))
                .build();
    }

}
