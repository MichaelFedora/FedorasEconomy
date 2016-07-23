package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.currency;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.config.CurrencyConfig;
import io.github.michaelfedora.fedoraseconomy.config.FeConfig;
import io.github.michaelfedora.fedoraseconomy.economy.FeCurrency;
import io.github.michaelfedora.fedoraseconomy.economy.FeEconomyService;
import io.github.michaelfedora.fedoraseconomy.registry.CurrencyRegistry;
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
import java.util.Map;
import java.util.Optional;

/**
 * Created by Michael on 3/25/2016.
 */
public class FeCurrencyReloadExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("reload");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Reloads currency configurations"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .executor(new FeCurrencyReloadExecutor())
                .build();
    }

    public static void exec(CommandSource src) {

        CurrencyRegistry.instance.clear();

        FeCurrency customDefaultCurrency = null;

        Map<String, CurrencyConfig> currencyConfigs = CurrencyConfig.loadAll();
        if(currencyConfigs.size() != 0) {

            int count = 0;
            for(Map.Entry<String, CurrencyConfig> entry : currencyConfigs.entrySet()) {

                entry.getValue().load();

                Optional<FeCurrency> opt_fc = entry.getValue().get();

                if(!opt_fc.isPresent())
                    return;

                FeCurrency fc = opt_fc.get();

                src.sendMessage(Text.of(TextColors.GRAY, "Registered currency [" + fc.getId() + "]!"));
                CurrencyRegistry.instance.registerAdditionalCatalog(fc);

                if(fc.getId().equals(FeConfig.instance.getDefaultCurrencyId())) {
                    src.sendMessage(Text.of(TextColors.GRAY, "^ Found default currency! ^"));
                    customDefaultCurrency = fc;
                }

                if(++count == currencyConfigs.size() && customDefaultCurrency == null) {
                    src.sendMessage(Text.of(TextColors.YELLOW, "Could not find default currency [" + FeConfig.instance.getDefaultCurrencyId() + "], setting it to [" + fc.getId() + "]!"));
                    customDefaultCurrency = fc; // just set it to one ;3
                }
            }

        } else {

            src.sendMessage(Text.of(TextColors.GRAY, "Could not find anything; using default currency!"));
            CurrencyRegistry.instance.registerAdditionalCatalog(FedorasEconomy.defaultCurrency);

            customDefaultCurrency = FedorasEconomy.defaultCurrency;
            CurrencyConfig defaultConfig = new CurrencyConfig(FedorasEconomy.defaultCurrency.getId());
            defaultConfig.load();
            defaultConfig.put(FedorasEconomy.defaultCurrency);
            defaultConfig.save();
        }

        FeEconomyService.instance.setDefaultCurrency(customDefaultCurrency);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        exec(src);

        src.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "FedorasEconomy", TextStyles.RESET, TextColors.GRAY, "-Currencies", TextColors.AQUA, " have been reloaded!"));

        return CommandResult.success();
    }
}
