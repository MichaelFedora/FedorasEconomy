package io.github.michaelfedora.fedoraseconomy.cmdexecutors;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.unique.FeUniqueGetExecutor;
import org.spongepowered.api.command.CommandCallable;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Michael on 3/23/2016.
 */
public class MoneyExecutor extends FeExecutorBase {
    public static final List<String> ALIASES = Collections.singletonList("money");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Check your balances"))
                .extendedDescription(Text.of("Check your balance, or a specific players"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.optional(GenericArguments.user(Text.of("user"))))
                .children(Collections.singletonMap(MoneyPayExecutor.ALIASES, (CommandCallable) new PayExecutor()))
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


        UniqueAccount account = tryGetUniqueAccount(user.get().getUniqueId());

        FeUniqueGetExecutor.run(src, account);

        return CommandResult.success();
    }
}
