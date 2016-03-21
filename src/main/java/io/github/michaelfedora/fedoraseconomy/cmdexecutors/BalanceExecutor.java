package io.github.michaelfedora.fedoraseconomy.cmdexecutors;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Michael on 3/19/2016.
 */
public class BalanceExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("balance", "bal");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Check your balances"))
                .extendedDescription(Text.of("Check your balance, or a specific players"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.optional(GenericArguments.user(Text.of("user"))))
                .executor(new BalanceExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<User> user = args.getOne("user");
        if(!user.isPresent())
            if(src instanceof Player)
                user = Optional.of((User) src);

        if(!user.isPresent())
            throw new CommandException(Text.of("Bad param [user]!"));


        Account account = tryGetAccount(user.get());

        Map<Currency, BigDecimal> balances = account.getBalances();

        int count = 0;

        Text.Builder tb = Text.builder("Balances: ");
        for(Map.Entry<Currency, BigDecimal> entry : balances.entrySet()) {
            tb.append(entry.getKey().format(entry.getValue()));
            if(++count < balances.size())
                tb.append(Text.of(", "));
        }

        src.sendMessage(tb.build());

        return CommandResult.success();
    }
}
