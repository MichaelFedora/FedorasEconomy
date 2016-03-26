package io.github.michaelfedora.fedoraseconomy.config;

import com.google.common.reflect.TypeToken;
import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.economy.FeCurrency;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by Michael on 3/22/2016.
 */
public class CurrencyConfig implements FeConfigurable {

    private final Path path;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode root;

    public CurrencyConfig(String id) {
        this.path = FedorasEconomy.getCurrenciesConfigDir().resolve(id.substring(id.indexOf(':') + 1) + ".currency");
        this.loader = HoconConfigurationLoader.builder().setPath(this.path).build();
    }

    private CurrencyConfig(Path path) {
        this.path = path;
        this.loader = HoconConfigurationLoader.builder().setPath(this.path).build();
    }

    public static Map<String, CurrencyConfig> loadAll() {
        Map<String, CurrencyConfig> configs = new HashMap<>();
        try {
            Files.walk(FedorasEconomy.getCurrenciesConfigDir()).forEach((filePath) -> {
                if(Files.isRegularFile(filePath)) {
                    String fileName = filePath.getFileName().toString();
                    String ext = fileName.substring(fileName.lastIndexOf('.')+1);
                    if(ext.equals("currency"))
                        configs.put(fileName.substring(0, fileName.lastIndexOf('.')), new CurrencyConfig(filePath));
                }
            });
        } catch(IOException e) {
            FedorasEconomy.getLogger().error("Could not walk the Currencies directory!");
        }

        return configs;
    }

    @Override
    public void load() {
        try {
            root = loader.load();
        } catch(IOException e) {
            FedorasEconomy.getLogger().error("Could not load configuration [" + path.getFileName().toString() + "]!", e);
        }
    }

    @Override
    public void save() {
        try {
            loader.save(root);
        } catch(IOException e) {
            FedorasEconomy.getLogger().error("Could not save configuration [" + path.getFileName().toString() + "]!", e);
        }
    }

    @Override
    public CommentedConfigurationNode root() { return root; }

    @Override
    public CommentedConfigurationNode getNode(Object... path) { return root.getNode(path); }

    public void put(FeCurrency currency) {
        if(this.root.getValue() == null) {
            try {
                this.root.setValue(TypeToken.of(FeCurrency.class), currency);
            } catch (ObjectMappingException e) {
                FedorasEconomy.getLogger().error("Could not map currency [" + currency.getId() + "]!", e);
            }
        }
    }

    public Optional<FeCurrency> get() {

        try {
            return Optional.of(this.root.getValue(TypeToken.of(FeCurrency.class)));
        } catch (ObjectMappingException e) {
            FedorasEconomy.getLogger().error("Could not get currency from [" + path.getFileName().toString() + "]!", e);
        }
        return Optional.empty();
    }
}
