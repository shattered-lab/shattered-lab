/*
 * Shattered Lab
 * Library card: instantly takes you to the library (floor 0) and back.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.hacks.LibraryLevel;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class LibraryCard extends Item {

	public static final String AC_USE = "USE";

	{
		image = ItemSpriteSheet.HACK_LIBRARY_CARD;
		unique = true;
		stackable = false;
		bones = false;
		defaultAction = AC_USE;
	}

	private int returnDepth = -1;
	private int returnBranch = 0;
	private int returnPos = -1;

	private static boolean inLibrary(){
		return Dungeon.depth == 0 && Dungeon.branch == 0;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (action.equals(AC_USE)) {
			return Messages.get(this, inLibrary() ? "ac_return" : "ac_go");
		}
		return super.actionName(action, hero);
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (!action.equals(AC_USE)) return;

		if (inLibrary()) {
			int depth = returnDepth;
			int branch = returnBranch;
			int pos = returnPos;
			if (depth <= 0) {
				//no bookmark - go to the deepest visited floor, by the stairs
				if (Statistics.deepestFloor < 1) {
					GLog.w(Messages.get(this, "nowhere"));
					return;
				}
				depth = Statistics.deepestFloor;
				branch = 0;
				pos = -1;
			}
			returnDepth = -1;
			teleport(depth, branch, pos);
		} else {
			if (!Dungeon.interfloorTeleportAllowed()) {
				GLog.w(Messages.get(this, "preventing"));
				return;
			}
			if (Dungeon.branch != 0) {
				GLog.w(Messages.get(this, "no_branch"));
				return;
			}
			returnDepth = Dungeon.depth;
			returnBranch = Dungeon.branch;
			returnPos = hero.pos;
			teleport(0, 0, LibraryLevel.SPAWN + LibraryLevel.W);
		}
	}

	private void teleport(int depth, int branch, int pos){
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		Level.beforeTransition();
		Invisibility.dispel();
		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = depth;
		InterlevelScene.returnBranch = branch;
		InterlevelScene.returnPos = pos;
		Game.switchScene(InterlevelScene.class);
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (returnDepth > 0) {
			desc += "\n\n" + Messages.get(this, "desc_set", returnDepth);
		}
		return desc;
	}

	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x66FF66, 2f);

	@Override
	public ItemSprite.Glowing glowing() {
		return returnDepth > 0 ? GREEN : null;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 0;
	}

	private static final String DEPTH  = "return_depth";
	private static final String BRANCH = "return_branch";
	private static final String POS    = "return_pos";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DEPTH, returnDepth);
		bundle.put(BRANCH, returnBranch);
		bundle.put(POS, returnPos);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		returnDepth = bundle.getInt(DEPTH);
		returnBranch = bundle.getInt(BRANCH);
		returnPos = bundle.getInt(POS);
	}
}
