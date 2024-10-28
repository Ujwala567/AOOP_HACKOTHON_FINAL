package com.game;

import java.time.LocalDateTime;

public class Player {
	private int id;
	private String name;
	private String currentRoom;
	private int health;
	private int score;
	private LocalDateTime lastUpdated;
	
	public Player(String name) {
		this.name=name;
		this.currentRoom="Entrance";
		this.health=100;
		this.score=0;
		this.lastUpdated=LocalDateTime.now();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id=id;
	}
	public String getName() {
		return name;
	}
	public String getCurrentRoom() {
		return currentRoom;
	}
	public void setCurrentRoom(String currentRoom) {
		this.currentRoom=currentRoom;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health=health;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score=score;
	}
	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated=lastUpdated;
	}
}
