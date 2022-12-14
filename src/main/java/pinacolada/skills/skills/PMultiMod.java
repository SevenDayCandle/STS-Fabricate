package pinacolada.skills.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUICardPreview;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.skills.base.modifiers.PMod_Branch;
import pinacolada.skills.skills.base.modifiers.PMod_Do;
import pinacolada.utilities.RotatingList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PMultiMod extends PMod implements PMultiBase<PMod>
{
    public static final PSkillData DATA = register(PMultiMod.class, PCLEffectType.General, 0, DEFAULT_MAX);
    protected ArrayList<PMod> effects = new ArrayList<>();

    public PMultiMod()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PMultiMod(PSkillSaveData content)
    {
        super(content);
        effects = EUIUtils.mapAsNonnull(splitJson(content.effectData), e -> (PMod) PSkill.get(e));
        setParentsForChildren();
    }

    public PMultiMod(PMod... effects)
    {
        super(DATA, EUIUtils.max(effects, effect -> effect.target), 0);
        if (target == null)
        {
            target = PCLCardTarget.None;
        }
        this.effects.addAll(Arrays.asList(effects));
        setParentsForChildren();
    }

    public static PMultiMod and(PMod... effects)
    {
        return new PMultiMod(effects);
    }

    public static PMultiMod or(PMod... effects)
    {
        return (PMultiMod) new PMultiMod(effects).setAlt(true);
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
        return alt ? PCLCoreStrings.joinWithOr(EUIUtils.map(getNotDoMods(), e -> e.getText(false))) : PSkill.joinEffectTexts(getNotDoMods(), ". ", false);
    }

    @Override
    public String getText(int index, boolean addPeriod)
    {
        return effects.size() > index ? effects.get(index).getText(index, addPeriod) : getText(addPeriod);
    }

    @Override
    public String getText(boolean addPeriod)
    {
        List<PMod_Do> doMods = getDoMods();
        if (doMods.isEmpty())
        {
            return super.getText(addPeriod);
        }

        return EUIUtils.joinStrings(EFFECT_SEPARATOR, EUIUtils.map(doMods, mod -> mod.getMoveString(false))) + EFFECT_SEPARATOR + super.getText(addPeriod);
    }

    @Override
    public PMultiMod makeCopy()
    {
        PMultiMod copy = (PMultiMod) super.makeCopy();
        for (PMod effect : effects)
        {
            copy.addEffect((PMod) effect.makeCopy());
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
    public PMultiMod onAddToCard(AbstractCard card)
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
    public PMultiMod onRemoveFromCard(AbstractCard card)
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

    public PMultiMod setCards(Collection<? extends AbstractCard> cards)
    {
        super.setCards(cards);
        for (PSkill be : effects)
        {
            be.setCards(cards);
        }
        return this;
    }

    @Override
    public PMultiMod setAmountFromCard()
    {
        super.setAmountFromCard();
        for (PSkill effect : effects)
        {
            effect.setAmountFromCard();
        }
        return this;
    }

    @Override
    public PMultiMod setSource(PointerProvider card)
    {
        super.setSource(card);
        for (PSkill effect : effects)
        {
            effect.setSource(card);
        }
        return this;
    }

    @Override
    public PMultiMod setSource(PointerProvider card, PCLCardValueSource source)
    {
        super.setSource(card, source);
        for (PSkill effect : effects)
        {
            effect.setSource(card, source);
        }
        return this;
    }

    @Override
    public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
    {
        super.refresh(m, c, conditionMet);
        for (PSkill effect : effects)
        {
            effect.refresh(m, c, conditionMet);
        }
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        int amount = 0;
        for (PMod effect : effects)
        {
            amount += effect.getModifiedAmount(be, info);
        }
        return amount;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (PMod effect : effects)
        {
            effect.use(info);
        }
        super.use(info);
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
        for (PMod effect : effects)
        {
            effect.use(info, index);
        }
        super.use(info, index);
    }

    @Override
    public void use(PCLUseInfo info, boolean isUsing)
    {
        for (PMod effect : effects)
        {
            effect.use(info, isUsing);
        }
        super.use(info, isUsing);
    }

    public List<PMod> getSubEffects()
    {
        return effects;
    }

    public PMod getSubEffect(int index)
    {
        return index < effects.size() ? effects.get(index) : null;
    }

    public PMultiMod addEffect(PMod effect)
    {
        this.effects.add(effect);
        setParentsForChildren();
        return this;
    }

    public PMultiMod setEffects(PMod... effects)
    {
        return setEffects(Arrays.asList(effects));
    }

    public PMultiMod setEffects(List<PMod> effects)
    {
        this.effects.clear();
        this.effects.addAll(effects);
        setParentsForChildren();
        return this;
    }

    public PMultiMod stack(PSkill other)
    {
        super.stack(other);
        if (other instanceof PMultiBase)
        {
            stackMulti((PMultiBase) other);
        }
        return this;
    }

    protected List<PMod_Do> getDoMods()
    {
        return EUIUtils.mapAsNonnull(effects, ef -> EUIUtils.safeCast(ef, PMod_Do.class));
    }

    protected List<PMod> getNotDoMods()
    {
        return EUIUtils.filter(effects, ef -> !(ef instanceof PMod_Do || ef instanceof PMod_Branch));
    }
}
