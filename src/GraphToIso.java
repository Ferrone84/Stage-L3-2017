import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

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

	/**
	* Constructeur qui initialise le nom du fichier.
	*
    * @param fileName
    *			Nom du fichier
	*/
	public GraphToIso(String fileName) {
		this.fileName = fileName;
	}

	/**
	* Cette méthode va afficher la matrice de facettes.
	*/
	public void displayFacettes() {
		System.out.println("Display facettes : (case 0 = nombre de sommets dans la facette)");
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
		this.similarityMatrix = new int[this.dimension][this.dimension];
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

		this.facettes = new String[this.dimension][0]; //on réalloue le nombre de ligne grâce à la dimension

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

	/**
	* Méthode qui permet de savoir si les sous graphes sont isomorphes entre-eux. <br>
	* S'ils le sont on met un 1 dans la matrice de similarité.
	*
	* @author Nicolas Duret &amp; Alexandra Moshina
	*
	*/
	public void match() {
		for(int i=0; i < this.facettes.length; i++) { //parcours les lignes
			for(int k=i+1; k < this.facettes.length; k++) { //permet d'assossier les lignes i avec les lignes i+1 (évite les doublons)
				//s'il y a le même nombre de sommets dans les facettes et qu'il y a le même nombre de liens (sinon pas isomorphe)
				if(this.facettes[i][0].compareTo(this.facettes[k][0]) == 0 && this.facettes[i].length-1 == this.facettes[k].length-1) {
					//System.out.println("Les facettes "+i+" et "+k+" ont le même nombre de sommets et le même nombre de liens.");
					//s'il n'y a qu'un lien et 2 sommets alors les graphes sont forcément isomorphe (petite optimisation)
					if(this.facettes[i][0].equals("2") && this.facettes[i].length-1 == 1) {
						System.out.println("Les facettes "+i+" et "+k+" sont isomorphes.");
						this.similarityMatrix[i][k] = 1;
						this.similarityMatrix[k][i] = 1;
					}
					else {
						String save = ""; //va contenir les noeuds et leurs associations (avec des '=' pour les trier plus tard)

						for(int j=1; j < this.facettes[i].length; j++) { //on va associer chaque noeud de i à un noeud de j correspondant
							String[] nodesi = this.facettes[i][j].split("-"); //on récupère le lien i
							String[] nodesk = this.facettes[k][j].split("-"); //on récupère le lien k

							if ( !save.matches("(.*)" + nodesi[0] + "=(.*)") ) { //si on l'a pas déjà mis dans save on l'ajoute
								save += nodesi[0] + "=" + nodesk[0] + "/";
							}
							if ( !save.matches("(.*)" + nodesi[1] + "=(.*)") ) { //la même pour le 2ème
								save += nodesi[1] + "=" + nodesk[1] + "/";
							}
						}
						save = save.substring(0, save.length()-1); //on enlève juste le '/' à la fin
						//System.out.println(save);

						String[] tokens = save.split("/"); //on split sur ce charactère
						// for(int a=0; a < tokens.length; a++)
						// 	System.out.print(tokens[a]+"|");
						// System.out.println();

						String[] tokensAndAsso = new String[(tokens.length * 2)]; //ce tableau va contenir chaque sommets, contenu dans tokens, et son association (sans les '=')
						int z=0; //index pour se déplacer dans tokensAndAsso

						for(int j=0; j < tokens.length; j++, z+=2) {
							String[] tmp = tokens[j].split("=");
							tokensAndAsso[z] = tmp[0]; //on met le premier
							tokensAndAsso[z+1] = tmp[1]; //puis le 2ème
						}

						// for(int a=0; a < tokensAndAsso.length; a++)
						// 	System.out.print(tokensAndAsso[a]+"|");
						// System.out.println();

						String[] substitut = new String[(this.facettes[i].length-1)]; //ce tableau va contenir la nouvelle chaine avec tous les liens remplacés par leurs substitutions
						z = 0; //index pour se déplacer dans substitut

						for(int j=1; j < this.facettes[i].length; j++) { //on parcours la chaine de i pour la transformer en celle de j (si la substitution est vraie)
							//System.out.println(facettes[i][j]);
							String[] nodes = this.facettes[i][j].split("-"); //on sépare le lien en deux

							for(int b=0; b < tokensAndAsso.length; b+=2) { //on parcours tokensAndAsso sur les noeuds pairs (€ à i) car tokensAndAsso possède les noeuds et leurs subsituts
								if(nodes[0].equals( tokensAndAsso[b] )) { //lorsque l'on a trouvé notre noeud
									substitut[z] = tokensAndAsso[b+1]; //on ajoute son substitut
									substitut[z] += "-"; //et on rajoute le petit lien
								}
							}
							for(int b=0; b < tokensAndAsso.length; b+=2) { //maintenant on cherche le 2éme noeud 
								if(nodes[1].equals( tokensAndAsso[b] )) { //quand on l'a trouvé
									substitut[z] += tokensAndAsso[b+1]; //on ajoute son substitut à la suite de son prédécesseur
								}
							}
							z++; //on incrémente l'index de substitut
						}

						// System.out.print("Resultat de la substitution : ");
						// for(int ab=0; ab < substitut.length; ab++) {
						// 	System.out.print(substitut[ab]+"|");
						// }
						// System.out.println();

						boolean isomorphe = true; //témoin si le graphe est isomorphe
						z=0; //index qui va parcourir substitut
						for(int ac = 1; ac < this.facettes[k].length; ac++, z++) { //on parcours enfin la 2éme facette pour vérifier si le substitut est pareil
							if(! this.facettes[k][ac].equals(substitut[z])) //s'il y a une seule différence, les sous-graphes ne sont pas isomorphes
								isomorphe = false;
						}

						//System.out.println(isomorphe);
						if(isomorphe) {	//s'il le sont on met la matrice de similarité à jour
							System.out.println("Les facettes "+i+" et "+k+" sont isomorphes.");
							this.similarityMatrix[i][k] = 1;
							this.similarityMatrix[k][i] = 1;
						}
					}
				}
				else
					System.out.println("Les facettes "+i+" et "+k+" ne sont pas isomorphes.");
			}
		}
	}

	/**
	* Cette méthode va écrire le contenu de la matrice de similarité dans un fichier _sim.txt .
	*/
	public void write() {
		FileWriter dest = null;
		String str = "";
		String file = this.fileName.substring(0, fileName.length() - 10) + "_sim.txt"; //on créer un nouveau nom avec l'ancien
		StringBuffer strbuff = new StringBuffer();

		strbuff.append(this.dimension+"\n"); //nb facettes
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
			System.out.println();
			object.displaySimilarityMatrix();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}