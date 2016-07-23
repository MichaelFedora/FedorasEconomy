package io.github.michaelfedora.fedoraseconomy.config;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Michael on 3/22/2016.
 */
public class FeConfig implements FeConfigurable {

    public static final FeConfig instance = new FeConfig();

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode root;

    public static String initDefaultCurrencyId = null;
    public static boolean initCleanOnStartup = false;
    public static boolean initVerboseLogging = false;

    private String defaultCurrencyId;
    private boolean cleanOnStartup;
    private boolean verboseLogging;

    private FeConfig() {

        this.defaultCurrencyId = initDefaultCurrencyId;
        this.cleanOnStartup = initCleanOnStartup;
        this.verboseLogging = initVerboseLogging;

        Path path = FedorasEconomy.getSharedConfigDir().resolve(PluginInfo.DATA_ROOT + ".cfg");
        this.loader = HoconConfigurationLoader.builder().setPath(path).build();
    }

    public static void initialize() {
        instance.defaultCurrencyId = initDefaultCurrencyId;
        instance.cleanOnStartup = initCleanOnStartup;
        instance.verboseLogging = initVerboseLogging;

        instance.load();
        instance.setupValues();
        instance.save();
    }

    public String getDefaultCurrencyId() { return this.defaultCurrencyId; }
    public boolean getCleanOnStartup() { return this.cleanOnStartup; }
    public boolean getVerboseLogging() { return this.verboseLogging; }

    public void setDefaultCurrencyId(String defaultCurrencyId) {
        this.defaultCurrencyId = defaultCurrencyId;
        this.getNode("defaultCurrencyId").setValue(this.defaultCurrencyId);
    }

    public void setCleanOnStartup(boolean cleanOnStartup) {
        this.cleanOnStartup = cleanOnStartup;
        this.getNode("cleanOnStartup").setValue(this.cleanOnStartup);
    }

    public void setVerboseLogging(boolean verboseLogging) {
        this.verboseLogging = verboseLogging;
        this.getNode("verboseLogging").setValue(this.verboseLogging);
    }

    private void setupValues() {
        if(this.getNode("defaultCurrencyId").getValue() == null)
            this.getNode("defaultCurrencyId").setComment("The default currency's id (from those in the `/config/currencies` folder)").setValue(this.defaultCurrencyId);
        else
            this.defaultCurrencyId = this.getNode("defaultCurrencyId").getString();

        if(this.getNode("cleanOnStartup").getValue() == null)
            this.getNode("cleanOnStartup").setComment("Clean bad/unknown currency-references & purges empty accounts on startup. WARNING: This is irreversible!").setValue(this.cleanOnStartup);
        else
            this.cleanOnStartup = this.getNode("cleanOnStartup").getBoolean();

        if(this.getNode("verboseLogging").getValue() == null)
            this.getNode("verboseLogging").setComment("Log (chat) *everything* that happens to an (your) account. TRUE: Everything. FALSE: This plugin's commands only.").setValue(this.verboseLogging);
        else
            this.verboseLogging = this.getNode("verboseLogging").getBoolean();
    }

    @Override
    public void load() {
        try {
            this.root = loader.load();
        } catch(IOException e) {
            FedorasEconomy.getLogger().error("Could not load configuration!", e);
        }
    }

    @Override
    public void save() {
        try {
            this.loader.save(root);
        } catch(IOException e) {
            FedorasEconomy.getLogger().error("Could not save configuration!", e);
        }
    }

    @Override
    public CommentedConfigurationNode root() { return this.root; }

    @Override
    public CommentedConfigurationNode getNode(Object... path) { return this.root.getNode(path); }
}
