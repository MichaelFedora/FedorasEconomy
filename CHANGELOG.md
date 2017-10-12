#### v1.1.3 (Critical)
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
