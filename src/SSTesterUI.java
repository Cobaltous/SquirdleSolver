import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class SSTesterUI {
	static SSTesterUI app; 

	boolean verbose = false, extremeDetail = false;
	FileWriter monLogWriter, testLogWriter;

	SquirdleSolverCore solver;
	
	JFrame appFrame;
	JPanel testInfoPanel, logFilePanel;
	JButton startTestButton, outLocButton;
	JProgressBar progressBar;
	JCheckBox verbosityCheck, detailCheck;
	JFileChooser outLocChooser;
	
	String outFolder = "logs" + File.separator, monLogFolder = "monLogs" + File.separator, outFolderDisplay, finalMessage;
	String[] outPieces;
	
	int guessesRemaining, maxGuesses = 8, mostGuessesUsed, percDone, processIndex;
	IntComparator iComp = new IntComparator();
	DoubleComparator dComp = new DoubleComparator();
	Pokemon monToGuess, bestGuess, hardestGuess;
	
	Task updateTask;	
	
	public static void main(String[] args) {
		app = new SSTesterUI();
	}
	
	public SSTesterUI() {
		
		solver = new SquirdleSolverCore();
		
		appFrame = new JFrame();
		
		testInfoPanel = new JPanel();
		logFilePanel = new JPanel();
		
		startTestButton = new JButton("Start Testing Run");
		
		startTestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				
				startTestButton.setEnabled(false);
				outLocChooser.setEnabled(false);
				verbosityCheck.setEnabled(false);
				detailCheck.setEnabled(false);
				
				updateTask = new Task(progressBar);
				updateTask.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
		                if (e.getPropertyName().equals("progress")) {
		                	progressBar.setValue((int)e.getNewValue());
		                }
					}
				});
				updateTask.execute();
				
//				progressBar.setString("Status");
				
//				test();
				
				startTestButton.setEnabled(true);
				outLocChooser.setEnabled(true);
				verbosityCheck.setEnabled(true);
				detailCheck.setEnabled(true);
				
			}
		});
		
		progressBar = new JProgressBar();
		progressBar.setString("Status: Inactive");
		progressBar.setStringPainted(true);
		
		verbosityCheck = new JCheckBox("Write overall log file?");
		verbosityCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				verbose = !verbose;
				solver.setVerbose(verbose);
				detailCheck.setEnabled(verbose);
				if(!verbose) {
					detailCheck.setSelected(false);
					extremeDetail = verbose;
				}
			}
		});
		
		detailCheck = new JCheckBox("Write extra detailed logs?");
		
		detailCheck.setEnabled(false);
		detailCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				extremeDetail = !extremeDetail;
				solver.setExtremeDetail(extremeDetail);
			}
		});
		
		
		outLocChooser = new JFileChooser(new File("."));

		/*
			This allows the button to more neatly display file locations on Linux machines
			as *well* as Windows, I think	
		*/
		outPieces = outFolder.replace("\\", "\\\\").split("\\\\");
		outLocButton = new JButton("Log Output: " + outPieces[outPieces.length - 1] + File.separator);
		
	    outLocButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent event) {
	    	    outLocChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    	    outLocChooser.setAcceptAllFileFilterUsed(false);
	    	    if(outLocChooser.showOpenDialog(outLocChooser) == JFileChooser.APPROVE_OPTION) {
		    		outFolder = outLocChooser.getSelectedFile().toString();
		    		
		    		outLocChooser.setCurrentDirectory(new File(outFolder));
		    		
		    		outPieces = outFolder.replace("\\", "\\\\").split("\\\\");
		    		outLocButton.setText("Log Output: " + outPieces[outPieces.length - 1]  + File.separator);	    	    	
	    	    }
	    	}
	    });
		
		testInfoPanel.add(startTestButton, BorderLayout.WEST);
		testInfoPanel.add(progressBar, BorderLayout.EAST);

		logFilePanel.add(outLocButton, BorderLayout.WEST);
		logFilePanel.add(verbosityCheck, BorderLayout.CENTER);
		logFilePanel.add(detailCheck, BorderLayout.EAST);

		
		appFrame.add(testInfoPanel, BorderLayout.NORTH);
		appFrame.add(logFilePanel, BorderLayout.SOUTH);

		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.setTitle("Squirdle Solver Tester");
		
//		appFrame.add(gbgPanel, BorderLayout.NORTH);
//		appFrame.add(feedbackPanel, BorderLayout.CENTER);
//		appFrame.add(miscPanel, BorderLayout.SOUTH);
//		appFrame.add(manualEntryPanel, BorderLayout.EAST);
//		
//		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		appFrame.pack();
		appFrame.setVisible(true);
	}
	
	String compareGuesses(Pokemon monToGuess, Pokemon bestGuess) {
		StringBuilder sb = new StringBuilder();
		
		switch(iComp.compare(monToGuess.gen, bestGuess.gen )) {
			case 1:
				sb.append('↑');
				break;
			case -1:
				sb.append('↓');
				break;
			case 0:
				sb.append('✔');
		}
		
		int typeCheck = 0;
		if(monToGuess.type2.equals(bestGuess.type1)) {
			typeCheck = 1;
		}
		else if(!monToGuess.type1.equals(bestGuess.type1)) {
			typeCheck = -1;
		}
		
		switch(typeCheck) {
			case 1:
				sb.append('⇆');
				break;
			case -1:
				sb.append('✖');
				break;
			case 0:
				sb.append('✔');
		}
		
		typeCheck = 0;
		if(monToGuess.type1.equals(bestGuess.type2)) {
			typeCheck = 1;
		}
		else if(!monToGuess.type2.equals(bestGuess.type2)) {
			typeCheck = -1;
		}
		
		switch(typeCheck) {
			case 1:
				sb.append('⇆');
				break;
			case -1:
				sb.append('✖');
				break;
			case 0:
				sb.append('✔');
		}
		
		switch(dComp.compare(monToGuess.height, bestGuess.height)) {
			case 1:
				sb.append('↑');
				break;
			case -1:
				sb.append('↓');
				break;
			case 0:
				sb.append('✔');
		}
		
		switch(dComp.compare(monToGuess.weight, bestGuess.weight)) {
			case 1:
				sb.append('↑');
				break;
			case -1:
				sb.append('↓');
				break;
			case 0:
				sb.append('✔');
		}
		
		//Need to do this to keep the solver from being confused by mons like
		//Plusle and Minun, which have the same attributes but different names
		if(!monToGuess.name.equals(bestGuess.name)) {
			sb.append('✖');
		}
		else {
			sb.append('✔');
		}
		
		return sb.toString();
	}
	
	int getDexSize() {
		return solver.getDexSize();
	}
	
	Pokemon getMonAtIndex(int index) {
		return solver.natDex.get(index);
	}
	
	void setSolverTestLogWriter() {
		solver.setTestLogWriter(testLogWriter);
	}
	
	void setSolverMonLogWriter() {
//		System.out.println("ssmlw null? : " + (monLogWriter == null));
		solver.setMonLogWriter(monLogWriter);
	}

	private class Task extends SwingWorker<Integer, Integer> {
		
		JProgressBar progressBar;
			
		Task (JProgressBar progressBar) { 
			this.progressBar = progressBar;
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			setProgress(0);
			progressBar.setString(getPBarStatus());;
		}
		
		protected Integer doInBackground() throws Exception {


			if(verbose) {
				File dir = new File(outFolder);
				if(!dir.exists()) {
					dir.mkdir();
				}
				if(extremeDetail) {
					dir = new File(outFolder + monLogFolder);
					if(!dir.exists()) {
						dir.mkdir();
					}
				}
			}
			
			try {
				if(verbose) {
					testLogWriter = new FileWriter(outFolder + "overallLog.txt");
					setSolverTestLogWriter();
				}
				
				for(processIndex = 0; processIndex < getDexSize() ; ++processIndex) {
					monToGuess = getMonAtIndex(processIndex);
					if(extremeDetail) {
						try {
							monLogWriter = new FileWriter(new File(String.format("%s%s\\log%d%s.txt",
								outFolder,
								monLogFolder,
								processIndex,
								monToGuess.getTitle().replaceAll(":", ""))));
							
							setSolverMonLogWriter();
							monLogWriter.write("Mon to guess: " + monToGuess.toString() + '\n');
						}
						catch (IOException e) {
							JOptionPane.showMessageDialog(null, "Couldn't write mon to be guessed to log file.");
						}
					}



					if(bestGuess != null) {
						solver.refreshMaps();
					}
					monToGuess = solver.natDex.get(processIndex);
					
					//Keeps the program happy; it doesn't like this being initialized in the loop
					bestGuess = solver.getRandomMon();
					
					
					String result;
					for(guessesRemaining = maxGuesses; guessesRemaining > 0; --guessesRemaining) {
						bestGuess = solver.getBestGuess();
						
						result = compareGuesses(monToGuess, bestGuess);
						
						if(result.equals("✔✔✔✔✔✔")) {
							if(verbose) {
								try {
									testLogWriter.write(String.format("Found mon %s with %d to spare (used %d)\n",
											bestGuess.getTitle(),
											solver.getGuessNo(),
											maxGuesses - solver.getGuessNo()));
									
									testLogWriter.flush();
								}
								catch (IOException e) {
									JOptionPane.showMessageDialog(null, "Couldn't write success message in solvingRun().");
								}
							}
							break;
						}
						solver.pruneMaps(result);
					}
					if(solver.getGuessNo() > mostGuessesUsed) {
						mostGuessesUsed = solver.getGuessNo();
						hardestGuess = bestGuess;
					}
					
					
					if(!monToGuess.equals(bestGuess)) {
						String message = "Final answer mismatch: could not guess " + monToGuess.name + ", guessed "
								+ bestGuess.name + "; got as far as " + processIndex + " of " + getDexSize();
						if(extremeDetail) {
							try {
								monLogWriter.write(message);
								monLogWriter.flush();
							}
							catch (IOException e) {
								JOptionPane.showMessageDialog(null, "Couldn't write test run failure message.");
							}
						}

						JOptionPane.showMessageDialog(null, message);
						System.exit(0);
					}
					if(extremeDetail) {
						try {
							monLogWriter.flush();
							monLogWriter.write(String.format("Guessed #%d %s with %d guesses to spare.\n",
								processIndex,
								hardestGuess.getTitle(),
								maxGuesses - solver.getGuessNo()));
						}
						catch (IOException e) {
							JOptionPane.showMessageDialog(null, "Couldn't write full results of run to log " + processIndex + monToGuess.name + '.');
							System.exit(0);
						}
					}
//					System.out.println(((double)(processIndex + 1)) / getDexSize() * 100);
					setProgress((int)((double)processIndex / getDexSize() * 100));
					progressBar.setString(getPBarStatus());
					
					/*
						Using this String:
						
							String.format("Status: %d%", progressBar.getValue())
						
						instead of
						
						getPBarStatus()
						
						causes the loop to break on the spot without triggering any of
						the exceptions I've made. I don't know why it does this and I
						care too little to find out now that I've found a workaround,
						but this is still strange enough that I'll leave this here in
						case I need to look at it later to fix the same bug.
					*/
					
				}
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Couldn't create initial monLogWriter.");
				System.exit(0);
			}
			return null;
		}

		protected void done() {

			setProgress(100);
			progressBar.setString("Progress: 100%");

			finalMessage = String.format("Finished with all %d mons guessed in time. Most guesses used: %s (%d out of 8 guesses used)\n",
					processIndex,
					hardestGuess.getTitle(),
					mostGuessesUsed);
			
			JOptionPane.showMessageDialog(null, finalMessage);
			if(verbose) {
				try {
					testLogWriter.write(finalMessage);
					testLogWriter.flush();
				}
				catch(IOException e) {
					JOptionPane.showMessageDialog(null, "Couldn't write final results to overall log.");
				}
			}
		}
		
		String getPBarStatus() {
			return String.format("Progress: %d%%", progressBar.getValue());
		}
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