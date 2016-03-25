package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Michael on 3/19/2016.
 */
public class FeExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("fe", "econ");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create(Map<List<String>, ? extends CommandCallable> children) {
        return CommandSpec.builder()
                .description(Text.of("Lists version information"))
                .permission(PluginInfo.DATA_ROOT + ".use")
                .children(children)
                .executor(new FeExecutor())
                .build();
    }

    public static void listSubCommands(CommandSource src, Map<List<String>, CommandSpec> subCommands, String parentCmd) {
        Text.Builder tb = Text.builder();
        int i = 0;
        String prefix = "/fe " + ((!parentCmd.equals("")) ? parentCmd + " " : "");
        for(Map.Entry<List<String>, CommandSpec> entry : subCommands.entrySet()) {
            tb.append(Text.builder()
                    .onHover(TextActions.showText(entry.getValue().getShortDescription(src).orElse(Text.EMPTY)))
                    .onClick(TextActions.suggestCommand(prefix + entry.getKey().get(0)))
                    .append(Text.of(TextColors.BLUE, entry.getKey()))
                    .build());
            if(++i < subCommands.entrySet().size()) {
                tb.append(Text.of(TextColors.GRAY, ", "));
            }
        }
        src.sendMessage(Text.of(TextStyles.BOLD, TextColors.GOLD, "[" + parentCmd + "]: ", TextStyles.RESET, tb.build()));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        src.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, PluginInfo.NAME, TextStyles.RESET, TextColors.GRAY, ": v", TextColors.AQUA, PluginInfo.VERSION));

        return CommandResult.success();
    }
}
