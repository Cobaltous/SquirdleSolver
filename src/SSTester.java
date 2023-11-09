import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;

public class SSTester {
	int guessesRemaining;
	IntComparator iComp = new IntComparator();
	DoubleComparator dComp = new DoubleComparator();
	Pokemon monToGuess;
	Pokemon bestGuess;
	SquirdleSolver solver;
	boolean verbose;
	
	FileWriter logWriter;
	
	public SSTester() {
		verbose = logWriter == null ? false : true;
		solver = new SquirdleSolver(logWriter);
	}

	
//	void main(String[] args) {
//		Scanner scan = new Scanner(System.in);
//		Pokemon bestGuess;
//		for(int i = 0; i < 8; ++i) {
//			
//		}
//	}
//	void main(String[] args) {
//		// TODO Auto-generated method stub
//		solver = new SquirdleSolver();
//		monToGuess = solver.getRandomMon();
////		Pokemon monToGuess = solver.getMonAtIndex(1115);
//		
//		System.out.println("Mon to guess: " + monToGuess.toString());
//		String result;
//		for(guessesRemaining += 0; guessesRemaining > 0; --guessesRemaining) {
//			System.out.printf("guessesRemaining left: %d\n", guessesRemaining);
//			bestGuess = solver.getBestGuess();
//			System.out.println("Best Guess: " + bestGuess.toString());
//			result = compareGuesses(monToGuess, bestGuess);
//			System.out.println("Result: " + result);
//			if(result.equals("CCCCC")) {
//				break;
//			}
//			solver.pruneMaps(result);
//			System.out.println();
//		}
//		if(guessesRemaining > 0) {
//			System.out.println("Success!");
//		}
//		else {
//			System.out.println("Failure.");
//		}
//	}
	
	//Returns the whole 'mon object just to be safe 
	public Pokemon solvingRun(int index) {
		if(bestGuess != null) {
			solver.initializeMaps();
		}
		monToGuess = solver.natDex.get(index);
//		monToGuess = solver.getRandomMon();
//		Pokemon monToGuess = solver.getMonAtIndex(1115);
		
		//Keeps the program happy; it doesn't like this being initialized in the loop
		bestGuess = solver.getRandomMon();
		
		String result;
		for(guessesRemaining = 8; guessesRemaining > 0; --guessesRemaining) {
//			System.out.printf("Guesses left: %d\n", guessesRemaining);
			bestGuess = solver.getBestGuess();
//			if(bestGuess.name.equals("Beedrill")) {
//				System.out.println("Remaining members: ");
//				for(int key : solver.monsByGen.keySet()) {
//					for(int i : solver.monsByGen.get(key)) {
//						System.out.println(solver.getMonAtIndex(i));
//					}
//				}
//			}
//			System.out.println("Best Guess: " + bestGuess.toString());
			result = compareGuesses(monToGuess, bestGuess);
//			System.out.println("Result: " + result);
			if(result.equals("CCCCCC")) {
				if(verbose) {
					try {
						logWriter.write(String.format("Found mon %s with %d to spare (used %d)\n", bestGuess.name, guessesRemaining, 8 - guessesRemaining));
						logWriter.flush();
					}
					catch (IOException e) {
						System.err.println("Couldn't write success message in solvingRun().");
					}
				}
				break;
			}
			solver.pruneMaps(result);
//			System.out.println();
		}
		return bestGuess;
		
	}
	
	String compareGuesses(Pokemon monToGuess, Pokemon bestGuess) {
		StringBuilder sb = new StringBuilder();
		
		switch(iComp.compare(monToGuess.gen, bestGuess.gen )) {
			case 1:
				sb.append('H');
				break;
			case -1:
				sb.append('L');
				break;
			case 0:
				sb.append('C');
		}
		
		int typeCheck = 0;
		if(monToGuess.type1.equals(bestGuess.type2)) {
			typeCheck = 1;
		}
		else if(!monToGuess.type1.equals(bestGuess.type1)) {
			typeCheck = -1;
		}
		
		switch(typeCheck) {
			case 1:
				sb.append('S');
				break;
			case -1:
				sb.append('I');
				break;
			case 0:
				sb.append('C');
		}
		
		typeCheck = 0;
		if(monToGuess.type2.equals(bestGuess.type1)) {
			typeCheck = 1;
		}
		else if(!monToGuess.type2.equals(bestGuess.type2)) {
			typeCheck = -1;
		}
		
		switch(typeCheck) {
			case 1:
				sb.append('S');
				break;
			case -1:
				sb.append('I');
				break;
			case 0:
				sb.append('C');
		}
		
		switch(dComp.compare(monToGuess.height, bestGuess.height)) {
			case 1:
				sb.append('H');
				break;
			case -1:
				sb.append('L');
				break;
			case 0:
				sb.append('C');
		}
		
		switch(dComp.compare(monToGuess.weight, bestGuess.weight)) {
			case 1:
				sb.append('H');
				break;
			case -1:
				sb.append('L');
				break;
			case 0:
				sb.append('C');
		}
		
		//Need to do this to keep the solver from being confused by mons like
		//Plusle and Minun, which have the same attributes but different names
		if(!monToGuess.name.equals(bestGuess.name)) {
			sb.append('I');
		}
		else {
			sb.append('C');
		}
		
		return sb.toString();
	}
	
	int getDexSize() {
		return solver.natDex.size();
	}
	
	Pokemon getMonAtIndex(int index) {
		return solver.natDex.get(index);
	}
	
	void setLogWriter(FileWriter logWriter) {
		this.logWriter = logWriter;
		verbose = logWriter == null ? false : true;
		solver.setLogWriter(logWriter);
	}

}

class IntComparator implements Comparator<Integer> {
	@Override
	public int compare(Integer a, Integer b) {
		if(a > b) {
			return 1;
		}
		else if(a < b) {
			return -1;
		}
		return 0;
	}
	
}


class DoubleComparator implements Comparator<Double> {
	@Override
	public int compare(Double a, Double b) {
		if(a > b) {
			return 1;
		}
		else if(a < b) {
			return -1;
		}
		return 0;
	}
	
}