package pinacolada.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.blights.Spear;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.SurroundedPower;
import pinacolada.patches.creature.AbstractMonsterPatches;
import pinacolada.powers.special.MacroscopePower;
import pinacolada.resources.PGR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PCLIntentInfo
{
    protected static final ArrayList<AbstractPower> ENEMY_POWERS = new ArrayList<>();
    protected static final ArrayList<AbstractPower> PLAYER_POWERS = new ArrayList<>();
    protected static final ArrayList<AbstractPower> TEMP_PLAYER_POWERS = new ArrayList<>();
    protected static final ArrayList<AbstractPower> TEMP_ENEMY_POWERS = new ArrayList<>();
    protected ArrayList<AbstractPower> powers = new ArrayList<>();
    public static AbstractMonster currentEnemy;
    public final AbstractMonster enemy;
    public final HashMap<String, Integer> modifiers = new HashMap<>();
    public AbstractMonster.Intent intent;
    public EnemyMoveInfo move;
    public boolean isAttacking;

    protected PCLIntentInfo(AbstractMonster enemy)
    {
        this.enemy = enemy;
    }

    public static void createPlayerPower(AbstractPower power, boolean replaceExisting)
    {
        final ArrayList<AbstractPower> powers = PLAYER_POWERS;
        for (int i = 0; i < powers.size(); i++)
        {
            if (powers.get(i).ID.equals(power.ID))
            {
                if (replaceExisting)
                {
                    powers.set(i, power);
                }

                return;
            }
        }

        powers.add(power);
    }

    public static PCLIntentInfo getCurrentIntent()
    {
        return currentEnemy == null ? null : get(currentEnemy);
    }

    public static PCLIntentInfo get(AbstractMonster enemy)
    {
        PCLIntentInfo intent = AbstractMonsterPatches.AbstractMonster_Fields.enemyIntent.get(enemy);
        if (intent == null)
        {
            intent = new PCLIntentInfo(enemy);
            AbstractMonsterPatches.AbstractMonster_Fields.enemyIntent.set(enemy, intent);
        }

        intent.intent = enemy.intent;
        intent.move = ReflectionHacks.getPrivate(enemy, AbstractMonster.class, "move");
        intent.isAttacking = (intent.move != null && intent.move.baseDamage >= 0); // It is -1 if not attacking

        return intent;
    }

    public boolean isAttacking()
    {
        return isAttacking;
    }

    public PCLIntentInfo addModifier(String powerID, int amount)
    {
        modifiers.put(powerID, amount);

        return this;
    }

    public int getDamage(boolean multi)
    {
        return isAttacking ? ((multi ? getDamageMulti() : 1) * enemy.getIntentDmg()) : 0;
    }

    public int getBaseDamage(boolean multi)
    {
        return isAttacking ? ((multi ? getDamageMulti() : 1) * enemy.getIntentBaseDmg()) : 0;
    }

    public int getDamageMulti()
    {
        return isAttacking ? (move.isMultiDamage ? move.multiplier : 1) : 0;
    }

    public Texture getIntentImage() {
        int tmp = getDamage(true);

        if (tmp < 5) {
            return ImageMaster.INTENT_ATK_1;
        } else if (tmp < 10) {
            return ImageMaster.INTENT_ATK_2;
        } else if (tmp < 15) {
            return ImageMaster.INTENT_ATK_3;
        } else if (tmp < 20) {
            return ImageMaster.INTENT_ATK_4;
        } else if (tmp < 25) {
            return ImageMaster.INTENT_ATK_5;
        } else {
            return tmp < 30 ? ImageMaster.INTENT_ATK_6 : ImageMaster.INTENT_ATK_7;
        }
    }

    public int getFinalDamage() {
        currentEnemy = enemy;
        float damage = getBaseDamage(false);

        final AbstractPlayer player = AbstractDungeon.player;
        load(player, enemy, modifiers);

        if (player.hasBlight(Spear.ID))
        {
            damage *= player.getBlight(Spear.ID).effectFloat();
        }

        for (AbstractPower p : TEMP_ENEMY_POWERS)
        {
            damage = recordDamage(p, damage, p.atDamageGive(damage, DamageInfo.DamageType.NORMAL));
        }

        for (AbstractPower p : TEMP_PLAYER_POWERS)
        {
            damage = recordDamage(p, damage, p.atDamageReceive(damage, DamageInfo.DamageType.NORMAL));
        }

        damage = player.stance.atDamageReceive(damage, DamageInfo.DamageType.NORMAL);

        //if (monster.applyBackAttack())
        if (player.hasPower(SurroundedPower.POWER_ID) && (player.flipHorizontal && player.drawX < enemy.drawX || !player.flipHorizontal && player.drawX > enemy.drawX))
        {
            damage = (float) ((int) (damage * 1.5f));
        }

        for (AbstractPower p : TEMP_ENEMY_POWERS)
        {
            damage = recordDamage(p, damage, p.atDamageFinalGive(damage, DamageInfo.DamageType.NORMAL));
        }

        for (AbstractPower p : TEMP_PLAYER_POWERS)
        {
            damage = recordDamage(p, damage, p.atDamageFinalReceive(damage, DamageInfo.DamageType.NORMAL));
        }

        currentEnemy = null;
        return Math.max(0, MathUtils.floor(damage));
    }

    protected float recordDamage(AbstractPower po, float damage, float result) {
        PGR.core.combatScreen.formulaDisplay.addEnemyAttackPower(po, damage, result);
        return result;
    }

    protected static void load(AbstractPlayer player, AbstractMonster enemy, HashMap<String, Integer> modifiers) {

        TEMP_PLAYER_POWERS.clear();
        for (AbstractPower p : player.powers)
        {
            if (!(p instanceof InvisiblePower && (!(p instanceof MacroscopePower))))
            {
                TEMP_PLAYER_POWERS.add(p);
            }
        }
        loadPowers(player, PLAYER_POWERS, TEMP_PLAYER_POWERS, modifiers);

        TEMP_ENEMY_POWERS.clear();
        for (AbstractPower p : enemy.powers)
        {
            if (!(p instanceof InvisiblePower && (!(p instanceof MacroscopePower))))
            {
                TEMP_ENEMY_POWERS.add(p);
            }
        }
        loadPowers(enemy, ENEMY_POWERS, TEMP_ENEMY_POWERS, modifiers);
    }

    protected static void loadPowers(AbstractCreature c, ArrayList<AbstractPower> defaultPowers, ArrayList<AbstractPower> powers, HashMap<String, Integer> modifiers)
    {
        boolean sort = false;
        for (AbstractPower p : defaultPowers)
        {
            if (loadPower(c, powers, modifiers, p))
            {
                sort = true;
            }
        }

        if (sort)
        {
            Collections.sort(powers);
        }
    }

    protected static boolean loadPower(AbstractCreature owner, ArrayList<AbstractPower> powers, HashMap<String, Integer> modifiers, AbstractPower power)
    {
        if ((power.amount = modifiers.getOrDefault(power.ID, 0)) != 0)
        {
            power.owner = owner;

            for (int i = 0; i < powers.size(); i++)
            {
                final AbstractPower p = powers.get(i);
                if (p.ID.equals(power.ID))
                {
                    power.amount += p.amount;
                    if (power.amount == 0)
                    {
                        powers.remove(i);
                    }
                    else
                    {
                        powers.set(i, power);
                    }

                    return true;
                }
            }

            powers.add(power);
            return true;
        }

        return false;
    }
}
