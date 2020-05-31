
public class PageRank {
 
 
 static void fillGraph(int size, int[][] graph) {           // modify this to the real matrix
	 int count = 0;
	 int[] data = {0,0,1,1,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0};
	 for(int i = 0; i < size; i++)
	 {
		 for(int j = 0; j < size; j++)
		 {
			 graph[i][j] = data[count];
			 count++;
//			 if(i == j)
//				 graph[i][j] = 0; //to solve the problem of link nt pointing to anything for now
//			 else if(j % 2 == 0 && (i != 1 || i != 2))
//				 graph[i][j] = 1;
//			 else
//				 graph[i][j] = 0;
		 }
	 }
 }
 
 static void normalize(float[] rank) {   
	 float sum = 0;
	 for(int i = 0; i < rank.length; i++)
		 sum += rank[i];
		 
	 for(int i = 0; i < rank.length; i++)
		 rank[i] /= sum;
 }
 
 public static void main(String[] args) {
	 int[][] graph = new int[5][5];
	 float[] rank0, rank1;
	 int[] L = new int[5];
	 
	 rank0 = new float[5];
	 rank1 = new float[5];
	 
	 fillGraph(5,graph);
	 
	 
	 for(int i = 0; i < rank0.length; i++)
	 {
		//initial itr all ranks are equal in the 0's iteration
		 rank0[i] = 1/(float)rank0.length;
		 // how many pages each page refer to "calculating the number of outgoing arrows from each page"
		 int count = 0;
		 for(int j = 0; j < rank0.length; j++)
		 {
			 if (graph[i][j] == 1)
				 count++;
		 }
		 L[i] = count;
	 }
	 
	 // printing the matrix
	 for(int i = 0; i < rank0.length; i++)
	 {
		 for(int j = 0; j < rank0.length; j++)
		 {
			 System.out.print(graph[i][j]);
		 }
		 System.out.print("\n");
	 }
	 
	 float lampda = 0.85f;
	 //-----------------------------------------------------------------------------------
	 for(int i = 0; i < 6; i++)
	 {
		 for(int j = 0; j < rank0.length; j++)
		 {
			 float pr = 0;
			 for(int k = 0; k < rank0.length; k++)
			 {
				 if(graph[k][j] == 1)
				 {
					 pr = pr + (rank0[k]/L[k]);
				 }
			 }
//			 rank1[j] = pr;
			 rank1[j] = (1-lampda)+lampda*pr;
//			 rank1[j] = (1-lampda)*rank0[j]+lampda*pr;
//				 System.out.println(rank1[j]);
		 }
//			 normalize(rank1);
		 rank0 = rank1;
	 }
	 for(int j = 0; j < rank0.length; j++)
	 {
			 System.out.println(rank1[j]);
	 }
	 
	}
}
