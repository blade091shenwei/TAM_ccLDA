import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Random;

public abstract class TopicModel {

	public void run(int iters, String filename) throws Exception {
		readDocs(filename);
		initialize();
		
		System.out.println("Sampling...");
		
		for (int iter = 1; iter <= iters; iter++) {
			System.out.println("Iteration "+iter);
			doSampling();
		}
		
		// write variable assignments

		writeOutput(filename);

		System.out.println("...done.");
	}
	
	public abstract void initialize();
	
	public abstract void doSampling();
	
	public abstract void readDocs(String filename) throws Exception;
	
	public abstract void writeOutput(String filename) throws Exception;
}
