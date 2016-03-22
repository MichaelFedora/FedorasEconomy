package io.github.michaelfedora.fedoraseconomy.config;

import com.google.common.reflect.TypeToken;
import io.github.michaelfedora.fedoraseconomy.economy.FeCurrency;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.text.Text;

/**
 * Created by Michael on 3/21/2016.
 */
public class FeCurrencySerializer implements TypeSerializer<FeCurrency> {

    /**
     * Deserialize an object required to be of a given type from the given configuration node
     *
     * @param type  The type of return value required
     * @param value The node containing serialized data
     * @return An object
     * @throws ObjectMappingException If the presented data is somehow invalid
     */
    @Override
    public FeCurrency deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String identifier = value.getNode("identifier").getString();
        Text displayName = value.getNode("displayName").getValue(TypeToken.of(Text.class));
        Text pluralDisplayName = value.getNode("pluralDisplayName").getValue(TypeToken.of(Text.class));
        Text symbol =  value.getNode("symbol").getValue(TypeToken.of(Text.class));
        boolean rightSideSymbol = value.getNode("rightSideSymbol").getBoolean();
        int valueScale = value.getNode("valueScale").getInt();
        Text valueFormat = value.getNode("valueFormat").getValue(TypeToken.of(Text.class));
        return new FeCurrency(identifier, displayName, pluralDisplayName, symbol, rightSideSymbol, valueScale, valueFormat.getFormat());
    }

    @Override
    public void serialize(TypeToken<?> type, FeCurrency obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("identifier").setValue(obj.getId());
        value.getNode("displayName").setValue(TypeToken.of(Text.class), obj.getDisplayName());
        value.getNode("pluralDisplayName").setValue(TypeToken.of(Text.class), obj.getPluralDisplayName());
        value.getNode("symbol").setValue(TypeToken.of(Text.class), obj.getSymbol());
        value.getNode("rightSideSymbol").setValue(obj.getRightSideSymbol());
        value.getNode("valueScale").setValue(obj.getValueScale());
        value.getNode("valueFormat").setValue(TypeToken.of(Text.class), Text.of(obj.getValueFormat()));
    }
}
