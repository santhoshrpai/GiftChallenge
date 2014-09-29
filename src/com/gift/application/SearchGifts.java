/*
 * 
 * This program uses dynamic programming to recursively check for the combinations and effectively call Zappos API
 * to get the product details
 */
package com.gift.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gift.utilities.JSONHandler;
import com.gift.utilities.Product;
import com.gift.utilities.RequestGenerator;

public class SearchGifts {
    public static RequestGenerator request = new RequestGenerator();
    public static JSONHandler jsonHelper = new JSONHandler();

    private static Map<Double,List<Product>> productMap = new HashMap<Double,List<Product>>();
    private static Map<Double,Integer> countMap = new HashMap<Double,Integer>();
    private static Map<Double,Integer> priceFacet = new TreeMap<Double,Integer>();
	private static ArrayList<List<Double>> finalList = new ArrayList<List<Double>>();
	
    private static int totalCombinations;

    public static void main(String[] args) {

        /*
         * 1. Get the Price facets
         * 2. Get the Data based on user input
         */
        try {
        	double lastAvailablePrice = 0;
        	String urlD = request.getFacetRequest();
            String json = request.generateRequest(urlD);
            JSONArray responseJSON = new JSONArray();
            responseJSON = (JSONArray) (new JSONObject(json)).get("facets");
            JSONArray dresponseJSON = ((JSONObject) responseJSON.get(0)).getJSONArray("values");
            for(int i = 0 ; i < dresponseJSON.length() ; i++){
                int count = 0;
                count += Integer.parseInt(dresponseJSON.getJSONObject(i).getString("count"));
                double price = Double.parseDouble(dresponseJSON.getJSONObject(i).getString("name"));
                lastAvailablePrice = price;
                priceFacet.put(price,count);
            }
            
            /* Get the input from the user */
            Scanner input = new Scanner(System.in);
            double targetPrice;
            int numberOfItems;
            System.out.print("Please enter the total number of items you would like to gift: ");
            numberOfItems = input.nextInt();
            System.out.print("Please enter your budget: ");
            targetPrice = input.nextDouble();
            System.out.println("\n Checking for products in your budget.......\n\n");
            input.close();
            
            if(numberOfItems<=0) {
            	System.out.println("You are not giving a gift to your friend?? Bad!!!!");
            	return;
            }
            
            if(lastAvailablePrice<targetPrice) {
            	System.out.println("You are so rich, we dont have any product of that price!!!!");
            }
            
            int count =0;
            int i=0;

            Double d = new Double(targetPrice);
            int arraySize = d.intValue();
            if(arraySize<priceFacet.size()) {
            	arraySize = priceFacet.size();
            }
            double[] allPrices = new double[arraySize];
            for(Map.Entry<Double,Integer> entry : priceFacet.entrySet()) {

            	  // check first entry or first two entries to limit the upper end of prices
            	  Integer value = entry.getValue();
                  count+=value;
                  countMap.put(entry.getKey(), value);
                  priceFacet.put(entry.getKey(), count);
                  allPrices[i++]=entry.getKey();

            }

			double[] temp = new double[arraySize];
			fun(allPrices,temp,0,0,allPrices.length-1,targetPrice,numberOfItems);

            for(List<Double> combination : finalList) {
    			for(Double value : combination) {
    				
    				int totalCount = priceFacet.get(value);
    				int pageNum = totalCount/100;
    				int position = totalCount%100;
    				int totalNumber = countMap.get(value);
    				int maxCumulative = totalCount - totalNumber;
    				int maxPages = maxCumulative/100;

    				if(position>0) {
    					pageNum++;
    				}

    				while(pageNum>=maxPages) {
    				urlD = request.getSearchRequest(pageNum);
    				json = request.generateRequest(urlD);
    				JSONArray ja = (JSONArray) (new JSONObject(json)).get("results");

    				Product product;
    				List<Product> productList = new ArrayList<Product>();
    				int checkCount = 0;
    				for(int j=position-1;j>=0;j--)
    				{	
    					product = new Product(ja.get(j).toString());
    					int retval = Double.compare(product.getPrice(), value);
    					if(retval==0) {
    						productList.add(product);
    						checkCount++;
    					}
    					if(checkCount==totalNumber) {
    						break;
    					}
    				}
    				if(checkCount==totalNumber) {
    					productMap.put(value,productList);
						break;
					}
    				pageNum--; //check for previous pages for the results
    				}
    				
    				
    			}
    		}
            
            for(List<Double> combination : finalList) {
    			System.out.println("*****************Product Combination*************");
    			for(Double value : combination) {
    				System.out.println("\nProducts of price: $"+ value+"\n");
    		          	  // check first entry or first two entries to limit the upper end of prices
    		          	   List<Product> products = productMap.get(value);
    		          	   if(products !=null) {
    		          	   Iterator<Product> iter = products.iterator();
    		          	   System.out.println("Choose from any of the below:\n");
    		          	   while(iter.hasNext()) {
    		          		   Product pim = iter.next();
    		          		   System.out.println(pim.getProductName()+"\n");
    		          	   }
    		          	   }
    			}
    			
            }
            
            System.out.println("\n\nTotal Combinations: "+totalCombinations);
    		

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void fun(double allPrices[],double[] tempCombinations,int index,int start,int end,double sum,int n)
    {
    	if((sum<0)||(start>end) || index>n)
    	     return;
        if((sum>=0 && sum<=0.01) && index==n)
        {   totalCombinations++;
	        List<Double> list = new ArrayList<Double>();
        	for(int i=0;i<index;i++)
        	{	
        		list.add(tempCombinations[i]);
        	}
        	finalList.add(list);
        	return;
        }
       tempCombinations[index]=allPrices[start];
       fun(allPrices,tempCombinations,index+1,start+1,end,sum-allPrices[start],n);
       fun(allPrices,tempCombinations,index,start+1,end,sum,n);
    }
}
