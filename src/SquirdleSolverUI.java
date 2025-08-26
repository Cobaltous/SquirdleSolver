import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SquirdleSolverUI {
	static SquirdleSolverUI app;
	
	String[] numDropOps = {"↑", "✔", "↓"}, strDropOps = {"✖", "✔", "⇆"};
	
	SquirdleSolverCore solver;
	JFrame appFrame;
	FlowLayout flow;
	JPanel /*layoutContainerPanel,*/ gbgPanel, feedbackPanel, miscPanel, manualEntryPanel;
	JButton gbgButton, feedbackButton, resetButton;
	JCheckBox manualEntryCheck;
	JComboBox<String> gen, type1, type2, height, weight, manualEntryBox;
	LinkedList<JComboBox<String>> boxes; 
	JLabel bgLabel, guessesLeftLabel;
	
	Pokemon bestGuess, manualGuess;
	
	int guessesLeft = 8;
	
	public SquirdleSolverUI() {
		solver = new SquirdleSolverCore();

		flow = new FlowLayout();
		
		appFrame = new JFrame();
		
//		layoutContainerPanel = new JPanel();
//		layoutContainerPanel.setLayout(flow);
//      flow.setAlignment(FlowLayout.TRAILING);
		
		gbgPanel = new JPanel();
		feedbackPanel = new JPanel();
		miscPanel = new JPanel();
		manualEntryPanel = new JPanel();
		
//		JPanel panel = new JPanel();
//		gbgPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		gbgPanel.setBorder(BorderFactory.createBevelBorder(0));
//		gbgPanel.setLayout(new GridLayout(2, 1));
//		gbgPanel.setLayout(new GridLayout(2, 4));
		feedbackPanel.setBorder(BorderFactory.createBevelBorder(0));
		manualEntryPanel.setBorder(BorderFactory.createBevelBorder(0));
		miscPanel.setBorder(BorderFactory.createBevelBorder(0));
		
		gbgButton = new JButton("Get Best Guess");

		//I *think* you can do an anonymous function here because
		//ActionListener is an interface
		gbgButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bestGuess = solver.getBestGuess();
				switch(solver.candidatesLeft) {
					case 1:
						bgLabel.setText("Final " + getGuessState().split(" ", 2)[1]);
						break;
					case 0:
						bgLabel.setText("I dunno man, I think you broke it. There's nothing left.");
						break;
					default:
						bgLabel.setText("Best Guess: " + bestGuess.getTitle());
						feedbackButton.setEnabled(true);
				}
				gbgButton.setEnabled(false);
				manualEntryCheck.setSelected(false);
			}
		});
		
		feedbackButton = new JButton("Submit");
		feedbackButton.setEnabled(false);
		
		feedbackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				for(JComboBox<String> box : boxes) {
					switch(box.getSelectedItem().toString().charAt(0)) {
						case '✖':
							sb.append('✖');
							break;
							
						case '↑':
							sb.append('↑');
							break;
							
						case '↓':
							sb.append('↓');
							break;
							
						case '✔':
							sb.append('✔');
							break;
							
						case '⇆':
							sb.append('⇆');
					}
				}
				//Here so I can still use the tester to check for names, too.
				sb.append('X');
				if(manualEntryCheck.isSelected()) {
					solver.setManualGuess(manualEntryBox.getSelectedIndex());
				}
				solver.pruneMaps(sb.toString());
				
				--guessesLeft;
				guessesLeftLabel.setText(getGuesses());

				if(guessesLeft == 0 /* || solver.candidatesLeft == 1 */) {
					gbgButton.setEnabled(false);
					feedbackButton.setEnabled(false);
					manualEntryBox.setEnabled(false);
					bgLabel.setText("Final " + getGuessState().split(" ", 2)[1]);
				}
				else {
					gbgButton.setEnabled(true);
					feedbackButton.setEnabled(false);
					
					bestGuess = null;
					bgLabel.setText("Best Guess:");
				}
				manualEntryCheck.setSelected(false);
//				feedbackButton.setText(sb.toString());;
			}
		});
		
		
//		https://docs.oracle.com/javase/tutorial/uiswing/components/button.html
//		https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html
		gen = new JComboBox<String>(numDropOps);
//		gen.setSelectedIndex(1);
		
		type1 = new JComboBox<String>(strDropOps);
		type2 = new JComboBox<String>(strDropOps);
		
		height = new JComboBox<String>(numDropOps);
//		height.setSelectedIndex(1);
		weight = new JComboBox<String>(numDropOps);
		
		boxes = new LinkedList<JComboBox<String>>();
		
		boxes.add(gen);
		boxes.add(type1);
		boxes.add(type2);
		boxes.add(height);
		boxes.add(weight);
//		weight.setSelectedIndex(1);
		
//		gen.add(higher);
//		gen.add(correct);
//		gen.add(lower);
		
		bgLabel = new JLabel("Best Guess: ");
		guessesLeftLabel = new JLabel(getGuesses());
		
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bgLabel.setText("Best Guess:");

				
				guessesLeft = 8;
				guessesLeftLabel.setText(getGuesses());
				

				feedbackButton.setEnabled(false);
				gbgButton.setEnabled(true);
				manualEntryBox.setEnabled(true);
				manualEntryCheck.setEnabled(true);
				manualEntryCheck.setSelected(false);
				
				for(JComboBox<String> box : boxes) {
					box.setSelectedIndex(0);
				}
				
				bestGuess = null;
				
				solver.refreshMaps();
				guessesLeftLabel.setText(getGuesses());
			}
		});
		

		gbgPanel.add(guessesLeftLabel);
		gbgPanel.add(gbgButton);
		gbgPanel.add(bgLabel);
		

//		feedbackPanel.setLayout(new GridLayout(2, 6));

		feedbackPanel.add(gen);
		feedbackPanel.add(type1);
		feedbackPanel.add(type2);
		feedbackPanel.add(height);
		feedbackPanel.add(weight);
		feedbackPanel.add(feedbackButton, BorderLayout.CENTER);
		
		miscPanel.add(resetButton, BorderLayout.CENTER);
		
		manualEntryBox = new JComboBox<String>(solver.getDexArray());
		
		
		manualEntryCheck = new JCheckBox();
		manualEntryCheck.setText("Manually enter a guess?");
		manualEntryCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gbgButton.setEnabled(!manualEntryCheck.isSelected() && bestGuess == null);
				manualGuess = solver.getMonAtIndex(manualEntryBox.getSelectedIndex());
				solver.setManualGuess(manualEntryBox.getSelectedIndex());
				bgLabel.setText(getGuessState());
				feedbackButton.setEnabled(manualEntryCheck.isSelected() ||  bestGuess != null);
			}
		});
		
		manualEntryBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manualEntryCheck.setSelected(true);
				manualGuess = solver.getMonAtIndex(manualEntryBox.getSelectedIndex());
				solver.setManualGuess(manualEntryBox.getSelectedIndex());
				feedbackButton.setEnabled(true);
				bgLabel.setText(getGuessState());
			}
		});
		
		manualEntryPanel.setLayout(new GridLayout(1, 2));
		manualEntryPanel.add(manualEntryBox);
		manualEntryPanel.add(manualEntryCheck);
		
		appFrame.add(gbgPanel, BorderLayout.NORTH);
		appFrame.add(feedbackPanel, BorderLayout.CENTER);
		appFrame.add(miscPanel, BorderLayout.SOUTH);
		appFrame.add(manualEntryPanel, BorderLayout.EAST);
		
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.setTitle("Squirdle Solver");
		
		appFrame.pack();
		appFrame.setVisible(true);
	}
	
	String getGuesses() {
		return (String.format("Candidates Left: %d; Guesses left: %d", solver.getCandidatesLeft(), guessesLeft));
	}
	
	String getGuessState() {
		return manualEntryCheck.isSelected() ?
				("Manual Guess: " + manualGuess.getTitle()) :
					("Best Guess: " + (bestGuess == null ? "" : bestGuess.getTitle()));
	}

	public static void main(String[] args) {
		app = new SquirdleSolverUI();
	}
//
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		label.setText("Best Guess: " + solver.getBestGuess().name);
//	}
}