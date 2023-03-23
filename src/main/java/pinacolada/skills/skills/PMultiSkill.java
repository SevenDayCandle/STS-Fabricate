package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.utilities.RotatingList;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PMultiSkill extends PSkill<PField_Empty> implements PMultiBase<PSkill<?>>
{
    public static final PSkillData<PField_Empty> DATA = register(PMultiSkill.class, PField_Empty.class, 0, DEFAULT_MAX);
    public boolean generated = false;
    protected ArrayList<PSkill<?>> effects = new ArrayList<>();

    public PMultiSkill()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PMultiSkill(PSkillSaveData content)
    {
        super(DATA, content);
        effects = EUIUtils.mapAsNonnull(splitJson(content.special), PSkill::get);
        setParentsForChildren();
    }

    public PMultiSkill(PSkill<?>... effects)
    {
        super(DATA, EUIUtils.max(effects, effect -> effect.target), 0);
        if (target == null)
        {
            target = PCLCardTarget.None;
        }
        this.effects.addAll(Arrays.asList(effects));
        setParentsForChildren();
    }

    public static PMultiSkill choose(int amount, PSkill<?>... effects)
    {
        return (PMultiSkill) new PMultiSkill(effects).setAmount(amount);
    }

    public static PMultiSkill choose(PSkill<?>... effects)
    {
        return choose(1, effects);
    }

    public static PMultiSkill join(PSkill<?>... effects)
    {
        return new PMultiSkill(effects);
    }

    public static PMultiSkill joinGen(PSkill<?>... effects)
    {
        return new PMultiSkill(effects).setGenerated(true);
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

    public String getSpecialData()
    {
        return PSkill.joinDataAsJson(effects, PSkill::serialize);
    }

    @Override
    public PSkill<PField_Empty> addAmountForCombat(int amount)
    {
        for (PSkill<?> effect : effects)
        {
            effect.addAmountForCombat(amount);
        }
        return this;
    }

    @Override
    public PSkill<PField_Empty> addExtraForCombat(int extra)
    {
        for (PSkill<?> effect : effects)
        {
            effect.addExtraForCombat(extra);
        }
        return this;
    }

    @Override
    public boolean canMatch(AbstractCard card)
    {
        return EUIUtils.any(effects, effect -> effect.canMatch(card));
    }

    @Override
    public boolean canPlay(PCLUseInfo info)
    {
        boolean canPlay = true;
        for (PSkill<?> be : effects)
        {
            canPlay = canPlay & be.canPlay(info);
        }
        return canPlay;
    }

    public void displayUpgrades(boolean value)
    {
        super.displayUpgrades(value);
        displayChildUpgrades(value);
    }

    @Override
    public String getSampleText()
    {
        return null;
    }

    @Override
    public String getSubText()
    {
        return null;
    }

    @Override
    public String getText(int index, boolean addPeriod)
    {
        return effects.size() > index ? effects.get(index).getText(addPeriod) : "";
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return amount > 0 ? (capital(TEXT.act_choose(amount), addPeriod) + ": " +
                (generated ? joinEffectTexts(effects) : PCLCoreStrings.joinWithOr(getEffectTextsWithoutPeriod(effects, addPeriod)))) :
                generated ? joinEffectTexts(effects) : PCLCoreStrings.joinWithAnd(getEffectTextsWithoutPeriod(effects, addPeriod)) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public boolean isBlank() {return effects.size() == 0 && !(childEffect != null && !childEffect.isBlank());}

    @Override
    public boolean isDetrimental()
    {
        return EUIUtils.any(effects, PSkill::isDetrimental);
    }

    @Override
    public PMultiSkill makeCopy()
    {
        PMultiSkill copy = (PMultiSkill) super.makeCopy();
        for (PSkill<?> effect : effects)
        {
            copy.addEffect(effect.makeCopy());
        }
        return copy;
    }

    public PMultiSkill makePreviews(RotatingList<EUICardPreview> previews)
    {
        for (PSkill<?> effect : effects)
        {
            effect.makePreviews(previews);
        }
        super.makePreviews(previews);
        return this;
    }

    @Override
    public float modifyBlock(PCLUseInfo info, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyBlock(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamage(PCLUseInfo info, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyDamage(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyHeal(PCLUseInfo info, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyHeal(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyHitCount(PCLUseInfo info, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyHitCount(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyMagicNumber(PCLUseInfo info, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyMagicNumber(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyRightCount(PCLUseInfo info, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyRightCount(info, amount);
        }
        return amount;
    }

    @Override
    public PMultiSkill onAddToCard(AbstractCard card)
    {
        addSubs(card);
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
    public PMultiSkill onRemoveFromCard(AbstractCard card)
    {
        removeSubs(card);
        super.onRemoveFromCard(card);
        return this;
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet)
    {
        for (PSkill<?> effect : effects)
        {
            effect.refresh(info, conditionMet);
        }
    }

    public boolean removable()
    {
        return effects.isEmpty() || EUIUtils.all(effects, PSkill::removable);
    }

    public boolean requiresTarget()
    {
        return target == PCLCardTarget.Single || EUIUtils.any(effects, PSkill::requiresTarget);
    }

    @Override
    public PMultiSkill setAmountFromCard()
    {
        super.setAmountFromCard();
        for (PSkill<?> effect : effects)
        {
            effect.setAmountFromCard();
        }
        return this;
    }

    @Override
    public PMultiSkill setSource(PointerProvider card)
    {
        super.setSource(card);
        for (PSkill<?> effect : effects)
        {
            effect.setSource(card);
        }
        return this;
    }

    @Override
    public PMultiSkill setTemporaryAmount(int amount)
    {
        for (PSkill<?> effect : effects)
        {
            effect.setTemporaryAmount(amount);
        }
        return this;
    }

    @Override
    public PMultiSkill setTemporaryExtra(int extra)
    {
        for (PSkill<?> effect : effects)
        {
            effect.setTemporaryExtra(extra);
        }
        return this;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (amount > 0)
        {
            chooseEffect(info);
        }
        else
        {
            for (PSkill<?> effect : effects)
            {
                effect.use(info);
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
        if (amount > 0)
        {
            chooseEffect(info);
        }
        else
        {
            if (index < effects.size() && index >= 0)
            {
                effects.get(index).use(info, index);
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (amount > 0)
        {
            chooseEffect(info);
        }
        else
        {
            for (PSkill<?> effect : effects)
            {
                effect.use(info, isUsing);
            }
        }
    }

    public void chooseEffect(PCLUseInfo info)
    {
        PCLCard choiceCard = EUIUtils.safeCast(sourceCard, PCLCard.class);
        if (choiceCard == null)
        {
            choiceCard = new QuestionMark();
        }

        getActions().tryChooseSkill(choiceCard.cardData, amount, info.source, info.target, effects);
    }

    public List<PSkill<?>> getSubEffects()
    {
        return effects;
    }

    public PSkill<?> getSubEffect(int index)
    {
        return index < effects.size() ? effects.get(index) : null;
    }

    public PMultiSkill addEffect(PSkill<?> newEffect)
    {
        this.effects.add(newEffect);
        setParentsForChildren();
        return this;
    }

    public PMultiSkill setEffects(PSkill<?>... effects)
    {
        return setEffects(Arrays.asList(effects));
    }

    public PMultiSkill setEffects(List<PSkill<?>> effects)
    {
        this.effects.clear();
        this.effects.addAll(effects);
        setParentsForChildren();

        return this;
    }

    public PMultiSkill setGenerated(boolean val)
    {
        generated = val;
        return this;
    }

    public PMultiSkill stack(PSkill<?> other)
    {
        super.stack(other);
        if (other instanceof PMultiBase)
        {
            stackMulti((PMultiBase<?>) other);
        }
        return this;
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
