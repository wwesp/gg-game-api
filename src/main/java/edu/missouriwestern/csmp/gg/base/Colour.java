package edu.missouriwestern.csmp.gg.base;

public enum Colour {
	BLACK, WHITE, GREY, GREEN, BLUE, RED, YELLOW, PINK, ORANGE, MAGENTA, CRAN;
	
	public Colour getNext(){
		return ordinal()<values().length-1?values()[ordinal()+1]:null;
	}
}