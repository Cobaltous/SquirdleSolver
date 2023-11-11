import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class SquirdleSolver
{	
	PokemonDBScraper scraper;
	
	int bestGuessIndex, candidatesLeft;
	double bestGuessProb;
	NationalDex natDex;
	
	FileWriter logWriter;
	
	boolean verbose;
	
//	public static void main(String[] args)
//	{
//		scraper = new PokemonDBScraper();
//		natDex = scraper.getDex();
//		initializeMaps();
////		getBestGuess();
//		System.out.printf("Best guess: %s, %f\n", natDex.get(bestGuessIndex).name, bestGuessProb);
////		regenerateMaps(natDex.monsByType);
////		for(Integer i : natDex.monsByGen.keySet()) {
////			System.out.printf("Gen %d # of mons: %d\n", i, natDex.monsByGen.get(i).size());
////		}
//	}
	
	public SquirdleSolver(FileWriter logWriter) {
		this.logWriter = logWriter;
		verbose = logWriter == null ? false : true;
		
		scraper = new PokemonDBScraper(logWriter);
		natDex = scraper.getDex();
		initializeMaps();
	}
	
	Pokemon getBestGuess() {
		Pokemon mon;
		bestGuessProb = Double.MAX_VALUE;
		bestGuessIndex = 0;
//		natDex.monsByGen.remove(9);
//		natDex.monsByGen.remove(8);
//		System.out.println("gen size: " + natDex.monsByGen.size());
		
		double monProb,
			relGenHalfPoint = (((double)natDex.monsByGen.size()) / 2) + .5/* + (natDex.monsByGen.size() % 2 == 0 ? .5 : 0)*/,
			relHeightClassHalfPoint = (((double)natDex.monsByHeight.size()) / 2) + .5,
			relWeightClassHalfPoint = (((double)natDex.monsByWeight.size()) / 2) + .5,
			midGenScale;
		
//		System.out.println("gen half point: " + relGenHalfPoint);
//		System.out.println("height half point: " + relHeightClassHalfPoint);
//		System.out.println("weight half point: " + relWeightClassHalfPoint);
//		System.out.println("this shit: " + (((double)natDex.monsByGen.size()) / 2));

		HashMap<Double, Double> heightClassToScale = new HashMap<Double, Double>();
		HashMap<Double, Double> weightClassToScale = new HashMap<Double, Double>();
		
		double heightClassMult = 1.0 / natDex.monsByHeight.keySet().size(), weightClassMult =  1.0 / natDex.monsByWeight.keySet().size(), keyIndex = 0;
		
		for(Double key : natDex.monsByHeight.keySet()) {
//			System.out.printf("Key index %d - %f = %f\n", keyIndex, relHeightClassHalfPoint, Math.abs(floorRelToZero(relHeightClassHalfPoint - keyIndex - 1)));
			heightClassToScale.put(key, 1 - (Math.abs(floorRelToZero(relHeightClassHalfPoint - ++keyIndex) * heightClassMult)));
//			System.out.printf("height class %f scale: %f\n", key, heightClassToScale.get(key));
		}
//		System.out.println();
//		System.out.println();
//		System.out.println();
		keyIndex = 0;
		for(Double key : natDex.monsByWeight.keySet()) {
//			System.out.printf("Key index %d - %f = %f\n", keyIndex, relWeightClassHalfPoint, Math.abs(floorRelToZero(relWeightClassHalfPoint - keyIndex - 1)));
			weightClassToScale.put(key, 1 - (Math.abs(floorRelToZero(relWeightClassHalfPoint - ++keyIndex) * weightClassMult)));
//			System.out.printf("weight class %f scale: %f\n", key, weightClassToScale.get(key));
		}
//		System.exit(1);
		keyIndex = 0;
//		System.out.println("Candidates left: " + candidatesLeft);
		for(Integer key : natDex.monsByGen.keySet()) {
			midGenScale = 1 - (Math.abs(floorRelToZero(relGenHalfPoint - ++keyIndex)) * .1);
//			System.out.println("Gen " + key + " multiplier: " + (1 - (Math.abs(floorRelToZero(relGenHalfPoint - keyIndex)) * .1)));
			for(Integer i : natDex.monsByGen.get(key)) {
//				if(i < 151 || i > 900)
//					break;
				mon = natDex.get(i);
//				System.out.println("Mon: " + mon.name);
				
				monProb = (((double)natDex.monsByGen.get(mon.gen).size()) / candidatesLeft) * midGenScale;
//				System.out.println("Gen multiplier: " + ((Math.abs(natDex.monsByGen.keySet().size() / 2 + (natDex.monsByGen.keySet().size() % 2 == 0 ? .5 : 0) - mon.gen)) * .1));
//				System.out.println("Gen multiplier: " + (1 - ((Math.abs(floorRelToZero(relGenHalfPoint - mon.gen)) - 1) * .1)));
//				System.out.println("Gen multiplier: " + (1 - (Math.abs(floorRelToZero(relGenHalfPoint - mon.gen)) * .1)));
				
				monProb *= ((double)(natDex.monsByType.get(mon.type1 + mon.type2).size())) / candidatesLeft;
				
				monProb *= (((double)(natDex.monsByHeight.get(mon.height).size())) / candidatesLeft) * heightClassToScale.get(mon.height);
				
				monProb *= (((double)(natDex.monsByWeight.get(mon.weight).size())) / candidatesLeft) * weightClassToScale.get(mon.weight);
				
				monProb = -Math.log(monProb);
				
				if(verbose) {
					try {
						logWriter.write(String.format("%s's prob: %f\n", mon.name, monProb));
					}
					catch (IOException e) {
						System.err.println("Couldn't write mon probability.");
					}
				}
				
				if(monProb < bestGuessProb) {
					bestGuessProb = monProb;
					bestGuessIndex = i;
					if(verbose) {
						try {
							logWriter.write(String.format("Best guess updated to %s: %.2f, index %d\n", mon.name, bestGuessProb, i));
						}
						catch (IOException e) {
							System.err.println("Couldn't write new best guess.");
						}
					}
				}
			}
		}
		if(verbose) {
			try {
				logWriter.write("\n\n\n");
				logWriter.flush();
			}
			catch (IOException e) {
				System.err.println("Couldn't flush logWriter within getBestGuess().");
			}
		}
		return natDex.get(bestGuessIndex);
	}

	void initializeMaps() {
		natDex.initializeMaps();
		candidatesLeft = natDex.candidatesLeft;
	}
	
	void pruneMaps(String result) {
		natDex.pruneMaps(result, bestGuessIndex);
		candidatesLeft = natDex.candidatesLeft;
	}
	
	double floorRelToZero(double d) {
		if(d < 0) {
			return Math.ceil(d);
		}
		return Math.floor(d);
	}
	
	Pokemon getRandomMon() {
		Random rand = new Random();
		return natDex.get(rand.nextInt(natDex.size()));
	}
	
	Pokemon getMonAtIndex(int index) {
		return natDex.get(index);
	}
	
	int getCandidatesLeft() {
		return candidatesLeft;
	}
	
	String[] getDexArray() {
		String[] dexNames = new String[natDex.size()];
		Pokemon mon;
		for(int i = 0; i < natDex.size(); ++i) {
			mon = natDex.get(i);
			dexNames[i] = mon.name + (mon.form.equals("None") ? "" : (" - " + mon.form));
		}
		return dexNames;
	}
	
	int setManualGuess(int i) {
		int hold = bestGuessIndex;
		bestGuessIndex = i;
		return hold;
	}
	
	void setLogWriter(FileWriter logWriter) {
		this.logWriter = logWriter;
		verbose = logWriter == null ? false : true;
		natDex.setLogWriter(logWriter);
	}
}