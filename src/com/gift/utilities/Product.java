package com.gift.utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
		private double price;
		private int colorId;
		private String productName;
		private int productId;
		
		public Product(String s) throws JSONException {
			JSONObject obj = new JSONObject(s);
			String p = (String) obj.get("price");
			p = p.substring(1);
			p = p.replace(",", "");
			this.price = Double.parseDouble(p);
			this.colorId = Integer.parseInt((String) obj.get("colorId"));
			this.productName = (String) obj.get("productName");
			this.productId = Integer.parseInt((String) obj.get("productId"));
		}
		public String toString() {
			String tmpString = "\nProduct ID: "+productId;
			tmpString += "\nProduct Name: "+productName;
			tmpString += "\nPrice: "+price;
			tmpString += "\nColor ID: "+colorId;
			tmpString += "\nProduct URL: http://www.zappos.com/product/"
					+productId+"/color/"+colorId+"\n";
			return tmpString;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(float price) {
			this.price = price;
		}
		public int getColorId() {
			return colorId;
		}
		public void setColorId(int colorId) {
			this.colorId = colorId;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public int getProductId() {
			return productId;
		}
		public void setProductId(int productId) {
			this.productId = productId;
		}
	}
