import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

/**
 * Classe qui va parser les fichiers _graph.txt en matrice de similarité entre les facettes (_sim.txt).
 *
 * @author Nicolas Duret
 *
 */
public class GraphToIso {
	String fileName;
	String[][] facettes; //va contenir chaque innéquation lue
	int dimension;

	public GraphToIso(String fileName) {
		this.fileName = fileName;
	}

	public void displayFacettes() {
		System.out.println("Display facettes :");
		for(int i=0; i < facettes.length; i++) {
			for(int j=0; j < facettes[i].length; j++) {
				System.out.print(facettes[i][j]+"|");
			}
			System.out.println();
		}
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
		if((lineContent = file.readLine()) != null) {			//on regarde la 1ere ligne
			this.dimension = Integer.parseInt(lineContent); 	//1ere ligne = nombre de facettes
		}

		this.facettes = new String[dimension][0]; //on réalloue le nombre de ligne grâce à la dimension

		while((lineContent = file.readLine()) != null) { //on lis tant qu'on peut
			if(line%2 == 1){
				String[] tmp = lineContent.split(":");
				String nbSommets = tmp[0].substring(1, tmp[0].length()-1); //on garde tout sauf le 1er et le dernier charactère

				lineContent = lineContent.substring(tmp[0].length()+2 , lineContent.length()); //on garde tout ce qu'il y a après les ":"

				String[] bonds = lineContent.split("/");

				this.facettes[currentLine] = new String[bonds.length+1];
				this.facettes[currentLine][0] = nbSommets;

				for(int i=0; i < bonds.length; i++)
					this.facettes[currentLine][i+1] = bonds[i];

				currentLine++;
			}

			line++;
		}

		file.close();
	}

	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Il faut rentrer le nom du fichier _graph.txt en argument.");
			return;
		}

		final String fileName = args[0];
		GraphToIso object = new GraphToIso(fileName);
		try {
			object.read();
			object.displayFacettes();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}