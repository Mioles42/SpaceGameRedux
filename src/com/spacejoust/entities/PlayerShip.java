package com.spacejoust.entities;

/*
 * PlayerShip: A Ship that is controlled directly
 * by the player through keyboard input.
 */
public abstract class PlayerShip extends Ship {

	private Item[] items;

	//The number designated to the player that controls this PlayerShip.
	private int playerNum;

	public PlayerShip(double x, double y, double rot, int player) {
		super(x, y, rot);
		this.playerNum = player;
		items = new Item[getMaxItems()];
	}

	//Getters and setters: Player number
	public int getPlayerNum() { return playerNum; }
	public void setPlayerNum(int playerNum) { this.playerNum = playerNum; }

	public abstract int getMaxItems(); //Stat for number of items that can be carried
	public abstract void press(int key);
	public abstract void release(int key);

	public boolean addItem(Item item) {
		for(int i = 0; i < items.length; i++) {
			if(items[i] == null) {
				items[i] = item;
				return true;
			}
		}
		return false;
	}
	public void useItem(int itemNum) {
		if(itemNum < getMaxItems() && items[itemNum] != null) {
			items[itemNum].use(this);
			items[itemNum] = null;
		}
	}
	public Item getItem(int itemNum) {
		return items[itemNum];
	}

	public void update() {
		//Deal with directional acceleration
		if(getAccX() != 0 && getAccY() != 0) {
			double r = this.getPosR();
			double ratioX; //A ratio of x to y motion
			if(r < 90) ratioX = (r / 90.0);
			else if(r >= 90 && r < 180) ratioX = ((90.0 - (r % 90.0)) / 90.0);
			else if(r >= 180 && r < 270)  ratioX = -((r % 90.0) / 90.0);
			else ratioX = -((90.0 - (r % 90.0)) / 90.0);
			System.out.println("ratioX: " + ratioX);
			this.setAccX(ratioX * 1000);
			double ratioY = 1 - Math.abs(ratioX);
			if(r >= 270 || r < 90) ratioY *= -1;
			this.setAccY(ratioY * 1000);
		}
		super.update();
	}
}
