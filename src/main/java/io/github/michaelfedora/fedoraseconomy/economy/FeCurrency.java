package io.github.michaelfedora.fedoraseconomy.economy;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyle;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeCurrency implements Currency {

    private String identifier;

    private Text displayName;
    private Text pluralDisplayName;

    private Text symbol;
    private boolean rightSideSymbol; // default left

    private int valueScale;
    private TextColor valueColor;
    private TextStyle valueStyle;

    public FeCurrency(String identifier, Text displayName, Text pluralDisplayName, Text symbol, boolean rightSideSymbol, int valueScale, TextColor valueColor, TextStyle valueStyle) {

        this.identifier = identifier;

        this.displayName = displayName;
        this.pluralDisplayName = pluralDisplayName;

        this.symbol = symbol;
        this.rightSideSymbol = rightSideSymbol;

        this.valueScale = valueScale;
        this.valueColor = valueColor;
        this.valueStyle = valueStyle;
    }

    /**
     * The currency's display name, in singular form. Ex: Dollar.
     * <p>
     * <p>This should be preferred over {@link CatalogType#getName()}
     * for display purposes.</p>
     *
     * @return displayName of the currency singular
     */
    @Override
    public Text getDisplayName() {
        return this.displayName;
    }

    /**
     * The currency's display name in plural form. Ex: Dollars.
     * <p>
     * <p>Not all currencies will have a plural name that differs from the
     * display name.</p>
     *
     * @return displayName of the currency plural
     */
    @Override
    public Text getPluralDisplayName() {
        return this.pluralDisplayName;
    }

    /**
     * The currency's symbol. Ex. $
     *
     * @return symbol of the currency
     */
    @Override
    public Text getSymbol() {
        return this.symbol;
    }

    /**
     * Formats the given amount using the default number of fractional digits.
     * <p>
     * <p>Should include the symbol if it is present</p>
     *
     * @param amount The amount to format
     * @return String formatted amount
     */
    @Override
    public Text format(BigDecimal amount) {

        Text.Builder formatted = Text.builder();

        amount = amount.setScale(this.valueScale, RoundingMode.FLOOR);

        Text formattedAmount = Text.of(this.valueColor, this.valueStyle, amount);

        if(this.symbol.compareTo(Text.EMPTY) > 0) {

            if(!this.rightSideSymbol) {

                formatted.append(this.symbol);
                formatted.append(formattedAmount);

            } else {

                formatted.append(formattedAmount);
                formatted.append(this.symbol);
            }

        } else {

            formatted.append(formattedAmount, Text.of(" "));
            formatted.append( ((amount.abs().compareTo(BigDecimal.ONE) == 0) ? this.displayName : this.pluralDisplayName) );
        }

        return formatted.build();
    }

    /**
     * Formats the given amount using the specified number of fractional digits.
     * <p>
     * <p>Should include the symbol if it is present</p>
     *
     * @param amount            The amount to format
     * @param numFractionDigits The number of fractional digits to use
     * @return String formatted amount.
     */
    @Override
    public Text format(BigDecimal amount, int numFractionDigits) {

        Text.Builder formatted = Text.builder();

        amount = amount.setScale(numFractionDigits, RoundingMode.FLOOR);

        Text formattedAmount = Text.of(this.valueColor, amount);

        if(this.symbol.compareTo(Text.EMPTY) > 0) {

            if(!this.rightSideSymbol) {

                formatted.append(this.symbol);
                formatted.append(formattedAmount);

            } else {

                formatted.append(formattedAmount);
                formatted.append(this.symbol);
            }

        } else {

            formatted.append(formattedAmount, Text.of(" "));
            formatted.append( ((amount.abs().compareTo(BigDecimal.ONE) == 0) ? this.displayName : this.pluralDisplayName) );
        }

        return formatted.build();
    }

    /**
     * This is the default number of fractional digits that is utilized for
     * formatting purposes.
     *
     * @return defaultFractionDigits utilized.
     */
    @Override
    public int getDefaultFractionDigits() {
        return this.valueScale;
    }

    /**
     * Returns true if this currency is the default currency for the economy,
     * otherwise false.
     *
     * @return true if this is the default currency
     */
    @Override
    public boolean isDefault() {

        Currency defaultCurrency;
        try {

            defaultCurrency = Sponge.getServiceManager().provide(EconomyService.class).orElseThrow(Exception::new).getDefaultCurrency();

        } catch(Exception e) {

            FedorasEconomy.getLogger().error("Could not get EconomyService!", e);
            return false;
        }

        return this.equals(defaultCurrency);
    }

    /**
     * Gets the unique identifier of this {@link CatalogType}. The identifier is
     * case insensitive, thus there cannot be another instance with a different
     * character case. The id of this instance must remain the same for the
     * entire duration of its existence. The identifier can be formatted however
     * needed.
     * <p>
     * <p>A typical id format follows the pattern of <code>`modId:name`</code>
     * or <code>`minecraft:name`</code>. However the prefix may be omitted for
     * default/vanilla minecraft types.</p>
     *
     * @return The unique identifier of this catalog type
     */
    @Override
    public String getId() {
        return this.identifier;
    }

    /**
     * Gets the human-readable name of this individual {@link CatalogType}. This
     * name is not guaranteed to be unique. This value should not be used for
     * serialization.
     *
     * @return The human-readable name of this catalog type
     */
    @Override
    public String getName() {
        return this.displayName.toPlain();
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof Currency))
            return false;

        return this.getId().equals( ((Currency) obj).getId() );
    }
}
