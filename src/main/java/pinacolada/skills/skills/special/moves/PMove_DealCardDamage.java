package pinacolada.skills.skills.special.moves;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import pinacolada.actions.damage.DealDamage;
import pinacolada.actions.damage.DealDamageToAll;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLEffekseerEFX;
import pinacolada.effects.VFX;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;
import java.util.Collections;

public class PMove_DealCardDamage extends PMove implements Hidden
{
    public static final PSkillData DATA = register(PMove_DealCardDamage.class, PCLEffectType.General);

    protected AbstractGameAction.AttackEffect attackEffect;
    protected Color vfxColor;
    protected Color vfxTargetColor;
    protected FuncT1<Float, AbstractCreature> damageEffect;
    protected ActionT2<PCLUseInfo, ArrayList<AbstractCreature>> onCompletion;

    public PMove_DealCardDamage(PSkillSaveData content)
    {
        super(content);
        attackEffect = AbstractGameAction.AttackEffect.valueOf(content.effectData);
    }

    public PMove_DealCardDamage(PointerProvider card, AbstractGameAction.AttackEffect attackEffect)
    {
        super(DATA,
                card instanceof PCLCard ? ((PCLCard) card).pclTarget : PCLCardTarget.Single,
                0);
        this.attackEffect = attackEffect;
        setSource(card, PCLCardValueSource.Damage, PCLCardValueSource.HitCount);
    }

    public void addAdditionalData(PSkillSaveData data)
    {
        data.effectData = attackEffect.name();
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
    public PMove_DealCardDamage makeCopy()
    {
        PMove_DealCardDamage copy = (PMove_DealCardDamage) super.makeCopy();
        copy.attackEffect = this.attackEffect;
        copy.setDamageEffect(this.damageEffect).setVFXColor(this.vfxColor, this.vfxTargetColor);
        copy.onCompletion = this.onCompletion;
        return copy;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        PCLCard pCard = EUIUtils.safeCast(sourceCard, PCLCard.class);
        if (pCard != null)
        {
            switch (target)
            {
                case All:
                    getActions().dealCardDamageToAll(pCard, info.source, attackEffect).forEach(e -> setDamageOptions(e, info));
                case Team:
                    getActions().dealCardDamage(pCard, info.source, AbstractDungeon.player, attackEffect).forEach(e -> setDamageOptions(e, info));
                case AllAlly:
                    getActions().dealCardDamageToAll(pCard, info.source, attackEffect).forEach(e -> setDamageOptions(e.targetAllies(true), info));
                    break;
                case AllEnemy:
                    getActions().dealCardDamageToAll(pCard, info.source, attackEffect).forEach(e -> setDamageOptions(e, info));
                    break;
                default:
                    getActions().dealCardDamage(pCard, info.source, info.target, attackEffect).forEach(e -> setDamageOptions(e, info));

            }
        }
    }

    @Override
    public String getSubText()
    {
        int count = source != null ? getExtraFromCard() : 1;
        EUITooltip tooltip = sourceCard instanceof PCLCard ? ((PCLCard) sourceCard).attackType.getTooltip() : PGR.core.tooltips.damage;
        String amountString = count > 1 ? getAmountRawString() + "x" + getExtraRawString() : getAmountRawString();
        if (target == PCLCardTarget.Self)
        {
            return TEXT.actions.takeDamage(amountString);
        }
        if (target == PCLCardTarget.Single)
        {
            return TEXT.actions.deal(amountString, tooltip);
        }
        return TEXT.actions.dealTo(amountString, tooltip, getTargetString());
    }

    public boolean hasCallback()
    {
        return this.onCompletion != null;
    }

    public PMove_DealCardDamage setCallback(ActionT2<PCLUseInfo, ArrayList<AbstractCreature>> onCompletion)
    {
        this.onCompletion = onCompletion;
        return this;
    }

    public PMove_DealCardDamage setDamageEffect(PCLEffekseerEFX effekseerKey)
    {
        this.damageEffect = (m) -> PCLEffects.Queue.add(VFX.eFX(effekseerKey, m.hb)).duration * 0.8f;
        return this;
    }

    public PMove_DealCardDamage setDamageEffect(PCLEffekseerEFX effekseerKey, Color color)
    {
        return setDamageEffect(effekseerKey, color, 0.8f);
    }

    public PMove_DealCardDamage setDamageEffect(PCLEffekseerEFX effekseerKey, float durationMult)
    {
        this.damageEffect = (m) -> PCLEffects.Queue.add(VFX.eFX(effekseerKey, m.hb)).duration * durationMult;
        return this;
    }

    public PMove_DealCardDamage setDamageEffect(PCLEffekseerEFX effekseerKey, Color color, float durationMult)
    {
        this.damageEffect = (m) -> PCLEffects.Queue.add(VFX.eFX(effekseerKey, m.hb).setColor(color)).duration * durationMult;
        return this;
    }

    public PMove_DealCardDamage setDamageEffect(FuncT1<Float, AbstractCreature> damageEffect)
    {
        this.damageEffect = damageEffect;
        return this;
    }

    protected void setDamageOptions(DealDamage damageAction, PCLUseInfo info)
    {
        if (damageEffect != null)
        {
            damageAction.setDamageEffect(enemy -> damageEffect.invoke(enemy));
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
            damageAction.setDamageEffect((enemy, __) -> damageEffect.invoke(enemy));
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

    public PMove_DealCardDamage setVFXColor(Color vfxColor, Color vfxTargetColor)
    {
        this.vfxColor = vfxColor;
        this.vfxTargetColor = vfxTargetColor;
        return this;
    }
}
