package io.github.michaelfedora.fedoraseconomy.cmdexecutors;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        src.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, PluginInfo.NAME, TextStyles.RESET, TextColors.GRAY, ": v", TextColors.AQUA, PluginInfo.VERSION));

        return CommandResult.success();
    }
}
