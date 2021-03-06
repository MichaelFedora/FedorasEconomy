package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.currency;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.economy.FeCurrency;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 3/21/2016.
 */
public class FeCurrencyDetailsExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("details", "cat");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Shows the details of a particular currency"))
                .permission(PluginInfo.DATA_ROOT + ".currency." + NAME)
                .arguments(GenericArguments.catalogedElement(Text.of("currency"), Currency.class))
                .executor(new FeCurrencyDetailsExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        FeCurrency currency = FeCurrency.of(args.<Currency>getOne("currency").orElseThrow(() -> new CommandException(Text.of("Bad param [currency]!"))));

        Text.Builder tb = Text.builder();

        tb.append(Text.of(TextColors.GOLD, "===== ", "Currency \"", TextColors.AQUA, currency.getName(), TextColors.GOLD, "\" Details", " =====", '\n'));
        if(currency.equals(FeEconomyService.instance.getDefaultCurrency()))
            tb.append(Text.of(TextColors.GRAY, "Default", TextColors.RESET,": ", TextColors.AQUA, "true\n"));
        tb.append(Text.of(TextColors.GRAY, "Identifier", TextColors.RESET,": ", currency.getId(), '\n'));
        tb.append(Text.of(TextColors.GRAY, "DisplayName: ", TextColors.RESET, currency.getDisplayName(), '\n'));
        tb.append(Text.of(TextColors.GRAY, "PluralDisplayName: ", TextColors.RESET, currency.getPluralDisplayName(), '\n'));
        tb.append(Text.of(TextColors.GRAY, "Symbol: ", TextColors.RESET, currency.getSymbol(), '\n'));
        tb.append(Text.of(TextColors.GRAY, "Formatting (1234.5678d): ", TextColors.RESET, currency.format(BigDecimal.valueOf(1234.5678d))));

        src.sendMessage(tb.build());

        return CommandResult.success();
    }
}
