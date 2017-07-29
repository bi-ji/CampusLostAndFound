

package com.edu.claf.wheel;


public class StrericWheelAdapter implements WheelAdapter {
	
	private String[] strContents;
	
	public StrericWheelAdapter(String[] strContents){
		this.strContents=strContents;
	}
	
	
	public String[] getStrContents() {
		return strContents;
	}


	public void setStrContents(String[] strContents) {
		this.strContents = strContents;
	}


	public String getItem(int index) {
		if (index >= 0 && index < getItemsCount()) {
			return strContents[index];
		}
		return null;
	}
	
	public int getItemsCount() {
		return strContents.length;
	}
	public int getMaximumLength() {
		int maxLen=5;
		return maxLen;
	}
}
