# Okey Match Emulator

In this small project I want to be able emulate a game of Okey.

## What is this game

It's a game from the Rummy family, which is very popular in Turkey (hence my naming of the test players :). Here's
a [Wikipedia link](https://en.wikipedia.org/wiki/Okey), if you want to learn more about the game.

## My Version

My version of the game is slighty different (rules below). I don't choose a seperate Joker, but just use the "fake"
ones, since the idea of cheating with a known deformation of the token doesn't really apply here.

### Run

To start the Emualtor, just run the Main Class.

```shell
gradle run
```

```shell
gradle run --args="YOUR NAMES :)"
```

As for now, there is no GUI, but I will make sure to implement it soon.

## How to play (Game rules)

Exercpt from [Wikipedia.com](https://en.wikipedia.org/wiki/Okey).

### Distribution of tiles and determination of joker

The first dealer is chosen at random. After the hands have been played and scored, the turn to deal passes to the right.

The 106 tiles are placed face down on the table and thoroughly mixed. Then the players set them up into 21 stacks of
five tiles, the tiles in each pile being face down. One tile is left over - this is temporarily kept by the dealer.

There is no specific rule about how many stacks should be in front of each player. It is convenient to have at least six
in front of the dealer, but this makes no real difference to the game.

The dealer now throws the die twice. The result of the first throw selects one of the tile stacks in front of the
dealer, counting from left to right. The dealer places the single remaining tile on top of this selected stack. If the
number thrown is greater than the number of stacks in front of the dealer, then the count will continue using the stacks
in front of the player to dealer's right, and one of these will be selected. The selected stack now has six tiles.

The second throw of the die selects one of the tiles in the selected stack, counting upwards from the bottom of the
stack. The selected tile is extracted from the stack and placed face up on top of it. If the selected tile is a false
joker, it is returned to the selected stack and the second throw of the die is repeated until a numbered tile is
selected.

This face up tile determines the "joker" (okey) for the game - a wild tile that can be used to represent other tiles to
complete a combination. The joker is the tile of the same colour and one number greater than the face up tile. For
example if the face up tile is the green 10, the green 11s are jokers. The false jokers are not wild - they are used
only to represent the tiles that have become jokers. So for example when the green 11s are jokers, the false jokers are
played as green 11s (and cannot represent any other tile). If the face up tile is a 13, the 1s of the same colour are
jokers.

Now the stacks of tiles are distributed to the players. The player to dealer's right will receive 15 tiles and the
others 14 each. The player to the right of the dealer takes the next stack after (to the right of) the selected stack
with the face up tile on top of it, then the player opposite the dealer takes the following stack, and so on
anticlockwise around the table, until each player has two stacks (10 tiles). Now the player to the dealer's right
receives the whole of the next stack, but the player sitting opposite the dealer is given only the top 4 tiles of the
following stack. The player to the dealer's left receives the last tile of this stack and 3 tiles from the top of the
next stack, and finally the dealer takes the last 2 tiles from this stack and 2 from the next stack.

In the above diagram the dealer threw a 5, placed the spare tile on top of the 5th stack from her left. She then threw a
2, and took the second tile from the bottom of the selected stack and placed it on top. It is a red 4, so red 5s will be
jokers for this deal. Now player 2 must take stack 'a', player 3 stack 'b', player 4 'c', player 1 'd', player 2 'e',
player 3 'f', player 4 'g', player 1 'h' and player 2 'i'. Next player 3 takes the top 4 tiles of stack 'j', player 4
the last tile of 'j' and three from 'k', and player 1 two from 'k' and two from 'l'.

All the players should arrange their tiles so that they can see their faces but the other players cannot. Wooden racks
are often used for this. The remaining tiles are left for the players to draw from during the game. They are moved to
the middle of the table, without looking at them or disturbing their order.

### Play

Before the play begins, if any player holds the tile that matches the face up tile on top of the last stack of six
tiles, the player may show that tile, and score one point.

Now the player to the dealer's right begins the play by discarding one tile, face up. After this, each player in turn
may either take the tile just discarded by the previous player, or draw the next tile from the supply in the centre of
the table, and must then discard one unwanted tile. This continues in anticlockwise rotation until a player forms a
winning hand and exposes it, ending the play.

Discarded tiles are placed to the right of the player who discarded them, in a stack, so that only the most recent
discard in the stack is visible.

The usual rule is that you are allowed to look through all the tiles in the discard stacks to your right (the tiles you
discarded) and to your left (the tiles you had an opportunity to take), but you can only see the exposed top tiles of
the two discard stacks on the other side of the table.

The object of the game is to collect sets and runs.

A set consists of three tiles (üçlü) or four tiles (dörtlü) of the same number and different colours. (So for example a
black 7 plus two red 7s would not form a valid set.) A run (el) consists of three or more consecutive tiles of the same
colour. The 1 can be used as the lowest tile, below the 2, or as the highest tile, above the 13, but not both at once.
So green 1-2-3 or yellow 12-13-1 would be valid runs, but black 13-1-2 would not be valid. A winning hand consists of 14
tiles formed entirely into sets and runs - for example two sets of 3 and two runs of 4, or a run of 6 plus a run of 3
plus a set of 4. No tile can be used as part of more than one combination (set or run) at the same time.

Another type of winning hand consists seven pairs. Each pair must consist of two identical tiles (for example two black
9s). Two tiles of the same number and different colours do not make a pair - the colours must be the same as well.

If you have a winning hand, then you can end the play by exposing all 14 of your tiles after discarding. Apart from the
discards and the face up tile on top of the six-tile stack, no tiles are exposed until a player shows a winning hand: no
sets or runs are exposed during the game.

Tiles are always drawn from the top of the next available stack. When only the final stack of 6 tiles remains, the
exposed tile is removed from the top of this stack and the other five tiles are drawn in order. The exposed tile (the
red 4 in the example diagrams) can never be drawn. When there are no tiles left in the centre except the single exposed
tile, if the next player to play does not want to take the previous player's discard, the play ends because there are no
cards left to draw.

As already explained, the two tiles that are the same colour as the face up tile and one greater in number are the
jokers. These tiles can be used to represent any tile the holder desires, in order to complete a set or run. For
example, if the red 4 is face up, the red 5s are jokers. {Green 6, red 5, red 5, green 9} would count as a run, using
jokers for the green 7 and 8. Alternatively, {yellow 10, black 10, red 5} would be a set, using the red 5 to represent
the red or green 10.

When collecting a hand of seven pairs, a joker can be used with any tile to form a pair.

The two false jokers - the tiles without numbers - are used only to represent the joker tiles. So for example when red
5s are jokers, the false jokers are played as red 5s: for example {red 4, false joker, red 6} is a run, and {black 5,
green 5, yellow 5, false joker} is a set.

[New players may wonder what is the purpose of using a different tile as the joker for each game. It would seem to be logically equivalent to use the false jokers as jokers and every tile to be what it appears to be. A possible reason may be that if through any defect in the tiles, some players learn to recognise the false jokers from the back, that would give them an unfair advantage. If a different tile is used as the joker in each game, recognising a joker from the back becomes much more difficult.]

If you have a winning hand of groups and runs using at least one joker, you do not have to expose it immediately. If you
wish, you can continue playing in the hope of forming a winning hand plus a joker. If you are able to end the game by
discarding a joker and exposing your remaining 14 tiles as a winning hand, your win is worth twice as much as an
ordinary win. Note that by continuing to play instead of exposing your ordinary win, you run the risk that another
player may complete a winning hand and expose it before you can achieve your double win, in which case you gain nothing
for your concealed winning hand.

### Scoring

Each player begins the game with 20 points and loses points each time another player wins a game, as follows:

When a player wins an ordinary game, each of the other players loses 2 points. When a player wins by discarding a joker,
each other player loses 4 points. When a player wins with seven pairs, each other player loses 4 points. Also, if at the
start of the play, a player shows the tile that matches the face up tile on the six-tile stack, each of the other
players loses 1 point. This is known as gösterme (to show), and can only be claimed before the holder of the matching
tile first draws a tile.

If the game ends without any player exposing a winning hand (because there are no tiles left to draw, and the player
whose turn it is cannot win by taking the previous discard), then there is no score.

Play continues until any player's score reaches zero or less. The two players with the highest scores at that time are
the winners and the two with the lowest scores are the losers.

## Future thougts

#### I'm thinking of trying to optimize (with AI tools) the way of playing the match as a player. Either in this or another repo. I will leave the link here, if I were to do so.

Note: I actually programmed this in about 30 consecutive hours. Sleep is for the weak :)