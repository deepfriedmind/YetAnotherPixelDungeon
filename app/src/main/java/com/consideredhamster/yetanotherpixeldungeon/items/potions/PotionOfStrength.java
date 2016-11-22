/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Yet Another Pixel Dungeon
 * Copyright (C) 2015-2016 Considered Hamster
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.consideredhamster.yetanotherpixeldungeon.items.potions;

import com.consideredhamster.yetanotherpixeldungeon.Badges;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Buff;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Withered;
import com.consideredhamster.yetanotherpixeldungeon.actors.hero.Hero;
import com.consideredhamster.yetanotherpixeldungeon.actors.hero.HeroClass;
import com.consideredhamster.yetanotherpixeldungeon.effects.Speck;
import com.consideredhamster.yetanotherpixeldungeon.sprites.CharSprite;
import com.consideredhamster.yetanotherpixeldungeon.ui.QuickSlot;
import com.consideredhamster.yetanotherpixeldungeon.utils.GLog;

public class PotionOfStrength extends Potion {

	{
		name = "Potion of Strength";
        shortName = "St";
	}
	
	@Override
	protected void apply( Hero hero ) {
		setKnown();

        hero.STR++;
        hero.strBonus++;

		int hpBonus = hero.heroClass == HeroClass.WARRIOR ? 3 : hero.heroClass == HeroClass.ACOLYTE ? 1 : 2 ;

        int restore = hero.HT - hero.HP;

        hero.HP = hero.HT += hpBonus;

        if( restore > 0 ) {
            hero.sprite.showStatus(CharSprite.POSITIVE, "%+dHP", restore);
        }

        hero.sprite.showStatus( CharSprite.POSITIVE, "+1 str +%d hp", hpBonus );

        hero.sprite.emitter().burst(Speck.factory(Speck.MASTERY), 12);

        Buff.detach(hero, Withered.class);

        GLog.p("Newfound strength surges through your body, boosting your physical capabilities." );

        QuickSlot.refresh();

		Badges.validateStrengthAttained();
	}
	
	@Override
	public String desc() {
		return
			"This powerful liquid will course through your muscles, " +
			"permanently increasing your physical stats and fully restoring your health.";
	}
	
	@Override
	public int price() {
		return isTypeKnown() ? 75 * quantity : super.price();
	}
}
