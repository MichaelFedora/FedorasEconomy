# Fedora's Economy

#### Built for `Sponge 5.1.0-SNAPSHOT` *(`1.10.2`)* and `Sponge v6.1.0-SNAPSHOT` *(`1.11.2`)*, and off of `JDK 1.8.0_144`.

Just another (light-weight) Economy implementation `^_^` .

---

### Features
 - H2 Database backend
 - Multi-currency support
 - Currency cataloging support (for developers, `GameRegistry` stuff)
 - Custom Currencies (with lots of formatting options!)
 - Permissions (WIP)

---

### TODO
 - MySQL Support (low priority)
 - Business support (could be implemented by separate plugin, tbh, so low priority)

---

### Most recent changelog(s)

### v1.1.3
- Updated Gradle to 4.2.1
- Added `/fe toss` to test withdraws
- Made it so you can't pay someone a negative amount
- Made it so you you shouldn't be able to withdraw or deposit a negative amount
  - it'll automatically redirect to the other function
- Updated plugin id so it compiles again
- Compile for 1.11.2 / SpongeAPI v5, on JDK 1.8.0_144
- Dropped 1.8.9 support

#### v1.1.2
- Added permission `fedoraseconomy.global.balance.other` (checking other's balance)
- Moved commands from Readme to Wiki

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
