import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.Scanner;

import org.junit.jupiter.api.Test;

class SSTest {
	FileWriter logWriter;
	String outFolder = "logs/";

	@Test
	void test() {
		boolean verbose = true;
		
		if(verbose) {
			File dir = new File(outFolder);
			if(!dir.exists()) {
				dir.mkdir();
			}
		}
		try {
			logWriter = new FileWriter("dummy");
		}
		catch (IOException e1) {
			System.err.println("Couldn't create initial logWriter.");
		}
		SSTester tester = new SSTester();
		Pokemon monToGuess, bestGuess;
		
//		tester.startTestingRun(382);
		for(int i = 0; i < tester.getDexSize() ; ++i) {
			monToGuess = tester.getMonAtIndex(i);
			if(verbose) {
				try {
					logWriter = new FileWriter(new File(outFolder + "log" + i + monToGuess.name + ".txt"));
					tester.setLogWriter(logWriter);
					logWriter.write("Mon to guess: " + monToGuess.toString() + '\n');
				}
				catch (IOException e) {
					System.err.println("Couldn't write mon to be guessed.");
				}
			}
			bestGuess = tester.solvingRun(i);
			if(!monToGuess.equals(bestGuess)) {
				String message = "Final answer mismatch: could not guess " + monToGuess.name + ", guessed " + bestGuess.name + "; got as far as " + i + " of " + tester.getDexSize();
				if(verbose) {
					try {
						logWriter.write(message);
						logWriter.flush();
					}
					catch (IOException e) {
						System.err.println("Couldn't write run failure message.");
					}
				}
				fail(message);
			}
			if(verbose) {
				try {
					logWriter.flush();
				}
				catch (IOException e) {
					System.err.println("Couldn't write full results of run to log " + i + monToGuess.name + '.');
				}
			}
		}
		return;
	}

}
