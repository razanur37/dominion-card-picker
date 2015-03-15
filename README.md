# Dominion Card Picker 1.1.0
This is a Java-based tool to generate a game of [Dominion](http://riograndegames.com/Game/278-Dominion). You choose which sets you wish use (as well as any promotional cards) and the game will randomly choose 10 cards from those sets to be used for your game. You can also restrict the cards that are chosen with various options (EG requiring no attack cards, or requiring a defense card if an attack has been selected).

# Usage
This tool requires at least Java Runtime Environment (JRE) 8. If you do not have JRE 8, you can obtain it from [Oracle](https://java.com/en/download/).

# Help
## Selecting the Sets to Use
To select which sets you want the program to choose from, simply check the box next to each set name. If you want to use any of the promotional cards, just check the boxes next to their names. At least 1 set must be selected before a game can be generated.

## Options
If you wish, you can set guidelines for the program by selecting any of the options. A description of each option follows.

### Use 3-5 Alchemy Card Recommendation
The instructions for the Alchemy set recommend you play using 3-5 cards from the set. If you wish to follow this recommendation, check the box.

Note that this option will be disabled unless you select another set <i>in addition to</i> Alchemy.

### No Attacks
If selected, the generated game will not have any cards that have the `Attack` type.

Selecting this will disable the `No Cursing` and `Require Defense` options.

### No Cursing
If selected, the generated game will not contain any cards which causes players to gain Curses.

Note that this will only exclude cards which explicitly say "gain a Curse".
There are some cards that, when used in a very specific way, can cause other players to gain Curses (such as Ambassador from the Seaside expansion). These cards will not be excluded and may still be selected for a game, even with this option selected.

### Require Buys
If selected, the generated game will include at least 1 card that grants an additional buy.
Note that this may include cards that will not always give an extra buy.

### Require Card Draw
If selected, the generated game will include at least 1 card that lets the player draw 2 or more cards.
Note that this may include cards that will not always let you draw 2 or more cards

### Require Defense
If selected, the generated game will contain at least 1 card that can prevent an attack from affecting the player, provided the game contains at least 1 attack.

Note that there are only 2 cards that completely negate attacks, Moat from the Base game and Lighthouse from Seaside. In order for this option to be selected, at least 1 of those sets (Base or Seaside) must be selected.

### Require Extra Actions
If selected, the generated game will have at least 1 card in it that supplies 2 or more actions.

Note that this may include cards that will not always give extra actions. For example, the Intrigue card Governor gives the player the choice between gaining 3 cards or 2 actions. Because it is at least <i>possible</i> for Governor to give extra actions is considered to give extra actions by the program.
Additionally the Seaside card Tactician gives the player a single action on their next turn if they used it to discard any cards on their current turn. Because the player won't need to spend an action in order to play Tactician on their next turn, the program considers it to give extra actions, despite it only giving 1.

### Require Trashing
If selected, the generated game will have at least 1 card that allows the player to trash <i>another</i> card.

Note that this does not include cards that only trash themselves.
Note that this includes cards which may not always allow the player to trash another card.

## Settings
Below is a description of the various settings available.

### Display Card Images
If enabled, generated games will use pictures of the cards that are chosen. This is the default.

If disabled, generated games will be displayed in a text-only format, listing each cards name, type(s), cost, attributes (if any), and set.

The attributes are directly related to the game options:
- Actions: Card gives at least 2 extra actions
- Buys: Card gives at least 1 buy
- Cards: Card gives at least 2 cards
- Curse: Card instructs opponents to gain a Curse
- Defense: Card is a complete defense against an Attack
- Ruins: Card forces other players to gain at least 1 Ruins (a card type specific to Dark Ages)
- Trash: Card lets the player trash other cards

### Sort By
This group of settings allows the user to choose how generated games are sorted.

Possible options are:
- Name: The cards will be sorted by name, alphabetically. This is the default.
- Cost: The cards will be sorted from lowest cost to highest cost. Cards with the same cost will be further sorted by name, alphabetically.
- Set, then Name: The cards will be sorted by set, alphabetically. Cards from the same set will be further sorted by name, alphabetically.
- Set, then Cost: The cards will be sorted by set, alphabetically. Cards from the same set will be further sorted by cost, from lowest to highest. Cards from the same set with the same cost will be further sorted by name, alphabetically.

## Generating the Game
To generate the game, simply press `Generate` after selecting your set(s) and options(s).

Be aware that the Cornucopia card Young Witch requires the game contain an 11th Kingdom card stack. If the program chooses Young Witch to be used in the game, it will also automatically select the 11th stack (or Bane card stack). This card will be labeled as the Bane card and will appear below the other cards.
Because Young Witch requires the Bane stack be a card that costs either 2 or 3, it is possible (especially if Cornucopia is the only set being used) for Young Witch to be in the game along with all 2 and 3 cost cards from the selected set(s). If this happens, the program will issue a warning, letting you know of the issue.
There are 3 ways to rectify the issue:
- Regenerate the game, possibly adding additional sets to try and prevent the issue from reoccurring.
- Designate one of the 2 or 3 cost cards in the game as the Bane card and replace it with another card from the selected set(s) that is not in the game.
- If sets are available beyond the ones chosen, pick a card from a different set to act as the Bane card.
