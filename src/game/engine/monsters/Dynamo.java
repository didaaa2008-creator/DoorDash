package game.engine.monsters;

import game.engine.Constants;
import game.engine.Role;

public class Dynamo extends Monster {
	
	public Dynamo(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}
	
	@Override
	public void executePowerupEffect(Monster opponentMonster) {
		opponentMonster.setFrozen(true);
	}

	@Override
	public int getEffectivePowerupCost() {
		// setEnergy doubles all energy changes, so deducting POWERUP_COST (500)
		// actually costs 1000. The monster must have 1000 energy before activating.
		return Constants.POWERUP_COST * 2;
	}
	
	@Override
	public void setEnergy(int energy) {
		super.setEnergy(getEnergy() + (energy - getEnergy()) * 2);
	}
}
