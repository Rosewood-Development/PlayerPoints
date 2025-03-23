package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.commands.arguments.StringSuggestingArgumentHandler;
import org.black_ixx.playerpoints.manager.DataManager;
import org.black_ixx.playerpoints.util.PointsUtils;

import java.util.UUID;

public class AccountCommand extends BasePointsCommand {

  public AccountCommand(PlayerPoints playerPoints) { super(playerPoints); }

  @RoseExecutable
  public void execute(CommandContext context, String operation, String accountName, String confirm) {
    DataManager dataManager = this.rosePlugin.getManager(DataManager.class);
    String prefixedAccountName = accountName.startsWith("*") ? accountName : "*" + accountName;

    if (operation.equalsIgnoreCase("create")) {
      if (!accountName.matches("^\\*?\\w+$")) {
        this.localeManager.sendCommandMessage(context.getSender(), "command-account-create-name-invalid");
        return;
      }

      if (dataManager.lookupCachedUUID(prefixedAccountName) != null) {
        this.localeManager.sendCommandMessage(context.getSender(), "command-account-create-exists", StringPlaceholders.of("account", accountName));
        return;
      }

      if (confirm == null) {
        this.localeManager.sendCommandMessage(context.getSender(), "command-account-create-warning", StringPlaceholders.of("account", prefixedAccountName));
        return;
      }

      dataManager.createNonPlayerAccount(UUID.randomUUID(), prefixedAccountName);
      this.localeManager.sendCommandMessage(context.getSender(), "command-account-create-success", StringPlaceholders.of("account", prefixedAccountName));
      return;
    } else if (operation.equalsIgnoreCase("delete")) {
      UUID accountID = dataManager.lookupCachedUUID(accountName);
      if (accountID == null) {
        this.localeManager.sendCommandMessage(context.getSender(), "command-account-delete-does-not-exist", StringPlaceholders.of("account", accountName));
        return;
      }

      if (confirm == null) {
        this.localeManager.sendCommandMessage(context.getSender(), "command-account-delete-warning", StringPlaceholders.of("account", accountName));
        return;
      }

      dataManager.deleteAccount(accountID);
      this.localeManager.sendCommandMessage(context.getSender(), "command-account-delete-success", StringPlaceholders.of("account", accountName));
      return;
    }

    this.localeManager.sendCommandMessage(context.getSender(), "command-account-usage");
  }

  @Override
  protected CommandInfo createCommandInfo() {
    return CommandInfo.builder("account")
        .descriptionKey("command-account-description")
        .permission("playerpoints.account")
        .arguments(ArgumentsDefinition.builder()
            .required("operation", new StringSuggestingArgumentHandler("create", "delete"))
            .required("target", new StringSuggestingArgumentHandler(PointsUtils::getPlayerTabComplete))
            .optional("confirm", ArgumentHandlers.forValues(String.class, "confirm"))
            .build())
        .build();
  }
}
