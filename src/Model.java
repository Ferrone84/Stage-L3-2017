
import ilog.concert.IloException; 
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;


/**
 * Classe qui presente une modélisation de la recherche d'isomorphisme entre deux graphes
 * @author Raouf Izem
 *
 */


public class Model {


	int V1[][];// le premier graphe
	int V2[][];// le deuxiem graphe
	IloCplex modele; // le modele
	IloNumVar X[][];// la variable decisionnelle 
	
	
	/**
	 * constructeur du modéle avec des paramétres 
	 * creation d'un modéle et de l'afficher
	 */
	public Model(int[][] t,int[][] p) throws IloException {
		V1 = t;
		V2 = p;
		modele = new IloCplex();//création d'un nouveau objet 
		X = new IloNumVar[t.length][p.length];
		createModele();
		System.out.println(modele.toString());
	}
	
	
	/**
         * Methode qui contient les élèments d'un modéle (les données, une fonction objectif et les contraintes).
	 */
	private void createModele() throws IloException {
		createVars();
		createConstaints();
		createFunction();
	}
    
	
	/**
         * Methode pour créer la variable décisionnelle 
	*/
	
	private void createVars() throws IloException {
		for (int i = 0; i < V1.length; i++)
			for (int k = 0; k < V2.length; k++) 
			{
	            	X[i][k]=modele.intVar(i, k);
			//X[i]=modele.boolVarArray(V1.length);
		}
	}
	
        /**
         * Methode qui crée les contraintes.
	 */
	
	private void createConstaints() throws IloException {
		
		createConstaints1();
		createConstaints2();
		createConstaints3();
		createConstaints4();
	}

	/**
	  * Methode contrainte 1 : qui consiste à chaque nœud i de V1 doit être associé à au plus un nœud k de V2.
         */
	
	private void createConstaints1() throws IloException {
		for (int i = 0; i < V1.length; i++) {
			IloLinearNumExpr C1 = modele.linearNumExpr();//Declaration de contrainte 
			for (int k = 0; k <V2.length; k++) {
				if (i!=k){
				C1.addTerm(1,X[i][k]);
			
			}
			modele.addLe(C1,1);
		}
	}

	
        /**
	  * Methode de contrainte 2 : qui consiste à chaque nœud k de V2 doit être associé à au plus un nœud i de V1.
         */   
   
	private void createConstaints2() throws IloException {
		for (int k = 0; k < V2.length; k++) {
			IloLinearNumExpr C2 = modele.linearNumExpr();
			for (int i = 0; i < V1.length; i++) {
				
				C2.addTerm(1, X[i][k]);
			}
			}
			modele.addLe(C2,1);
		}
	}
     
	
	/**
         * Methode de contrainte 3 & 4 : qui consite pour chaque arc ij de V1 doit être associé à au plus un arc kl de V2. 
	 * c'est à dire chaque nœud de les deux nœuds de V1 doit être associé à au plus un nœud de les  nœuds de V2.
	 *
	 * contrainte 3
	 *i------------k
	 *|            |
	 *j------------l
	 *
	 *
	 *contrainte 4
	 *i------------l
	 *|            |
	 *j------------k
	 */

	private void createConstaints3() throws IloException {
		
		IloLinearNumExpr C3 = modele.linearNumExpr();
		for (int i=0; i<V1.length;i++) {
            for (int k=0; k<V2.length; k++){
        			
				C3.addTerm(1,X[i][k]);	// l'ajout de cette expression 1*X[i][k] à la 1ére contrainte.
        			}}
		for (int j=0; j<V1.length;j++) {
            for (int l=0; l<V2.length; l++){
         	     
				C3.addTerm(1,X[j][l]);	
        			}}	
			
		
           modele.addLe(C3, 1); // l'ajout de contrainte au modéle " C3 <= 1 " c'est à dire que le contrainte 3 sera inférieure ou égale à 1
		}
	
	
	private void createConstaints4() throws IloException {

		IloLinearNumExpr C4 = modele.linearNumExpr();
		for (int i=0; i<V1.length;i++) {
            for (int l=0; l<V2.length; l++){
        			
				C4.addTerm(1,X[i][l]);	
        			}}
		for (int j=0; j<V1.length;j++) {
            for (int k=0; k<V2.length; k++){
         	       
				C4.addTerm(1,X[j][k]);	
        			}}	
			
		
           modele.addLe(C4, 1);
		
	}
	
        /**
	  * Methode qui presente la fonction objective et qui maximise la recherche des isomorphismes entre deux graphes 
         */
	
	private void createFunction() throws IloException {
		IloLinearNumExpr fct = modele.linearNumExpr();
		for (int i = 0; i < V1.length; i++) {
			for (int k = 0; k < V2.length; k++) {
				fct.addTerm(1, X[i][k]);
			}
		}
		modele.addMaximize(fct);
	}
	/**
	  * Methode qui retourne la solution optimale 
         */
	private boolean solve() throws IloException{
		return modele.solve();             
	}
	
	/**
	  * Methode qui prend la resultat et la retourne dans une matrice
         */
	private double[][] getX() throws IloException{
		
		double [][] matrix = new double [V1.length][V2.length];
		
		if (solve())
			for (int i = 0; i < V1.length; i++) 
				for (int k = 0; k < V2.length; k++) {
				matrix[i][k] = modele.getValue(X[i][k]);
			}
		
		return matrix;
		
	}
	
	public static void main(String[] args) {
		
		 int V1[][] ={{0,1,1,0},
		              {1,0,0,1},
			      {1,0,0,0},
			      {0,1,0,0}};
		/**
		 *   modéle du graphe V1
	         *        1
		 *      ------
		 *    2|      |3
		 *    4|
                */
				      
	      
		 
		 int V2[][] ={{0,1,1,0},
			      {1,0,0,0},
			      {1,0,0,1},
			      {0,0,1,0}};
		
		/**
		 *   modéle du graphe V1
	         *        3
		 *      ------
		 *    1|      |4
		 *    2|
                */
		 try { 
			
			Model mod = new Model(V1,V2); 
			double s[][] = mod.getX(); 
			System.out.println("Matrice Résultat :");
			for (int i = 0; i < s.length; i++) {
				for (int j = 0; j < s[i].length;j++) {
					System.out.print("\t" + s[i][j]);
				}
				System.out.println();
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
}
