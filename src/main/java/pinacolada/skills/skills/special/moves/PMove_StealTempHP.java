package pinacolada.skills.skills.special.moves;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.megacritCopy.HemokinesisEffect2;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Attack;

public class PMove_StealTempHP extends PMove<PField_Attack>
{
    public static final PSkillData<PField_Attack> DATA = register(PMove_StealTempHP.class, PField_Attack.class);

    public PMove_StealTempHP()
    {
        this(1, AbstractGameAction.AttackEffect.NONE);
    }

    public PMove_StealTempHP(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_StealTempHP(int amount, AbstractGameAction.AttackEffect attackEffect)
    {
        this(amount, attackEffect, PCLCardTarget.Single);
    }

    public PMove_StealTempHP(int amount, AbstractGameAction.AttackEffect attackEffect, PCLCardTarget target)
    {
        super(DATA, target, amount);
        fields.setAttackEffect(attackEffect);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_stealAmount(TEXT.subjects_x, PGR.core.tooltips.tempHP.title);
    }

    @Override
    public String getSubText()
    {
        return TEXT.act_stealFrom(getAmountRawString(), PGR.core.tooltips.tempHP, getTargetString());
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (target.targetsMulti())
        {
            int[] damage = DamageInfo.createDamageMatrix(amount, true, false);
            getActions().dealDamageToAll(damage, DamageInfo.DamageType.HP_LOSS, fields.attackEffect)
                    .addCallback((enemy) -> {
                        for (AbstractCreature c : enemy)
                        {
                            PCLEffects.List.add(new HemokinesisEffect2(c.hb.cX, c.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY).setColor(Color.GOLDENROD));
                            PCLActions.top.gainTemporaryHP(amount);
                        }
                    });
        }
        else
        {
            getActions().dealDamage(getSourceCreature(), target == PCLCardTarget.Self ? getSourceCreature() : info.target, amount, DamageInfo.DamageType.HP_LOSS, fields.attackEffect).isCancellable(target != PCLCardTarget.Self)
                    .addCallback((enemy) -> {
                        PCLEffects.List.add(new HemokinesisEffect2(enemy.hb.cX, enemy.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY).setColor(Color.GOLDENROD));
                        PCLActions.top.gainTemporaryHP(amount);
                    });
        }
        super.use(info);
    }
}
