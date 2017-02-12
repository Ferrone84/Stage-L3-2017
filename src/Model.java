
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Model {

/*install jdk on 32bit*/
	
	//les données
	int n,m;
	int V1[][];
	int V2[][];
	IloCplex modele;
	IloNumVar X[][];
	
	public Model(int[][] t,int[][] p) throws IloException {
		this.n = t.length;
		this.m = p.length;
		V1 = t;
		V2 = p;
		modele = new IloCplex();
		X = new IloNumVar[n][m];
		
		createModele();
		
		
		
		System.out.println(modele.toString());
	}
	
	private void createModele() throws IloException {
		createVars();
		createConstaints();
		createFunction();
	}
    
	
	
	//declaration d'une variable decisionnelle 
	private void createVars() throws IloException {
		for (int i = 0; i < n; i++) {
			X[i]=modele.boolVarArray(m); 
		}
	}
	

	private void createConstaints() throws IloException {
		
		createConstaints1();
		createConstaints2();
		createConstaints3();
		createConstaints4();
	}

	
	// i e V1 , Sum X_ik <= 1 & k e V2
	//
	private void createConstaints1() throws IloException {
		for (int i = 0; i < n; i++) {
			IloLinearNumExpr C1 = modele.linearNumExpr();
			for (int k = 0; k <m; k++) {
				if (i!=k){
				C1.addTerm(1, X[i][k]);
			}
			}
			modele.addLe(C1,1);
		}
	}

	

    // k e V2 , Sum X_ik <= 1 & i e V1
	//
	private void createConstaints2() throws IloException {
		for (int k = 0; k < m; k++) {
			IloLinearNumExpr C2 = modele.linearNumExpr();
			for (int i = 0; i < n; i++) {
				if (i!=k){
				C2.addTerm(1, X[i][k]);
			}
			}
			modele.addLe(C2,1);
		}
	}
     
	
	
	//X_ik + X_jl <=1 
	private void createConstaints3() throws IloException {
		
		IloLinearNumExpr C3 = modele.linearNumExpr();
		for (int i=0; i<n;i++) {
            for (int k=0; k<m; k++){
        			if (i!=k){
				C3.addTerm(1,X[i][k]);	
        			}}}
		for (int j=0; j<n;j++) {
            for (int l=0; l<m; l++){
         	   if(j!=l){
				C3.addTerm(1,X[j][l]);	
        			}}	}	
			
		
           modele.addLe(C3, 1);
		}
	
	//X_ik + X_jl <=1 ,
	private void createConstaints4() throws IloException {

		IloLinearNumExpr C4 = modele.linearNumExpr();
		for (int i=0; i<n;i++) {
            for (int l=0; l<m; l++){
        			if (i!=l){
				C4.addTerm(1,X[i][l]);	
        			}}}
		for (int j=0; j<n;j++) {
            for (int k=0; k<m; k++){
         	   if(j!=k){
				C4.addTerm(1,X[j][k]);	
        			}}	}	
			
		
           modele.addLe(C4, 1);
		
	}
	
  // MAX  Sum Sum X_ik
	private void createFunction() throws IloException {

		IloLinearNumExpr fct = modele.linearNumExpr();
		for (int i = 0; i < n; i++) {
			for (int k = 0; k < m; k++) {
				fct.addTerm(1, X[i][k]);
			}
		}
		modele.addMaximize(fct);
	}
	
	private boolean solve() throws IloException{
		return modele.solve();
	}
	
	private double[][] getX() throws IloException{
		
		double [][] matrix = new double [n][m];
		
		if (solve())
			for (int i = 0; i < n; i++) {
				matrix[i] = modele.getValues(X[i]);
			}
		
		return matrix;
		
	}
	
	public static void main(String[] args) {
		
		 int V1[][] ={{0,1,1,1},
				      {1,0,0,0},
				      {1,0,0,0},
				      {1,0,0,0}};
				      
				     
		 
		 int V2[][] ={{0,1,0,0},
			          {1,0,1,1},
			          {0,1,0,0},
			          {0,1,0,0}};;
		 try { 
			Model mod = new Model(V1,V2); // model creation
			
			double s[][] = mod.getX(); 
			System.out.println("Matrice Résultat :");
			for (int i = 0; i < s.length; i++) {
				for (int j = 0; j < s[i].length; j++) {
					System.out.print("\t" + s[i][j]);
				}
				System.out.println();
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
}
