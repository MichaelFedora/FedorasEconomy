package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.config.FeConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Michael on 3/25/2016.
 */
public class FeGetConfigExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("getconfig");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Get values from the main-config (via flags)"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.flags()
                        .flag("-cos", "-cleanOnStartup")
                        .flag("-dc", "-defaultCurrency")
                        .flag("-v", "-verboseLogging")
                        .buildWith(GenericArguments.none()))
                .executor(new FeGetConfigExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        boolean specific = args.hasAny("cos") || args.hasAny("dc") || args.hasAny("v");

        if(args.hasAny("cos") || !specific)
            src.sendMessage(Text.of(TextColors.GOLD, "cleanOnStartup", TextColors.RESET, " is ", TextColors.AQUA, FeConfig.instance.getCleanOnStartup()));

        if(args.hasAny("dc") || !specific) {

            Optional<Currency> opt_c = Sponge.getRegistry().getType(Currency.class, FeConfig.instance.getDefaultCurrencyId());
            if(opt_c.isPresent())
                src.sendMessage(Text.of("The ", TextColors.GOLD, "default currency", TextColors.RESET, " is ", opt_c.get().getDisplayName()));
        }

        if(args.hasAny("v") || !specific)
            src.sendMessage(Text.of("Value of ", TextColors.GOLD, "verboseLogging", TextColors.RESET, " is ", TextColors.AQUA, FeConfig.instance.getVerboseLogging()));

        return CommandResult.success();
    }
}
