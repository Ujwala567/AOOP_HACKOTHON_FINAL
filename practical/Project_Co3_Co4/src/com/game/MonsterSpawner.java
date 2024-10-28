package com.game;

public class MonsterSpawner implements Runnable{
	private AdventureGame game;
	private volatile boolean running = true;
	public MonsterSpawner(AdventureGame game) {
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
				Thread.sleep(15000);
				if(Math.random()<0.5) {
					game.spawnMonster();
				}
			}
			catch(InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		
	}
	

}
