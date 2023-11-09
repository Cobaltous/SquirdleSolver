# SquirdleSolver
A program that is built to solver "Squirdle", a Pokemon-themed Wordle offshoot done by Fireblend. Built with jsoup and Swing.


This program scrapes data from PokemonDB and uses that information to solve games of Squirdle. To make guesses, it uses a formula to determine the information entropy of any given 'mon choice (with some extra contextual weighting) to determine a best guess for any given state of the game. Comes with a (still barebones) UI made with Java Swing for user convenienc, and also comes with a prebuilt runnable .jar file.

Source code is available but is spaghettified and uncommented as of writing - read at your own risk.

Also comes with a tester that runs simulations for all 'mons that exist, but it isn't very verbose as of yet.


Squirdle: https://squirdle.fireblend.com/

PokemonDB Page: https://pokemondb.net/pokedex/stats/height-weight

jsoup Homepage: https://jsoup.org/



Created to get myself familiar with both libaries and to have something else hosted here.
