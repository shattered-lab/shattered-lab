/*
 * Shattered Lab
 * Tap teleport: pick any free cell on the floor - the hero appears there.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class HackTeleport {

	public static void select(){
		GameScene.selectCell(new CellSelector.Listener() {
			@Override
			public void onSelect(Integer cell) {
				if (cell == null) return;
				teleport(cell);
			}

			@Override
			public String prompt() {
				return Messages.get(HackTeleport.class, "prompt");
			}
		});
	}

	public static void teleport(int cell){
		Hero hero = Dungeon.hero;
		if (cell < 0 || cell >= Dungeon.level.length()
				|| (!Dungeon.level.passable[cell] && !Dungeon.level.avoid[cell])
				|| (Actor.findChar(cell) != null && Actor.findChar(cell) != hero)){
			GLog.w(Messages.get(HackTeleport.class, "blocked"));
			return;
		}
		HackCheats.markUsed();
		ScrollOfTeleportation.appear(hero, cell);
		Dungeon.level.occupyCell(hero);
		Dungeon.observe();
		GameScene.updateFog();
		hero.spendAndNext(Actor.TICK);
	}
}
