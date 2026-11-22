/*
 * Shattered Lab
 * On-screen grimoire button (in the tag stack above the toolbar), G key on PC.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.HackerGrimoire;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.watabou.input.GameAction;

public class GrimoireTag extends Tag {

	private ItemSprite icon;

	public GrimoireTag() {
		super(0x2E7D32);
		setSize(SIZE, SIZE);
		visible = true;
	}

	@Override
	protected void createChildren() {
		super.createChildren();
		icon = new ItemSprite(ItemSpriteSheet.HACK_GRIMOIRE);
		add(icon);
	}

	@Override
	protected void layout() {
		super.layout();
		if (!flipped)   icon.x = x + (SIZE - icon.width()) / 2f + 1;
		else            icon.x = x + width - (SIZE + icon.width()) / 2f - 1;
		icon.y = y + (height - icon.height()) / 2f;
		PixelScene.align(icon);
	}

	@Override
	public GameAction keyAction() {
		return SPDAction.GRIMOIRE;
	}

	@Override
	protected void onClick() {
		super.onClick();
		if (Dungeon.hero != null && Dungeon.hero.ready && Dungeon.hero.isAlive()) {
			GameScene.show(new WndHackMenu());
		}
	}

	@Override
	protected String hoverText() {
		return Messages.titleCase(new HackerGrimoire().name());
	}

	@Override
	public void update() {
		visible = Dungeon.hero != null && Dungeon.hero.isAlive();
		HackSelfTest.maybeRun();
		super.update();
	}
}
