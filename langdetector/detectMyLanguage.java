package langdetector;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class detectMyLanguage {

	private static final int n = 3; // value of n in n-gram
	public static final int maxDistance = 300;
	
	private static final String training_directory = "trainingData";
	private static final String profile_directory = "langModels";

	private static final List<String> languages = new ArrayList<String>();
	
	/**
	 * Instantiating a new language detector and ask a query whether to train language models or not.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		File[] files = new File(training_directory).listFiles();	
		for (File file : files) {
		    if (file.isFile()) {
		    	languages.add(file.getName().substring(0, 3));
		    }
		}
	
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Do you wish to train your model first (y/n)?");
		try{
			String input = br.readLine();
			while(!(input.equalsIgnoreCase("y") || input.equalsIgnoreCase("n"))){
				System.out.println("Please enter your choice as y or n : ");
				System.out.println("Do you wish to train your model first (y/n)?");
				input = br.readLine();
			}
			char choice = input.charAt(0);
			if(choice == 'y'){
				detectMyLanguage.preprocessTrainingData();
				detectMyLanguage.identifyLanguage();			
			}
			else{
				detectMyLanguage.identifyLanguage();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Reading the language profiles from profile_directory
	 * @return name with the corresponding profile
	 */
	private static HashMap<String, HashMap<String, Integer>> getLanguageModel() {

		HashMap<String, HashMap<String, Integer>> languageModel = new HashMap<String, HashMap<String, Integer>>();

		for (int langCount = 0; langCount < languages.size(); langCount++) {

			String filename_read = profile_directory + "/" + languages.get(langCount) + "_profile.txt";
			HashMap<String, Integer> map = new HashMap<String, Integer>();

			try {
				// read from training data
				BufferedReader reader = new BufferedReader(new FileReader(filename_read));
				String line = "";
				//System.out.println(languages[langCount] + " model reading.....");

				while ((line = reader.readLine()) != null) {
					int lastSpaceIndex = line.lastIndexOf(" ");	
					
					String strNgram = line.substring(0, lastSpaceIndex);
					
					Integer countOfNgram = Integer.parseInt(line.substring(lastSpaceIndex + 1));
					map.put(strNgram, countOfNgram);
				}
				reader.close();
				languageModel.put(languages.get(langCount),new HashMap<String, Integer>(map));
				map.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return languageModel;
	}
	
	
	/**
	 * Sorting the language profile by the number of frequency. Cut of the first 300 n-grams.
	 * @param hp unsorted hashmap
	 * @return sorted hashmap
	 */
	private static HashMap<String, Integer> sortedMap(HashMap<String, Integer> hp){
		HashMap<String, Integer> sortedMap = hp.entrySet().stream()
		        .sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
		        .limit(300)
		        .collect(
		                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
		                        LinkedHashMap::new));
		return sortedMap;			
	}
	
	
	/**
	 * Calculate two language profiles
	 * @param input the input language profile
	 * @param langModel the existing language profile
	 * @return space distance between two profiles
	 */
	private static int distanceMeasure(HashMap<String, Integer> input, HashMap<String, Integer> langModel){
		int dist = 0;
		input = sortedMap(input);
		langModel = sortedMap(langModel);
		List<String> indexes_input = new ArrayList<String>(input.keySet()); // <== Set to List
		List<String> indexes_langModel = new ArrayList<String>(langModel.keySet()); 
		Iterator<String> itForInpText = input.keySet().iterator();
		while (itForInpText.hasNext()) {
			String ngKeyForInpText = (String) itForInpText.next();
		    if (langModel.containsKey(ngKeyForInpText)) {
		    	dist += Math.abs(indexes_input.indexOf(ngKeyForInpText) - indexes_langModel.indexOf(ngKeyForInpText));    	
		    }
		    else{
		    	dist += detectMyLanguage.maxDistance;
		    }		
		}
		return dist;
		
	}

	
	/**
	 * Calculating input text language profile with all language models
	 * printout the language with the closest distance 
	 */
	private static void identifyLanguage() {
		HashMap<String, HashMap<String, Integer>> languageModel = getLanguageModel();
		String inputText = "init";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		try {		
			while (inputText!= null) {
				System.out.println("Enter some text (or exit): ");
			    inputText = reader.readLine();
			    
				if (inputText.equalsIgnoreCase("exit")) {
					inputText = null;
					break;
				}
			  
				Integer langDist[] = new Integer[languages.size()];
				
				//build the ngramMap for input text---maoForInpText
				NgramBuilder ngb = new NgramBuilder();
				ngb.createNgrams(n, inputText);
				HashMap<String, Integer> mapForInpText = ngb.getNgrams();
				HashMap<String, Integer> sortedInpMap = sortedMap(mapForInpText);	       
			
				for (int langCount = 0; langCount < languages.size(); langCount++) {
					langDist[langCount] = distanceMeasure(sortedInpMap, languageModel.get(languages.get(langCount)));
				}
									   
				int minIndex = 0;				
				for (int i = 0; i < languages.size(); i++) {
					System.out.println(languages.get(i) + " " + langDist[i] );
					if (langDist[i] < langDist[minIndex])
						minIndex = i; 
				}
				
				System.out.println("Detected Language : " + languages.get(minIndex) +"\n");
				
				langDist = null;
				ngb = null;
				mapForInpText.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * Reading training texts from the given folder and calculates the corresponding frequency profiles.
	 * Saving them in the langModels folder
	 */
	private static void preprocessTrainingData() {

		for (int langCount = 0; langCount < languages.size(); langCount++) {

			String filename_read = training_directory + "/"	+ languages.get(langCount) + ".txt";
			String filename_write = profile_directory + "/" + languages.get(langCount)	+ "_profile.txt";

			NgramBuilder createTrainingSet = new NgramBuilder();

			try {
				// read from training data
				BufferedReader reader = new BufferedReader(new FileReader(filename_read));
				String line = "";
				HashMap<String, Integer> nGramMap = new HashMap<String, Integer>();

				//System.out.println(languages[langCount] + " reading.....");

				while ((line = reader.readLine()) != null) {
					createTrainingSet.createNgrams(n, line);
				}

			
				nGramMap = sortedMap(createTrainingSet.getNgrams());
				reader.close();

				// write result set
				File file = new File(filename_write);
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				//System.out.println(languages[langCount] + " writing.....");
				Iterator<String> it = nGramMap.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					Integer countOfNgram = (Integer) nGramMap.get(key);// / totalNgrams;
					String countOfNgramString = String.valueOf(countOfNgram);
					line = key + " " + countOfNgramString;
					writer.write(line + "\n");
				}
				writer.close();

				nGramMap.clear();
				createTrainingSet = null;

			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println(languages[langCount] + " done\n");
		}
		System.out.println("Language profiles are saved\n");
	}
}
