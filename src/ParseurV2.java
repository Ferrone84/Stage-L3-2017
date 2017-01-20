import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Classe qui va permetre de parser des fichiers .txt contenant des graphes en forme de matrices ou en forme de liens.<br>
 * Type matrice : première ligne on écrit la dimension, donc le nombre de sommets. Puis on met la matrice.<br>
 * Type noeuds : le fichier devra s'écrire "nameFile" + "_bonds.txt".<br>
 * Première ligne on écrit la dimension, donc le nombre de sommets. Et il devra contenir les liens entre les noeuds de la forme : 1-2 (un lien par ligne)
 *
 * @author Nicolas Duret
 *
 */
public class ParseurV2 {

	/**
	* Nom du fichier à parser
	*/
	private String fileName;

	/**
	* Dimension = nombre de sommets du graphe
	*/
	private int dimension;

	/**
	* Matrice qui va contenir le graphe (0 => pas de lien / 1 => lien)
	*/
	private int[][] matrix;

	public ParseurV2(String fileName) {
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

	/**
	 * Méthode qui va retourner la valeur de la case i,j
     * 
     * @param i
     * 			ligne i de la matrice
     * @param j
     *			colonne j de la matrice
     * @return 	la valeur de la case i,j
     *
     */
	public int getMatrixPos(int i, int j) {
		return matrix[i][j];
	}

	/**
	 * Méthode qui va afficher la matrice
     */
	public void displayMatrix() {
		for(int i=0; i < this.matrix.length; i++) {
			for(int j=0; j < this.matrix[i].length; j++) {
				System.out.print(this.matrix[i][j]+" ");
			}
			System.out.println();
		}
	}

	/**
	 * Méthode qui va lire le fichier .txt et va en extraire une matrice qui représente le graphe 
     */
	public void parseMatrix() throws IOException{
		BufferedReader file = null; //le fichier
		String lineContent = ""; 	//le contenu de la ligne lue
		int line = 0;				//le numero de la ligne

		try{
			file = new BufferedReader(new FileReader(this.fileName));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		if((lineContent = file.readLine()) != null) {			//on regarde la 1ere ligne
			this.dimension = Integer.parseInt(lineContent); 	//1ere ligne = nombre de ligne
		}
		this.matrix = new int[this.dimension][this.dimension];	//on initialise la matrice de la classe

		while((lineContent = file.readLine()) != null){			//on lis le fichier tant qu'on peut
			String[] tmp = lineContent.split(" ");				//on coupe le string en tokens selon le délimiteur
			for(int i=0; i < tmp.length; i++)
				this.matrix[line][i] = Integer.parseInt(tmp[i]);//on récupère la valeur de chaque tokens
			line++; //on passe à la ligne suivante
		}
		file.close();
	}

	/**
	 * Méthode qui va faire la même chose que parseMatrix() mais qui va lire un fichier contenant les liens entre les noeuds
     */
	public void parseBonds() throws IOException{
		BufferedReader file = null; //le fichier
		String lineContent = ""; 	//le contenu de la ligne lue

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
		for(int i=0; i < this.matrix.length; i++) {
			for(int j=0; j < this.matrix[i].length; j++) {
				matrix[i][j] = 0;
			}
		}

		while((lineContent = file.readLine()) != null) { //jen suis la
			String[] tmp = lineContent.split("-");
			for(int k=0; k < tmp.length-1; k++)
				for(int i=0; i < this.matrix.length; i++)
					for(int j=0; j < this.matrix[i].length; j++)
						if(i == Integer.parseInt(tmp[k])-1 && j == Integer.parseInt(tmp[k+1])-1 
						|| j == Integer.parseInt(tmp[k])-1 && i == Integer.parseInt(tmp[k+1])-1)	//la condition "ou" créer les doublons dans la matrice carré
							matrix[i][j] = 1;
		}
		file.close();
	}

	/**
	 * Méthode qui va écrire un fichier .ieq (fichier Porta) qui va correspondre au fichier .txt
     */
	public void write() {
		FileWriter dest = null;	//objet fichier où l'on va écrire
		String str = "";		//contiendra le string écrit dans le fichier
		String fileIeq = getNameIeq(); //nom du fichier où l'on va écrire
		StringBuffer strbuff = new StringBuffer();	//objet qui sera modifié pour contenir toute l'écriture

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
			str = strbuff.toString(); //on transforme en string
			out.write(str); //et on l'écrit dans le fichier
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

	/**
	 * Méthode qui retourne le nom du fichier en .ieq
	 *
     * @return 	le string contenant le nom
     */
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
			System.out.println("Il faut rentrer le nom du fichier txt à parser en argument.");
			System.out.println("Si le fichier a un nom normal il devra avoir une matrice carré binaire.");
			System.out.println("Sinon le fichier doit avoir '_bonds' dans son nom et il devra avoir les liens du graphe.");
			return;
		}
		
		final String fileName = args[0];
		ParseurV2 parseur = new ParseurV2(fileName);
		int length = 0;
		boolean isMatrix = true; //fais la distinction entre un fichier matrice et un fichier lien

		if(fileName.matches("(.*)_bonds(.*)"))
			isMatrix = false;

		if(isMatrix) {
			System.out.println("Fichier de type matrice.");
			try{
				parseur.parseMatrix();
				parseur.displayMatrix();
				parseur.write();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Fichier de type bonds.");
			try{
				parseur.parseBonds();
				parseur.displayMatrix();
				parseur.write();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}

