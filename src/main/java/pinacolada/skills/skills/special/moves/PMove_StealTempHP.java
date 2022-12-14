package pinacolada.skills.skills.special.moves;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.damage.DealDamage;
import pinacolada.actions.damage.DealDamageToAll;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.effects.vfx.megacritCopy.HemokinesisEffect2;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.skills.base.moves.PMove_DealDamage;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameEffects;

public class PMove_StealTempHP extends PMove_DealDamage implements Hidden
{
    public static final PSkillData DATA = register(PMove_StealTempHP.class, PCLEffectType.General);

    public PMove_StealTempHP()
    {
        this(1, AbstractGameAction.AttackEffect.NONE);
    }

    public PMove_StealTempHP(PSkillSaveData content)
    {
        super(content);
        damageType = DamageInfo.DamageType.HP_LOSS;
    }

    public PMove_StealTempHP(int amount, AbstractGameAction.AttackEffect attackEffect)
    {
        this(amount, attackEffect, PCLCardTarget.Single);
    }

    public PMove_StealTempHP(int amount, AbstractGameAction.AttackEffect attackEffect, PCLCardTarget target)
    {
        super(amount, attackEffect, target);
        damageType = DamageInfo.DamageType.HP_LOSS;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.stealAmount("X", PGR.core.tooltips.tempHP.title);
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.stealFrom(getAmountRawString(), PGR.core.tooltips.tempHP, getTargetString());
    }

    protected void setDamageOptions(DealDamage damageAction, PCLUseInfo info)
    {
        super.setDamageOptions(damageAction, info);
        damageAction.addCallback((enemy) -> {
            GameEffects.List.add(new HemokinesisEffect2(enemy.hb.cX, enemy.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY).setColor(Color.GOLDENROD));
            GameActions.top.gainTemporaryHP(amount);
        });
    }

    protected void setDamageOptions(DealDamageToAll damageAction, PCLUseInfo info)
    {
        super.setDamageOptions(damageAction, info);
        damageAction.addCallback((enemy) -> {
            for (AbstractCreature c : enemy)
            {
                GameEffects.List.add(new HemokinesisEffect2(c.hb.cX, c.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY).setColor(Color.GOLDENROD));
                GameActions.top.gainTemporaryHP(amount);
            }
        });
    }
}
