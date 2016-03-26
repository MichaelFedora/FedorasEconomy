package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.currency;

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
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Collections;
import java.util.List;

/**
 * Created by Michael on 3/25/2016.
 */
public class FeCurrencySetDefaultExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("setdefault");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Sets the default currency (does not persist without /fe config)"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.catalogedElement(Text.of("currency"), Currency.class))
                .executor(new FeCurrencySetDefaultExecutor())
                .build();
    }

    public static CommandResult exec(CommandSource src, CommandContext args) throws CommandException {

        final Currency currency = args.<Currency>getOne(Text.of("currency")).orElseThrow(() -> new CommandException(Text.of("Bad param [currency]!")));

        FeEconomyService.instance.setDefaultCurrency(currency);
        FeConfig.instance.setDefaultCurrencyId(currency.getId());

        src.sendMessage(Text.of("Set the ", TextColors.GOLD, TextStyles.BOLD, "default currency", TextFormat.NONE, " to ", currency.getDisplayName()));

        return CommandResult.success();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        return exec(src, args);
    }
}