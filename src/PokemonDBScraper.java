import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
	This gets all the necessary information that Squirdle uses
	when being played and, therefore, the information needed
	to solve it by scraping two pages from PokemonDB.
*/
public class PokemonDBScraper {	
	
	/*
		NationalDex object which is returned back to the solver
		once filled.
	*/
	NationalDex natDex;

	/*
		Holds the (in-game) National Dex indices that mark
		the beginnings of each Regional Dex's starts. Used and
		explained some more above getGen(). 
	*/
	ArrayList<Integer> genStarts = new ArrayList<Integer>();

	/*
		The URLs for the two pages that are scraped in order to
		get the information Squirdle uses when played. Used and
		explained some more above getDex().
	*/
	String genURL = "https://pokemondb.net/pokedex/national",
			statURL = "https://pokemondb.net/pokedex/stats/height-weight";
	

	/*
		Empty constructor.
	*/
	public PokemonDBScraper() {
		
	}

	/*
		This method creates a new NationalDex object and fills it
		with data scraped from two pages of PokemonDB, which list
		the generations each Pokemon belong to and their characteristics,
		respectively. It then returns the NationalDex object once filled.
	*/
	NationalDex getDex() {
		natDex = new NationalDex();
		
		try {
			Document page = Jsoup.parse(new URL(genURL), 10000);
			Elements table = page.select("div[class=infocard-list infocard-list-pkmn-lg]");
			
			int dexSizeSum = 1;
			genStarts.add(0);
			for(Element child : table) {
				genStarts.add(dexSizeSum += child.childrenSize());
			}
			genStarts.remove(genStarts.size() - 1);
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Could not load dex number wiki page to start scraping (" + genURL + "). Check your internet connection?");
			System.exit(0);
			
		}
		
		HashMap<Integer, ArrayList<Pokemon>> mons = new HashMap<Integer, ArrayList<Pokemon>>();
		
		ArrayList<Pokemon> formList;
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		

		try {
			Document page = Jsoup.parse(new URL(statURL), 10000);
			Element monE, nameField, table = page.selectFirst("tbody");
			Elements types;
			
			Pokemon curr;
			
			int dexNo = 0, gen = 0;
			double height = 0, weight = 0;
			String name = "", type1 = "", type2, typeCombo, form;
			for(int i = 0; i < table.childrenSize(); ++i) {
				type2 = "None";
				form = "None";
				monE = table.child(i);
				gen = -1;
				try {
					dexNo = Integer.parseInt(monE.child(0).select("span").get(1).html());
					nameField = monE.child(1).selectFirst("a");
					name = nameField.html();
					if(monE.child(1).selectFirst("small") != null) {
						form = monE.child(1).selectFirst("small").html();
					}
					
					height = Double.parseDouble(monE.child(4).html());
					weight = Double.parseDouble(monE.child(6).html());
					
					types = monE.child(2).select("a");
					type1 = types.get(0).html();
					
					if(types.size() > 1) {
						type2 = types.get(1).html();
					}

					if(!form.equals("None")) {
						gen = getFormGen(form);
					}
					if(gen == -1) {
						gen = getGen(dexNo);
					}
					
					curr = new Pokemon(name, gen, type1, type2, form, height, weight);
					
					if(mons.get(dexNo) == null || mons.get(dexNo).get(0).checkIfFormChanged(curr)) {
						formList = mons.get(dexNo);
						if(formList == null) {
							formList = new ArrayList<Pokemon>();
						}

						//Gen
						indexList = natDex.monsByGen.containsKey(curr.gen) ? natDex.monsByGen.get(curr.gen) : new ArrayList<Integer>();
						indexList.add(natDex.getDexSize());
						natDex.monsByGen.put(curr.gen,  indexList);
						
						//Type
						typeCombo = curr.type1 + curr.type2;
						indexList = natDex.monsByType.containsKey(typeCombo) ? natDex.monsByType.get(typeCombo) : new ArrayList<Integer>();
						indexList.add(natDex.getDexSize());
						natDex.monsByType.put(typeCombo,  indexList);
						
						//Height
						indexList = natDex.monsByHeight.containsKey(curr.height) ? natDex.monsByHeight.get(curr.height) : new ArrayList<Integer>();
						indexList.add(natDex.getDexSize());
						natDex.monsByHeight.put(curr.height,  indexList);
						
						//Weight
						indexList = natDex.monsByWeight.containsKey(curr.weight) ? natDex.monsByWeight.get(curr.weight) : new ArrayList<Integer>();
						indexList.add(natDex.getDexSize());
						natDex.monsByWeight.put(curr.weight,  indexList);
						
						formList.add(curr);
						mons.put(dexNo, formList);
						//This is down here so I can use its size from 0-max as indices
						//without having to keep track of it in a separate variable
						natDex.monList.add(curr);
					}
					
				}
				//This is here to catch any mons that have been officialy revealed but have not
				//made game appearances and therefore not been given Pokedex numbers.
				catch(NumberFormatException e) {
					continue;
				}
			}
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Could not load stat wiki page to start scraping (" + statURL + "). Check your internet connection?");
			System.exit(0);
		}
		natDex.refreshCandidatesLeft();
		
		return natDex;
	}
	
	
	/*
		This gets the "Generation" of games during which the Pokemon was introduced
		by comparing its Pokedex number to first number of each Pokemon of each
		Generation within the National Dex categorization system; i.e., since
		Gen I has 151 total mons, 152 will mark Gen II's first, and so on.
		 
		Using the starts here instead of the ends also helps avoid a nasty bug that
		can come up if PokemonDB doesn't update its National Dex page in time with
		its stat page - if a Pokemon doesn't exist in PDB's dex list, this program
		can't accurately retrieve the last index of the most recent gen's dex and
		therefore can't solve for them. Using the starts allows you to assume
		whatever comes after the last "Generational start" is part of the same dex.
		
		At the time of writing, all of the mons that were introduced in the
		Teal Mask expansion aren't in the National Dex list but still have listings
		in the stat list :)
		So using the ends would leave them with gen == -1 :))))))
		
			fix ur site
	*/
	int getGen(int dexNo) {
		int gen = 0;
		for(int start : genStarts) {
			if(dexNo < start) {
				break;
			}
			++gen;
		}
		return gen;
	}

	/*
		I had to write this method to more manually assign gens to Pokemon with
		alternate forms, like those with Mega Evolutions and regional variants.
		I don't know how I'd do this via another HTML retrieval and I will have
		to manually update this when another game releases regional forms,
		which sucks big time.
	*/
	int getFormGen(String form) {
		String[] bits = form.split(" ");
		String formMaybe = bits[0];
		switch(formMaybe) {
			case "Mega" :
				return 6;
				
			case "Primal" :
				return 6;
				
			case "Alolan":
				return 7;
			
			case "Galarian":
				return 8;
				
			case "Hisuian":
				return 8;
				
			case "Paldean":
				return 9;
		}
		
		if(bits.length > 1) {
			formMaybe = bits[1];
			switch(formMaybe) {
				case "Cloak":
					return 4;
					
				case "Necrozma":
					return 7;
			
				case "Breed":
					return 9;
				}
		}
		
		switch(form) {
			case "Bloodmoon":
				return 9;
		}
		
		return -1;
	}
	

}
