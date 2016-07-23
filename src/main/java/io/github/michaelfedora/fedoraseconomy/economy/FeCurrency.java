package io.github.michaelfedora.fedoraseconomy.economy;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeCurrency implements Currency {

    private final String identifier;

    private final Text displayName;
    private final Text pluralDisplayName;

    private final Text symbol;
    private final boolean rightSideSymbol; // default left

    private final int valueScale;
    private final TextFormat valueFormat;
    private final Text bigNumSeparator;

    public FeCurrency(String identifier, Text displayName, Text pluralDisplayName, Text symbol, boolean rightSideSymbol, int valueScale, TextFormat valueFormat, Text bigNumSeparator) {

        this.identifier = identifier;

        this.displayName = displayName;
        this.pluralDisplayName = pluralDisplayName;

        this.bigNumSeparator = bigNumSeparator;

        this.symbol = symbol;
        this.rightSideSymbol = rightSideSymbol;

        this.valueScale = (valueScale > 0) ? valueScale : 0;
        this.valueFormat = valueFormat;
    }

    public static FeCurrency of(Currency c) {

        if(c instanceof FeCurrency)
            return (FeCurrency) c;

        return new FeCurrency(c.getId(), c.getDisplayName(), c.getPluralDisplayName(), c.getSymbol(),
                false, 2, TextFormat.NONE, Text.EMPTY); // so evil >:>
    }

    public boolean getRightSideSymbol() { return this.rightSideSymbol; }
    public int getValueScale() { return this.valueScale; }
    public TextFormat getValueFormat() { return this.valueFormat; }
    public Text getBigNumSeparator() { return this.bigNumSeparator; }

    public static final FeCurrency NONE = new FeCurrency("currency:_", Text.EMPTY, Text.EMPTY, Text.EMPTY, false, 0, TextFormat.NONE, Text.EMPTY);

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
        return this.format(amount, this.valueScale);
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
        String amountString = amount.abs().toString();
        int indexOfDecimal = amountString.indexOf('.');
        indexOfDecimal = (indexOfDecimal < amountString.length() && indexOfDecimal > 0) ? indexOfDecimal : amountString.length();

        List<Integer> placeForSeparator = new ArrayList<>();
        int count = 0;
        for(int i = indexOfDecimal-1; i >= 0; i--, count++) {
            if(count%3 != 0)
                continue;
            placeForSeparator.add(i);
        }

        Collections.reverse(placeForSeparator);

        Text.Builder tb = Text.builder();
        if(amount.compareTo(BigDecimal.ZERO) < 0) tb.append(Text.of(this.bigNumSeparator.getFormat(), '-'));

        int begin = 0;
        for(int i : placeForSeparator) {
            if(i+1 != indexOfDecimal) {
                tb.append(Text.of(this.valueFormat, amountString.substring(begin, i+1), TextStyles.RESET, TextColors.RESET, this.bigNumSeparator));
            } else {
                tb.append(Text.of(this.valueFormat, amountString.substring(begin, i+1))); // the last chars
                break;
            }
            begin = i + 1;
        }
        if(indexOfDecimal < amountString.length()) {
            tb.append(Text.of(this.bigNumSeparator.getFormat(), '.'));
            tb.append(Text.of(this.valueFormat, amountString.substring(indexOfDecimal+1)));
        }

        // add formatting for decimal places, this.bigNumSeparator

        Text formattedAmount = tb.build();

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
