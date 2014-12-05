import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Random;

public class TAM extends TopicModel {

	public HashMap<String,Integer> wordMap;
	public HashMap<Integer,String> wordMapInv;

	public int[][] docs;
	public int[] docsC;
	public int[][] docsZ;
	public int[][] docsY;
	public int[][] docsL;
	public int[][] docsX;

	public int[][] nDZ;
	public int[][] nDY;
	public int[][] nDL;
	public int[][] nZW;
	public int[][][] nZWY;
	public int[] nW;
	public int n0;
	public int[][] nWY;
	public int[] nY;
	public int[] nZ;
	public int[][] nZY;
	public int[][][] nLZX;
	public int[][] nLZ;

	public int D;
	public int W;
	public int Y;
	public int Z;
	
	public double beta;
	public double alpha;
	public double omega;
	public double gamma0;
	public double gamma1;
	public double delta0;
	public double delta1;
	public double labelPrior;
	
	public TAM(int z, int y, double a, double b, double w, double g0, double g1, 
				double d0, double d1, double lp) {
		alpha = a;
		beta = b;
		omega = w;
		gamma0 = g0;
		gamma1 = g1;
		delta0 = d0;
		delta1 = d1;
		Z = z;
		Y = y;
		labelPrior = lp;
		if (labelPrior == 0) labelPrior = beta;
	}
	
	public void initialize() {
		System.out.println("Initializing...");
		Random r = new Random();

		docsZ = new int[D][];
		docsY = new int[D][];
		docsL = new int[D][];
		docsX = new int[D][];

		nDZ = new int[D][Z];
		nDY = new int[D][Y];
		nDL = new int[D][2];
		nZW = new int[Z][W];
		nZWY = new int[Z][W][Y];
		nW = new int[W];
		n0 = 0;
		nWY = new int[W][Y];
		nY = new int[Y];
		nZ = new int[Z];
		nZY = new int[Z][Y];
		nLZX = new int[2][Z][2];
		nLZ = new int[2][Z];
		
		for (int d = 0; d < D; d++) { 
			docsZ[d] = new int[docs[d].length];
			docsY[d] = new int[docs[d].length];
			docsL[d] = new int[docs[d].length];
			docsX[d] = new int[docs[d].length];
			
			for (int n = 0; n < docs[d].length; n++) {
				int w = docs[d][n];

				int z = r.nextInt(Z);		// select random z value in {0...Z-1}
				docsZ[d][n] = z;
				int y = r.nextInt(Y);		// select random y value in {0...Y-1}
				docsY[d][n] = y;				
				int l = r.nextInt(2);		// select random l value in {0,1}
				docsL[d][n] = l;
				int x = r.nextInt(2);		// select random x value in {0,1}
				docsX[d][n] = x;
				
				// update counts

				nDZ[d][z] += 1;
				nDY[d][y] += 1;
				nDL[d][l] += 1;
				int z0 = (l == 0) ? 0 : z;
				nLZX[l][z0][x] += 1;
				nLZ[l][z0] += 1;
				
				if (l == 0 && x == 0) {
					nW[w] += 1;
					n0 += 1;
				}
				else if (l == 0 && x == 1){
					nWY[w][y] += 1;	
					nY[y] += 1;				
				}				
				else if (l == 1 && x == 0) {
					nZW[z][w] += 1;
					nZ[z] += 1;
				}
				else if (l == 1 && x == 1){
					nZWY[z][w][y] += 1;	
					nZY[z][y] += 1;				
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
		int aspect = docsY[d][n];
		int level = docsL[d][n];
		int route = docsX[d][n];
		
		// decrement counts

		nDZ[d][topic] -= 1;
		nDY[d][aspect] -= 1;
		nDL[d][level] -= 1;
		int z0 = (level == 0) ? 0 : topic;
		nLZX[level][z0][route] -= 1;
		nLZ[level][z0] -= 1;

		if (level == 0 && route == 0) {
			nW[w] -= 1;
			n0 -= 1;
		}
		else if (level == 0 && route == 1) {
			nWY[w][aspect] -= 1;	
			nY[aspect] -= 1;
		}		
		else if (level == 1 && route == 0) {
			nZW[topic][w] -= 1;	
			nZ[topic] -= 1;
		}
		else if (level == 1 && route == 1) {
			nZWY[topic][w][aspect] -= 1;	
			nZY[topic][aspect] -= 1;
		}

		double omegaNorm = W * omega;
		double gammaNorm = gamma0 + gamma1;
		
		// sample new value for level and route
		
		double pTotal = 0.0;
		double[] p = new double[4];
		
		// l = 0, x = 0
		p[0] = (nDL[d][0] + delta0) *
				(nLZX[0][0][0] + gamma0) / (nLZ[0][0] + gammaNorm) *
				(nW[w] + omega) / (n0 + omegaNorm);
		// l = 0, x = 1
		p[1] = (nDL[d][0] + delta0) *
				(nLZX[0][0][1] + gamma1) / (nLZ[0][0] + gammaNorm) *
				(nWY[w][aspect] + omega) / (nY[aspect] + omegaNorm);
		// l = 1, x = 0
		p[2] = (nDL[d][1] + delta1) *
				(nLZX[1][topic][0] + gamma0) / (nLZ[1][topic] + gammaNorm) *
				(nZW[topic][w] + omega) / (nZ[topic] + omegaNorm);
		// l = 1, x = 1
		p[3] = (nDL[d][1] + delta1) *
				(nLZX[1][topic][1] + gamma1) / (nLZ[1][topic] + gammaNorm) *
				(nZWY[topic][w][aspect] + omega) / (nZY[topic][aspect] + omegaNorm);
				
		
		pTotal = p[0]+p[1]+p[2]+p[3];
				
		Random r = new Random();
		double u = r.nextDouble() * pTotal;
		
		double v = 0;
		for (int a = 0; a < 4; a++) {
			v += p[a];
			
			if (v > u) {
				if (a >= 2) level = 1;
				else level = 0;

				if (a % 2 == 1) route = 1;
				else route = 0;

				break;
			}
		}
		
		// sample new value for topic
		
		pTotal = 0.0;
		p = new double[Z];

		if (level == 0) {
			for (int z = 0; z < Z; z++) {
				p[z] = (nDZ[d][z] + alpha);
				pTotal += p[z];
			}
		}
		else if (level == 1 && route == 0) {
			for (int z = 0; z < Z; z++) {
				p[z] = (nDZ[d][z] + alpha) *
						(nZW[z][w] + omega) / (nZ[z] + omegaNorm);
				pTotal += p[z];
			}
		}
		else if (level == 1 && route == 1) {
			for (int z = 0; z < Z; z++) {
				p[z] = (nDZ[d][z] + alpha) *
						(nZWY[z][w][aspect] + omega) / (nZY[z][aspect] + omegaNorm);
				pTotal += p[z];
			}
		}
		
		r = new Random();
		u = r.nextDouble() * pTotal;
		
		v = 0;
		for (int z = 0; z < Z; z++) {
			v += p[z];
			
			if (v > u) {
				topic = z;
				break;
			}
		}

		// sample new value for aspect
		
		pTotal = 0.0;
		p = new double[Y];
		
		double beta0;

		if (route == 0) {
			for (int y = 0; y < Y; y++) {
				if (y == c) beta0 = labelPrior;
				else beta0 = beta;
				
				p[y] = (nDY[d][y] + beta0);
				pTotal += p[y];
			}
		}
		else if (route == 1 && level == 0) {
			for (int y = 0; y < Y; y++) {
				if (y == c) beta0 = labelPrior;
				else beta0 = beta;
				
				p[y] = (nDY[d][y] + beta0) * 
						(nWY[w][y] + omega) / (nY[y] + omegaNorm);
				pTotal += p[y];
			}
		}
		else if (route == 1 && level == 1) {
			for (int y = 0; y < Y; y++) {
				if (y == c) beta0 = labelPrior;
				else beta0 = beta;
				
				p[y] = (nDY[d][y] + beta0) * 
						(nZWY[topic][w][y] + omega) / (nZY[topic][y] + omegaNorm);
				pTotal += p[y];
			}
		}
		
		r = new Random();
		u = r.nextDouble() * pTotal;
		
		v = 0;
		for (int y = 0; y < Y; y++) {
			v += p[y];
			
			if (v > u) {
				aspect = y;
				break;
			}
		}
		
		// increment counts

		nDZ[d][topic] += 1;
		nDY[d][aspect] += 1;
		nDL[d][level] += 1;
		z0 = (level == 0) ? 0 : topic;
		nLZX[level][z0][route] += 1;
		nLZ[level][z0] += 1;

		if (level == 0 && route == 0) {
			nW[w] += 1;
			n0 += 1;
		}
		else if (level == 0 && route == 1) {
			nWY[w][aspect] += 1;	
			nY[aspect] += 1;
		}		
		else if (level == 1 && route == 0) {
			nZW[topic][w] += 1;	
			nZ[topic] += 1;
		}
		else if (level == 1 && route == 1) {
			nZWY[topic][w][aspect] += 1;	
			nZY[topic][aspect] += 1;
		}
		
		// set new assignments

		docsZ[d][n] = topic;
		docsY[d][n] = aspect;
		docsL[d][n] = level;
		docsX[d][n] = route;
	}

	
	public void readDocs(String filename) throws Exception {
		System.out.println("Reading input...");
		
		wordMap = new HashMap<String,Integer>();
		wordMapInv = new HashMap<Integer,String>();
		
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr); 

		String s;
	
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
			docsC[d] = Integer.parseInt(tokens[0]);		// first token is the collection ID											// this is used for defining a prior
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
		
		W = wordMap.size();

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
				bw.write(word+":"+docsZ[d][n]+":"+docsY[d][n]+":"+docsL[d][n]+":"+docsX[d][n]+" ");
			}
			bw.newLine();
		}
		
		bw.close();
		fw.close();
	}
}
