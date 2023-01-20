package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUICardPreview;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.RotatingList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PMultiTrait extends PTrait<PField_Empty> implements PMultiBase<PTrait<?>>
{
    public static final PSkillData<PField_Empty> DATA = register(PMultiTrait.class, PField_Empty.class, 0, DEFAULT_MAX);
    protected ArrayList<PTrait<?>> effects = new ArrayList<>();

    public PMultiTrait()
    {
        super(DATA, 0);
    }

    public PMultiTrait(PSkillSaveData content)
    {
        super(DATA, content);
        effects = EUIUtils.mapAsNonnull(splitJson(content.special), e -> (PTrait<?>) PSkill.get(e));
        setParentsForChildren();
    }

    public PMultiTrait(PTrait<?>... effects)
    {
        super(DATA, 0);
        if (target == null)
        {
            target = PCLCardTarget.None;
        }
        this.effects.addAll(Arrays.asList(effects));
        setParentsForChildren();
    }

    public static PMultiTrait join(PTrait<?>... effects)
    {
        return new PMultiTrait(effects);
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
    public boolean canPlay(AbstractCard card, AbstractMonster m)
    {
        boolean canPlay = true;
        for (PSkill<?> be : effects)
        {
            canPlay = canPlay & be.canPlay(card, m);
        }
        return canPlay;
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
    public String getText(int index, boolean addPeriod)
    {
        return effects.size() > index ? effects.get(index).getText(addPeriod) : "";
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return EUIUtils.joinStrings(" ", getEffectTexts(effects, addPeriod));
    }

    @Override
    public boolean isDetrimental()
    {
        return EUIUtils.any(effects, PSkill::isDetrimental);
    }

    public PMultiTrait makePreviews(RotatingList<EUICardPreview> previews)
    {
        for (PSkill<?> effect : effects)
        {
            effect.makePreviews(previews);
        }
        super.makePreviews(previews);
        return this;
    }

    @Override
    public float modifyBlock(AbstractCard card, AbstractMonster m, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyBlock(card, m, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamage(AbstractCard card, AbstractMonster m, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyDamage(card, m, amount);
        }
        return amount;
    }

    @Override
    public float modifyHitCount(PCLCard card, AbstractMonster m, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyHitCount(card, m, amount);
        }
        return amount;
    }

    @Override
    public float modifyMagicNumber(AbstractCard card, AbstractMonster m, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyMagicNumber(card, m, amount);
        }
        return amount;
    }

    @Override
    public float modifyRightCount(PCLCard card, AbstractMonster m, float amount)
    {
        for (PSkill<?> be : effects)
        {
            amount = be.modifyRightCount(card, m, amount);
        }
        return amount;
    }

    @Override
    public PMultiTrait onAddToCard(AbstractCard card)
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

    public boolean removable()
    {
        return effects.isEmpty() || EUIUtils.all(effects, PSkill::removable);
    }

    @Override
    public PMultiTrait setAmountFromCard()
    {
        super.setAmountFromCard();
        for (PSkill<?> effect : effects)
        {
            effect.setAmountFromCard();
        }
        return this;
    }

    @Override
    public PMultiTrait setSource(PointerProvider card)
    {
        super.setSource(card);
        for (PSkill<?> effect : effects)
        {
            effect.setSource(card);
        }
        return this;
    }

    @Override
    public PMultiTrait setSource(PointerProvider card, PCLCardValueSource source)
    {
        super.setSource(card, source);
        for (PSkill<?> effect : effects)
        {
            effect.setSource(card, source);
        }
        return this;
    }

    @Override
    public PMultiTrait setTemporaryAmount(int amount)
    {
        for (PSkill<?> effect : effects)
        {
            effect.setTemporaryAmount(amount);
        }
        return this;
    }

    @Override
    public PMultiTrait setTemporaryExtra(int extra)
    {
        for (PSkill<?> effect : effects)
        {
            effect.setTemporaryExtra(extra);
        }
        return this;
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
    public PMultiTrait makeCopy()
    {
        PMultiTrait copy = (PMultiTrait) super.makeCopy();
        for (PTrait<?> effect : effects)
        {
            copy.addEffect(effect.makeCopy());
        }
        return copy;
    }

    @Override
    public PMultiTrait onRemoveFromCard(AbstractCard card)
    {
        removeSubs(card);
        super.onRemoveFromCard(card);
        return this;
    }

    @Override
    public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
    {
        for (PSkill<?> effect : effects)
        {
            effect.refresh(m, c, conditionMet);
        }
    }

    @Override
    public String getSubDescText()
    {
        return null;
    }

    @Override
    public String getSubSampleText()
    {
        return null;
    }

    public List<PTrait<?>> getSubEffects()
    {
        return effects;
    }

    public PTrait<?> getSubEffect(int index)
    {
        return index < effects.size() ? effects.get(index) : null;
    }

    public PMultiTrait addEffect(PTrait<?> newEffect)
    {
        this.effects.add(newEffect);
        setParentsForChildren();
        return this;
    }

    public PMultiTrait setEffects(PTrait<?>... effects)
    {
        return setEffects(Arrays.asList(effects));
    }

    public PMultiTrait setEffects(List<PTrait<?>> effects)
    {
        this.effects.clear();
        this.effects.addAll(effects);
        setParentsForChildren();

        return this;
    }

    public PMultiTrait stack(PSkill<?> other)
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
