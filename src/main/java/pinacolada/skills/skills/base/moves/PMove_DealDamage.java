package pinacolada.skills.skills.base.moves;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT2;
import pinacolada.actions.damage.DealDamage;
import pinacolada.actions.damage.DealDamageToAll;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.effects.PCLEffekseerEFX;
import pinacolada.effects.VFX;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.ArrayList;
import java.util.Collections;

public class PMove_DealDamage extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_DealDamage.class, PField_Empty.class);

    protected final AbstractGameAction.AttackEffect attackEffect;
    protected Color vfxColor;
    protected Color vfxTargetColor;
    protected DamageInfo.DamageType damageType = DamageInfo.DamageType.THORNS;
    protected FuncT2<Float, AbstractCreature, AbstractCreature> damageEffect;
    protected ActionT2<PCLUseInfo, ArrayList<AbstractCreature>> onCompletion;

    public PMove_DealDamage(PSkillSaveData content)
    {
        super(DATA, content);
        attackEffect = AbstractGameAction.AttackEffect.valueOf(content.effectData);
    }

    public PMove_DealDamage()
    {
        this(1, AbstractGameAction.AttackEffect.NONE);
    }

    public PMove_DealDamage(int amount, AbstractGameAction.AttackEffect attackEffect)
    {
        this(amount, attackEffect, PCLCardTarget.Single);
    }

    public PMove_DealDamage(int amount, AbstractGameAction.AttackEffect attackEffect, PCLCardTarget target)
    {
        super(DATA, target, amount);
        this.attackEffect = attackEffect;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.deal(TEXT.subjects.x, PGR.core.tooltips.normalDamage.title);
    }

    @Override
    public boolean isDetrimental()
    {
        return target == PCLCardTarget.None || target == PCLCardTarget.Self || target == PCLCardTarget.All;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (target == PCLCardTarget.AllEnemy || target == PCLCardTarget.All)
        {
            int[] damage = DamageInfo.createDamageMatrix(amount, true, false);
            DealDamageToAll action = getActions().dealDamageToAll(damage, damageType, attackEffect);
            setDamageOptions(action, info);
        }
        else
        {
            DealDamage action = (DealDamage) getActions().dealDamage(getSourceCreature(), target == PCLCardTarget.Self ? getSourceCreature() : info.target, amount, damageType, attackEffect).isCancellable(target != PCLCardTarget.Self);
            setDamageOptions(action, info);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        if (target == PCLCardTarget.Self)
        {
            return TEXT.actions.takeDamage(getAmountRawString());
        }
        if (target == PCLCardTarget.Single)
        {
            return TEXT.actions.deal(getAmountRawString(), PGR.core.strings.subjects.damage);
        }
        return TEXT.actions.dealTo(getAmountRawString(), PGR.core.strings.subjects.damage, getTargetString());
    }

    public void serialize(PSkillSaveData data)
    {
        data.effectData = attackEffect.name();
    }

    public PMove_DealDamage setCallback(ActionT2<PCLUseInfo, ArrayList<AbstractCreature>> onCompletion)
    {
        this.onCompletion = onCompletion;
        return this;
    }

    public PMove_DealDamage setDamageEffect(FuncT2<Float, AbstractCreature, AbstractCreature> damageEffect)
    {
        this.damageEffect = damageEffect;
        return this;
    }

    public PMove_DealDamage setDamageEffect(PCLEffekseerEFX effekseerKey)
    {
        this.damageEffect = (s, m) -> VFX.eFX(effekseerKey, m.hb).duration;
        return this;
    }

    protected void setDamageOptions(DealDamage damageAction, PCLUseInfo info)
    {
        if (damageEffect != null)
        {
            damageAction.setDamageEffect(damageEffect);
        }
        if (vfxColor != null)
        {
            if (vfxTargetColor != null)
            {
                damageAction.setVFXColor(vfxColor, vfxTargetColor);
            }
            else
            {
                damageAction.setVFXColor(vfxColor);
            }
        }
        if (onCompletion != null)
        {
            damageAction.addCallback(info, (i, m) -> onCompletion.invoke(i, new ArrayList<>(Collections.singletonList(m))));
        }
    }

    protected void setDamageOptions(DealDamageToAll damageAction, PCLUseInfo info)
    {
        if (damageEffect != null)
        {
            damageAction.setDamageEffect((enemy, __) -> damageEffect.invoke(info.source, enemy));
        }
        if (vfxColor != null)
        {
            if (vfxTargetColor != null)
            {
                damageAction.setVFXColor(vfxColor, vfxTargetColor);
            }
            else
            {
                damageAction.setVFXColor(vfxColor);
            }
        }
        if (onCompletion != null)
        {
            damageAction.addCallback(info, (i, mo) -> onCompletion.invoke(i, mo));
        }
    }

    public PMove_DealDamage setDamageType(DamageInfo.DamageType damageType)
    {
        this.damageType = damageType;
        return this;
    }

    public PMove_DealDamage setVFXColor(Color vfxColor, Color vfxTargetColor)
    {
        this.vfxColor = vfxColor;
        this.vfxTargetColor = vfxTargetColor;
        return this;
    }
}
