package io.github.michaelfedora.fedoraseconomy.cmdexecutors;

import com.google.common.collect.Lists;
import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Created by Michael on 3/19/2016.
 */
public class FeHelpExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("help", "?");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Displays the subCommands and their usages"))
                .description(Text.of("Displays the subCommands and their usages, or the help for the command specified"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("cmd"))))
                .executor(new FeHelpExecutor())
                .build();
    }

    private static Optional<Map.Entry<List<String>, CommandSpec>> findCmd(HashMap<List<String>, CommandSpec> commands, String cmd) {
        for(Map.Entry<List<String>, CommandSpec> entry : commands.entrySet()) {
            if(entry.getKey().contains(cmd))
                return Optional.of(entry);
        }

        return Optional.empty();
    }

    public static void helpFunc(CommandSource src, CommandContext args, HashMap<List<String>,CommandSpec> commands, String parentCmd) throws CommandException {

        Optional<String> opt_cmd = args.getOne("cmd");
        if(opt_cmd.isPresent()) {
            Optional<Map.Entry<List<String>, CommandSpec>> opt_cmdspec = findCmd(commands, opt_cmd.get());
            if(opt_cmdspec.isPresent()) {
                Map.Entry<List<String>, CommandSpec> cmdspec = opt_cmdspec.get();

                Text.Builder builder = Text.builder();
                builder.append(Text.of(cmdspec.getKey().toString(), ": ", TextColors.BLUE, cmdspec.getValue().getUsage(src)));

                Text t = Text.of(cmdspec.getValue().getHelp(src).orElse(cmdspec.getValue().getShortDescription(src).orElse(Text.EMPTY)));
                if(t != Text.EMPTY)
                    builder.append(t);

                src.sendMessage(builder.build());
                return;
            }
            throw new CommandException(Text.of("No such command: ", TextColors.BLUE, "[", opt_cmd.get(), "]", TextColors.RESET, "!"));
        }

        List<Text> helpList = Lists.newArrayList();
        String prefix = "/fm " + ((!parentCmd.equals("")) ? parentCmd + " " : "");
        for(List<String> aliases : commands.keySet()) {
            CommandSpec commandSpec = commands.get(aliases);
            Text usage = commandSpec.getUsage(src);
            if(!usage.equals(Text.EMPTY))
                usage = Text.of(": ", TextColors.BLUE, usage);
            else
                usage = Text.of(": ", TextColors.RED, "[ ]");
            Text commandHelp = Text.builder()
                    .append(Text.builder()
                            .onHover(TextActions.showText(commandSpec.getShortDescription(src).orElseThrow(() -> new CommandException(Text.of("Could not get ShortDescription!")))))
                            .onClick(TextActions.suggestCommand(prefix + aliases.get(0)))
                            .append(Text.of(TextColors.GREEN, TextStyles.BOLD, aliases.toString()))
                            .build())
                    .append(Text.of(usage))
                    .build();
            helpList.add(commandHelp);
        }

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).orElseThrow(() -> new CommandException(Text.of("Could not get PaginationService!")));
        Text parentReference = Text.EMPTY;
        if(!parentCmd.equals("")) {
            parentReference = Text.of(TextColors.WHITE, " [" + parentCmd + "]");
        }

        PaginationList.Builder paginationBuilder = paginationService.builder().title(Text.of(TextColors.AQUA, PluginInfo.NAME, parentReference, TextColors.AQUA, " Help")).padding(Text.of(TextColors.GOLD, "=")).contents(helpList);
        paginationBuilder.sendTo(src);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        helpFunc(src, args, FedorasEconomy.getSubCommands(), "");

        return CommandResult.success();
    }
}
