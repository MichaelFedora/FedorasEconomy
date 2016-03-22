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
import io.github.michaelfedora.fedoraseconomy.config.Config;
import io.github.michaelfedora.fedoraseconomy.config.FeCurrencySerializer;
import io.github.michaelfedora.fedoraseconomy.economy.FeCurrency;
import io.github.michaelfedora.fedoraseconomy.economy.FeEconomyService;
import io.github.michaelfedora.fedoraseconomy.registry.CurrencyRegistry;
import me.flibio.updatifier.Updatifier;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

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
    private Path configDir; //TODO: Implement config
    public static Path getConfigDir() { return instance.configDir; }

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

    private LinkedHashMap<List<String>, CommandSpec> subCommands = new LinkedHashMap<>();
    public static LinkedHashMap<List<String>, CommandSpec> getSubCommands() {
        return instance.subCommands;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent gpie) {
        instance = this;
    }

    public static FeCurrency defaultCurrency = new FeCurrency("currency:fedorians", Text.of(TextColors.AQUA, "Fedorian"), Text.of(TextColors.AQUA, "Fedorian", TextColors.DARK_PURPLE, "s"), Text.of(TextColors.AQUA, "f$"), false, 0, Text.of(TextColors.GOLD, TextStyles.NONE).getFormat());

    @Listener
    public void onInit(GameInitializationEvent gie) {

        getLogger().info("===== " + PluginInfo.NAME + " v" + PluginInfo.VERSION + ": Initializing! =====");

        // register listeners - transaction list

        // register registry
        CurrencyRegistry currencyRegistry = new CurrencyRegistry();
        Sponge.getRegistry().registerModule(Currency.class, currencyRegistry);

        // config stuff

        /*TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(FeCurrency.class), new FeCurrencySerializer());
        ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);*/
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(FeCurrency.class), new FeCurrencySerializer());

        Config config = new Config();
        config.load();
        if(config.getNode("defaultCurrency").getValue() == null) {
            try {
                config.getNode("defaultCurrency").setComment("The default currency").setValue(TypeToken.of(FeCurrency.class), defaultCurrency);
            } catch (ObjectMappingException e) {
                logger.error("Could not map defaultCurrency!", e);
            }
        }
        if(config.getNode("currencies").getValue() == null || !(config.getNode("currencies").getValue() instanceof List))
            config.getNode("currencies").setValue(Collections.emptyList()).setComment("The list of currencies");
        /*currencyRegistry.getAll().forEach((c) -> {

            FeCurrency fc = (c instanceof FeCurrency) ? (FeCurrency) c : new FeCurrency(c);
            try {
                config.getNode("currencies").getAppendedNode().setValue(TypeToken.of(FeCurrency.class), fc);
            } catch(ObjectMappingException e) {
                logger.error("Could not map currency " + c.getName() + "!");
            }
        });*/
        FeCurrency customDefaultCurrency = defaultCurrency;
        try {

            customDefaultCurrency = config.getNode("defaultCurrency").getValue(TypeToken.of(FeCurrency.class), defaultCurrency);
            currencyRegistry.registerAdditionalCatalog(customDefaultCurrency);

            List<FeCurrency> currencies = config.getNode("currencies").getList(TypeToken.of(FeCurrency.class));
            currencies.forEach(currencyRegistry::registerAdditionalCatalog);

        } catch(ObjectMappingException e) {
            logger.error("Could not get currencies!", e);
        }
        config.save();

        // register api
        Sponge.getServiceManager().setProvider(this, EconomyService.class, new FeEconomyService(customDefaultCurrency));

        registerCommands();

        getLogger().info("===== " + PluginInfo.NAME + " v" + PluginInfo.VERSION + ": Done! =====");
    }

    private void registerCommands() {
        CommandManager commandManager = Sponge.getCommandManager();

        commandManager.register(this, BalanceExecutor.create(), BalanceExecutor.ALIASES);
        commandManager.register(this, PayExecutor.create(), PayExecutor.ALIASES);

        subCommands.put(FeHelpExecutor.ALIASES, FeHelpExecutor.create());
        subCommands.put(FeListExecutor.ALIASES, FeListExecutor.create());
        subCommands.put(FeDetailsExecutor.ALIASES, FeDetailsExecutor.create());
        subCommands.put(FeSetExecutor.ALIASES, FeSetExecutor.create());
        subCommands.put(FeAddExecutor.ALIASES, FeAddExecutor.create());

        commandManager.register(this, FeExecutor.create(subCommands), FeExecutor.ALIASES);
    }

    @Listener
    public void onLoadComplete(GameLoadCompleteEvent glce) {
        // database stuff ..?
    }
}
