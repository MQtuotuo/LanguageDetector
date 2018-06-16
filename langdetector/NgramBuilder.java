package langdetector;
import java.util.HashMap;

public class NgramBuilder {
	private static final String pad = "_";

	private HashMap<String, Integer> nGramsMap = new HashMap<String, Integer>();


	/**
	 * Create n grams. Read a text and split into words. Then create all n-grams. 
	 * @param n
	 * @param str
	 */
	public void createNgrams(int n, String str) {
		//preprocess the string
		str = filterString(str);
		
		if (str!=null){
			String words[] = str.split(" ");
			for (int i = 0; i < words.length; i++) {
				
				//preprocess the word
				String word = this.filterWord(words[i]);
				if (word!=null){
					for (int len=1; len<=n && len<=word.length();len++){
						int pos = 0;
						// create all n-grams of the word
						while (pos + len <= word.length()) {
							String ngram = word.substring(pos, pos + len);
							
							// add them to the ngramList
							if (!nGramsMap.containsKey(ngram)) {
								nGramsMap.put(ngram, 1);
							} else {
								int countOfNgram = nGramsMap.get(ngram);
								countOfNgram++;
								nGramsMap.remove(ngram);
								nGramsMap.put(ngram, countOfNgram);
							}
							
							pos++;
							}
						}		
				}		
			}	
		}	
	}
	
	
	
	/**
	 * Returns the given token in lower-case with padding.
	 * @param word
	 * @return filtered word
	 */
	private String filterWord(String word) {

		// remove whitespace and upper-case letters
		word = word.trim().toLowerCase();

		// check for at least one letter
		if (!word.equals(""))
			// return with padding
			return pad + word.toLowerCase() + pad;
		else
			return null;

	}
	
	
	/**
	 * Returns processed line in lower-case with padding.
	 * @param line 
	 * @return filtered line
	 */
	private String filterString(String line) {

		line = line.replaceAll("[0-9:,;()%.\"/!+$&@?*=]", ""); // removing punctuation
		line = line.replaceAll("\\t", " "); // replacing tabs with space
		while (line.indexOf("  ") >= 0){
			line = line.replaceAll("  ", " "); // replacing multiple spaces with single space
		}
		
		// remove whitespace and upper-case letters
		line = line.trim().toLowerCase();
		
		return line;
	}
	
	/**
	 * Get the nGramsMap
	 * @return
	 */
	public HashMap<String, Integer> getNgrams() {
		return nGramsMap;
	}
	
	
}

