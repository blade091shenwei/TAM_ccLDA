import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Random;

public class CcLDA extends TopicModel {

	public HashMap<String,Integer> wordMap;
	public HashMap<Integer,String> wordMapInv;

	public int[][] docs;
	public int[] docsC;
	public int[][] docsZ;
	public int[][] docsX;

	public int[][] nDZ;
	public int[][] nZW;
	public int[][][] nZWc;
	public int[] nZ;
	public int[][] nZc;
	public int[][][] nX;

	public int D;
	public int W;
	public int C;
	public int Z;
	
	public double beta;
	public double delta;
	public double alpha;
	public double gamma0;
	public double gamma1;
	
	public CcLDA(int z, double a, double b, double d, double g0, double g1) {
		alpha = a;
		beta = b;
		delta = d;
		gamma0 = g0;
		gamma1 = g1;
		Z = z;
	}
	
	public void initialize() {
		System.out.println("Initializing...");
		Random r = new Random();

		docsZ = new int[D][];
		docsX = new int[D][];
		
		nDZ = new int[D][Z];
		nZW = new int[Z][W];
		nZWc = new int[Z][W][C];
		nZ = new int[Z];
		nZc = new int[Z][C];
		nX = new int[2][C][Z];
		
		for (int d = 0; d < D; d++) {
			docsZ[d] = new int[docs[d].length];
			docsX[d] = new int[docs[d].length];
			
			for (int n = 0; n < docs[d].length; n++) {
				int w = docs[d][n];
				int c = docsC[d];
				
				int z = r.nextInt(Z);		// select random z value in {0...Z-1}
				docsZ[d][n] = z;
				
				int x = r.nextInt(2);		// select random x value in {0,1}
				docsX[d][n] = x;
				
				// update counts

				nDZ[d][z] += 1;
				nX[x][c][z] += 1;
				
				if (x == 0) {
					nZW[z][w] += 1;
					nZ[z] += 1;
				}
				else {
					nZWc[z][w][c] += 1;	
					nZc[z][c] += 1;				
				}
			}
		}
	}
	
	public void doSampling() {
		for (int d = 0; d < D; d++) {
			for (int n = 0; n < docs[d].length; n++) {
				sample(d, n);
			}
		}
	}
	
	public void sample(int d, int n) {
		int w = docs[d][n];
		int c = docsC[d];
		int topic = docsZ[d][n];
		int route = docsX[d][n];
		
		// decrement counts

		nDZ[d][topic] -= 1;
		nX[route][c][topic] -= 1;
		
		if (route == 0) {
			nZW[topic][w] -= 1;	
			nZ[topic] -= 1;
		}
		else {
			nZWc[topic][w][c] -= 1;	
			nZc[topic][c] -= 1;
		}

		double betaNorm = W * beta;
		double deltaNorm = W * delta;
		
		// sample new value for route
		
		double pTotal = 0.0;
		double[] p = new double[2];

		p[0] = (nX[0][c][topic] + gamma0) *
				(nZW[topic][w] + beta) / (nZ[topic] + betaNorm);
				
		p[1] = (nX[1][c][topic] + gamma1) * 
				(nZWc[topic][w][c] + delta) / (nZc[topic][c] + deltaNorm);
		
		pTotal = p[0] + p[1];
				
		Random r = new Random();
		double u = r.nextDouble() * pTotal;
		
		if (u > p[0]) route = 1;
		else route = 0;
		
		// sample new value for topic
		
		pTotal = 0.0;
		p = new double[Z];
		
		if (route == 0) {
			for (int z = 0; z < Z; z++) {
				p[z] = (nDZ[d][z] + alpha) *
						(nZW[z][w] + beta) / (nZ[z] + betaNorm);
			
				pTotal += p[z];
			}
		}
		else {
			for (int z = 0; z < Z; z++) {
				p[z] = (nDZ[d][z] + alpha) *
						(nZWc[z][w][c] + delta) / (nZc[z][c] + deltaNorm);
			
				pTotal += p[z];
			}
		}
		
		r = new Random();
		u = r.nextDouble() * pTotal;
		
		double v = 0;
		for (int z = 0; z < Z; z++) {
			v += p[z];
			
			if (v > u) {
				topic = z;
				break;
			}
		}
		
		// increment counts

		nDZ[d][topic] += 1;
		nX[route][c][topic] += 1;
		
		if (route == 0) {
			nZW[topic][w] += 1;	
			nZ[topic] += 1;
		}
		else {
			nZWc[topic][w][c] += 1;	
			nZc[topic][c] += 1;
		}
		
		// set new assignments

		docsZ[d][n] = topic;
		docsX[d][n] = route;
	}

	
	public void readDocs(String filename) throws Exception {
		System.out.println("Reading input...");
		
		wordMap = new HashMap<String,Integer>();
		wordMapInv = new HashMap<Integer,String>();
		
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr); 

		String s;
	
		C = 0;
		D = 0;
		while((s = br.readLine()) != null) {
			D++;
		}

		docs = new int[D][];
		docsC = new int[D];

		fr = new FileReader(filename);
		br = new BufferedReader(fr); 

		int d = 0;
		while ((s = br.readLine()) != null) {
			String[] tokens = s.split("\\s+");
			
			int N = tokens.length;
			
			docs[d] = new int[N];
			docsC[d] = Integer.parseInt(tokens[0]);		// first token is the collection ID
			
			if (docsC[d] > C) C = docsC[d];	// determine number of collections
			
			for (int n = 1; n < N; n++) {
				String word = tokens[n];
				
				int key = wordMap.size();
				if (!wordMap.containsKey(word)) {
					wordMap.put(word, new Integer(key));
					wordMapInv.put(new Integer(key), word);
				}
				else {
					key = ((Integer) wordMap.get(word)).intValue();
				}
				
				docs[d][n] = key;
			}
			
			d++;
		}
		
		br.close();
		fr.close();
		
		C++;
		W = wordMap.size();

		System.out.println(C+" collections");
		System.out.println(D+" documents");
		System.out.println(W+" word types");
	}

	public void writeOutput(String filename) throws Exception {
		FileWriter fw = new FileWriter(filename+".assign");
		BufferedWriter bw = new BufferedWriter(fw); 		

		for (int d = 0; d < D; d++) {
			bw.write(docsC[d]+" ");
			
			for (int n = 0; n < docs[d].length; n++) {
				String word = wordMapInv.get(docs[d][n]);
				bw.write(word+":"+docsZ[d][n]+":"+docsX[d][n]+" ");
			}
			bw.newLine();
		}
		
		bw.close();
		fw.close();
	}
}
