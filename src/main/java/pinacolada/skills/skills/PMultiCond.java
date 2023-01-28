package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUICardPreview;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.misc.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Or;
import pinacolada.utilities.RotatingList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PMultiCond extends PCond<PField_Or> implements PMultiBase<PCond<?>>
{
    public static final PSkillData<PField_Or> DATA = register(PMultiCond.class, PField_Or.class, 0, DEFAULT_MAX);
    protected ArrayList<PCond<?>> effects = new ArrayList<>();

    public PMultiCond()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PMultiCond(PSkillSaveData content)
    {
        super(DATA, content);
        effects = EUIUtils.mapAsNonnull(splitJson(content.special), e -> (PCond<?>) PSkill.get(e));
        setParentsForChildren();
    }

    public PMultiCond(PCond<?>... effects)
    {
        super(DATA, EUIUtils.max(effects, effect -> effect.target), 0);
        if (target == null)
        {
            target = PCLCardTarget.None;
        }
        this.effects.addAll(Arrays.asList(effects));
        setParentsForChildren();
    }

    public static PMultiCond and(PCond<?>... effects)
    {
        return new PMultiCond(effects);
    }

    public static PMultiCond or(PCond<?>... effects)
    {
        return (PMultiCond) new PMultiCond(effects).edit(r -> r.setOr(true));
    }

    public String getSpecialData()
    {
        return PSkill.joinDataAsJson(effects, PSkill::serialize);
    }

    public void displayUpgrades()
    {
        super.displayUpgrades();
        displayChildUpgrades();
    }

    @Override
    public Color getGlowColor()
    {
        Color c = super.getGlowColor();
        for (PSkill<?> effect : effects)
        {
            Color c2 = effect.getGlowColor();
            if (c2 != null)
            {
                c = c2;
            }
        }
        return c;
    }

    @Override
    public AbstractMonster.Intent getIntent()
    {
        AbstractMonster.Intent c = super.getIntent();
        for (PSkill<?> effect : effects)
        {
            AbstractMonster.Intent c2 = effect.getIntent();
            if (c2 != null)
            {
                c = c2;
            }
        }
        return c;
    }

    @Override
    public String getSubText()
    {
        return fields.or ? PCLCoreStrings.joinWithOr(EUIUtils.map(effects, e -> e.getText(false))) : PSkill.joinEffectTexts(effects, ". ", false);
    }

    @Override
    public String getText(int index, boolean addPeriod)
    {
        return effects.size() > index ? effects.get(index).getText(index, addPeriod) : getText(addPeriod);
    }

    @Override
    public PMultiCond makeCopy()
    {
        PMultiCond copy = (PMultiCond) super.makeCopy();
        for (PCond<?> effect : effects)
        {
            copy.addEffect((PCond<?>) effect.makeCopy());
        }
        return copy;
    }

    public PMultiCond makePreviews(RotatingList<EUICardPreview> previews)
    {
        for (PSkill<?> effect : effects)
        {
            effect.makePreviews(previews);
        }
        super.makePreviews(previews);
        return this;
    }

    @Override
    public PMultiCond onAddToCard(AbstractCard card)
    {
        for (PSkill<?> effect : effects)
        {
            effect.onAddToCard(card);
        }
        super.onAddToCard(card);
        return this;
    }

    @Override
    public void onDrag(AbstractMonster m)
    {
        for (PSkill<?> effect : effects)
        {
            effect.onDrag(m);
        }
    }

    @Override
    public PMultiCond onRemoveFromCard(AbstractCard card)
    {
        removeSubs(card);
        super.onRemoveFromCard(card);
        return this;
    }

    @Override
    public boolean requiresTarget()
    {
        return target == PCLCardTarget.Single || EUIUtils.any(effects, PSkill::requiresTarget);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return effects.isEmpty() || (fields.or) ? EUIUtils.any(effects, c -> c.checkCondition(info, isUsing, fromTrigger)) : EUIUtils.all(effects, c -> c.checkCondition(info, isUsing, fromTrigger));
    }

    @Override
    public PMultiCond setAmountFromCard()
    {
        super.setAmountFromCard();
        for (PSkill<?> effect : effects)
        {
            effect.setAmountFromCard();
        }
        return this;
    }

    @Override
    public PMultiCond setSource(PointerProvider card)
    {
        super.setSource(card);
        for (PSkill<?> effect : effects)
        {
            effect.setSource(card);
        }
        return this;
    }

    @Override
    public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
    {
        conditionMetCache = checkCondition(makeInfo(m), false, false);
        boolean refreshVal = conditionMetCache & conditionMet;
        for (PSkill<?> effect : effects)
        {
            effect.refresh(m, c, refreshVal);
        }
    }

    @Override
    public String getText(boolean addPeriod)
    {
        if (amount != 0)
        {
            return getSubText() + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : ": ") + childEffect.getText(0, true)) + " " +
                    TEXT.conditions.otherwise(childEffect.getText(1, addPeriod)) : "");
        }
        return effects.isEmpty() ? (childEffect != null ? childEffect.getText(addPeriod) : "")
                : getSubText() + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : ": ") + childEffect.getText(addPeriod)) : PCLCoreStrings.period(addPeriod));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (amount != 0 && childEffect != null)
        {
            if (checkCondition(info, true, false))
            {
                childEffect.use(info, 0);
            }
            else
            {
                childEffect.use(info, 1);
            }
        }
        else
        {
            if (checkCondition(info, true, false) && childEffect != null)
            {
                childEffect.use(info);
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
        if (checkCondition(info, true, false) && childEffect != null)
        {
            childEffect.use(info, index);
        }
    }

    @Override
    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (amount != 0 && childEffect != null)
        {
            if (checkCondition(info, isUsing, false))
            {
                childEffect.use(info, 0);
            }
            else
            {
                childEffect.use(info, 1);
            }
        }
        else
        {
            if (checkCondition(info, true, false) && childEffect != null)
            {
                childEffect.use(info);
            }
        }
    }

    public List<PCond<?>> getSubEffects()
    {
        return effects;
    }

    public PCond<?> getSubEffect(int index)
    {
        return index < effects.size() ? effects.get(index) : null;
    }

    public PMultiCond addEffect(PCond<?> effect)
    {
        this.effects.add(effect);
        setParentsForChildren();
        return this;
    }

    public PMultiCond setEffects(PCond<?>... effects)
    {
        return setEffects(Arrays.asList(effects));
    }

    public PMultiCond setEffects(List<PCond<?>> effects)
    {
        this.effects.clear();
        this.effects.addAll(effects);
        setParentsForChildren();
        return this;
    }

    public PMultiCond stack(PSkill<?> other)
    {
        super.stack(other);
        if (other instanceof PMultiBase)
        {
            stackMulti((PMultiBase<?>) other);
        }
        return this;
    }

    public void triggerOnAllyDeath(PCLCard c, PCLCardAlly ally)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnAllyDeath(c, ally);
        }
    }

    public void triggerOnAllySummon(PCLCard c, PCLCardAlly ally)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnAllySummon(c, ally);
        }
    }

    public void triggerOnAllyTrigger(PCLCard c, PCLCardAlly ally)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnAllyTrigger(c, ally);
        }
    }

    public void triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnAllyWithdraw(c, ally);
        }
    }

    public void triggerOnCreate(AbstractCard c, boolean startOfBattle)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnCreate(c, startOfBattle);
        }
    }

    public void triggerOnDiscard(AbstractCard c)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnDiscard(c);
        }
    }

    public void triggerOnDraw(AbstractCard c)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnDraw(c);
        }
    }

    public boolean triggerOnEndOfTurn(boolean isUsing)
    {
        boolean trigger = false;
        for (PSkill<?> effect : effects)
        {
            trigger = trigger | effect.triggerOnEndOfTurn(isUsing);
        }
        return trigger;
    }

    public void triggerOnExhaust(AbstractCard c)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnExhaust(c);
        }
    }

    public void triggerOnOtherCardPlayed(AbstractCard c)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnOtherCardPlayed(c);
        }
    }

    public void triggerOnPurge(AbstractCard c)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnPurge(c);
        }
    }

    public void triggerOnReshuffle(AbstractCard c, CardGroup sourcePile)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnReshuffle(c, sourcePile);
        }
    }

    public void triggerOnRetain(AbstractCard c)
    {
        for (PSkill<?> effect : effects)
        {
            effect.triggerOnRetain(c);
        }
    }

    public void subscribeChildren()
    {
        for (PSkill<?> effect : effects)
        {
            effect.subscribeChildren();
        }
        if (this.childEffect != null)
        {
            this.childEffect.subscribeChildren();
        }
    }

    public void unsubscribeChildren()
    {
        for (PSkill<?> effect : effects)
        {
            effect.unsubscribeChildren();
        }
        if (this.childEffect != null)
        {
            this.childEffect.unsubscribeChildren();
        }
    }
}
