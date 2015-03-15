# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## [1.1.0] - 15-March-2015
### Added
- Added menu bar, with some options.
- Added sorting options, changing sorting option after game has been generated will resort the game according to the new sorting selection.
- Added an option to display a game as a text-only table.
- Preferences persist after exiting.

### Changed
- Changed where card images directory structure to include set.
- Changed the `Require Card Draw` option to put a card that gives at least +2 cards in the game, as opposed to +1 before.
- Added custom icon.

### Fixed
- Fixed bug where a card chosen to fulfil a `Require X` option would not be removed from the global card pool, thus enabling it to be chosen twice for the same game.

## [1.0.2] - 7-March-2015
### Fixed
- Fixed bug that would cause `Require X` options to not be honored if `Require Defense` was selected.
- Fixed bug that would cause `Require X` options to prohibit an additional card with the same attributes from appearing in the game.

## [1.0.1] - 5-March-2015
### Changed
- Modified how SQLite database is accessed. This will allow for the program to be released as a JAR file.

## [1.0.0] - 4-March-2015
### Added
- Official release