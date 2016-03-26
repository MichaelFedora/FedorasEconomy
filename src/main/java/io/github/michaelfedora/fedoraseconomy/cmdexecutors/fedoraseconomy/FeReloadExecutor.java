package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.currency.FeCurrencyReloadExecutor;
import io.github.michaelfedora.fedoraseconomy.config.FeConfig;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Collections;
import java.util.List;

/**
 * Created by Michael on 3/25/2016.
 */
public class FeReloadExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("reload");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Reloads the main & sub configs"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .executor(new FeReloadExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        FeConfig.instance.load();

        if(FeConfig.instance.getCleanOnStartup()) {
            FeCleanExecutor.cleanAll();
            FePurgeExecutor.run();
        }

        FeCurrencyReloadExecutor.exec(src); // due to new "default currency"

        src.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "FedorasEconomy", TextStyles.RESET, TextColors.AQUA, " has been reloaded!"));

        return CommandResult.success();
    }
}