import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JOptionPane;

public class SquirdleSolverCore	{
	boolean verbose = false, extremeDetail = false;
	FileWriter testLogWriter, monLogWriter;
	
	PokemonDBScraper scraper;
	
	NationalDex natDex;
	
	int bestGuessIndex, candidatesLeft, guessNo;
	double bestGuessProb;
	
	public SquirdleSolverCore() {		
		scraper = new PokemonDBScraper();
		natDex = scraper.getDex();
		candidatesLeft = natDex.getDexSize();
		guessNo = 0;
	}
	
	Pokemon getBestGuess() {
		++guessNo;
		if(extremeDetail) {
			try {
				monLogWriter.write(String.format("\n\n\nGetting Best Guess #%d\n\n\n", guessNo));
			}
			catch(IOException e) {
				JOptionPane.showMessageDialog(null, "Couldn't initiate new guess routine.");
			}
		}
		
		//Have to initialize bestGuess because a write() will complain if it isn't
		Pokemon mon, bestGuess = natDex.get(0);
		bestGuessProb = Double.MAX_VALUE;
		bestGuessIndex = 0;

		HashMap<Double, Double> heightClassToScale = new HashMap<Double, Double>();
		HashMap<Double, Double> weightClassToScale = new HashMap<Double, Double>();
		
		
		double monProb,
			midGenScale,
			relGenHalfPoint = (((double)natDex.monsByGen.size()) / 2) + .5/* + (natDex.monsByGen.size() % 2 == 0 ? .5 : 0)*/,
			relHeightClassHalfPoint = (((double)natDex.monsByHeight.size()) / 2) + .5,
			relWeightClassHalfPoint = (((double)natDex.monsByWeight.size()) / 2) + .5,
			heightClassMult = 1.0 / natDex.monsByHeight.keySet().size(),
			weightClassMult =  1.0 / natDex.monsByWeight.keySet().size(),
			keyIndex = 0;
		
		for(Double key : natDex.monsByHeight.keySet()) {
			heightClassToScale.put(key, 1 - (Math.abs(floorRelToZero(relHeightClassHalfPoint - ++keyIndex) * heightClassMult)));
		}
		
		keyIndex = 0;
		for(Double key : natDex.monsByWeight.keySet()) {
			weightClassToScale.put(key, 1 - (Math.abs(floorRelToZero(relWeightClassHalfPoint - ++keyIndex) * weightClassMult)));
		}
		
		keyIndex = 0;
		for(Integer key : natDex.monsByGen.keySet()) {
			midGenScale = 1 - (Math.abs(floorRelToZero(relGenHalfPoint - ++keyIndex)) * .1);
			for(Integer i : natDex.monsByGen.get(key)) {
				
				mon = natDex.get(i);
				
				monProb = (((double)natDex.monsByGen.get(mon.gen).size()) / candidatesLeft) * midGenScale;
				
				monProb *= ((double)(natDex.monsByType.get(mon.type1 + mon.type2).size())) / candidatesLeft;
				
				monProb *= (((double)(natDex.monsByHeight.get(mon.height).size())) / candidatesLeft) * heightClassToScale.get(mon.height);
				
				monProb *= (((double)(natDex.monsByWeight.get(mon.weight).size())) / candidatesLeft) * weightClassToScale.get(mon.weight);
				
				monProb = -Math.log(monProb);
				
				if(extremeDetail) {
					try {
						monLogWriter.write(String.format("%s's prob: %f\n",
								mon.getTitle(),
								monProb));
					}
					catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Couldn't write mon probability.");
					}
				}
				
				if(monProb < bestGuessProb) {
					bestGuessProb = monProb;
					bestGuessIndex = i;
					bestGuess = mon;
					if(extremeDetail) {
						try {
							monLogWriter.write(String.format("\nBest guess updated to %s: %.2f, index %d\n\n", mon.getTitle(), bestGuessProb, i));
						}
						catch (IOException e) {
							JOptionPane.showMessageDialog(null, "Couldn't write new best guess.");
						}
					}
				}
			}
		}
		if(extremeDetail) {
			try {
				monLogWriter.write(String.format("\n\nReturning with best guess #%d %s\n\n",
						guessNo,
						bestGuess.getTitle()));
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Couldn't message signaling return from solvingRun().");
			}
			
		}
		return natDex.get(bestGuessIndex);
	}

	void refreshMaps() {
		natDex.refreshMaps();
		candidatesLeft = natDex.candidatesLeft;
		guessNo = 0;
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
		return natDex.get(rand.nextInt(natDex.getDexSize()));
	}
	
	Pokemon getMonAtIndex(int index) {
		return natDex.get(index);
	}
	
	int getCandidatesLeft() {
		return candidatesLeft;
	}
	
	void refreshCandidatesLeft() {
		natDex.refreshCandidatesLeft();
	}
	
	String[] getDexArray() {
		String[] dexNames = new String[natDex.getDexSize()];
		Pokemon mon;
		for(int i = 0; i < natDex.getDexSize(); ++i) {
			mon = natDex.get(i);
			dexNames[i] = mon.getTitle();
		}
		return dexNames;
	}
	
	int setManualGuess(int i) {
		int hold = bestGuessIndex;
		bestGuessIndex = i;
		return hold;
	}
	
	void setTestLogWriter(FileWriter testLogWriter) {
		verbose = true;
		this.testLogWriter = testLogWriter;
	}
	
	void setMonLogWriter(FileWriter monLogWriter) {
		verbose = true;
		extremeDetail = true;
		this.monLogWriter = monLogWriter;
		natDex.setMonLogWriter(monLogWriter);
	}
	
	void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	void setExtremeDetail(boolean extremeDetail) {
		verbose = extremeDetail;
		this.extremeDetail = extremeDetail;
	}
	
	int getGuessNo() {
		return guessNo;
	}
	
	int getDexSize() {
		return natDex.getDexSize();
	}
}