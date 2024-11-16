package org.black_ixx.playerpoints.commands.arguments;

import dev.rosewood.rosegarden.command.argument.StringArgumentHandler;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class StringSuggestingArgumentHandler extends StringArgumentHandler {

    private final Function<CommandContext, List<String>> suggestionsFunction;

    public StringSuggestingArgumentHandler(String... suggestions) {
        this(context -> Arrays.asList(suggestions));
    }

    public StringSuggestingArgumentHandler(Function<CommandContext, List<String>> suggestionsFunction) {
        this.suggestionsFunction = suggestionsFunction;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return this.suggestionsFunction.apply(context);
    }

}
