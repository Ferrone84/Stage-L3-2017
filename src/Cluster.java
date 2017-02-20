import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;


import fr.lri.tao.apro.ap.Apro;
import fr.lri.tao.apro.ap.AproBuilder;
import fr.lri.tao.apro.data.DataProvider;
import fr.lri.tao.apro.data.MatrixProvider;

/**
 * Classe qui va repartir un ensemble de facettes dans des groupes selon leurs affinites
 * @author Ayoub Enjoumi &amp; Noe Cecillon
 *
 */

public class Cluster {
	
	/**
	 * Nom du fichier contenant la matrice de similarite.
	 */
	private String fileName;
	
	/**
	 * Nombre de facettes
	 */
	private int dimension;
	
	/**
	 * Matrice de similarite
	 */
	private double[][] matrix;
	
	/**
	 * Matrice contenant le resultat du clustering
	 */
	private int[][] result;
	 
		
		/**
		 * Constructeur qui initialise le nom du fichier
		 * @param fileName 
		 * Nom du fichier
		 */
		public Cluster(String fileName) {
			this.fileName = fileName;
		}
		
		/**
		 * Methode qui va effectuer le clustering avec la bibliotheque Apro utilisant l'algorithme de propagation d'affinite.
		 */
		public void clustering() {
			
		      DataProvider provider = new MatrixProvider(this.matrix);	//objet contenant la matrice de similarite
		      AproBuilder builder = new AproBuilder();
		      builder.setThreads(1);
		      Apro apro = builder.build(provider);	//objet qui va executer l'algorithme de propagation d'affinite	      
		      apro.setDamping(0.9); //Facteur d'amortissement entre 0 et 1.
		      apro.run(300);  	//execute le nombre d'iterations
		        		      
		      this.result = new int[this.dimension][3];	//tableau contenant le resultat du clustering, chaque ligne represente une facette.
		      for (int i=0; i<this.dimension; i++){ 
		    	  this.result[i][0] = i; //la premiere colonne contient le numero de la facette
		    	  this.result[i][1] = apro.getExemplar(i); //la deuxieme colonne contient la facette representative du chaque facette
		      }
		      for (int i=0; i<this.dimension; i++){ //Definition du groupe auquel chaque facette appartient.
		    	  if( this.result[i][0] == this.result [this.result[i][1]][1]) { //si 2 facettes sont representatives l'une de l'autre elles sont dans le meme groupe
		    		  this.result[i][2] = this.result[this.result[i][1]][2] = this.result[i][1]; 
		    	  }
		    	  else {
		    		  this.result[i][2] = this.result[i][1];
		    	  }
		      }
		      
		      Arrays.sort(this.result, new Comparator<int[]>() { //Tri du tableau pour regrouper les membres d'un meme groupe.
		            @Override
		            public int compare(final int[] entry1, final int[] entry2) {
		                final Integer val1 = entry1[2];
		                final Integer val2 = entry2[2];
		                return val1.compareTo(val2);
		            }
		        });
		      
		}
		
		/**
		 * Methode qui affiche le resultat du clustering.
		 */
		public void displayCluster() {
			int nbCluster = 1; //nombre de clusters differents
		      int currentNum = -1; //cluster qui est en cours d'affichage
		      
		      StringBuffer strbuff = new StringBuffer(); //contient la string e afficher
		      for (int i=0; i<this.dimension; i++){
		    	  if (i == 0) { //pour la premiere iteration
		    		  strbuff.append("Cluster" + nbCluster + ": [" + this.result[i][0]);
		    		  currentNum = this.result[i][2]; 
		    	  }
		    	  else if (this.result[i][2] == currentNum) { // si la facette est dans le groupe courant
		    		 strbuff.append(", " + this.result[i][0]);
		    		 currentNum = this.result[i][2];
		    	  }
		    	  else { //si la facette n'est pas dans le groupe courant
		    		  currentNum = this.result[i][2];
		    		  strbuff.append("]");
		    		  System.out.println(strbuff);
		    		  nbCluster++;
		    		  strbuff.delete(0, strbuff.length()); //on vide le buffer apres l'avoir affiche
		    		  strbuff.append("Cluster" + nbCluster + ": [" + this.result[i][0]);
		    	  }
		      }
		      strbuff.append("]");
		      System.out.println(strbuff); //pour afficher le dernier groupe.
		}
		
		/**
		 * Methode qui charge la matrice de similarite.
		 * 
		 */
		public void loadMatrix() throws IOException{
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
				this.dimension = Integer.parseInt(lineContent); //1ere ligne = nombre de facettes
			}
			this.matrix = new double[this.dimension][this.dimension]; //on alloue la matrice grece e la dimension

			while((lineContent = file.readLine()) != null){ //tant qu'il y a des lignes e lire
				String[] tmp = lineContent.split(" "); //on separe chaque element de la matrice
				for(int i=0; i < tmp.length; i++)
					if(line == i){ //si on se trouve sur la diagonale
						this.matrix[line][i] = 0.6;	//la valeur sur la diagonale (preference) est fixe
					}
					else { //si ce n'est pas la diagonale
						if (Double.parseDouble(tmp[i]) == 0)
							this.matrix[line][i] = 0.001;	//il ne faut pas de 0 dans la matrice
						else
							this.matrix[line][i] = Double.parseDouble(tmp[i]);	//si ce n'est pas un 0, on recopie la valeur
					}
				line++;
			}
			file.close();			
		}
		
		/**
		 * Fonction principale.
		 * @param args
		 * Nom du fichier en parametre.
		 */
		public static void main(String[] args) {
	    	if(args.length == 0) {
				System.out.println("Il faut rentrer le nom du fichier txt contenant la matrice de similarite en argument.");
				return;
			}
			final String fileName = args[0];
			//final String fileName = "5.txt";
			Cluster cluster = new Cluster(fileName);

			try{
				cluster.loadMatrix(); 
				cluster.clustering();
				cluster.displayCluster();
			}
			catch(IOException e){
				e.printStackTrace();
			}
	    }
}