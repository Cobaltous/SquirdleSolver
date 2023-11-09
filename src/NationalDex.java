import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class NationalDex {
	int candidatesLeft;
	boolean verbose;
	
	ArrayList<Pokemon> natDex;
	
	HashMap<Integer, ArrayList<Integer>> monsByGen;
	HashMap<String, ArrayList<Integer>> monsByType;
	TreeMap<Double, ArrayList<Integer>> monsByHeight;
	TreeMap<Double, ArrayList<Integer>> monsByWeight;

	FileWriter logWriter;
	
	NationalDex(FileWriter logWriter) {
		this.logWriter = logWriter;
		verbose = logWriter == null ? false : true;
		
		natDex = new ArrayList<Pokemon>();
		
		monsByGen = new HashMap<Integer, ArrayList<Integer>>();
		monsByType = new HashMap<String, ArrayList<Integer>>();
		monsByHeight = new TreeMap<Double, ArrayList<Integer>>();
		monsByWeight = new TreeMap<Double, ArrayList<Integer>>();
	}
	

	public void initializeMaps() {
		monsByGen = new HashMap<Integer, ArrayList<Integer>>();
		monsByType = new HashMap<String, ArrayList<Integer>>();
		monsByHeight = new TreeMap<Double, ArrayList<Integer>>();
		monsByWeight = new TreeMap<Double, ArrayList<Integer>>();
		
		ArrayList<Integer> list;
		Pokemon mon;
		
//		System.out.println("Natdex size: " + natDex.size());
		
		String typeCombo;
		
		for(int i = 0; i < natDex.size(); ++i) {
			mon = natDex.get(i);
			
			//Gen
			list = monsByGen.containsKey(mon.gen) ? monsByGen.get(mon.gen) : new ArrayList<Integer>();
			list.add(i);
			monsByGen.put(mon.gen,  list);
//			System.out.printf("gen %d list size: %d\n", mon.gen, list.size());
			
			//Type
//			list = monsByType.get(mon.type1 + mon.type2);
			typeCombo = mon.type1 + mon.type2;
			list = monsByType.containsKey(typeCombo) ? monsByType.get(typeCombo) : new ArrayList<Integer>();
			list.add(i);
			monsByType.put(typeCombo,  list);
			
			//Height
//			list = monsByHeight.get(mon.height);
			list = monsByHeight.containsKey(mon.height) ? monsByHeight.get(mon.height) : new ArrayList<Integer>();
			list.add(i);
			monsByHeight.put(mon.height,  list);
			
			//Weight
//			list = monsByWeight.get(mon.weight);
			list = monsByWeight.containsKey(mon.weight) ? monsByWeight.get(mon.weight) : new ArrayList<Integer>();
			list.add(i);
			monsByWeight.put(mon.weight,  list);
		}

		candidatesLeft = natDex.size();
	}
	
	
//	public static void regenerateMaps() {
////		monsByGen = new HashMap<Integer, ArrayList<Integer>>();
//		//Clone the map now because it'll be cleared out within the next couple of lines
//		//Clone the type map specifically to get around having to concatenate strings again (?)
//		HashMap<String, ArrayList<Integer>> clone = cloneMap(monsByType);
//		
//		for(Integer key : monsByGen.keySet()) {
//			candidatesLeft -= monsByGen.get(key).size();
//			monsByGen.put(key,  new ArrayList<Integer>());
//		}
//		System.out.printf("Maps trimmed to %d candidates\n", candidatesLeft);
////		monsByType = new HashMap<String, ArrayList<Integer>>();
//		for(String key : monsByType.keySet()) {
//			monsByType.put(key,  new ArrayList<Integer>());
//		}
////		monsByHeight = new HashMap<Double, ArrayList<Integer>>();
//		for(Double key : monsByHeight.keySet()) {
//			monsByHeight.put(key,  new ArrayList<Integer>());
//		}
////		monsByWeight = new HashMap<Double, ArrayList<Integer>>();
//		for(Double key : monsByWeight.keySet()) {
//			monsByWeight.put(key,  new ArrayList<Integer>());
//		}
//		
//		Pokemon mon;
//		ArrayList<Integer> list;
//		
//		for(String key : clone.keySet()) {
////			System.out.println("set contains " + key.toString() + "? : " + clone.containsKey(key));
//			for(Integer i : clone.get(key)) {
////				System.out.println("Integer: " + i);
//				mon = natDex.get(i);
//				
//				//Gen
//				list = monsByGen.get(mon.gen);
////				if(list == null) {
////					list = new ArrayList<Integer>();
////				}
//				list.add(i);
//				monsByGen.put(mon.gen,  list);
////				System.out.printf("gen %d list size: %d\n", mon.gen, list.size());
//				
//				//Type
//				list = monsByType.get(key);
////				if(list == null) { 
////					list = new ArrayList<Integer>();
////				}
//				list.add(i);
//				monsByType.put(key,  list);
//				
//				//Height
//				list = monsByHeight.get(mon.height);
////				if(list == null) { 
////					list = new ArrayList<Integer>();
////				}
//				list.add(i);
//				monsByHeight.put(mon.height,  list);
//				
//				//Weight
//				list = monsByWeight.get(mon.weight);
////				if(list == null) { 
////					list = new ArrayList<Integer>();
////				}
//				list.add(i);
//				monsByWeight.put(mon.weight,  list);
//			}
//		}
//	}
	
	//Result charMap:
	//	Gen - Type1 - Type2 - Height - Weight - Name
	//	 0      1       2       3        4       5
	void pruneMaps(String guessResult, int bestGuessIndex) {
		Pokemon mon, lastGuess = natDex.get(bestGuessIndex);
		
		HashMap<String, ArrayList<Integer>> clone = cloneMap(monsByType);
		
		//Compare against bestGuess
		for(Object key : clone.keySet()) {
			for(Integer i : clone.get(key)) {
				mon = natDex.get(i);
				try {
					
					switch(guessResult.charAt(0)) {
						case 'H':
							if(mon.gen <= lastGuess.gen) {
								throw new Exception(String.format("Gen H; (%d) %s's gen %d >= %s's gen %d\n", i, mon.name, mon.gen, lastGuess.name, lastGuess.gen));
							}
							break;
						
						case 'L':
							if(mon.gen >= lastGuess.gen) {
								throw new Exception(String.format("Gen L; (%d) %s's gen %d <= %s's gen %d\n", i, mon.name, mon.gen, lastGuess.name, lastGuess.gen));
							}
							break;
							
						case 'C':
							if(mon.gen != lastGuess.gen) {
								throw new Exception(String.format("Gen C; (%d) %s's gen %d != %s's gen %d\n", i, mon.name, mon.gen, lastGuess.name, lastGuess.gen));
							}
					}
	
					
					switch(guessResult.charAt(1)) {							
						case 'I':
							if(mon.type1.equals(lastGuess.type1)) {
								throw new Exception(String.format("Type1 I; (%d) %s's type 1 %s ==  %s's type 1 %s \n", i, mon.name, mon.type1, lastGuess.name, lastGuess.type1));
							}
							break;

						case 'S':
							if(!mon.type1.equals(lastGuess.type2)) {
								throw new Exception(String.format("Type1 S; (%d) %s's type 1 %s !=  %s's type 2 %s \n", i, mon.name, mon.type2, lastGuess.name, lastGuess.type1));
							}
							break;
							
						case 'C':
							if(!mon.type1.equals(lastGuess.type1)) {
								throw new Exception(String.format("Type1 C; (%d) %s's type 1 %s ==  %s's type 1 %s \n", i, mon.name, mon.type1, lastGuess.name, lastGuess.type1));
							}
							
					}
					
					switch(guessResult.charAt(2)) {
						case 'I':
							if(mon.type2.equals(lastGuess.type2)) {
								throw new Exception(String.format("Type2 I; (%d) %s's type 1 %s !=  %s's type 2 %s \n", i, mon.name, mon.type1, lastGuess.name, lastGuess.type1));
							}
							break;
							
						case 'S':
							if(!mon.type2.equals(lastGuess.type1)) {
								throw new Exception(String.format("Type2 S; (%d) %s's type 2 %s !=  %s's type 1 %s \n", i, mon.name, mon.type2, lastGuess.name, lastGuess.type1));
							}
							break;
							
						case 'C':
							if(!mon.type2.equals(lastGuess.type2)) {
								throw new Exception(String.format("Type2 C; (%d) %s's type 2 %s ==  %s's type 2 %s \n", i, mon.name, mon.type1, lastGuess.name, lastGuess.type1));
							}
						
					}
//					if(guessResult.charAt(1) == 'S') {
//							typeInfo += 1;
//					}
//					else if(guessResult.charAt(1) == 'I')
//					
//
//					if(guessResult.charAt(2) == 'S') {
//							typeInfo += 1;
//					}
					
//					if(typeInfo > 0 && !mon.type1.equals(lastGuess.type2) && !mon.type2.equals(lastGuess.type1)) {
//						System.out.printf("(%d) %s's types %s %s !=  %s's types %s <-> %s\n", i, mon.name, mon.type1, mon.type2, lastGuess.name, lastGuess.type1, lastGuess.type2);
//						throw new Exception();
//					}

					switch(guessResult.charAt(3)) {
						case 'H':
							if(mon.height <= lastGuess.height) {
								throw new Exception(String.format("Height H; (%d) %s's height %f >= %s's height %f\n", i, mon.name, mon.height, lastGuess.name, lastGuess.height));
							}
							break;
						
						case 'L':
							if(mon.height >= lastGuess.height) {
								throw new Exception(String.format("Height L; (%d) %s's height %f <= %s's height %f\n", i, mon.name, mon.height, lastGuess.name, lastGuess.height));
							}
							break;
							
						case 'C':
							if(mon.height != lastGuess.height) {
								throw new Exception(String.format("Height C; (%d) %s's height %f != %s's height %f\n", i, mon.name, mon.height, lastGuess.name, lastGuess.height));
							}
					}

					switch(guessResult.charAt(4)) {
						case 'H':
							if(mon.weight <= lastGuess.weight) {
								throw new Exception(String.format("Weight H; (%d) %s's weight %f >= %s's weight %f\n", i, mon.name, mon.weight, lastGuess.name, lastGuess.weight));
							}
							break;
						
						case 'L':
							if(mon.weight >= lastGuess.weight) {
								throw new Exception(String.format("Weight L; (%d) %s's weight  %f <= %s's weight %f\n", i, mon.name, mon.weight, lastGuess.name, lastGuess.weight));
							}
							break;
							
						case 'C':
							if(mon.weight != lastGuess.weight) {
								throw new Exception(String.format("Weight C; (%d) %s's weight %f != %s's weight %f\n", i, mon.name, mon.weight, lastGuess.name, lastGuess.weight));
							}
					}
					
					if(guessResult.charAt(5) == 'I' && mon.name.equals(lastGuess.name)) {
						throw new Exception(String.format("Name I; Last guess %s was incorrect\n", mon.name));
					}
				}
				catch(Exception e) {
					monsByGen.get(mon.gen).remove(i);
					monsByType.get(key).remove(i);
					monsByHeight.get(mon.height).remove(i);
					monsByWeight.get(mon.weight).remove(i);
					--candidatesLeft;
					if(verbose) {
						try {
							logWriter.write(e.getMessage());
						}
						catch (IOException e1) {
							System.err.printf("Couldn't write reason for removal of %s\n", mon.name);
						}
//						System.out.printf("Removed mon %s\n", mon.name);
//						System.out.println("Gen " + mon.gen + " list size: " + monsByGen.get(mon.gen).size());
					}
				}
			}
//			System.out.println("Candidates left: " + candidatesLeft);
		}
		if(verbose)
		{
			try {
				logWriter.flush();
			}
			catch (IOException e) {
				System.err.println("Couldn't flush logWriter in pruneMaps().");
			}
		}
//		candidatesLeft = monsByType.size();
//		System.out.println("Candidates left: " + candidatesLeft);
	}
	
	//Used to use "? extends Object" instead of String; changed to avoid having to concatenate strings later
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
	
	Pokemon get(int i) {
		return natDex.get(i);
	}
	
	int size() {
		return natDex.size();
	}
	
	void add(Pokemon mon) {
		ArrayList<Integer> list;
		String typeCombo;
		
		//Gen
		list = monsByGen.containsKey(mon.gen) ? monsByGen.get(mon.gen) : new ArrayList<Integer>();
		list.add(natDex.size());
		monsByGen.put(mon.gen,  list);
//		System.out.printf("gen %d list size: %d\n", mon.gen, list.size());
		
		//Type
//		list = monsByType.get(mon.type1 + mon.type2);
		typeCombo = mon.type1 + mon.type2;
		list = monsByType.containsKey(typeCombo) ? monsByType.get(typeCombo) : new ArrayList<Integer>();
		list.add(natDex.size());
		monsByType.put(typeCombo,  list);
		
		//Height
//		list = monsByHeight.get(mon.height);
		list = monsByHeight.containsKey(mon.height) ? monsByHeight.get(mon.height) : new ArrayList<Integer>();
		list.add(natDex.size());
		monsByHeight.put(mon.height,  list);
		
		//Weight
//		list = monsByWeight.get(mon.weight);
		list = monsByWeight.containsKey(mon.weight) ? monsByWeight.get(mon.weight) : new ArrayList<Integer>();
		list.add(natDex.size());
		monsByWeight.put(mon.weight,  list);
			
		
		
		natDex.add(mon);
		
	}
	
	void setLogWriter(FileWriter logWriter) {
		this.logWriter = logWriter;
		verbose = logWriter == null ? false : true;
	}

}
