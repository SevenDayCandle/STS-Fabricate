package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.markers.SelectFromPileMarker;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.CardSelection;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public abstract class PMod_DoBranch extends PMod_Branch<AbstractCard> implements SelectFromPileMarker
{

    public PMod_DoBranch(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_DoBranch(PSkillData data)
    {
        super(data);
    }

    public PMod_DoBranch(PSkillData data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PMod_DoBranch(PSkillData data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups)
    {
        super(data, target, amount, groups);
    }

    public PMod_DoBranch(PSkillData data, PCLCardTarget target, int amount, PSkill effect)
    {
        super(data, target, amount, effect);
    }

    public PMod_DoBranch(PSkillData data, PCLCardTarget target, int amount, PSkill... effect)
    {
        super(data, target, amount, effect);
    }

    public PMod_DoBranch(PSkillData data, PCLCardTarget target, int amount, PCLAffinity... affinities)
    {
        super(data, target, amount, affinities);
    }

    public PMod_DoBranch(PSkillData data, PCLCardTarget target, int amount, PCLOrbHelper... orbs)
    {
        super(data, target, amount, orbs);
    }

    public PMod_DoBranch(PSkillData data, PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers)
    {
        super(data, target, amount, powerHelpers);
    }

    protected PCLActionWithCallback<ArrayList<AbstractCard>> createPileAction()
    {
        return getAction().invoke(getName(), amount, getCardGroup())
                .setOptions(alt ? CardSelection.Random : origin, true);
    }

    public String getQualifier(int i)
    {
        PCLAffinity affinity = i < affinities.size() ? affinities.get(i) : null;
        AbstractCard.CardType type = i < types.size() ? types.get(i) : null;
        return affinity != null && type != null ? affinity.getTooltip().getTitleOrIcon() + " " + EUIGameUtils.textForType(type) :
                affinity != null ? affinity.getTooltip().getTitleOrIcon() :
                        type != null ? EUIGameUtils.textForType(type) : TEXT.subjects.other;
    }

    @Override
    public String getText(boolean addPeriod)
    {
        if (this.childEffect instanceof PMultiBase)
        {
            return getSubText() + EFFECT_SEPARATOR +
                    TEXT.conditions.doForEach() + ": | " + getEffectTexts(addPeriod);
        }
        return getSubText();
    }

    @Override
    public boolean matchesBranch(AbstractCard c, int i, PCLUseInfo info)
    {
        boolean valid = true;
        if (types.size() > 0)
        {
            AbstractCard.CardType type = i < types.size() ? types.get(i) : null;
            valid = type != null ? c.type == type : !types.contains(c.type);
        }
        if (affinities.size() > 0)
        {
            PCLAffinity affinity = i < affinities.size() ? affinities.get(i) : null;
            valid = valid & affinity != null ? GameUtilities.hasAffinity(c, affinity) : EUIUtils.all(affinities, af -> !GameUtilities.hasAffinity(c, affinity));
        }
        return valid;
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.verbNoun(tooltipTitle(), "X") + EFFECT_SEPARATOR + TEXT.conditions.doForEach();
    }

    @Override
    public String getSubText()
    {
        return !groupTypes.isEmpty() ? TEXT.actions.genericFrom(tooltipTitle(), getAmountRawString(), getShortCardString(), getGroupString())
                : EUIRM.strings.verbNoun(tooltipTitle(), getAmountRawString());
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().add(createPileAction())
                .addCallback(cards -> {
                    this.cards = cards;
                    if (this.childEffect != null)
                    {
                        this.childEffect.setCards(cards);
                        branch(info, cards);
                    }
                });
    }
}
