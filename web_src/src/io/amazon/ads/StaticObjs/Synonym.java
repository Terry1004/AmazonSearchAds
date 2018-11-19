package io.amazon.ads.StaticObjs;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a (word, synonyms list) pair as an intermediate container of the 
 * synonym data parsed from the json file.
 */
public class Synonym {
	public String word = "";
	public List<String> synonyms = new ArrayList<>();
}
