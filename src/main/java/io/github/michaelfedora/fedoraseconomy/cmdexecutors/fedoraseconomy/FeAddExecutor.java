package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeAccount;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Created by Michael on 3/19/2016.
 */
public class FeAddExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("add");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Add an amount to an account"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.string(Text.of("accountName")),
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.catalogedElement(Text.of("currency"), Currency.class))
                .executor(new FeAddExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        String accountName = args.<String>getOne("accountName").orElseThrow(() -> new CommandException(Text.of("Bad param [accountName]!")));

        FeAccount account = tryGetAccount(accountName);

        BigDecimal amount = BigDecimal.valueOf(args.<Double>getOne("amount").orElseThrow(() -> new CommandException(Text.of("Bad param [amount]!"))));

        Currency currency = args.<Currency>getOne("currency").orElseThrow(() -> new CommandException(Text.of("Bad param [currency]!")));

        TransactionResult result;
        if(amount.compareTo(BigDecimal.ZERO) < 0)
            result = account.withdraw(currency, amount.abs(), Cause.of(NamedCause.of(src.getName(), src)), null, true);
        else
            result = account.deposit(currency, amount, Cause.of(NamedCause.of(src.getName(), src)), null, true);

        if(result.getResult() != ResultType.SUCCESS) {
            src.sendMessage(Text.of("Could not add ", currency.format(amount), " to ", TextColors.AQUA, account.getDisplayName(), TextColors.RESET, ": ", result.getResult()));
        } else {
            src.sendMessage(Text.of("Added ", currency.format(amount), " to ", TextColors.AQUA, account.getDisplayName(), TextColors.RESET, "!"));
        }

        return CommandResult.success();
    }
}
