import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Random;

public class LDA extends TopicModel {

	public HashMap<String,Integer> wordMap;
	public HashMap<Integer,String> wordMapInv;

	public int[][] docs;
	public int[][] docsZ;
	public int[][] docsX;

	public int[][] nDZ;
	public int[] nD;
	public int[][] nZW;
	public int[] nZ;
	public int[] nBW;
	public int nB;
	public int[] nX;

	public int D;
	public int W;
	public int Z;
	
	public double beta;
	public double alpha;
	public double gamma0;
	public double gamma1;
	
	public LDA(int z, double a, double b, double g0, double g1) {
		alpha = a;
		beta = b;
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
		nD = new int[D];
		nZW = new int[Z][W];
		nZ = new int[Z];
		nBW = new int[W];
		nB = 0;
		nX = new int[2];
		
		for (int d = 0; d < D; d++) {
			docsZ[d] = new int[docs[d].length];
			docsX[d] = new int[docs[d].length];
			
			for (int n = 0; n < docs[d].length; n++) {
				int w = docs[d][n];

				int z = r.nextInt(Z);		// select random z value in {0...Z-1}
				docsZ[d][n] = z;

				//int x = r.nextInt(2);		// select x uniformly
				int x = 0;
				double u = r.nextDouble();		// select random x value in {0,1}
				u *= (double)(gamma0+gamma1);		// from distribution given by prior
				if (u > gamma0) x = 1;
				docsX[d][n] = x;
				
				// update counts
				
				nX[x] += 1;
				
				if (x == 0) {
					nBW[w] += 1;
					nB += 1;
				}
				else {
					nDZ[d][z] += 1;
					nD[d] += 1;
					nZW[z][w] += 1;	
					nZ[z] += 1;				
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
		int topic = docsZ[d][n];
		int level = docsX[d][n];
		
		// decrement counts

		nX[level] -= 1;

		if (level == 0) {
			nBW[w] -= 1;
			nB -= 1;
		} else {
			nDZ[d][topic] -= 1;
			nD[d] -= 1;
			nZW[topic][w] -= 1;
			nZ[topic] -= 1;
		}

		double alphaNorm = Z * alpha;
		double betaNorm = W * beta;

		// sample new value for level
		
		double pTotal = 0.0;
		double[] p = new double[Z+1];
	
		// this will be p(x=0)	
		p[Z] = (nX[0] + gamma0) *
			(nBW[w] + beta) / (nB + betaNorm);
		pTotal += p[Z];
		
		// sample new value for topic and level
	
		for (int z = 0; z < Z; z++) {
			p[z] = (nX[1] + gamma1) * 
				(nDZ[d][z] + alpha) / (nD[d] + alphaNorm) *
				(nZW[z][w] + beta) / (nZ[z] + betaNorm);
			pTotal += p[z];
		}

		Random r = new Random();
		double u = r.nextDouble() * pTotal;
		
		double v = 0.0;
		for (int z = 0; z < Z+1; z++) {
			v += p[z];
			
			if (v > u) {
				topic = z;
				break;
			}
		}

		if (topic == Z) level = 0;
		else level = 1;
		
		// increment counts

		nX[level] += 1;

		if (level == 0) {
			nBW[w] += 1;
			nB += 1;
		} else {
			nDZ[d][topic] += 1;
			nD[d] += 1;
			nZW[topic][w] += 1;
			nZ[topic] += 1;
		}
		
		// set new assignments

		docsZ[d][n] = topic;
		docsX[d][n] = level;
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

		fr = new FileReader(filename);
		br = new BufferedReader(fr); 

		int d = 0;
		while ((s = br.readLine()) != null) {
			String[] tokens = s.split("\\s+");
			
			int N = tokens.length;
			
			docs[d] = new int[N];
			
			for (int n = 0; n < N; n++) {
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
		System.out.println("Writing output...");

		FileWriter fw = new FileWriter(filename+".assign");
		BufferedWriter bw = new BufferedWriter(fw); 		

		for (int d = 0; d < D; d++) {
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
