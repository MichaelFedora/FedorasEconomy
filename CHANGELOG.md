#### v1.1.2
- Added permission `fedoraseconomy.global.balance.other` (checking other's balance)
- Moved commands from Readme to Wiki

#### v1.1.1
- Fixed help command highlight-clicking giving `/fm` instead of `/fe`.

#### v1.1.0
- Added `verboseLogging` option to toggle whether or not it should display messages from all transactions vs internal commands only
- Added `fe [reset, zero] <account>` and `fe user [reset, zero] <user>` to reset or "zero" an account's balances
- Added value-highlighting to `fe config`
- Added `fe getconfig` to get config values
- Updated to Sponge API v4.2.0 & started supporting v5.0.0
- Fixed `fe [user, u]` requiring a user parameter to show subcommands (*sigh*)
- Fixed formatting issues with negative amounts of currency

#### v1.0.1
- Adjusted Stats linking

#### v1.0
- Adjusted permissions
- Released (finally)!