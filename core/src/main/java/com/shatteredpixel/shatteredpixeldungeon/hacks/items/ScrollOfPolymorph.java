/*
 * Shattered Lab
 * Scroll of polymorph: turns all visible enemies (except bosses) into sheep.
 * XP for the transformed enemies is still awarded.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfPolymorph extends Scroll {

	{
		talentChance = 0;
	}

	@Override
	public void reset() {
		super.reset();
		image = ItemSpriteSheet.HACK_SCROLL_POLYMORPH;
	}

	@Override
	public boolean isKnown() {
		return true;
	}

	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);

		new Flare(5, 32).color(0xFF88FF, true).show(curUser.sprite, 2f);
		Sample.INSTANCE.play(Assets.Sounds.READ);

		int count = transformVisible(curUser);
		if (count > 0) {
			Sample.INSTANCE.play(Assets.Sounds.PUFF);
			Sample.INSTANCE.play(Assets.Sounds.SHEEP);
			GLog.p(Messages.get(this, "transformed", count));
		} else {
			GLog.i(Messages.get(this, "nothing"));
		}

		identify();
		readAnimation();
	}

	//turns visible enemies into sheep, returns how many
	public static int transformVisible(com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero curUser){
		int count = 0;
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (mob.alignment == Char.Alignment.ENEMY
					&& Dungeon.level.heroFOV[mob.pos]
					&& !Char.hasProp(mob, Char.Property.BOSS)
					&& !Char.hasProp(mob, Char.Property.MINIBOSS)
					&& !Char.hasProp(mob, Char.Property.IMMOVABLE)) {

				int pos = mob.pos;
				int exp = mob.EXP;
				boolean expAllowed = curUser.lvl <= mob.maxLvl;

				if (TargetHealthIndicator.instance != null) {
					TargetHealthIndicator.instance.target(null);
				}
				mob.destroy();
				if (mob.sprite != null) mob.sprite.killAndErase();

				Sheep sheep = new Sheep();
				sheep.initialize(15);
				sheep.pos = pos;
				GameScene.add(sheep);
				CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

				if (expAllowed && exp > 0) {
					curUser.earnExp(exp, mob.getClass());
				}
				count++;
			}
		}
		return count;
	}

	@Override
	public int value() {
		return 50 * quantity;
	}

	@Override
	public int energyVal() {
		return 6 * quantity;
	}
}
