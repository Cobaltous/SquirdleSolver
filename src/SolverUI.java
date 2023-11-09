import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SolverUI {
	static SolverUI app;
	
	String[] numDropOps = {"↑", "✔", "↓"}, strDropOps = {"✖", "✔", "⇆"};
	
	SquirdleSolver solver;
	JFrame appFrame;
	JPanel gbgPanel, feedbackPanel, miscPanel;
	JButton gbgButton, feedbackButton, resetButton;
	JComboBox<String> gen, type1, type2, height, weight;
	LinkedList<JComboBox<String>> boxes; 
//	JMenuItem higher, lower, correct, incorrect, swap;
	JLabel bgLabel, guessesLeftLabel;
	
	Pokemon bestGuess;
	
	int guessesLeft = 8;
	
	public SolverUI() {
		solver = new SquirdleSolver(null);
		
		appFrame = new JFrame();
		
		gbgPanel = new JPanel();		
		feedbackPanel = new JPanel();
		miscPanel = new JPanel();
		
//		JPanel panel = new JPanel();
		gbgPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		gbgPanel.setLayout(new GridLayout(2, 1));
		
		gbgButton = new JButton("Get Best Guess");
		

		//I *think* you can do an anonymous function here because
		//ActionListener is an interface
		gbgButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bestGuess = solver.getBestGuess();
				switch(solver.candidatesLeft) {
					case 1:
						bgLabel.setText("Final Guess: " + bestGuess.name + (bestGuess.form.equals("None") ? "" : (" - " + bestGuess.form)));
						break;
					case 0:
						bgLabel.setText("Final Guess: I dunno man, I think you broke it. There's nothing left.");
						feedbackButton.setEnabled(true);
						break;
					default:
						bgLabel.setText("Best Guess: " + bestGuess.name + (bestGuess.form.equals("None") ? "" : (" - " + bestGuess.form)));
						feedbackButton.setEnabled(true);
						
						
				}
				gbgButton.setEnabled(false);
			}
		});
		
		
		feedbackButton = new JButton("Submit");
		feedbackButton.setEnabled(false);
		
		feedbackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				for(JComboBox<String> box : boxes) {
					switch((String)box.getSelectedItem()) {
						case "✖":
							sb.append("I");
							break;
							
						case "↑":
							sb.append('H');
							break;
							
						case "↓":
							sb.append('L');
							break;
							
						case "✔":
							sb.append('C');
							break;
							
						case "⇆":
							sb.append('S');
					}
				}
//				for(Component c : feedbackPanel.getComponents()) {
//					if(c instanceof JComboBox) {
//						String item = (String)((JComboBox<String>)c).getSelectedItem();
//						switch(item) {
//							case "✖":
//								sb.append("I");
//								break;
//								
//							case "↑":
//								sb.append('H');
//								break;
//								
//							case "↓":
//								sb.append('L');
//								break;
//								
//							case "✔":
//								sb.append('C');
//								break;
//								
//							case "⇆":
//								sb.append('S');
//						}
//					}
//				}
				sb.append('X');
				solver.pruneMaps(sb.toString());
				
				--guessesLeft;
				guessesLeftLabel.setText(getGuesses());

				gbgButton.setEnabled(true);
				feedbackButton.setEnabled(false);
//				feedbackButton.setText(sb.toString());;
			}
		});

//		higher = new JMenuItem("↑");
//		lower = new JMenuItem("↓");
//		correct = new JMenuItem("✔");
//		incorrect = new JMenuItem("X");
//		swap = new JMenuItem("⇆");
		
		
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
				solver.initializeMaps();

				feedbackButton.setEnabled(false);
				gbgButton.setEnabled(true);
			}
		});
		
		
		gbgPanel.add(gbgButton);
		gbgPanel.add(bgLabel);

		feedbackPanel.add(gen);
		feedbackPanel.add(type1);
		feedbackPanel.add(type2);
		feedbackPanel.add(height);
		feedbackPanel.add(weight);
		feedbackPanel.add(feedbackButton, BorderLayout.CENTER);
		
		miscPanel.add(resetButton, BorderLayout.CENTER);
		
		appFrame.add(gbgPanel, BorderLayout.NORTH);
		appFrame.add(feedbackPanel, BorderLayout.CENTER);
		appFrame.add(miscPanel, BorderLayout.SOUTH);
		
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.setTitle("Squirdle Solver");
		
		appFrame.pack();
		appFrame.setVisible(true);
	}
	
	String getGuesses() {
		return ("Guesses left: " + guessesLeft);
	}

	public static void main(String[] args) {
		app = new SolverUI();
	}
//
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		label.setText("Best Guess: " + solver.getBestGuess().name);
//	}
}