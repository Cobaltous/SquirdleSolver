import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PokemonDBScraper {
	boolean verbose;
	FileWriter logWriter;
	
	int[] genStarts = {152, 252, 387, 495, 650, 722, 810, 906};
	String dataFolder = "data";
	String url = "https://pokemondb.net/pokedex/stats/height-weight";
	NationalDex natDex;
//	public static void main(String[] args) {
//		getDex();
//	}
//	
	public PokemonDBScraper(FileWriter logWriter) {
		this.logWriter = logWriter;
		verbose = logWriter == null ? false : true;
	}
	
	NationalDex getDex() {
		natDex = new NationalDex(logWriter);
		
//		ArrayList<HashMap<Object, Object>> maps = new ArrayList<HashMap<Object, Object>>();
		
//		HashMap<Integer, ArrayList<Integer>> monsByGen = new HashMap<Integer, ArrayList<Integer>>();
//		HashMap<String, ArrayList<Integer>> monsByType = new HashMap<String, ArrayList<Integer>>();
//		HashMap<Double, ArrayList<Integer>> monsByHeight = new HashMap<Double, ArrayList<Integer>>();
//		HashMap<Double, ArrayList<Integer>> monsByWeight = new HashMap<Double, ArrayList<Integer>>();
		
		
		HashMap<Integer, ArrayList<Pokemon>> mons = new HashMap<Integer, ArrayList<Pokemon>>();
			
		Pokemon curr;
		Element mon;

//			Document page = Jsoup.parse(new File("Size Pokédex_ List of Pokémon by height and weight _ Pokémon Database.html"));
		try {
			Document page = Jsoup.parse(new URL(url), 10000);
			Element table = page.selectFirst("tbody");
			
			Elements types;
			int dexNo = 0, gen = 0;
			double height = 0, weight = 0;
			String name = "", type1 = "", type2, form;
			ArrayList<Pokemon> list;
			
			Element nameField;
			
//				System.out.println("Getting mons");
			for(int i = 0; i < table.childrenSize(); ++i) {
				type2 = "None";
				form = "None";
				mon = table.child(i);
				gen = -1;
//					System.out.println(mon);
				try {
					dexNo = Integer.parseInt(mon.child(0).select("span").get(1).html());
//						System.out.println("gen: " + gen);
					nameField = mon.child(1).selectFirst("a");
					name = nameField.html();
					if(mon.child(1).selectFirst("small") != null) {
						form = mon.child(1).selectFirst("small").html();
//							System.out.println("New form: " + form);
					}
					
//						System.out.println("Name: " + name);
					
//						System.out.println("targetL : " + mon.child(4).html());
					height = Double.parseDouble(mon.child(4).html());
//						System.out.println("Height: " + height);
					weight = Double.parseDouble(mon.child(6).html());
//						System.out.println("Weight: " + weight); 
					
					types = mon.child(2).select("a");
					type1 = types.get(0).html();
//						System.out.println("Type1: " + type1);
					if(types.size() > 1) {
						type2 = types.get(1).html();
//							System.out.println("Type2: " + type2);
					}
					curr = new Pokemon(name, type1, type2, form, height, weight);
//						System.out.println("mons length: " + mons.size() + "; dexNo: " + dexNo);
//						int test = mons.get(dexNo);
//						hold = natDex.get(mons.get(dexNo));
					if(mons.get(dexNo) == null || curr.checkIfFormChanged(mons.get(dexNo).get(0))) {
						if(!form.equals("None")) {
							gen = getGen(form);
						}
						if(gen == -1) {
							gen = getGen(dexNo);
						}
//							System.out.println("Gen of " + form + ": " + gen);
						
						curr.gen = gen;
						
						
//							if(gen > lastGen) {
//								monsByGens.put();
//								pokemon = new ArrayList<Pokemon>();
//								++lastGen;
//							}
						list = mons.get(dexNo);
						if(list == null) {
							list = new ArrayList<Pokemon>();
						}
						list.add(curr);
						mons.put(dexNo, list);

						natDex.add(curr);
						
//							//Gen
//							list = monsByGen.get(gen);
//							if(list == null) {
//								list = new ArrayList<Integer>();
//							}
//							list.add(collecNo);
//							monsByGen.put(gen,  list);
//							
//							//Type
//							list = monsByType.get(type1 + type2);
//							if(list == null) { 
//								list = new ArrayList<Integer>();
//							}
//							list.add(collecNo);
//							monsByType.put(type1 + type2,  list);
//							
//							//Height
//							list = monsByHeight.get(height);
//							if(list == null) { 
//								list = new ArrayList<Integer>();
//							}
//							list.add(collecNo);
//							monsByHeight.put(height,  list);
//							
//							//Weight
//							list = monsByWeight.get(weight);
//							if(list == null) { 
//								list = new ArrayList<Integer>();
//							}
//							list.add(collecNo);
//							monsByWeight.put(weight,  list);
						
						
						
						
//							System.out.println("!! " + dexNo + " Form: " + form);
					}
					
				}
				catch(NumberFormatException e) {
//						System.err.println("NFE in gD()");
					continue;
				}
			}
//				monsByGens.add(pokemon);
//				System.out.println("Regional dexes: " + regionalDexes.size());
		}
		catch(IOException e1) {
			JOptionPane.showMessageDialog(null, "Could not load wiki page to start scraping. Check your internet connection?");
			System.exit(0);
//				System.err.println("Could not load wiki page to start scraping.");
		}
			
			
			
			
		return natDex;
	}
	
	int getGen(String form) {
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
	
	int getGen(int dexNo) {
		int gen = 1;
		for(int start : genStarts) {
			if(dexNo < start) {
				break;
			}
			++gen;
		}
		return gen;
	}

}
