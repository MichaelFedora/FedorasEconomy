package io.github.michaelfedora.fedoraseconomy.cmdexecutors;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Michael on 3/19/2016.
 */
public class BalanceExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("balance", "bal");
    public static final List<String> MONEY_ALIASES = Collections.singletonList("money");

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

    public static CommandSpec createAsMoneyAlias() {
        return CommandSpec.builder()
                .description(Text.of("Check your balances"))
                .extendedDescription(Text.of("Check your balance, or a specific players"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.optional(GenericArguments.user(Text.of("user"))))
                .children(Collections.singletonMap(PayExecutor.ALIASES, PayExecutor.create()))
                .executor(new BalanceExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        boolean me = false;

        Optional<User> opt_user = args.getOne("user");
        if(!opt_user.isPresent()) {
            if (src instanceof Player) {
                opt_user = Optional.of((User) src);
                me = true;
            }
        }

        if(!opt_user.isPresent())
            throw new CommandException(Text.of("Bad param [user]!"));

        User user = opt_user.get();

        UniqueAccount uniqueAccount = tryGetUniqueAccount(user.getUniqueId());

        Map<org.spongepowered.api.service.economy.Currency, BigDecimal> balances = uniqueAccount.getBalances();

        Text prefix = (!me) ? Text.of(TextStyles.BOLD, TextColors.AQUA, user.getName(), TextColors.GRAY, "'s ") : Text.EMPTY;

        int count = 0;

        Text.Builder tb = Text.builder().append(prefix).append(Text.of(TextStyles.BOLD, TextColors.GOLD, "Balances: "));
        for(Map.Entry<org.spongepowered.api.service.economy.Currency, BigDecimal> entry : balances.entrySet()) {
            tb.append(entry.getKey().format(entry.getValue()));
            if(++count < balances.size())
                tb.append(Text.of(", "));
        }

        src.sendMessage(tb.build());

        return CommandResult.success();
    }
}
