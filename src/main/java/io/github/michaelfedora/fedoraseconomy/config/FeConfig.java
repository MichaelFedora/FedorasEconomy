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

    private String defaultCurrencyId;
    private boolean cleanOnStartup;

    private FeConfig() {

        this.defaultCurrencyId = null;
        this.cleanOnStartup = false;

        Path path = FedorasEconomy.getSharedConfigDir().resolve(PluginInfo.DATA_ROOT + ".cfg");
        this.loader = HoconConfigurationLoader.builder().setPath(path).build();
    }

    public static FeConfig set(String defaultCurrency, boolean cleanOnStartup) {

        instance.defaultCurrencyId = defaultCurrency;
        instance.cleanOnStartup = cleanOnStartup;

        return instance;
    }

    public static void initialize() {
        instance.load();
        instance.setupValues();
        instance.save();
    }

    public String getDefaultCurrencyId() { return this.defaultCurrencyId; }
    public boolean getCleanOnStartup() { return this.cleanOnStartup; }

    public void setDefaultCurrencyId(String defaultCurrencyId) {
        this.defaultCurrencyId = defaultCurrencyId;
        this.getNode("defaultCurrencyId").setValue(this.defaultCurrencyId);
    }

    public void setCleanOnStartup(boolean cleanOnStartup) {
        this.cleanOnStartup = cleanOnStartup;
        this.getNode("cleanOnStartup").setValue(this.cleanOnStartup);
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
