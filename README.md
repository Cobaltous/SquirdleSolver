# SquirdleSolver
A program that is built to solve "Squirdle", a Pokemon-themed Wordle offshoot done by Fireblend. Built with jsoup and Swing.


This program scrapes data from PokemonDB and uses that information to solve games of Squirdle. To make guesses, it uses a formula to determine the information entropy of any given 'mon choice (with some extra contextual weighting) to determine a best guess for any given state of the game. In addition, there is a testing program that runs game simulations by itself to assure that all Pokemon can be guessed via the solver's guesses. Both the solver and tester come with a UI made with Java Swing for user convenience, and both have been packaged into included prebuilt runnable .jar files.

Source code is available but is uncommented as of writing - read at your own risk.

Should be able to solve a game if its best guesses are used exclusively and hopefully patch up a game where manual guesses/misinputs are entered.


Squirdle: https://squirdle.fireblend.com/

PokemonDB Page: https://pokemondb.net/pokedex/stats/height-weight

jsoup Homepage: https://jsoup.org/



Created to get myself familiar with both libaries and to have something else hosted here.
