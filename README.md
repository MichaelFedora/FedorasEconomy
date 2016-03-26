# Fedora's Economy
#### Built for `Sponge 4.1.0-SNAPSHOT`, and off of `JDK 1.8.0_71`.

Just another (light-weight) Economy implementation ^ _^ .

Currently in BETA (3), will release soon though, just a couple more tweaks. (really soon now!)

---
### Features
 - H2 Database backend
 - Multi-currency support
 - Currency cataloging support (for developers, `GameRegistry` stuff)
 - Custom Currencies (with lots of formatting options!)
 - Permissions (WIP)
---
### Command List

#### Key
```python
[group, of, aliases]
(optional)
<required>
```
#### Global Commands
```python
/balance (user) # The user to check the balance of, or you (the sender) if unspecified
/pay <user> <amount> <currency> # Pays the specified user the amount of currency
/money (user) # Alias to `/balance`
/money pay <user> <amount> <currency> # Alias to `/pay` (pretty fancy huh?)
```
#### Plugin Commands
```python
/fe # Root command, shows version info
/fe [currency, c] ... # The currency subcommand group (see below)
/fe [user, u] ... # The user subcommand group (see below)
/fe [help, ?] (cmd) # The help command, shows all subcommands & usages or the specified command's details
/fe config [-cos, -cleanOnStart <cleanOnStart>] [-dc, -defaultCurrency <defaultCurrency> # Change the main-config (via flags)
/fe reload # Reloads every config
/fe [list, l] # Lists all the (valid) accounts in the database
/fe clean <accountName> # Cleans the account of bad-references (to old currencies)
/fe purge # Purges the database of empty-data accounts (virtual & unique)
/fe [get, balance, bal] <accountName> # Gets the balance of the specified account
/fe [getraw, rawbalance, rawbal] <accountName> # Gets the "raw balance" of the account (i.e. going through database instead of by the `Account#GetBalances`)
/fe set <accountName> <amount> <currency> # Sets the account's amount of currency to that specified
/fe add <accountName> <amount> <currency> # Adds the amount specified to the account's balance
/fe pay <accountFrom> <userTo> <amount> <currency> # Pays the specified user the amount from the specified account
/fe transfer <accountFrom> <accountTo> <amount> <currency> # Transfers the amount specified from the first account to the second
```
##### Currency
```python
/fe [currency, c] # Root command, shows subcommands
/fe [currency, c] [help, ?] (cmd) # The help command, shows all subcommands & usages or the specified command's details
/fe [currency, c] reload # reloads all the currency configs (and registry)
/fe [currency, c] [list, l] # Lists all currencies
/fe [currency, c] [details, cat] <currency> # Shows the details of a currency
/fe [currency, c] setdefault <currency> # Sets the default currency (does not persist without `/fe config!`)
```
##### User
```python
/fe [user, u] # Root command, shows subcommands
/fe [user, u] [help, ?] (cmd) # The help command, shows all subcommands & usages or the specified command's details
/fe [user, u] [list, l] # Lists all the (valid) unique-accounts in the database
/fe [user, u] clean <user> # Cleans the user's account of bad-references (to old currencies)
/fe [user, u] [get, balance, bal] <user> # Gets the balance of the specified user's account
/fe [user, u] [getraw, rawbalance, rawbal] <user> # Gets the "raw balance" of the user's account (i.e. going through database instead of by the `Account#GetBalances`)
/fe [user, u] set <user> <amount> <currency> # Sets the user's amount of currency to that specified
/fe [user, u] add <user> <amount> <currency> # Adds the amount specified to the user's balance
/fe [user, u] pay <userFrom> <userTo> <amount> <currency> # Pays the specified user(To) the amount from the other specified user(From)
/fe [user, u] transfer <userFrom> <accountTo> <amount> <currency> # Transfers the amount specified from the user to the account
```
---
### TODO
 - MySQL Support (low priority)
 - Better permissions (medium priority)
---
### Most recent changelog(s)
#### v1.0-PRE-1
- Almost almost almost ready to release.
- Added some colors to the console output (!)
- Added to reload configs (main/currencies)
- Added the ability to edit the main config (only)
- Added the ability to switch default-currencies
- Added (or at least attempting to add) stats
- Some other minor changes

#### v1.0-BETA-3
- Finished adding all of the commands.
- Moved the currencies directory to "currencies".
- Updated main config file to allow for cleaning & purging accounts.
- **BREAKING:** Adjusted all account identifiers to have the `account:` prefix in the database.
 - This means all older-versioned accounts will be broken now :disappointed: Delete your h2/database files (`/mods/FedorasData/econAccounts.*`) and start over!
- Added output for transactions (deposit/withdraw), like if someone payed you something.
- Many other things.
---
### Links

[Github Source](https://github.com/MichaelFedora/FedorasEconomy)

[Downloads](https://github.com/MichaelFedora/FedorasEconomy/releases)

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

![stats or something](http://i.mcstats.org/Fedora's%20Economy/Global+Statistics.png)

<right><sup>*Soli deo gloria*</sup></right>