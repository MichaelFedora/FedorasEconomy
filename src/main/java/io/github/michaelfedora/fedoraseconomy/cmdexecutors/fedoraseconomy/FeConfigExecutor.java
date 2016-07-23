package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.config.FeConfig;
import io.github.michaelfedora.fedoraseconomy.economy.FeEconomyService;
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
public class FeConfigExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("config");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Change the main-config, via flags, and save it"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.flags()
                        .valueFlag(GenericArguments.bool(Text.of("cleanOnStartup")), "-cos","-cleanOnStartup")
                        .valueFlag(GenericArguments.catalogedElement(Text.of("defaultCurrency"), Currency.class), "-dc", "-defaultCurrency")
                        .valueFlag(GenericArguments.bool(Text.of("verboseLogging")), "-v","-verboseLogging")
                        .buildWith(GenericArguments.none()))
                .executor(new FeConfigExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<Boolean> opt_cleanOnStartup = args.getOne("cleanOnStartup");
        if(opt_cleanOnStartup.isPresent()) {

            FeConfig.instance.setCleanOnStartup(opt_cleanOnStartup.get());
            src.sendMessage(Text.of("Set ", TextColors.GOLD, "cleanOnStartup", TextColors.RESET, " to ", TextColors.AQUA, opt_cleanOnStartup.get()));
        }

        Optional<Currency> opt_defaultCurrency = args.getOne("defaultCurrency");
        if(opt_defaultCurrency.isPresent()) {

            FeConfig.instance.setDefaultCurrencyId(opt_defaultCurrency.get().getId());
            src.sendMessage(Text.of("Set ", TextColors.GOLD, "defaultCurrencyId", TextColors.RESET, " to ", TextColors.AQUA, opt_defaultCurrency.get().getId()));

        } else {

            FeConfig.instance.setDefaultCurrencyId(FeEconomyService.instance.getDefaultCurrency().getId());
        }

        Optional<Boolean> opt_verboseLogging = args.getOne("verboseLogging");
        if(opt_verboseLogging.isPresent()) {

            FeConfig.instance.setVerboseLogging(opt_verboseLogging.get());
            src.sendMessage(Text.of("Set ", TextColors.GOLD, "verboseLogging", TextColors.RESET, " to ", TextColors.AQUA, opt_verboseLogging.get()));
        }

        src.sendMessage(Text.of("Saved configuration settings to the file!"));

        FeConfig.instance.save();

        return CommandResult.success();
    }
}
