package org.black_ixx.playerpoints.commands;

import dev.rosewood.rosegarden.command.HelpCommand;
import dev.rosewood.rosegarden.command.PrimaryCommand;
import dev.rosewood.rosegarden.command.ReloadCommand;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import java.util.Optional;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.SettingKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BaseCommand extends PrimaryCommand {

    private final PlayerPoints playerPoints;

    public BaseCommand(PlayerPoints playerPoints) {
        super(playerPoints);
        this.playerPoints = playerPoints;
    }

    @RoseExecutable
    @Override
    public void execute(CommandContext context) {
        String baseRedirect = SettingKey.BASE_COMMAND_REDIRECT.get();
        if (baseRedirect.trim().isEmpty())
            baseRedirect = "me";

        CommandSender sender = context.getSender();
        Optional<RoseCommand> subcommand = this.findCommand(sender, baseRedirect);
        if (subcommand.isPresent()) {
            subcommand.get().invoke(context);
        } else {
            this.findCommand(sender, "help").ifPresent(x -> x.invoke(context));
        }
    }

    private Optional<RoseCommand> findCommand(CommandSender sender, String name) {
        Argument.SubCommandArgument argument = (Argument.SubCommandArgument) this.getCommandArguments().get(0);
        return argument.subCommands().stream()
                .filter(x -> x.getName().equalsIgnoreCase(name) || x.getAliases().stream().anyMatch(y -> y.equalsIgnoreCase(name)))
                .findFirst()
                .filter(x -> !x.isPlayerOnly() || sender instanceof Player);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("points")
                .aliases("p")
                .permission("playerpoints.basecommand")
                .arguments(ArgumentsDefinition.builder()
                        .optionalSub(
                                new BroadcastCommand(this.playerPoints),
                                new ConvertCommand(this.playerPoints),
                                new ExportCommand(this.playerPoints),
                                new GiveAllCommand(this.playerPoints),
                                new GiveCommand(this.playerPoints),
                                new HelpCommand(this.playerPoints, this),
                                new ImportCommand(this.playerPoints),
                                new ImportLegacyCommand(this.playerPoints),
                                new LeadCommand(this.playerPoints),
                                new LookCommand(this.playerPoints),
                                new MeCommand(this.playerPoints),
                                new PayCommand(this.playerPoints),
                                new ReloadCommand(this.playerPoints),
                                new ResetCommand(this.playerPoints),
                                new SetCommand(this.playerPoints),
                                new TakeCommand(this.playerPoints),
                                new VersionCommand(this.playerPoints)
                        ))
                .build();
    }

}
