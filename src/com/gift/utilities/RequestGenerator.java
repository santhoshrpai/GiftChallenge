package com.gift.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestGenerator {

		private static final String API_KEY = "&key=52ddafbe3ee659bad97fcce7c53592916a6bfd73";
		private static final String BASE_URL = "http://api.zappos.com";
		private static final String URL_ENCODING = "?";


		public String getPriceFacetRequest() {
			StringBuffer request = new StringBuffer(BASE_URL + ZapposAPI.SEARCH_PATH +ZapposAPI.GET_ALL_PATH + URL_ENCODING);
			String urlParam = "facets=[\"priceFacet\"]&excludes=[\"results\"]";
			request.append(urlParam).append(API_KEY);
			return request.toString();
		}

		public String getSearchRequest(int page) {
			StringBuffer request = new StringBuffer(BASE_URL + ZapposAPI.SEARCH_PATH +ZapposAPI.GET_ALL_PATH);
			request.append("&page="+page+"&limit=100&excludes=[\"brandName\",\"productUrl\",\"originalPrice\",\"thumbnailImageUrl\",\"percentOff\",\"styleId\"]");
			request.append("&sort={\"price\":\"asc\"}");
			request.append(API_KEY);
			return request.toString();
		}

		public String getFacetRequest() {
			StringBuffer request = new StringBuffer(BASE_URL + ZapposAPI.SEARCH_PATH  + URL_ENCODING);
			request.append("facets=[\"priceSort\"]&includes=[\"facets\"]&excludes=[\"results\"]");
			request.append(API_KEY);
			return request.toString();
		}

		public String generateRequest(String url) throws JSONException {
			String json = "";
				try {

					HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

					if(conn.getResponseCode() != 200) {
							throw new RuntimeException("Error : " + conn.getResponseCode()+" : "+conn.getResponseMessage());
					}

					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String line;
					while((line = br.readLine())!=null)
							json += line;

			        conn.disconnect();

				  } catch (MalformedURLException e) {
					  e.printStackTrace();
				  } catch (IOException e) {
					  e.printStackTrace();
				  }
				return json;
		}
		
		public Map<Double,Integer> getPriceFacets() {
		  Map<Double,Integer> priceFacet =  new HashMap<Double,Integer>();
		  String urlD = getFacetRequest();
          try {
			String json = generateRequest(urlD);
			JSONArray responseJSON = new JSONArray();
			responseJSON = (JSONArray) (new JSONObject(json)).get("facets");
			JSONArray dresponseJSON = ((JSONObject) responseJSON.get(0)).getJSONArray("values");
			for(int i = 0 ; i < dresponseJSON.length() ; i++){
				int count = 0;
				count += Integer.parseInt(dresponseJSON.getJSONObject(i).getString("count"));
				double price = Double.parseDouble(dresponseJSON.getJSONObject(i).getString("name"));
				priceFacet.put(price,count);
			}
          } catch (JSONException e) {
			e.printStackTrace();
          }
          return priceFacet;
		}
	}
