import java.util.HashMap;

public class LearnTopicModel {

	public static HashMap<String,String> arguments;
	
	public static void main(String[] args) throws Exception {
		arguments = new HashMap<String,String>();
		
		for (int i = 0; i < args.length; i += 2) {
			arguments.put(args[i], args[i+1]);
		}

		String model = arguments.get("-model");
		String filename = arguments.get("-input");
		
		if (model == null) {
			System.out.println("No model specified.");
			return;
		}
		
		if (filename == null) {
			System.out.println("No input file given.");
			return;
		}
		
		TopicModel topicModel = null;

		if (model.equals("lda")) {
			if (!arguments.containsKey("-Z")) {
				System.out.println("Must specify number of topics using -Z");
				return;
			}
			
			int Z = Integer.parseInt(arguments.get("-Z"));
			
			double alpha = 1.0;
			double beta = 0.01;
			double gamma0 = 1.0;
			double gamma1 = 1.0;

			if (arguments.containsKey("-alpha")) 
				alpha = Double.parseDouble(arguments.get("-alpha"));
			if (arguments.containsKey("-beta")) 
				beta = Double.parseDouble(arguments.get("-beta"));
			if (arguments.containsKey("-gamma0")) 
				gamma0 = Double.parseDouble(arguments.get("-gamma0"));
			if (arguments.containsKey("-gamma1")) 
				gamma1 = Double.parseDouble(arguments.get("-gamma1"));
			
			topicModel = new LDA(Z, alpha, beta, gamma0, gamma1);
		}
		else if (model.equals("cclda")) {
			if (!arguments.containsKey("-Z")) {
				System.out.println("Must specify number of topics using -Z");
				return;
			}
			
			int Z = Integer.parseInt(arguments.get("-Z"));
			
			double alpha = 1.0;
			double beta = 0.01;
			double delta = 0.01;
			double gamma0 = 1.0;
			double gamma1 = 1.0;

			if (arguments.containsKey("-alpha")) 
				alpha = Double.parseDouble(arguments.get("-alpha"));
			if (arguments.containsKey("-beta")) 
				beta = Double.parseDouble(arguments.get("-beta"));
			if (arguments.containsKey("-delta")) 
				delta = Double.parseDouble(arguments.get("-delta"));
			if (arguments.containsKey("-gamma0")) 
				gamma0 = Double.parseDouble(arguments.get("-gamma0"));
			if (arguments.containsKey("-gamma1")) 
				gamma1 = Double.parseDouble(arguments.get("-gamma1"));
			
			topicModel = new CcLDA(Z, alpha, beta, delta, gamma0, gamma1);
		}
		else if (model.equals("tam")) {
			if (!arguments.containsKey("-Z")) {
				System.out.println("Must specify number of topics using -Z");
				return;
			}
			if (!arguments.containsKey("-Y")) {
				System.out.println("Must specify number of aspects using -Y");
				return;
			}
			
			int Z = Integer.parseInt(arguments.get("-Z"));
			int Y = Integer.parseInt(arguments.get("-Y"));
			
			double alpha = 0.1;
			double beta = 0.1;
			double omega = 0.01;
			double gamma0 = 1.0;
			double gamma1 = 1.0;
			double delta0 = 10.0;
			double delta1 = 10.0;
			double labelPrior = 0;

			if (arguments.containsKey("-alpha")) 
				alpha = Double.parseDouble(arguments.get("-alpha"));
			if (arguments.containsKey("-beta")) 
				beta = Double.parseDouble(arguments.get("-beta"));
			if (arguments.containsKey("-omega")) 
				omega = Double.parseDouble(arguments.get("-omega"));
			if (arguments.containsKey("-gamma0")) 
				gamma0 = Double.parseDouble(arguments.get("-gamma0"));
			if (arguments.containsKey("-gamma1")) 
				gamma1 = Double.parseDouble(arguments.get("-gamma1"));
			if (arguments.containsKey("-delta0")) 
				delta0 = Double.parseDouble(arguments.get("-delta0"));
			if (arguments.containsKey("-delta1")) 
				delta1 = Double.parseDouble(arguments.get("-delta1"));
			if (arguments.containsKey("-labelPrior")) 
				labelPrior = Double.parseDouble(arguments.get("-labelPrior"));
			
			topicModel = new TAM(Z, Y, alpha, beta, omega, gamma0, gamma1, delta0, delta1, labelPrior);
			
		}
		else {
			System.out.println("Invalid model specification. Options: lda | cclda | tam");
			return;
		}
		
		int iters = 100;
		if (arguments.containsKey("-iters")) 
			iters = Integer.parseInt(arguments.get("-iters"));
		
		topicModel.run(iters, filename);
	}

}
