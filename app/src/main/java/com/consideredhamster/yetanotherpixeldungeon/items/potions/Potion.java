
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
 */package com.consideredhamster.yetanotherpixeldungeon.items.potions;

import java.util.ArrayList;
import java.util.HashSet;

import com.watabou.noosa.audio.Sample;
import com.consideredhamster.yetanotherpixeldungeon.Assets;
import com.consideredhamster.yetanotherpixeldungeon.Badges;
import com.consideredhamster.yetanotherpixeldungeon.Dungeon;
import com.consideredhamster.yetanotherpixeldungeon.actors.hero.Hero;
import com.consideredhamster.yetanotherpixeldungeon.effects.Splash;
import com.consideredhamster.yetanotherpixeldungeon.items.Item;
import com.consideredhamster.yetanotherpixeldungeon.items.ItemStatusHandler;
import com.consideredhamster.yetanotherpixeldungeon.levels.Level;
import com.consideredhamster.yetanotherpixeldungeon.levels.Terrain;
import com.consideredhamster.yetanotherpixeldungeon.scenes.GameScene;
import com.consideredhamster.yetanotherpixeldungeon.sprites.ItemSprite;
import com.consideredhamster.yetanotherpixeldungeon.sprites.ItemSpriteSheet;
import com.consideredhamster.yetanotherpixeldungeon.utils.GLog;
import com.consideredhamster.yetanotherpixeldungeon.windows.WndOptions;
import com.watabou.utils.Bundle;

public abstract class Potion extends Item {
	
	public static final String AC_DRINK	= "DRINK";

	private static final String TXT_HARMFUL		= "Harmful potion!";
	private static final String TXT_BENEFICIAL	= "Beneficial potion";
	private static final String TXT_YES			= "Yes, I know what I'm doing";
	private static final String TXT_NO			= "No, I changed my mind";
	private static final String TXT_R_U_SURE_DRINK = 
		"Are you sure you want to drink it? In most cases you should throw such potions at your enemies.";
	private static final String TXT_R_U_SURE_THROW = 
		"Are you sure you want to throw it? In most cases it makes sense to drink it.";
	
	private static final float TIME_TO_DRINK = 1f;
	
	private static final Class<?>[] potions = {
		PotionOfMending.class,
		PotionOfWisdom.class,
		PotionOfCorrosiveGas.class,
		PotionOfLiquidFlame.class,
		PotionOfStrength.class,
		PotionOfThunderstorm.class,
		PotionOfLevitation.class,
		PotionOfMindVision.class, 
		PotionOfBlessing.class,
		PotionOfInvisibility.class,
		PotionOfOvergrowth.class,
		PotionOfFrigidVapours.class
	};
	private static final String[] colors = {
		"turquoise", "crimson", "azure", "jade", "golden", "magenta", 
		"charcoal", "ivory", "amber", "bistre", "indigo", "silver"};
	private static final Integer[] images = {
		ItemSpriteSheet.POTION_TURQUOISE, 
		ItemSpriteSheet.POTION_CRIMSON, 
		ItemSpriteSheet.POTION_AZURE, 
		ItemSpriteSheet.POTION_JADE, 
		ItemSpriteSheet.POTION_GOLDEN, 
		ItemSpriteSheet.POTION_MAGENTA, 
		ItemSpriteSheet.POTION_CHARCOAL, 
		ItemSpriteSheet.POTION_IVORY, 
		ItemSpriteSheet.POTION_AMBER, 
		ItemSpriteSheet.POTION_BISTRE, 
		ItemSpriteSheet.POTION_INDIGO, 
		ItemSpriteSheet.POTION_SILVER};
	
	private static ItemStatusHandler<Potion> handler;

	private String color;

    protected boolean harmful;
	
	{
		stackable = true;
        harmful = false;
        shortName = "??";

    }
	
	@SuppressWarnings("unchecked")
	public static void initColors() {
		handler = new ItemStatusHandler<Potion>( (Class<? extends Potion>[])potions, colors, images );
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}
	
	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<Potion>( (Class<? extends Potion>[])potions, colors, images, bundle );
	}
	
	public Potion() {
		super();
		image = handler.image( this );
		color = handler.label( this );
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions(hero );
		actions.add( AC_DRINK );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {
		if (action.equals( AC_DRINK )) {
			
			if (isTypeKnown() && harmful) {
				
					GameScene.show( 
						new WndOptions( TXT_HARMFUL, TXT_R_U_SURE_DRINK, TXT_YES, TXT_NO ) {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									drink( hero );
								}
							};
						}
					);
					
				} else {
					drink( hero );
				}
			
		} else {
			
			super.execute(hero, action);
			
		}
	}
	
	@Override
	public void doThrow( final Hero hero ) {

		if (isTypeKnown() && !harmful) {
		
			GameScene.show( 
				new WndOptions( TXT_BENEFICIAL, TXT_R_U_SURE_THROW, TXT_YES, TXT_NO ) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							Potion.super.doThrow( hero );
						}
					};
				}
			);
			
		} else {
			super.doThrow(hero);
		}
	}
	
	protected void drink( Hero hero ) {
		
		detach( hero.belongings.backpack );
		
		hero.spend( TIME_TO_DRINK );
		hero.busy();
        apply( hero );
		
		Sample.INSTANCE.play( Assets.SND_DRINK );
		
		hero.sprite.operate( hero.pos );
	}
	
	@Override
	protected void onThrow( int cell ) {
        if (Dungeon.level.map[cell] == Terrain.WELL || Level.chasm[cell]) {

            super.onThrow(cell);

        } else {

            detach(curUser.belongings.backpack);
			
			shatter( cell );
			
		}
	}
	
	protected void apply( Hero hero ) {
		shatter( hero.pos );
	}
	
	public void shatter( int cell ) {
		if (Dungeon.visible[cell]) {
			GLog.i( "The flask shatters and " + color() + " liquid splashes harmlessly" );
			Sample.INSTANCE.play( Assets.SND_SHATTER );
			splash( cell );
		}
	}

    @Override
	public boolean isTypeKnown() {
		return handler.isKnown( this );
	}
	
	public void setKnown() {
		if (!isTypeKnown()) {
			handler.know( this );
		}
		
		Badges.validateAllPotionsIdentified();
	}
	
	@Override
	public Item identify() {
		setKnown();
		return this;
	}

    @Override
    public String quickAction() {

        if( !isTypeKnown() )
            return null;

        return harmful ? AC_THROW : AC_DRINK;
    }
	
	protected String color() {
		return color;
	}
	
	@Override
	public String name() {
		return isTypeKnown() ? name : color + " potion";
	}
	
	@Override
	public String info() {
		return isTypeKnown() ?
			desc() :
			"This flask contains a swirling " + color + " liquid. " +
			"Who knows what it will do when drunk or thrown?";
	}
	
	@Override
	public boolean isIdentified() {
		return isTypeKnown();
	}
	
	@Override
	public boolean isUpgradeable() {
		return false;
	}
	
	public static HashSet<Class<? extends Potion>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Potion>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler.known().size() == potions.length;
	}

    public static int alchemySkill() {

        int result = handler.known().size();

        if( handler.isKnown( PotionOfStrength.class ) )
            result--;

        if( handler.isKnown( PotionOfWisdom.class ) )
            result--;

        return result;
     }
	
	protected void splash( int cell ) {
		Splash.at( cell, ItemSprite.pick( image,  8, 10 ), 10 );
//		Splash.at( cell, ItemSprite.pick( image, 10,  3 ), 3 );
//		Splash.at( cell, ItemSprite.pick( image,  5,  3 ), 3 );
//		Splash.at( cell, ItemSprite.pick( image,  8,  2 ), 1 );
	}
	
	@Override
	public int price() {
		return 25 * quantity;
	}
}
