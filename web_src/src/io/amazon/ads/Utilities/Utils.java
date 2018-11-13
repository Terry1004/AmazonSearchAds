package io.amazon.ads.Utilities;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

/**
 * This class contains some general utility functions
 */
public class Utils {
	
	private static final Version LUCENE_VERSION = Version.LUCENE_40; 
	private static final Logger logger = Logger.getLogger(Utils.class);
	
	/**
	 * Split a string into keywords that are tokenized and stemmed with stop words removed.
	 * Return an empty list if the input string is null or empty string.
	 * @param string The string to be split into key words
	 * @return A list of keywords obtained from the given <code>title</code>. It is empty
	 * if the input string is null or empty.
	 */
	public static List<String> splitKeyWords(String string) {
		List<String> keyWords = new ArrayList<String>();
		if (string == null || string.equals("")) {
			return keyWords;
		}
		CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
		StringReader reader = new StringReader(string.toLowerCase());
		Tokenizer tokenizer = new StandardTokenizer(LUCENE_VERSION, reader);
		TokenStream tokenStream = new StandardFilter(LUCENE_VERSION, tokenizer);
		tokenStream = new StopFilter(LUCENE_VERSION, tokenStream, stopWords);
		tokenStream = new KStemFilter(tokenStream);
        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
        try {
        	tokenStream.reset();
	        while (tokenStream.incrementToken()) {
	            String term = charTermAttribute.toString();
	            
	            keyWords.add(term);
	            sb.append(term + " ");
	        }
	        tokenStream.end();
	        tokenStream.close();
	        tokenizer.close();  
		} catch (IOException e) {
			logger.error("Error when cleaning input string: " + string, e);
		}
	return keyWords;
	}
}
