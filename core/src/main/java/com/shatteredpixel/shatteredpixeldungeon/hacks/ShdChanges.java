/*
 * Shattered Lab
 * Mod changelog - shown first on the "Changes" screen.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeInfo;

import java.util.ArrayList;

public class ShdChanges {

	private static String m(String key){
		return Messages.get(ShdChanges.class, key);
	}

	public static void addAllChanges(ArrayList<ChangeInfo> changeInfos){
		ChangeInfo v12 = new ChangeInfo(m("title12"), true, m("intro12"));
		v12.hardlight(0x66FF66);
		changeInfos.add(v12);
		v12.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.TORCH), m("fx12"), m("fx12_desc")));
		v12.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_CRIMSON), m("fixes12"), m("fixes12_desc")));

		ChangeInfo v112 = new ChangeInfo(m("title112"), true, m("intro112"));
		v112.hardlight(0x66FF66);
		changeInfos.add(v112);
		v112.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.HACK_SCROLL_MEGA), m("mega112"), m("mega112_desc")));
		v112.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.HACK_WAND_ALL), m("fixes112"), m("fixes112_desc")));

		ChangeInfo v111 = new ChangeInfo(m("title111"), true, m("intro111"));
		v111.hardlight(0x66FF66);
		changeInfos.add(v111);
		v111.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.HACK_BESTIARY), m("fixes111"), m("fixes111_desc")));

		ChangeInfo v11 = new ChangeInfo(m("title11"), true, m("intro11"));
		v11.hardlight(0x66FF66);
		changeInfos.add(v11);
		v11.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.DEWDROP), m("fixes11"), m("fixes11_desc")));
		v11.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.HACK_GRIMOIRE), m("str11"), m("str11_desc")));
		v11.addButton(new ChangeButton(Icons.get(Icons.LANGS), m("uk11"), m("uk11_desc")));
		v11.addButton(new ChangeButton(Icons.get(Icons.DATA), m("reports11"), m("reports11_desc")));

		ChangeInfo info = new ChangeInfo(m("title"), true, m("intro"));
		info.hardlight(0x66FF66);
		changeInfos.add(info);

		info.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_SPELLBOOK), m("library"), m("library_desc")));
		info.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.HACK_GRIMOIRE), m("grimoire"), m("grimoire_desc")));
		info.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_HOURGLASS), m("tools"), m("tools_desc")));
		info.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.HACK_BESTIARY), m("arena"), m("arena_desc")));
		info.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.HACK_SCROLL_DEGRADE), m("items"), m("items_desc")));
		info.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.HACK_BAG_OF_HOLDING), m("qol"), m("qol_desc")));
	}
}
