package io.amazon.ads.StaticObjs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an ad with fields the same as stored in database
 */
public class Ad implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public String title = "";
	public Long adId;
	public Long campaignId;
	public List<String> keyWords = new ArrayList<>();
	public double relevanceScore;
	public double pClick;	
	public double bidPrice;
	public double rankScore;
	public double qualityScore;
	public double costPerClick;
	public int position = 1; //1: top , 2: bottom
    public double price; 
    public String thumbnail = ""; 
    public String brand = ""; 
    public String detailUrl = ""; 
    public String query = ""; 
    public String category = "";
}
