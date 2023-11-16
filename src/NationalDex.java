
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JOptionPane;

public class NationalDex {

	/*
		Boolean used to indicate when the user wants to have logs
		written that pertain to each individual Pokemon that is
		the solution to each Squirdle game. Only ever able to be
		modified via the ttester's UI, as it's for testing.
	*/
	boolean extremeDetail = false;

	/*
		FileWriter used to write logs pertaining to each individual
		Pokemon that is the solution to each Squirdle game. Only
		writes when extremeDetail is true.
	*/
	FileWriter monLogWriter;

	/*
		Just the number of mons left in the maps. Kept track of here
		so I don't have to iterate through all of the keys of one of
		them and then the lists those keys unlock.
	*/
	int candidatesLeft;
	

	/*
		An ArrayList that contains every single Pokemon in the
		order in which they were scraped from PDB. Never
		modified once the scraping is done and is used to
		rebuild the maps below for multiple games, the test
		programs I've written, and a myriad of minor calls below.
	*/
	ArrayList<Pokemon> monList;
	

	/*
		These maps categorize each Pokemon by the characteristic
		contained in their names for better guessing optimization
		later on. Before each guess, they contain the same Pokemon
		as each other under different keys, but keys are left and
		how many Pokemon are mapped to them after a guess has been
		made are factored in to making a new one.
		
		The height and weight maps are TreeMaps because I needed a
		structure that could give its keys back to me in order from
		least to greatest for logging.
									...I think.
							It's been a while since I wrote this part.
	*/
	HashMap<Integer, ArrayList<Integer>> monsByGen;
	HashMap<String, ArrayList<Integer>> monsByType;
	TreeMap<Double, ArrayList<Integer>> monsByHeight;
	TreeMap<Double, ArrayList<Integer>> monsByWeight;
	

	/*
		Constructor that initializes the monList and all of the maps.
	*/
	NationalDex() {
		monList = new ArrayList<Pokemon>();
		monsByGen = new HashMap<Integer, ArrayList<Integer>>();
		monsByType = new HashMap<String, ArrayList<Integer>>();
		monsByHeight = new TreeMap<Double, ArrayList<Integer>>();
		monsByWeight = new TreeMap<Double, ArrayList<Integer>>();
	}
	

	/*
		Reinitializes all of the maps and puts all of the Pokemon
		from monList back into them so that a new game may be played.
	*/
	public void refreshMaps() {
		monsByGen = new HashMap<Integer, ArrayList<Integer>>();
		monsByType = new HashMap<String, ArrayList<Integer>>();
		monsByHeight = new TreeMap<Double, ArrayList<Integer>>();
		monsByWeight = new TreeMap<Double, ArrayList<Integer>>();

		Pokemon mon;
		String typeCombo;
		ArrayList<Integer> list;
		
		
		for(int i = 0; i < monList.size(); ++i) {
			mon = monList.get(i);
			
			//Gen
			list = monsByGen.containsKey(mon.gen) ? monsByGen.get(mon.gen) : new ArrayList<Integer>();
			list.add(i);
			monsByGen.put(mon.gen,  list);
			
			//Type
			typeCombo = mon.type1 + mon.type2;
			list = monsByType.containsKey(typeCombo) ? monsByType.get(typeCombo) : new ArrayList<Integer>();
			list.add(i);
			monsByType.put(typeCombo,  list);
			
			//Height
			list = monsByHeight.containsKey(mon.height) ? monsByHeight.get(mon.height) : new ArrayList<Integer>();
			list.add(i);
			monsByHeight.put(mon.height,  list);
			
			//Weight
			list = monsByWeight.containsKey(mon.weight) ? monsByWeight.get(mon.weight) : new ArrayList<Integer>();
			list.add(i);
			monsByWeight.put(mon.weight,  list);
		}

		candidatesLeft = monList.size();
	}

	/*
		Removes all Pokemon within the maps who do not comply
		with the results of the last guess in order to shrink
		the pool of Pokemon up for consideration in the next
		guess.
	*/
	//String guessResult's character mappings
	//	Gen - Type1 - Type2 - Height - Weight - Name
	//	 0      1       2       3        4       5
	void pruneMaps(String guessResult, int bestGuessIndex) {
		Pokemon mon, lastGuess = monList.get(bestGuessIndex);
		
		HashMap<String, ArrayList<Integer>> clone = cloneMap(monsByType);
		
		if(extremeDetail) {
			try {
				monLogWriter.write(String.format("\n\nLast guess result: %s (%s)\n\n", guessResult, lastGuess.getTitle()));
			}
			catch(IOException e) {
				JOptionPane.showMessageDialog(null, "Could not write guess result to log.");
			}
		}
		
		//Compare against bestGuess
		for(Object key : clone.keySet()) {
			for(Integer i : clone.get(key)) {
				mon = monList.get(i);
				try {
					
					switch(guessResult.charAt(0)) {
						case '↑':
							if(mon.gen <= lastGuess.gen) {
								throw new Exception(String.format("Gen ↑; (%d) %s's gen %d >= %s's gen %d\n",
									i,
									mon.getTitle(),
									mon.gen,
									lastGuess.getTitle(),
									lastGuess.gen));
							}
							break;
						
						case '↓':
							if(mon.gen >= lastGuess.gen) {
								throw new Exception(String.format("Gen ↓; (%d) %s's gen %d <= %s's gen %d\n",
									i,
									mon.getTitle(),
									mon.gen, lastGuess.getTitle(),
									lastGuess.gen));
							}
							break;
							
						case '✔':
							if(mon.gen != lastGuess.gen) {
								throw new Exception(String.format("Gen ✔; (%d) %s's gen %d != %s's gen %d\n",
									i,
									mon.getTitle(),
									mon.gen,
									lastGuess.getTitle(),
									lastGuess.gen));
							}
					}
	
					
					switch(guessResult.charAt(1)) {							
						case '✖':
							if(mon.type1.equals(lastGuess.type1)) {
								throw new Exception(String.format("Type1 ✖; (%d) %s's type 1 %s ==  %s's type 1 %s \n",
									i,
									mon.getTitle(),
									mon.type1,
									lastGuess.getTitle(),
									lastGuess.type1));
							}
							break;

						case '⇆':
							if(!mon.type2.equals(lastGuess.type1)) {
								throw new Exception(String.format("Type1 ⇆; (%d) %s's type 1 %s !=  %s's type 2 %s \n",
									i,
									mon.getTitle(),
									mon.type2,
									lastGuess.getTitle(),
									lastGuess.type1));
							}
							break;
							
						case '✔':
							if(!mon.type1.equals(lastGuess.type1)) {
								throw new Exception(String.format("Type1 ✔; (%d) %s's type 1 %s ==  %s's type 1 %s \n",
									i,
									mon.getTitle(),
									mon.type1,
									lastGuess.getTitle(),
									lastGuess.type1));
							}
							
					}
					
					switch(guessResult.charAt(2)) {
						case '✖':
							if(mon.type2.equals(lastGuess.type2)) {
								throw new Exception(String.format("Type2 ✖; (%d) %s's type 1 %s !=  %s's type 2 %s \n",
									i,
									mon.getTitle(),
									mon.type1,
									lastGuess.getTitle(),
									lastGuess.type1));
							}
							break;
							
						case '⇆':
							if(!mon.type1.equals(lastGuess.type2)) {
								throw new Exception(String.format("Type2 ⇆; (%d) %s's type 2 %s !=  %s's type 1 %s \n",
										i,
										mon.getTitle(),
										mon.type2,
										lastGuess.getTitle(),
										lastGuess.type1));
							}
							break;
							
						case '✔':
							if(!mon.type2.equals(lastGuess.type2)) {
								throw new Exception(String.format("Type2 ✔; (%d) %s's type 2 %s ==  %s's type 2 %s \n",
									i,
									mon.getTitle(),
									mon.type1,
									lastGuess.getTitle(),
									lastGuess.type1));
							}
						
					}

					switch(guessResult.charAt(3)) {
						case '↑':
							if(mon.height <= lastGuess.height) {
								throw new Exception(String.format("Height ↑; (%d) %s's height %f >= %s's height %f\n",
									i,
									mon.getTitle(),
									mon.height,
									lastGuess.getTitle(),
									lastGuess.height));
							}
							break;
						
						case '↓':
							if(mon.height >= lastGuess.height) {
								throw new Exception(String.format("Height ↓; (%d) %s's height %f <= %s's height %f\n",
									i,
									mon.getTitle(),
									mon.height,
									lastGuess.getTitle(),
									lastGuess.height));
							}
							break;
							
						case '✔':
							if(mon.height != lastGuess.height) {
								throw new Exception(String.format("Height ✔; (%d) %s's height %f != %s's height %f\n",
									i,
									mon.getTitle(),
									mon.height, lastGuess.getTitle(),
									lastGuess.height));
							}
					}

					switch(guessResult.charAt(4)) {
						case '↑':
							if(mon.weight <= lastGuess.weight) {
								throw new Exception(String.format("Weight ↑; (%d) %s's weight %f >= %s's weight %f\n",
									i, 
									mon.getTitle(),
									mon.weight,
									lastGuess.getTitle(),
									lastGuess.weight));
							}
							break;
						
						case '↓':
							if(mon.weight >= lastGuess.weight) {
								throw new Exception(String.format("Weight ↓; (%d) %s's weight  %f <= %s's weight %f\n",
									i,
									mon.getTitle(),
									mon.weight,
									lastGuess.getTitle(),
									lastGuess.weight));
							}
							break;
							
						case '✔':
							if(mon.weight != lastGuess.weight) {
								throw new Exception(String.format("Weight ✔; (%d) %s's weight %f != %s's weight %f\n",
									i,
									mon.getTitle(),
									mon.weight,
									lastGuess.getTitle(),
									lastGuess.weight));
							}
					}
					
					if(guessResult.charAt(5) == '✖' && mon.name.equals(lastGuess.name)) {
						throw new Exception(String.format("Name ✖; Last guess %s was incorrect\n", mon.name));
					}
				}
				catch(Exception e) {
					monsByGen.get(mon.gen).remove(i);
					monsByType.get(key).remove(i);
					monsByHeight.get(mon.height).remove(i);
					monsByWeight.get(mon.weight).remove(i);
					--candidatesLeft;
					if(extremeDetail) {
						try {
							monLogWriter.write(e.getMessage());
						}
						catch (IOException e1) {
							JOptionPane.showMessageDialog(null, String.format("Couldn't write reason for removal of %s in NatDex.", mon.name));
						}
					}
				}
			}
		}
		
		if(extremeDetail)
		{
			try {
				monLogWriter.flush();
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Couldn't flush logWriter in pruneMaps() of NatDex.");
			}
		}
	}
	
	//Used to use "? extends Object" instead of String; changed to avoid having to concatenate strings later
	/*
		Creates and returns a deep clone of a HashMap object;
		only ever used to clone monsByGen because of how many
		fewer keys it will have to go through on average, but
		I'm leaving it like this in case I change my mind on
		something.
	*/
	HashMap<String, ArrayList<Integer>> cloneMap(HashMap<String, ArrayList<Integer>> map) {
		HashMap<String, ArrayList<Integer>> clone = new HashMap<String, ArrayList<Integer>>();
		ArrayList<Integer> list;
		
		for(String key : map.keySet()) {
			list = new ArrayList<Integer>();
			for(Integer i : map.get(key)) {
				list.add(i);
			}
			clone.put(key, list);
		}
		return clone;
	}

	/*
		Gets a Pokemon at the selected index from the monList.
	*/
	Pokemon get(int i) {
		return monList.get(i);
	}

	/*
		Returns the number of Pokemon contained in the natDex
		ArrayList.
	*/
	int getDexSize() {
		return monList.size();
	}

	/*
		Sets the number of Pokemon left in consideration for a
		guess to that of how many there are total in the monList.
	*/
	void refreshCandidatesLeft() {
		candidatesLeft = monList.size();
	}

	/*
		Sets the FileWriter to be used in "extremeDetail logging."
	*/
	void setMonLogWriter(FileWriter monLogWriter) {
		extremeDetail = true;
		this.monLogWriter = monLogWriter;
	}
}
