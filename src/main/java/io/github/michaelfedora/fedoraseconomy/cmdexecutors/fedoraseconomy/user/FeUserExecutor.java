package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.user;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.FeExecutor;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Michael on 3/23/2016.
 */
public class FeUserExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("user", "u");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create(Map<List<String>, ? extends CommandCallable> children) {
        return CommandSpec.builder()
                .description(Text.of("Do things with user accounts (lists subcommands)"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME + ".use")
                .arguments(GenericArguments.none())
                .children(children)
                .executor(new FeUserExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        FeExecutor.listSubCommands(src, FedorasEconomy.getGrandChildCommands(NAME).orElseThrow(() -> new CommandException(Text.of("Could not get [" + NAME + "]'s sub commands!"))), NAME);

        return CommandResult.success();
    }
}
