import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

/**
 * Classe qui va parser les fichiers .ieq.poi (fichier Porta) contenant les facettes, en fichier _graph.txt qui va en extraire les sous graphes.<br>
 * Cette classe aura besoin du fichier initial .txt qui contient le graphe, pour savoir si les liens entre les noeuds (des sous graphes) existent.<br>
 * L'utilisateur pourras le fournir en 2éme argument si jamais le nom est différent.
 *
 * @author Nicolas Duret
 *
 */
public class InnToGraph {

	/**
	* Nom du fichier .poi.ieq
	*/
	private String fileName;

	/**
	* Contiendra les sous graphes des innégalitées (une innégalité par ligne)
	*/
	private String[][] bonds;

	/**
	* Le nombre d'innégalité qu'a produit le fichier porta .poi.ieq
	*/
	private int dimension;

	/**
	* On créer un objet de l'autre classe pour pouvoir utiliser les méthodes qu'elle implémente qui lisent les fichiers .txt initiaux.
	*/
	private ParseurV2 parseur;

	/**
	* Constructeur qui initialise le nom du fichier.
	*
    * @param fileName
    *			Nom du fichier
	*/
	public InnToGraph(String fileName) {
		this.fileName = fileName;
	}

	/**
	* Méthode qui va afficher les sous graphes
	*/
	public void displayBonds() {
		System.out.println("Facettes et leurs noeuds.");
		for(int i=0; i < bonds.length; i++) {
			for(int j=0; j < bonds[i].length; j++) {
				System.out.print(bonds[i][j]+"|");
			}
			System.out.println();
		}
	}

	/**
	* Méthode qui va lire le fichier pour savoir combien de facettes devront être lue.
	*/
	public void setDimInn() throws IOException{
		BufferedReader file = null; //le fichier
		String lineContent = ""; 	//le contenu de la ligne lue
		int line = 0;				//le numero de la ligne
		int tmpDimension = 0;		//compteur qui contiendra le nombre de facettes (sans compter celles <= 0)

		try{
			file = new BufferedReader(new FileReader(this.fileName));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}

		while((lineContent = file.readLine()) != null) { //on lis le fichier tant qu'on le peut
			if(line > 5 && lineContent.compareTo("\n") != -1 && lineContent.compareTo("END") != 0
				&& lineContent.charAt(lineContent.length()-1) != '0') { 
				//cette quadruple condition dit que :
				// - on prend à partir de la ligne 6 (car les facettes sont toujours écrites à partir de cette ligne)
				// - on ne prend pas les lignes vide
				// - on ne prend pas le "END" à la fin
				// - on ne prend pas les facettes <= 0 (car le charactère à la position "taille -1" est le chiffre à droite de l'innéquation)
				tmpDimension++;
			}
			line++; //on va à la ligne suivante
		}
		file.close();
		this.dimension = tmpDimension; //on set la dimension avec le nombre de facettes lues
	}

	/**
	* Cette méthode va lire le fichier .iop.ieq et le parser en récupérant les sous graphes
	*/
	public void read() throws IOException{
		BufferedReader file = null; //le fichier
		String lineContent = ""; 	//le contenu de la ligne lue
		int line = 0;				//le numero de la ligne
		int currentLine = 0;		//la facette courrante

		try{
			file = new BufferedReader(new FileReader(this.fileName));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		bonds = new String[dimension][0]; //on réalloue le nombre de ligne grâce à la dimension

		while((lineContent = file.readLine()) != null) { //on lis tant qu'on peut
			if(line > 5 && lineContent.compareTo("\n") != -1 && lineContent.compareTo("END") != 0
				&& lineContent.charAt(lineContent.length()-1) != '0') { //même contraintes que dans setDimInn()
				String[] tokens = lineContent.split("x"); //on split sur le charactère "x" pour récupérer chaque noeuds de chaque lignes du fichier
				for(int i=1; i < tokens.length; i++) {	//cette boucle va permettre de parcourir tous les tokens obtenus
					tokens[i] = deletePlus(tokens[i]);	//on supprime tous les "+" des tokens
					tokens[i] = deleteSpace(tokens[i]);	//on supprime tous les " " des tokens
				}
				bonds[currentLine] = new String[tokens.length]; //on réalloue le nombre de colonne (nombre de noeuds)

				this.bonds[currentLine][0] = lineContent;	//dans la case 0 de chaque colonne on aura l'innéquation correspondante écrite en brut
				for(int i=1; i < tokens.length; i++) {
					this.bonds[currentLine][i] = tokens[i];	//on ajoute ensuite dans chaque colonne chaque noeuds lue
				}
				currentLine++;
			}
			line++;
		}
		file.close();
	}

	/**
	* Cette méthode va écrire dans un nouveau fichier .txt pour afficher l'extraction des sous graphes des facettes.
	*/
	public void write() {
		FileWriter dest = null;
		String str = "";
		String file = this.fileName.substring(0, fileName.length() - 8) + "_graph.txt"; //on créer un nouveau nom avec l'ancien
		StringBuffer strbuff = new StringBuffer();

		strbuff.append(dimension+"\n"); //nb facettes
		for(int i=0; i < this.bonds.length; i++) { //on parcours chaque ligne
			strbuff.append("INNEQUATION N°"+i+" : "+bonds[i][0] + "\n\t"); //on écrit l'innéquation pour la compréhension
			strbuff.append(this.bonds[i].length-1 + " : "); //on met aussi le nombre de noeuds contenus dans le sous graphe

			for(int j=1; j < this.bonds[i].length-1; j++) {	//on parcours à partir de la colonne 1 car en colonne 0 il y a la facette
				for(int k = j+1; k < this.bonds[i].length; k++) { //cette boucle permet de tester tous les noeuds entre eux
					if(itExist(Integer.parseInt(bonds[i][j]) -1, Integer.parseInt(bonds[i][k]) -1)) { //si le lien existe on l'ajoute dans le fichier

						if(j != this.bonds[i].length-2) //condition qui évite d'avoir un "/" à la fin des lignes
							strbuff.append(bonds[i][j]+"-"+bonds[i][k]+"/");
						else
							strbuff.append(bonds[i][j]+"-"+bonds[i][k]);
					}
				}
			}
			if(i != this.bonds.length - 1) //évite le retour à la ligne final
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

	/**
	* Cette méthode va vérifier si dans la matrice de l'objet "parseur" il y a un lien entre les noeuds k et p.
	*
    * @param k
    *			ligne k de la matrice
    * @param p
    *			colonne p de la matrice
    * @return 	retourne true si le lien existe, false sinon
	*/
	public boolean itExist(int k, int p) {
		return (this.parseur.getMatrixPos(k,p) == 1);
	}

	/**
	* Cette méthode va permettre de vérifier si le fichier initial est de type _bonds ou de type matrix.
	*/
	public void initParseur() {
		String tmpName = this.fileName.substring(0, this.fileName.length() - 8) + "_bonds.txt";

		File f = new File(tmpName);
		if(f.exists() && !f.isDirectory()) { 
			System.out.println("Fichier initial de type bonds.");
		}
		else {
			System.out.println("Fichier initial de type matrix.");
			tmpName = this.fileName.substring(0, this.fileName.length() - 8) + ".txt"; //si c'est pas un fichier bonds c'est un fichier matrix
		}

		this.parseur = new ParseurV2(tmpName);
		try {
			this.parseur.parseMatrix();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	* Même méthode que l'autre sauf que l'utilisateur précise le fichier initial en paramètre
	*/
	public void initParseur(String tmpName) {
		File f = new File(tmpName);
		if(f.exists() && !f.isDirectory()) {
			this.parseur = new ParseurV2(tmpName);

			if(tmpName.matches("(.*)_bonds(.*)")) {
				try {
					this.parseur.parseBonds();
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
			else {
				try {
					this.parseur.parseMatrix();
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
			System.out.println("Matrice correspondante.");
			parseur.displayMatrix();
		}
		else {
			System.out.println("Fichier initial introuvable.");
		}
	}

	/**
	* Cette méthode va permettre d'enlever chaque "+" de la chaine de charactère str.
	*
    * @param str
    *			chaine de charactère à parser
    * @return 	retourne le string une fois parsé
	*/
	private String deletePlus(String str) {
		for(int i=0; i < str.length(); i++) {
			if(str.charAt(i) == '+') {
				str = str.substring(0, i) + str.substring(i+1, str.length());
			}
		}
		return str;
	}

	/**
	* Cette méthode va permettre d'enlever chaque " " de la chaine de charactère str.
	*
    * @param str
    *			chaine de charactère à parser
    * @return 	retourne le string une fois parsé
	*/
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
			System.out.println("Il faut rentrer le nom du fichier .poi.ieq en argument. Vous pouvez aussi rentrer le nom du fichier initial.");
			return;
		}
		else if(args.length == 1) { //s'il rentre juste le fichier .poi.ieq en argument
			final String fileName = args[0];
			InnToGraph object = new InnToGraph(fileName);
			try {
				object.initParseur();
				object.setDimInn();
				object.read();
				object.displayBonds();
				object.write();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		else if(args.length == 2) { //s'il rentre aussi le fichier initial (s'il a un nom différent)
			final String fileName = args[0];
			InnToGraph object = new InnToGraph(fileName);
			try {
				object.initParseur(args[1]);
				object.setDimInn();
				object.read();
				object.displayBonds();
				object.write();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	
	}
}

