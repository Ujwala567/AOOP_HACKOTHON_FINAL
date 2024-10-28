package com.game;

public class GameAutosave implements Runnable{
	private AdventureGame game;
	private volatile boolean running = true;
	public GameAutosave(AdventureGame game) {
		this.game=game;
	}
	public void stop() {
		running=false;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(running) {
			try {
				Thread.sleep(30000);
				game.savePlayer();
				System.out.println("\n Game autosaved!");
			}
			catch(InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		
	}
	

}
