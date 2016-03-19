package io.github.michaelfedora.fedoraseconomy.economy;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeCurrency implements Currency {
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
        return null;
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
        return null;
    }

    /**
     * The currency's symbol. Ex. $
     *
     * @return symbol of the currency
     */
    @Override
    public Text getSymbol() {
        return null;
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
        return null;
    }

    /**
     * Formats the given amount using the specified number of fractional digits.
     * <p>
     * <p>Should include the symbol if it is present</p>
     *
     * @param amount            The amount to format
     * @param numFractionDigits The numer of fractional digits to use
     * @return String formatted amount.
     */
    @Override
    public Text format(BigDecimal amount, int numFractionDigits) {
        return null;
    }

    /**
     * This is the default number of fractional digits that is utilized for
     * formatting purposes.
     *
     * @return defaultFractionDigits utilized.
     */
    @Override
    public int getDefaultFractionDigits() {
        return 0;
    }

    /**
     * Returns true if this currency is the default currency for the economy,
     * otherwise false.
     *
     * @return true if this is the default currency
     */
    @Override
    public boolean isDefault() {
        return false;
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
        return null;
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
        return null;
    }
}
