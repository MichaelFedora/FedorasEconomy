package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.unique;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.FeHelpExecutor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 3/23/2016.
 */
public class FeUniqueHelpExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("help", "?");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Displays the subCommands and their usages"))
                .description(Text.of("Displays the subCommands and their usages, or the help for the command specified"))
                .permission(PluginInfo.DATA_ROOT + ".unique." + NAME)
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("cmd"))))
                .executor(new FeHelpExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        String name = FeUniqueExecutor.NAME;
        FeHelpExecutor.helpFunc(src, args, FedorasEconomy.getGrandChildCommands(name).orElseThrow(() -> new CommandException(Text.of("Could not get [" + name + "]'s sub commands!"))), name);

        return CommandResult.success();
    }
}
