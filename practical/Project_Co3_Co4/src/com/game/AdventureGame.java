package com.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdventureGame {
	private Player player;
	private Scanner scanner;
	private Connection connection;
	private GameAutosave autosave;
	private MonsterSpawner monsterSpawner;
	private ExecutorService executorService;
	private volatile boolean monsterPresent = false;
	private int rowsAffected;
	 public AdventureGame() {
		 scanner =new Scanner(System.in);
		 try {
			 connection=DBConnection.getConnection();
		 }
		 catch(SQLException e) {
			 System.out.println("Database connection failed:"+e.getMessage());
			 System.exit(1);
		 }
		 executorService=Executors.newFixedThreadPool(2);
	 }
	 public void start() {
		 System.out.println("Welcome to the Adventure Game!");
		 System.out.print("Enter your name: ");
		 String playername=scanner.nextLine();
		 player=loadPlayer(playername);
		 if(player==null) {
			 player=new Player(playername);
			 savePlayer();
		 }
		 autosave=new GameAutosave(this);
		 monsterSpawner=new MonsterSpawner(this);
		 executorService.submit(autosave);
		 executorService.submit(monsterSpawner);
		 gameLoop();
	 }
	 public synchronized void spawnMonster() {
		 if(!monsterPresent) {
			 monsterPresent=true;
			 System.out.println("\nA monster has appeared!");
		 }
	 }
	 private void gameLoop() {
		 boolean running =true;
		 while(running && player.getHealth()>0) {
			 System.out.println("\nLocation: "+player.getCurrentRoom());
			 System.out.println("Health: "+player.getHealth());
			 System.out.println("Score: "+player.getScore());
			 if(monsterPresent) {
				 System.out.println("WARNING: Monster present!");
			 }
			 System.out.println("\nCommands:north,south,east,west,fight,heal,quit");
			 System.out.print("What would you like to do?");
			 String command=scanner.nextLine().toLowerCase();
			 switch(command) {
			       case "north":
			       case "south":
			       case "east":
			       case "west":
			    	   move(command);
			    	   break;
			       case "fight":
			    	   if(monsterPresent) {
			    		   fight();
			    		   monsterPresent=false;
			    	   }
			    	   else {
			    		   System.out.println("No monster to fight!");
			    	   }
			    	   break;
			       case "heal":
			    	   heal();
			    	   break;
			       case "quit":
			    	   running=false;
			    	   break;
			    	   default:
			    		   System.out.println("Invalid command!");
			 }
		 }
		 autosave.stop();
		 monsterSpawner.stop();
		 executorService.shutdown();
		 savePlayer();
		 System.out.println("Game Over! Final score: " +player.getScore());
	 }
	 private void move(String direction) {
		 String[] rooms= {"Entrance","Cave","Forest","Mountain","River","Castle","Dungeon"};
		 int randomRoom=(int)(Math.random()*rooms.length);
		 player.setCurrentRoom(rooms[randomRoom]);
		 System.out.println("You moved "+ direction +" to the"+ rooms[randomRoom]);
	 }
	 private void fight() {
		int damage=(int)(Math.random()*20);
		player.setHealth(player.getHealth()-damage);
		player.setScore(player.getScore()+15);
		System.out.println("You fought a monster! Took" + damage +" damage but gained 15 points!");
	 }
	 private void heal() {
		 if(player.getScore()>=10) {
			 player.setHealth(Math.min(100,player.getHealth() + 20));
			 player.setScore(player.getScore()-10);
			 System.out.println("Healed 20 HP! Costs:10 points");
		 }
		 else {
			 System.out.println("Not enough points to heal!");
		 }
	 }
	 public synchronized void savePlayer() {
		    try {
		        if(player.getId() == 0) {
		            PreparedStatement insertStmt = connection.prepareStatement(
		                "INSERT INTO players (player_name,current_room,health,score,last_updated) VALUES(?,?,?,?,?)",
		                Statement.RETURN_GENERATED_KEYS
		            );
		            insertStmt.setString(1, player.getName());
		            insertStmt.setString(2, player.getCurrentRoom());
		            insertStmt.setInt(3, player.getHealth());
		            insertStmt.setInt(4, player.getScore());
		            insertStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
		            insertStmt.executeUpdate();
		            
		            ResultSet rs = insertStmt.getGeneratedKeys();
		            if(rs.next()) {
		                player.setId(rs.getInt(1));
		            }
		        }
		        PreparedStatement updateStmt = connection.prepareStatement(
		            "UPDATE players SET current_room = ?, health = ?, score = ?, last_updated = ? WHERE player_name = ?"
		        );
		        updateStmt.setString(1, player.getCurrentRoom());
		        updateStmt.setInt(2, player.getHealth());
		        updateStmt.setInt(3, player.getScore());
		        updateStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
		        updateStmt.setString(5, player.getName());
		        updateStmt.executeUpdate();
		    } catch(SQLException e) {
		        System.out.println("Error saving player: " + e.getMessage());
		    }
		}
	 private Player loadPlayer(String playerName) {
		 try {
			 PreparedStatement stmt=connection.prepareStatement(
					 "SELECT *FROM players WHERE player_name = ?");
			 stmt.setString(1, playerName);
			 ResultSet rs=stmt.executeQuery();
			 if(rs.next()) {
				 Player p=new Player(playerName);
				 p.setId(rs.getInt("id"));
				 p.setCurrentRoom(rs.getString("current_room"));
				 p.setHealth(rs.getInt("health"));
				 p.setScore(rs.getInt("score"));
				 p.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
				 return p;
			 }
		 }
		 catch(SQLException e) {
			 System.out.println("Error loading player :"+e.getMessage());
		 }
		 return null;
	 }
	 public static void main(String[] args) {
		 new AdventureGame().start();
	 }
	public int getRowsAffected() {
		return rowsAffected;
	}
	public void setRowsAffected(int rowsAffected) {
		this.rowsAffected = rowsAffected;
	}
}

					 
					 
					 
					 
					 
					 
					 
					 
		 
		 
							 
							 
							 
							 
							 
							 
							 
							 
							 
							 
							 
							