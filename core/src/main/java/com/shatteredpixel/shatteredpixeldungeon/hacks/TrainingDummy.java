/*
 * Shattered Lab
 * Training dummy: does not move, attack or die, counts the damage it takes.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;

import java.util.ArrayList;

public class TrainingDummy extends Mob {

	private static final int WINDOW = 10; //turns for the damage count

	{
		spriteClass = DummySprite.class;

		HP = HT = 1_000_000;
		defenseSkill = 0;
		EXP = 0;
		maxLvl = -2;

		alignment = Alignment.ENEMY;
		state = PASSIVE;

		properties.add(Property.IMMOVABLE);
	}

	private final ArrayList<float[]> log = new ArrayList<>();
	private int lastHit = 0;
	private int totalHits = 0;

	@Override
	protected boolean act() {
		//passive state: stands still and does nothing
		state = PASSIVE;
		return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);
		if (dmg > 0) {
			log.add(new float[]{Actor.now(), dmg});
			lastHit = dmg;
			totalHits++;
		}
		HP = HT;
		state = PASSIVE;
	}

	@Override
	public int attackSkill(Char target) {
		return 0;
	}

	@Override
	public int damageRoll() {
		return 0;
	}

	@Override
	public int drRoll() {
		return 0;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return false;
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	protected boolean getFurther(int target) {
		return false;
	}

	@Override
	public void beckon(int cell) {
		//does not react
	}

	@Override
	public String description() {
		float now = Actor.now();
		int sum = 0;
		for (float[] e : log.toArray(new float[0][])){
			if (now - e[0] <= WINDOW) sum += (int) e[1];
		}
		while (!log.isEmpty() && now - log.get(0)[0] > WINDOW) log.remove(0);

		return super.description() + "\n\n" + Messages.get(this, "stats",
				lastHit, sum, WINDOW, Math.round(sum / (float) WINDOW * 10f) / 10f, totalHits);
	}

	public static class DummySprite extends StatueSprite {
		public DummySprite(){
			super();
			resetColor();
		}

		@Override
		public void resetColor() {
			super.resetColor();
			hardlight(1.0f, 0.78f, 0.52f);
		}
	}
}
