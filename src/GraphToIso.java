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

	/**
	* Nom du fichier _graph.txt
	*/
	String fileName;

	/**
	* Va contenir chaque innéquation lue.
	*/
	String[][] facettes;

	/**
	* Rprésente le nombre de facettes contenues dans le fichier _graph.
	*/
	int dimension;

	/**
	* Va contenir la matrice de similarité.
	*/
	int[][] similarityMatrix;

	public GraphToIso(String fileName) {
		this.fileName = fileName;
	}

	/**
	* Cette méthode va afficher la matrice de facettes.
	*/
	public void displayFacettes() {
		System.out.println("Display facettes : (case 0 = nombre de sommets)");
		for(int i=0; i < facettes.length; i++) {
			for(int j=0; j < facettes[i].length; j++) {
				System.out.print(facettes[i][j]+"|");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	* Cette méthode va initialiser la matrice de similarité grâce à la dimension.
	*/
	public void initSimilarityMatrix() {
		similarityMatrix = new int[this.dimension][this.dimension];
		for(int i=0; i < this.dimension; i++) {
			for(int j=0; j < this.dimension; j++) {
				this.similarityMatrix[i][j] = 0;
			}
		}
	}

	/**
	* Cette méthode va afficher la matrice de similarité.
	*/
	public void displaySimilarityMatrix() {
		System.out.println("Display matrice de similarité :");
		for(int i=0; i < this.dimension; i++) {
			for(int j=0; j < this.dimension; j++) {
				if(i == j)
					System.out.print("X "); //affiche la diagonale en "X" car pas utile
				else
					System.out.print(this.similarityMatrix[i][j]+" ");
			}
			System.out.println();
		}
	}

	/**
	* Cette méthode va lire le fichier _graph.txt et le parser pour obtenir tous les liens entre les noeuds des facettes.<br>
	* Elle va ensuite placer ces noeuds dans la matrice de String facettes.
	*/
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
			if(line%2 == 1) { //permet de ne prendre qu'une ligne sur deux (celles qui nous intéressent)
				String[] tmp = lineContent.split(":");
				String nbSommets = tmp[0].substring(1, tmp[0].length()-1); //on garde tout sauf le 1er et le dernier charactère

				String[] bonds = tmp[1].split("/"); //on split sur la deuxième partie du tmp
				bonds[0] = bonds[0].substring(1, bonds[0].length()); //on enlève le petit espace (qui s'était glissé ici pour faire plus jolie dans _graph.txt)

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

	public void match() {
		for(int i=0; i < facettes.length; i++) { //parcours les lignes
			for(int k=i+1; k < facettes.length; k++) {
				if(facettes[i][0].compareTo(facettes[k][0]) == 0 && facettes[i].length-1 == facettes[k].length-1) {
					System.out.println("les facettes "+i+" et "+k+" ont le même nombre de sommets");
					if(facettes[i].length-1 == 1) {
						similarityMatrix[i][k] = 1;
						similarityMatrix[k][i] = 1;
					}
					else {
						String[] save = new String[ (facettes[i].length - 1) *4 ];
						int nbsave = 0;

						for(int j=1; j < facettes[i].length; j++) {
							String[] nodesi = facettes[i][j].split("-");
							String[] nodesk = facettes[k][j].split("-");

							if(nbsave != 0) {
								for(int p=0; p < nbsave; p+=2) {
									if(save[p] != nodesi[0] && save[p] != nodesi[1]) {
										
									}
								}
							}
							else { //1ere itération
								save[nbsave++] = nodesi[0];
								save[nbsave++] = nodesk[0];
								save[nbsave++] = nodesi[1];
								save[nbsave++] = nodesk[1];
							}
						}
					}
				}
			}
		}
	}

	/**
	* Cette méthode va écrire le contenue de la matrice de similarité dans un fichier _sim.txt .
	*/
	public void write() {
		FileWriter dest = null;
		String str = "";
		String file = this.fileName.substring(0, fileName.length() - 10) + "_sim.txt"; //on créer un nouveau nom avec l'ancien
		StringBuffer strbuff = new StringBuffer();

		strbuff.append(dimension+"\n"); //nb facettes
		for(int i=0; i < this.similarityMatrix.length; i++) {
			for(int j=0; j < this.similarityMatrix[i].length; j++) {
				if(i == j)
					strbuff.append("X "); //affiche la diagonale en "X" car pas utile
				else
					strbuff.append(this.similarityMatrix[i][j]+" ");
			}
			if(i != this.similarityMatrix.length -1) //évite un retour à la ligne finale
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
			object.initSimilarityMatrix();
			object.match();
			object.write();
			object.displaySimilarityMatrix();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}