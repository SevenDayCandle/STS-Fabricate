package pinacolada.skills.skills.special.primary;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import pinacolada.actions.creature.DealDamage;
import pinacolada.actions.creature.DealDamageToAll;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.effects.EffekseerEFK;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.VFX;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Attack;
import pinacolada.skills.skills.PCardPrimary;

public class PCardPrimary_DealDamage extends PCardPrimary<PField_Attack>
{
    public static final PSkillData<PField_Attack> DATA = register(PCardPrimary_DealDamage.class, PField_Attack.class);

    // Damage effects are only customizable in code and cannot be saved in fields
    protected FuncT2<Float, AbstractCreature, AbstractCreature> damageEffect;

    // Needed for effect editor initialization. PLEASE do not call this anywhere else without setting a card first
    public PCardPrimary_DealDamage()
    {
        super(DATA, PCLCardTarget.Single, 0);
    }

    public PCardPrimary_DealDamage(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCardPrimary_DealDamage(PointerProvider card)
    {
        super(DATA, card);
    }

    public PCardPrimary_DealDamage(PointerProvider card, AbstractGameAction.AttackEffect attackEffect)
    {
        super(DATA, card);
        fields.attackEffect = attackEffect;
    }

    public PCardPrimary_DealDamage setProvider(PointerProvider card)
    {
        setTarget(card instanceof PCLCard ? ((PCLCard) card).pclTarget : PCLCardTarget.Single);
        setSource(card, PCLCardValueSource.Damage, PCLCardValueSource.HitCount);
        return this;
    }

    @Override
    public ColoredString getColoredValueString(Object displayBase, Object displayAmount)
    {
        return new ColoredString(displayAmount,
                (sourceCard != null ?
                        sourceCard.upgradedDamage ? Settings.GREEN_TEXT_COLOR :
                        sourceCard.isDamageModified ? (amount > baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR)
                                : Settings.CREAM_COLOR : Settings.CREAM_COLOR));
    }

    @Override
    public PCardPrimary_DealDamage makeCopy()
    {
        return (PCardPrimary_DealDamage) super.makeCopy();
    }

    @Override
    public void useImpl(PCLUseInfo info)
    {
        PCLCard pCard = EUIUtils.safeCast(sourceCard, PCLCard.class);
        if (pCard != null)
        {
            switch (target)
            {
                case All:
                    getActions().dealCardDamageToAll(pCard, info.source, fields.attackEffect).forEach(e -> setDamageOptions(e, info));
                case Team:
                    getActions().dealCardDamage(pCard, info.source, AbstractDungeon.player, fields.attackEffect).forEach(e -> setDamageOptions(e, info));
                case AllAlly:
                    getActions().dealCardDamageToAll(pCard, info.source, fields.attackEffect).forEach(e -> setDamageOptions(e.targetAllies(true), info));
                    break;
                case AllEnemy:
                    getActions().dealCardDamageToAll(pCard, info.source, fields.attackEffect).forEach(e -> setDamageOptions(e, info));
                    break;
                default:
                    getActions().dealCardDamage(pCard, info.source, info.target, fields.attackEffect).forEach(e -> setDamageOptions(e, info));
            }
        }
    }

    @Override
    public String getSubText()
    {
        int count = source != null ? getExtraFromCard() : 1;
        String amountString = count > 1 ? getAmountRawString() + "x" + getExtraRawString() : getAmountRawString();

        String targetShortString = target.getShortString();

        // When displayed as text, we can just write normal damage down as "damage"
        EUITooltip attackTooltip = getAttackTooltip();
        String attackString = attackTooltip == PGR.core.tooltips.normalDamage && EUIConfiguration.disableDescrptionIcons.get() ? PGR.core.strings.subjects_damage : attackTooltip.toString();

        if (targetShortString != null)
        {
            return EUIRM.strings.numAdjNoun(amountString, targetShortString, attackString);
        }
        return EUIRM.strings.numNoun(amountString, attackString);
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey)
    {
        this.damageEffect = (s, m) -> PCLEffects.Queue.add(VFX.eFX(effekseerKey, m.hb)).duration * 0.8f;
        return this;
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey, Color color)
    {
        return setDamageEffect(effekseerKey, color, 0.8f);
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey, float durationMult)
    {
        this.damageEffect = (s, m) -> PCLEffects.Queue.add(VFX.eFX(effekseerKey, m.hb)).duration * durationMult;
        return this;
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey, Color color, float durationMult)
    {
        this.damageEffect = (s, m) -> PCLEffects.Queue.add(VFX.eFX(effekseerKey, m.hb).setColor(color)).duration * durationMult;
        return this;
    }

    public PCardPrimary_DealDamage setDamageEffect(FuncT2<Float, AbstractCreature, AbstractCreature> damageEffect)
    {
        this.damageEffect = damageEffect;
        return this;
    }

    protected void setDamageOptions(DealDamage damageAction, PCLUseInfo info)
    {
        if (damageEffect != null)
        {
            damageAction.setDamageEffect(damageEffect);
        }
        if (fields.vfxColor != null)
        {
            if (fields.vfxTargetColor != null)
            {
                damageAction.setVFXColor(fields.vfxColor, fields.vfxTargetColor);
            }
            else
            {
                damageAction.setVFXColor(fields.vfxColor);
            }
        }
    }

    protected void setDamageOptions(DealDamageToAll damageAction, PCLUseInfo info)
    {
        if (damageEffect != null)
        {
            damageAction.setDamageEffect((enemy, __) -> damageEffect.invoke(info.source, enemy));
        }
        if (fields.vfxColor != null)
        {
            if (fields.vfxTargetColor != null)
            {
                damageAction.setVFXColor(fields.vfxColor, fields.vfxTargetColor);
            }
            else
            {
                damageAction.setVFXColor(fields.vfxColor);
            }
        }
    }

    public PCardPrimary_DealDamage setVFXColor(Color vfxColor, Color vfxTargetColor)
    {
        fields.setVFXColor(vfxColor, vfxTargetColor);
        return this;
    }
}
