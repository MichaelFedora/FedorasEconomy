/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
package io.github.michaelfedora.fedoraseconomy;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.*;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.*;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.currency.FeCurrencyDetailsExecutor;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.currency.FeCurrencyListExecutor;
import io.github.michaelfedora.fedoraseconomy.config.CurrencyConfig;
import io.github.michaelfedora.fedoraseconomy.config.FeConfig;
import io.github.michaelfedora.fedoraseconomy.config.FeCurrencySerializer;
import io.github.michaelfedora.fedoraseconomy.economy.FeCurrency;
import io.github.michaelfedora.fedoraseconomy.economy.FeEconomyService;
import io.github.michaelfedora.fedoraseconomy.registry.CurrencyRegistry;
import me.flibio.updatifier.Updatifier;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * The main class yo!
 */
@Updatifier(repoName = "FedorasEconomy", repoOwner = "MichaelFedora", version = PluginInfo.VERSION)
@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION, description = PluginInfo.DESCRIPTION, authors = PluginInfo.AUTHORS)
public class FedorasEconomy {

    private static FedorasEconomy instance;

    @Inject
    private Logger logger;
    public static Logger getLogger() { return instance.logger; }

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path sharedConfigDir; //TODO: Implement config
    public static Path getSharedConfigDir() { return instance.sharedConfigDir; }

    public static Path getCurrenciesConfigDir() { return instance.sharedConfigDir.resolve("Currencies"); }

    private static SqlService SQL;
    public static javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException {
        if(SQL == null)
            SQL = Sponge.getServiceManager().provide(SqlService.class).orElseThrow(() -> new SQLException("Could not get SqlService!"));

        return SQL.getDataSource(jdbcUrl);
    }

    public static Connection getAccountsConnection() throws SQLException {
        return getDataSource("jdbc:h2:./mods/FedorasData/econAccounts.db").getConnection();
    }

    public static Connection getTransactionListsConnection() throws SQLException {
        return getDataSource("jdbc:h2:./mods/FedorasData/econTransactionLists.db").getConnection();
    }

    //connBal.prepareStatement("CREATE TABLE IF NOT EXISTS " + identifier + "(currency VARCHAR(255), balance DECIMAL)").execute();
    // ex: fedorascurrency:coins | 500.25
    // ex: fedorascurrency:doubloons | 9001

    //connTrans.prepareStatement("CREATE TABLE IF NOT EXISTS " + identifier + "(id IDENTITY, stamp TIMESTAMP, otherParty VARCHAR(255), operation VARCHAR(255), amount DECIMAL, currency VARCHAR(255)");
    // ex: 101 | 2016-03-21 10:43:50 | null | DEPOSIT | 14.25 | fedorascurrency:coins
    // ex: 102 | 2016-03-21 10:45:29 | {uuid} | TRANSFER | 7 | fedorascurrency:doubloons

    private final LinkedHashMap<List<String>, CommandSpec> subCommands = new LinkedHashMap<>();
    public static LinkedHashMap<List<String>, CommandSpec> getSubCommands() {
        return instance.subCommands;
    }

    private final LinkedHashMap<String, LinkedHashMap<List<String>, CommandSpec>> grandChildCommands = new LinkedHashMap<>();
    public static Optional<LinkedHashMap<List<String>, CommandSpec>> getGrandChildCommands(String name) {
        return Optional.ofNullable(instance.grandChildCommands.get(name));
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent gpie) {
        instance = this;
    }

    public static final FeCurrency defaultCurrency = new FeCurrency(
            "currency:fedorian",
            Text.of(TextColors.AQUA, "Fedorian"),
            Text.of(TextColors.AQUA, "Fed", TextColors.DARK_GRAY, TextStyles.OBFUSCATED, "ori", TextColors.AQUA, TextStyles.RESET, "ans"),
            Text.of(TextColors.AQUA, "f$"), true,
            3, Text.of(TextColors.DARK_GRAY, TextStyles.NONE).getFormat(),
            Text.of(TextColors.GRAY, ","));

    @Listener
    public void onInit(GameInitializationEvent gie) {

        getLogger().info("===== " + PluginInfo.NAME + " v" + PluginInfo.VERSION + ": Initializing! =====");

        // register listeners - transaction list

        // register registry
        CurrencyRegistry currencyRegistry = new CurrencyRegistry();
        Sponge.getRegistry().registerModule(Currency.class, currencyRegistry);

        // config stuff

        File currencyConfigDir = getCurrenciesConfigDir().toFile();
        if(!currencyConfigDir.exists()) {
            try {
                currencyConfigDir.mkdir();
            } catch(SecurityException e) {
                logger.error("Could not make private directory!", e);
            }
        }

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(FeCurrency.class), new FeCurrencySerializer());

        String defaultCurrencyId = defaultCurrency.getId().substring(defaultCurrency.getId().indexOf(':')+1);

        FeConfig mainConfig = new FeConfig();
        mainConfig.load();
        if(mainConfig.getNode("defaultCurrency").getValue() == null) {
            mainConfig.getNode("defaultCurrency").setComment("The default currency (in the private-plugin folder)").setValue(defaultCurrencyId);
        } else {
            defaultCurrencyId = mainConfig.getNode("defaultCurrency").getString();
        }
        mainConfig.save();

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

                logger.info("Registered currency [" + fc.getId() + "]!");
                currencyRegistry.registerAdditionalCatalog(fc);

                if(entry.getKey().equals(defaultCurrencyId)) {
                    logger.info("Found default currency [" + defaultCurrencyId + "]!");
                    customDefaultCurrency = fc;
                }

                if(++count == currencyConfigs.size() && customDefaultCurrency == null) {
                    logger.warn("Could not find default currency [" + defaultCurrencyId + "](file), setting it to [" + fc.getId() + "](id)!");
                    customDefaultCurrency = fc; // just set it to one ;3
                }
            }

        } else {

            logger.info("Could not find anything; using default currency!");
            currencyRegistry.registerAdditionalCatalog(defaultCurrency);

            customDefaultCurrency = defaultCurrency;
            CurrencyConfig defaultConfig = new CurrencyConfig(defaultCurrency.getId().substring(defaultCurrency.getId().indexOf(':')+1));
            defaultConfig.load();
            defaultConfig.put(defaultCurrency);
            defaultConfig.save();
        }

        // register api
        Sponge.getServiceManager().setProvider(this, EconomyService.class, new FeEconomyService(customDefaultCurrency));
        logger.info("Registered the Economy API!");

        registerCommands();

        getLogger().info("===== " + PluginInfo.NAME + " v" + PluginInfo.VERSION + ": Done! =====");
    }

    private void registerCommands() {
        CommandManager commandManager = Sponge.getCommandManager();

        commandManager.register(this, BalanceExecutor.create(), BalanceExecutor.ALIASES);
        commandManager.register(this, BalanceExecutor.createAsMoneyAlias(), BalanceExecutor.MONEY_ALIASES);
        commandManager.register(this, PayExecutor.create(), PayExecutor.ALIASES);

        subCommands.put(FeHelpExecutor.ALIASES, FeHelpExecutor.create());
        subCommands.put(FeCurrencyListExecutor.ALIASES, FeCurrencyListExecutor.create());
        subCommands.put(FeCurrencyDetailsExecutor.ALIASES, FeCurrencyDetailsExecutor.create());
        subCommands.put(FeSetExecutor.ALIASES, FeSetExecutor.create());
        subCommands.put(FeAddExecutor.ALIASES, FeAddExecutor.create());

        commandManager.register(this, FeExecutor.create(subCommands), FeExecutor.ALIASES);
    }
}
