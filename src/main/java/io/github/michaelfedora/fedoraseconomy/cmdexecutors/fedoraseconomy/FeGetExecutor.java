package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Michael on 3/25/2016.
 */
public class FeGetExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("get", "balance", "bal");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Gets the accounts's balances"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.string(Text.of("accountName")))
                .executor(new FeGetExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        String accountName = args.<String>getOne("accountName").orElseThrow(() -> new CommandException(Text.of("Bad param [accountName]!")));

        Account account = tryGetAccount(accountName);

        Map<Currency, BigDecimal> balances = account.getBalances();

        int count = 0;

        Text.Builder tb = Text.builder().append(Text.of(TextColors.AQUA, account.getDisplayName(), TextColors.GOLD, "'s Balances: "));
        for(Map.Entry<Currency, BigDecimal> entry : balances.entrySet()) {
            tb.append(entry.getKey().format(entry.getValue()));
            if(++count < balances.size())
                tb.append(Text.of(", "));
        }

        src.sendMessage(tb.build());

        return CommandResult.success();
    }
}
