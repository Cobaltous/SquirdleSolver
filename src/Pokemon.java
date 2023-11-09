
//Maybe make these iterable by adding a "next" for formes?

public class Pokemon
{
	String name, type1, type2, form;
	int gen;
	double height, weight;
	
	Pokemon(String name, String type1, String type2, String form, double height, double weight)
	{
		this.name = name;
		this.type1 = type1;
		this.type2 = type2;
		this.form = form;
		this.height = height;
		this.weight = weight;
	}
	
	Pokemon(String name, String type1, String type2, String form, int gen, double height, double weight)
	{
		this.name = name;
		this.type1 = type1;
		this.type2 = type2;
		this.form = form;
		this.gen = gen;
		this.height = height;
		this.weight = weight;
	}
	
	public boolean checkIfFormChanged(Pokemon form) {
		return !(/*name.equals(form.name) && */ type1.equals(form.type1) && type2.equals(form.type2) && height == form.height && weight == form.weight);
	}
	
	public String toString() {
		return (name + (!form.equals("None") ? (" (" + form + ") : ") : ": ") + gen + " " + type1 + " " + type2 + " " + height + " " + weight);
	}
}
