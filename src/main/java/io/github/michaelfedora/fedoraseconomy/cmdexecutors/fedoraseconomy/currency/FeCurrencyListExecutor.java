package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.currency;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.economy.FeEconomyService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Created by Michael on 3/19/2016.
 */
public class FeCurrencyListExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("list", "l");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("List all available currencies"))
                .permission(PluginInfo.DATA_ROOT + ".currency." + NAME)
                .executor(new FeCurrencyListExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Collection<Currency> currencies = Sponge.getRegistry().getAllOf(Currency.class);

        Text.Builder tb = Text.builder().append(Text.of(TextColors.GOLD, TextStyles.BOLD, "Currencies: "));

        int count = 0;
        for(Currency c : currencies) {
            tb.append(Text.builder()
                    .onHover(TextActions.showText(Text.of(c.getId())))
                    .append(c.getDisplayName())
                    .append(c.equals(FeEconomyService.instance.getDefaultCurrency()) ?
                            Text.builder().onHover(TextActions.showText(Text.of("default"))).append(Text.of(TextStyles.ITALIC, TextColors.DARK_GRAY, " (", TextColors.AQUA, "*", TextColors.DARK_GRAY, ")")).build() :
                            Text.EMPTY)
                    .build());
            if(++count < currencies.size())
                tb.append(Text.of(", "));
        }

        src.sendMessage(tb.build());

        return CommandResult.success();
    }
}
