package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.GameUtilities;

public abstract class PMod_DoBranch extends PMod_Branch<PField_CardCategory, AbstractCard>
{

    public PMod_DoBranch(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_DoBranch(PSkillData<PField_CardCategory> data)
    {
        super(data);
    }

    public PMod_DoBranch(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PMod_DoBranch(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups)
    {
        super(data, target, amount);
        fields.setCardGroup(groups);
    }

    public String getQualifier(int i)
    {
        PCLAffinity affinity = i < fields.affinities.size() ? fields.affinities.get(i) : null;
        AbstractCard.CardType type = i < fields.types.size() ? fields.types.get(i) : null;
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
        if (fields.types.size() > 0)
        {
            AbstractCard.CardType type = i < fields.types.size() ? fields.types.get(i) : null;
            valid = type != null ? c.type == type : !fields.types.contains(c.type);
        }
        if (fields.affinities.size() > 0)
        {
            PCLAffinity affinity = i < fields.affinities.size() ? fields.affinities.get(i) : null;
            valid = valid & affinity != null ? GameUtilities.hasAffinity(c, affinity) : EUIUtils.all(fields.affinities, af -> !GameUtilities.hasAffinity(c, affinity));
        }
        return valid;
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.verbNoun(getActionTitle(), "X") + EFFECT_SEPARATOR + TEXT.conditions.doForEach();
    }

    @Override
    public String getSubText()
    {
        return !fields.groupTypes.isEmpty() ? TEXT.actions.genericFrom(getActionTitle(), getAmountRawString(), fields.getShortCardString(), fields.getGroupString())
                : EUIRM.strings.verbNoun(getActionTitle(), getAmountRawString());
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().add(fields.getGenericPileAction(getAction(), info))
                .addCallback(cards -> {
                    if (this.childEffect != null)
                    {
                        info.setData(cards);
                        branch(info, cards);
                    }
                });
    }

    protected String getActionTitle()
    {
        return getActionTooltip().title;
    }

    public abstract EUITooltip getActionTooltip();
    public abstract FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction();
}
