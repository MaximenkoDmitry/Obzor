package com.itransition.obzor.entity;

public enum TypeOfOverview {
	BOOKS,GAMES,FILMS;

	public String getType(){
		return this.name();
	}
}
