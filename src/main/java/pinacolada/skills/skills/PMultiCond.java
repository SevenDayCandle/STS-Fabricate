package pinacolada.skills.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.tooltips.EUICardPreview;
import pinacolada.cards.base.*;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.powers.PCLPower;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.RotatingList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PMultiCond extends PCond implements PMultiBase<PCond>
{
    public static final PSkillData DATA = register(PMultiCond.class, PCLEffectType.General, 0, DEFAULT_MAX);
    protected ArrayList<PCond> effects = new ArrayList<>();

    public PMultiCond()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PMultiCond(PSkillSaveData content)
    {
        super(content);
        effects = EUIUtils.mapAsNonnull(splitJson(content.effectData), e -> (PCond) PSkill.get(e));
        setParentsForChildren();
    }

    public PMultiCond(PCond... effects)
    {
        super(DATA, EUIUtils.max(effects, effect -> effect.target), 0);
        if (target == null)
        {
            target = PCLCardTarget.None;
        }
        this.effects.addAll(Arrays.asList(effects));
        setParentsForChildren();
    }

    public static PMultiCond and(PCond... effects)
    {
        return new PMultiCond(effects);
    }

    public static PMultiCond or(PCond... effects)
    {
        return (PMultiCond) new PMultiCond(effects).setAlt(true);
    }

    public void addAdditionalData(PSkillSaveData data)
    {
        data.effectData = PSkill.joinDataAsJson(effects, PSkill::serialize);
    }

    public void displayUpgrades()
    {
        super.displayUpgrades();
        displayChildUpgrades();
    }

    @Override
    public String getSubText()
    {
        return alt ? PCLCoreStrings.joinWithOr(EUIUtils.map(effects, e -> e.getText(false))) : PSkill.joinEffectTexts(effects, ". ", false);
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
        for (PCond effect : effects)
        {
            copy.addEffect((PCond) effect.makeCopy());
        }
        return copy;
    }

    public PSkill makePreviews(RotatingList<EUICardPreview> previews)
    {
        for (PSkill effect : effects)
        {
            effect.makePreviews(previews);
        }
        return super.makePreviews(previews);
    }

    @Override
    public PMultiCond onAddToCard(AbstractCard card)
    {
        for (PSkill effect : effects)
        {
            effect.onAddToCard(card);
        }
        super.onAddToCard(card);
        return this;
    }

    @Override
    public void onDrag(AbstractMonster m)
    {
        for (PSkill effect : effects)
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

    public PMultiCond setCards(Collection<? extends AbstractCard> cards)
    {
        super.setCards(cards);
        for (PSkill be : effects)
        {
            be.setCards(cards);
        }
        return this;
    }

    @Override
    public boolean triggerOnElementReact(AffinityReactions reactions, AbstractCreature target)
    {
        return triggerOn((effect) -> effect.triggerOnElementReact(reactions, target));
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return effects.isEmpty() || (alt ? EUIUtils.any(effects, c -> c.checkCondition(info, isUsing, fromTrigger)) : EUIUtils.all(effects, c -> c.checkCondition(info, isUsing, fromTrigger)));
    }

    @Override
    public PMultiCond setAmountFromCard()
    {
        super.setAmountFromCard();
        for (PSkill effect : effects)
        {
            effect.setAmountFromCard();
        }
        return this;
    }

    @Override
    public PMultiCond setSource(PointerProvider card)
    {
        super.setSource(card);
        for (PSkill effect : effects)
        {
            effect.setSource(card);
        }
        return this;
    }

    @Override
    public PMultiCond setSource(PointerProvider card, PCLCardValueSource source)
    {
        super.setSource(card, source);
        for (PSkill effect : effects)
        {
            effect.setSource(card, source);
        }
        return this;
    }


    @Override
    public PMultiCond setSource(PointerProvider card, PCLCardValueSource source, PCLCardValueSource extra)
    {
        super.setSource(card, source, extra);
        for (PSkill effect : effects)
        {
            effect.setSource(card, source, extra);
        }
        return this;
    }

    @Override
    public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
    {
        conditionMetCache = checkCondition(makeInfo(m), false, false);
        boolean refreshVal = conditionMetCache & conditionMet;
        for (PSkill effect : effects)
        {
            effect.refresh(m, c, refreshVal);
        }
    }

    @Override
    public boolean triggerOnReshuffle(AbstractCard c, CardGroup sourcePile)
    {
        return triggerOn((effect) -> effect.triggerOnReshuffle(c, sourcePile));
    }

    @Override
    public boolean triggerOnAllyDeath(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(effect -> effect.triggerOnAllyDeath(c, ally));
    }

    @Override
    public boolean triggerOnAllySummon(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(effect -> effect.triggerOnAllySummon(c, ally));
    }

    @Override
    public boolean triggerOnAllyTrigger(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(effect -> effect.triggerOnAllyTrigger(c, ally));
    }

    @Override
    public boolean triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(effect -> effect.triggerOnAllyWithdraw(c, ally));
    }

    @Override
    public boolean triggerOnApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower c)
    {
        return triggerOn(() -> this.childEffect.triggerOnApplyPower(source, target, c), makeInfo(target).setData(c));
    }

    @Override
    public boolean triggerOnCreate(AbstractCard c, boolean startOfBattle)
    {
        return triggerOn((effect) -> effect.triggerOnCreate(c, startOfBattle));
    }

    @Override
    public boolean triggerOnDiscard(AbstractCard c)
    {
        return triggerOn((effect) -> effect.triggerOnDiscard(c));
    }

    @Override
    public boolean triggerOnDraw(AbstractCard c)
    {
        return triggerOn((effect) -> effect.triggerOnDraw(c));
    }

    @Override
    public boolean triggerOnEndOfTurn(boolean isUsing)
    {
        for (PSkill effect : effects)
        {
            if (effect.triggerOnEndOfTurn(isUsing))
            {
                if (isUsing && this.childEffect != null)
                {
                    this.childEffect.use(makeInfo(null));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean triggerOnExhaust(AbstractCard c)
    {
        return triggerOn((effect) -> effect.triggerOnExhaust(c));
    }

    @Override
    public boolean triggerOnIntensify(PCLAffinity c)
    {
        return triggerOn((effect) -> effect.triggerOnIntensify(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnMatch(AbstractCard c, PCLUseInfo info)
    {
        return triggerOn((effect) -> effect.triggerOnMatch(c, info), info);
    }

    @Override
    public boolean triggerOnMismatch(AbstractCard c, PCLUseInfo info)
    {
        return triggerOn((effect) -> effect.triggerOnMismatch(c, info), info);
    }

    @Override
    public boolean triggerOnOrbChannel(AbstractOrb c)
    {
        return triggerOn((effect) -> effect.triggerOnOrbChannel(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOrbFocus(AbstractOrb c)
    {
        return triggerOn((effect) -> effect.triggerOnOrbFocus(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOrbEvoke(AbstractOrb c)
    {
        return triggerOn((effect) -> effect.triggerOnOrbEvoke(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOrbTrigger(AbstractOrb c)
    {
        return triggerOn((effect) -> effect.triggerOnOrbTrigger(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOtherCardPlayed(AbstractCard c)
    {
        return triggerOn((effect) -> effect.triggerOnOtherCardPlayed(c));
    }

    @Override
    public boolean triggerOnPCLPowerUsed(PCLPower c)
    {
        return triggerOn((effect) -> effect.triggerOnPCLPowerUsed(c));
    }

    @Override
    public boolean triggerOnPurge(AbstractCard c)
    {
        return triggerOn((effect) -> effect.triggerOnPurge(c));
    }

    @Override
    public boolean triggerOnScry()
    {
        return triggerOn(PSkill::triggerOnScry);
    }

    @Override
    public boolean triggerOnShuffle(boolean c)
    {
        return triggerOn((effect) -> effect.triggerOnShuffle(c));
    }

    @Override
    public boolean triggerOnStartOfTurn()
    {
        return triggerOn(PSkill::triggerOnStartOfTurn);
    }

    @Override
    public boolean triggerOnStartup()
    {
        return triggerOn(() -> this.childEffect.triggerOnStartup());
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

    public List<PCond> getSubEffects()
    {
        return effects;
    }

    public PCond getSubEffect(int index)
    {
        return index < effects.size() ? effects.get(index) : null;
    }

    public PMultiCond addEffect(PCond effect)
    {
        this.effects.add(effect);
        setParentsForChildren();
        return this;
    }

    public PMultiCond setEffects(PCond... effects)
    {
        return setEffects(Arrays.asList(effects));
    }

    public PMultiCond setEffects(List<PCond> effects)
    {
        this.effects.clear();
        this.effects.addAll(effects);
        setParentsForChildren();
        return this;
    }

    public PMultiCond stack(PSkill other)
    {
        super.stack(other);
        if (other instanceof PMultiBase)
        {
            stackMulti((PMultiBase) other);
        }
        return this;
    }

    protected boolean triggerOn(FuncT1<Boolean, PSkill> action)
    {
        return triggerOn(action, makeInfo(null));
    }

    protected boolean triggerOn(FuncT1<Boolean, PSkill> action, PCLUseInfo info)
    {
        for (PSkill effect : effects)
        {
            if (action.invoke(effect) && this.childEffect != null)
            {
                this.childEffect.use(info);
                return true;
            }
        }
        return false;
    }
}
