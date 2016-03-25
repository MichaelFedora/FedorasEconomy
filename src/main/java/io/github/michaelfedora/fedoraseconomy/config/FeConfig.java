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

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode root;

    private String defaultCurrency;
    private boolean cleanOnStartup;

    public String getDefaultCurrency() { return this.defaultCurrency; }
    public boolean getCleanOnStartup() { return this.cleanOnStartup; }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
        this.getNode("defaultCurrency").setValue(this.defaultCurrency);
    }

    public void setCleanOnStartup(boolean cleanOnStartup) {
        this.cleanOnStartup = cleanOnStartup;
        this.getNode("cleanOnStartup").setValue(this.cleanOnStartup);
    }


    public FeConfig(String defaultCurrency, boolean cleanOnStartup) {

        this.defaultCurrency = defaultCurrency;
        this.cleanOnStartup = cleanOnStartup;

        Path path = FedorasEconomy.getSharedConfigDir().resolve(PluginInfo.DATA_ROOT + ".cfg");
        this.loader = HoconConfigurationLoader.builder().setPath(path).build();

        this.load();
        this.setupValues();
        this.save();
    }

    private void setupValues() {
        if(this.getNode("defaultCurrency") == null)
            this.getNode("defaultCurrency").setComment("The default currency (in the private-plugin folder)").setValue(this.defaultCurrency);
        else
            this.defaultCurrency = this.getNode("defaultCurrency").getString();

        if(this.getNode("cleanOnStartup") == null)
            this.getNode("cleanOnStartup").setComment("Clean bad/unknown currency-references on startup. WARNING: This is irreversible!").setValue(this.cleanOnStartup);
        else
            this.cleanOnStartup = this.getNode("cleanOnStartup").getBoolean();

        this.save();
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
