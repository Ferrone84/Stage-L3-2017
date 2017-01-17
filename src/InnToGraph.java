import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InnToGraph {
	private String fileName;
	private String[][] bonds;
	private int dimension;

	public InnToGraph(String fileName) {
		this.fileName = fileName;
	}

	public void displayBonds() {
		for(int i=0; i < bonds.length; i++) {
			for(int j=0; j < bonds[i].length; j++) {
				System.out.print(bonds[i][j]+" / ");
			}
			System.out.println();
		}
	}

	public void getDimInn() throws IOException{
		BufferedReader file = null; //le fichier
		String lineContent = ""; 	//le contenu de la ligne lue
		int line = 0;				//le numero de la ligne
		int tmp = 0;

		try{
			file = new BufferedReader(new FileReader(this.fileName));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}

		while((lineContent = file.readLine()) != null){
			if(line > 5 && lineContent.compareTo("\n") != -1 && lineContent.compareTo("END") != 0
				&& lineContent.charAt(lineContent.length()-1) != '0') {
				tmp++;
			}
			line++;
		}
		file.close();
		this.dimension = tmp;
	}

	public void read() throws IOException{
		BufferedReader file = null; //le fichier
		String lineContent = ""; 	//le contenu de la ligne lue
		int line = 0;				//le numero de la ligne
		int currentLine = 0;

		try{
			file = new BufferedReader(new FileReader(this.fileName));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		bonds = new String[dimension][0];

		while((lineContent = file.readLine()) != null){
			if(line > 5 && lineContent.compareTo("\n") != -1 && lineContent.compareTo("END") != 0
				&& lineContent.charAt(lineContent.length()-1) != '0') {
				String[] tokens = lineContent.split("x");
				for(int i=1; i < tokens.length; i++) {
					tokens[i] = deletePlus(tokens[i]);
					tokens[i] = deleteSpace(tokens[i]);
				}
				bonds[currentLine] = new String[tokens.length];

				this.bonds[currentLine][0] = lineContent;
				for(int i=1; i < tokens.length; i++) {
					this.bonds[currentLine][i] = tokens[i];
				}
				currentLine++;
			}
			line++;
		}
		file.close();
	}

	public void write() {
		FileWriter dest = null;
		String str = "";
		String file = this.fileName.substring(0, fileName.length() - 8) + ".txt";
		StringBuffer strbuff = new StringBuffer();

		for(int i=0; i < this.bonds.length; i++) {
				strbuff.append("INNEQUATION N°"+i+" : "+bonds[i][0]+"\n\t");
			for(int j=1; j < this.bonds[i].length; j++) {
				if(j != this.bonds[i].length-1)
					strbuff.append(bonds[i][j]+"-");
				else
					strbuff.append(bonds[i][j]);
			}
			strbuff.append("\n");
		}

		try{
			dest = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(dest);
			str = strbuff.toString();
			out.write(str);
			out.flush();
			out.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	private String deletePlus(String str) {
		for(int i=0; i < str.length(); i++) {
			if(str.charAt(i) == '+') {
				str = str.substring(0, i) + str.substring(i+1, str.length());
			}
		}
		return str;
	}

	private String deleteSpace(String str) {
		for(int i=0; i < str.length(); i++) {
			if(str.charAt(i) == ' ') {
				str = str.substring(0, i);
				break;
			}
		}
		return str;
	}

	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Il faut rentrer le nom du fichier .poi.ieq en argument.");
			return;
		}
		final String fileName = args[0];
		InnToGraph object = new InnToGraph(fileName);
		try {
			object.getDimInn();
			object.read();
			object.displayBonds();
			object.write();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}

