import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Parseur {
	private String fileName;
	private int dimension;
	private int[][] matrix;

	public Parseur(String fileName) {
		this.fileName = fileName;
	}

	public String getNameFile() {
		return fileName;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public void displayMatrix() {
		for(int i=0; i < this.matrix.length; i++) {
			for(int j=0; j < this.matrix[i].length; j++) {
				System.out.print(this.matrix[i][j]+" ");
			}
			System.out.println();
		}
	}

	public void parse() throws IOException{
		BufferedReader file = null; //le fichier
		String lineContent = ""; 	//le contenu de la ligne lue
		int line = 0;				//le numero de la ligne

		try{
			file = new BufferedReader(new FileReader(this.fileName));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		if((lineContent = file.readLine()) != null) {
			this.dimension = Integer.parseInt(lineContent); //1ere ligne = nombre de ligne
		}
		this.matrix = new int[this.dimension][this.dimension];

		while((lineContent = file.readLine()) != null){
			String[] tmp = lineContent.split(" ");
			for(int i=0; i < tmp.length; i++)
				this.matrix[line][i] = Integer.parseInt(tmp[i]);
			line++;
		}
		file.close();
	}

	public void write() {
		FileWriter dest = null;
		String str = "";
		String fileIeq = getNameIeq();
		StringBuffer strbuff = new StringBuffer();

		strbuff.append("DIM = "+this.dimension+"\n");
		strbuff.append("VALID \n");
		for(int i=0; i < this.dimension; i++)
			strbuff.append("0 ");
		strbuff.append("\n\nLOWER_BOUNDS\n");
		for(int i=0; i < this.dimension; i++)
			strbuff.append("0 ");
		strbuff.append("\nUPPER_BOUNDS\n");
		for(int i=0; i < this.dimension; i++)
			strbuff.append("1 ");
		strbuff.append("\n\nINEQUALITIES_SECTION\n");
		for(int i=0; i < this.matrix.length; i++) {
			for(int j=i; j < this.matrix[i].length; j++) {
				if(this.matrix[i][j] == 1) {
					int k = i+1;
					int p = j+1;
					strbuff.append("+x"+k+" +x"+p+" <= 1\n");
				}
			}
		}
		strbuff.append("\nEND\n");

		try{
			dest = new FileWriter(fileIeq);
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

	public String getNameIeq() {
		String str = "";
		int length = 0;

		while(length < this.fileName.length()) {
			if(this.fileName.charAt(length) == '.')
				break;
			length++;
		}
		str = this.fileName.substring(0, length);
		str += ".ieq";

		return str;
	}



	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Il faut rentrer le nom du fichier à parser en argument.");
			return;
		}
		final String fileName = args[0];
		Parseur parseur = new Parseur(fileName);
		try{
			parseur.parse();
			parseur.displayMatrix();
			parseur.write();
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}
}

