# Fedora's Economy

#### Built for `Sponge 4.2.0-SNAPSHOT` *(`1.8.9`)* and `Sponge v5.0.0-SNAPSHOT` *(`1.10.2`)*, and off of `JDK 1.8.0_101`.

Just another (light-weight) Economy implementation `^_^` .

---

### Features
 - H2 Database backend
 - Multi-currency support
 - Currency cataloging support (for developers, `GameRegistry` stuff)
 - Custom Currencies (with lots of formatting options!)
 - Permissions (WIP)

---

### Command List

#### Legend
```python
[group, of, aliases]
(optionalArg)
<requiredArg>
```

Permissions are pretty easy to deduce from the commands.
**Global** commands are `fedoraseconomy.global.[first_alias_of_command]`
**Plugin** commands are `fedoraseconomy.[first_alias_of_command]` or `fedoraseconomy.[category].[first_alias_of_command]`

For the "category" commands or the root command (`/fe`), instead of the `[first_alias_of_command]` they are `.use`.

#### Global Commands
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/balance (user)` | `fedoraseconomy.global.balance` | The user to check the balance of, or you (the sender) if unspecified |
| `/pay <user> <amount> <currency>` | `fedoraseconomy.global.pay` | Pays the specified user the amount of currency |
| `/money (user)` | see `/balance` | Alias to `/balance` |
| `/money pay <user> <amount> <currency>` | see `/pay` | Alias to `/pay` (pretty fancy huh?) |

#### Plugin Commands

##### Root
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fe` | `fedoraseconomy.use` | Root command, shows version info
| `/fe [currency, c] ...` | `fedoraseconomy.currency.[...]` | The currency subcommand group (see below)
| `/fe [user, u] ...` | `fedoraseconomy.user.[...]` | The user subcommand group (see below)
| `/fe [help, ?] (cmd)` | `fedoraseconomy.help` | The help command, shows all subcommands & usages or the specified command's details
| `/fe config [--cos, --cleanOnStart <true|false>] [--dc, --defaultCurrency <defaultCurrency>] [--v, -verboseLogging <true|false>` | `fedoraseconomy.config` | Changes the main-config, via flags, and saves it
| `/fe getconfig [--cos, --cleanOnStart <cleanOnStart>] [--dc, --defaultCurrency <defaultCurrency>]` | `fedoraseconomy.config` | Gets values from the main-config, via flags
| `/fe reload` | `fedoraseconomy.reload` | Reloads *every* config
| `/fe [list, l]` | `fedoraseconomy.list` |Lists all the (valid) accounts in the database
| `/fe clean <accountName>` | `fedoraseconomy.clean` | Cleans the account of bad-references (to old currencies)
| `/fe purge` | `fedoraseconomy.purge` | Purges the database of empty-data accounts (virtual & unique)
| `/fe [get, balance, bal] <accountName>` | `fedoraseconomy.get` | Gets the balance of the specified account
| `/fe [getraw, rawbalance, rawbal] <accountName>` | `fedoraseconomy.getraw` | Gets the "raw balance" of the account (i.e. going through database instead of by the `Account#GetBalances`)
| `/fe set <accountName> <amount> <currency>` | `fedoraseconomy.set` | Sets the account's amount of currency to that specified
| `/fe add <accountName> <amount> <currency>` | `fedoraseconomy.add` | Adds the amount specified to the account's balance
| `/fe pay <accountFrom> <userTo> <amount> <currency>` | `fedoraseconomy.pay` | Pays the specified user the amount from the specified account
| `/fe transfer <accountFrom> <accountTo> <amount> <currency>` | `fedoraseconomy.transfer` | Transfers the amount specified from the first account to the second
| `/fe [reset, zero] <accountName>` | `fedoraseconomy.reset` | Resets an account's balance to zero

##### Currency
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fe [currency, c]` | `fedoraseconomy.currency.use` | Root command, shows subcommands
| `/fe [currency, c] [help, ?] (cmd)` | `fedoraseconomy.currency.help` | The help command, shows all subcommands & usages or the specified command's details
| `/fe [currency, c] reload` | `fedoraseconomy.currency.reload` | Reloads all the currency configs (and registry)
| `/fe [currency, c] [list, l]` | `fedoraseconomy.currency.list` | Lists all currencies
| `/fe [currency, c] [details, cat] <currency>` | `fedoraseconomy.currency.details` | Shows the details of a currency
| `/fe [currency, c] setdefault <currency>` | `fedoraseconomy.currency.setdefault` | Sets the default currency (does not persist without `/fe config!`)

##### User
| Command & Usage | Permission | Description|
|:----------------|:-----------|:-----------|
| `/fe [user, u]` | `fedoraseconomy.user.use` | Root command, shows subcommands
| `/fe [user, u] [help, ?] (cmd)` | `fedoraseconomy.user.help` | The help command, shows all subcommands & usages or the specified command's details
| `/fe [user, u] [list, l]` | `fedoraseconomy.user.list` | Lists all the (valid) unique-accounts in the database
| `/fe [user, u] clean <user>` | `fedoraseconomy.user.clean` | Cleans the user's account of bad-references (to old currencies)
| `/fe [user, u] [get, balance, bal] <user>` | `fedoraseconomy.user.get` | Gets the balance of the specified user's account
| `/fe [user, u] [getraw, rawbalance, rawbal] <user>` | `fedoraseconomy.user.getraw` | Gets the "raw balance" of the user's account (i.e. going through database instead of by the `Account#GetBalances`)
| `/fe [user, u] set <user> <amount> <currency>` | `fedoraseconomy.user.set` | Sets the user's amount of currency to that specified
| `/fe [user, u] add <user> <amount> <currency>` | `fedoraseconomy.user.add` | Adds the amount specified to the user's balance
| `/fe [user, u] pay <userFrom> <userTo> <amount> <currency>` | `fedoraseconomy.user.pay` | Pays the specified user(To) the amount from the other specified user(From)
| `/fe [user, u] transfer <userFrom> <accountTo> <amount> <currency>` | `fedoraseconomy.user.transfer` | Transfers the amount specified from the user to the account
| `/fe [user, u] [reset, zero] <user>` | `fedoraseconomy.reset` | Resets a user's balance to zero

---

### TODO
 - MySQL Support (low priority)
 - Business support (could be implemented by separate plugin, tbh, so low priority)

---

### Most recent changelog(s)

#### v1.1.1
- Fixed help command highlight-clicking giving `/fm` instead of `/fe`.

#### v1.1.0
- Added `verboseLogging` option to toggle whether or not it should display messages from all transactions vs internal commands only
- Added `fe [reset, zero] <account>` and `fe user [reset, zero] <user>` to reset or "zero" an account's balances
- Added highlighting to `fe config`
- Added `fe getconfig` to get config values
- Updated to Sponge API v4.2.0 & started supporting v5.0.0
- Fixed `fe [user, u]` requiring a user parameter to show subcommands (*sigh*)
- Fixed formatting issues with negative amounts of currency

---

### Links

[![Github Source](https://img.shields.io/badge/source-on%20GitHub-brightgreen.svg)](https://github.com/MichaelFedora/FedorasEconomy)

[![Downloads](https://img.shields.io/github/downloads/MichaelFedora/FedorasEconomy/total.svg)](https://github.com/MichaelFedora/FedorasEconomy/releases)

- Latest: [![Latest](https://img.shields.io/github/downloads/MichaelFedora/FedorasEconomy/latest/total.svg)](https://github.com/MichaelFedora/FedorasEconomy/releases/latest)

[![Issues](https://img.shields.io/github/issues/MichaelFedora/FedorasEconomy.svg)](https://github.com/MichaelFedora/FedorasEconomy/issues)

[![Wiki](https://img.shields.io/badge/wiki-really%20cool-brightgreen.svg)](https://github.com/MichaelFedora/FedorasEconomy/wiki)

---

## Big thanks to a couple of people
 - **God**, for creating this awesome place to live in, and for giving me the time and talents to create this. All glory to him.
 - The **SpongePowered** team for making their API (including all their contributors)
 - And the **SpongeDocs** team, for making it easy to learn!
 - **Flibio** for making *EconomyLite*, and licensing it under the MIT license. I learned the basics of filling out this API thanks to you.
 - **hsyyid** for having examples for the Configuration stuff in your also-MIT-licensed code, *EssentialCmds*

I couldn't have made this without you. :heart:

---

Enjoy!

![stats or something](http://i.mcstats.org/FedorasEconomy/Global+Statistics.borderless.png)

<right><sup>*Soli deo gloria*</sup></right>
