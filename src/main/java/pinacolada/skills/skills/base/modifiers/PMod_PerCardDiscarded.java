package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;


@VisibleSkill
public class PMod_PerCardDiscarded extends PMod_Per<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCardDiscarded.class, PField_CardCategory.class).selfTarget();

    public PMod_PerCardDiscarded(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerCardDiscarded()
    {
        super(DATA);
    }

    public PMod_PerCardDiscarded(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setCardGroup(groups);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.cond_perXY(TEXT.subjects_x, TEXT.subjects_card, PGR.core.tooltips.discard.past());
    }

    @Override
    public String getSubText()
    {
        return fields.getFullCardOrString();
    }

    @Override
    public String getConditionText()
    {
        return this.amount <= 1 ? getSubText() : EUIRM.strings.numNoun(getAmountRawString(), getSubText());
    }

    @Override
    public String getText(boolean addPeriod)
    {
        String childString = childEffect != null ? capital(childEffect.getText(false), addPeriod) : "";
        return (fields.forced ? TEXT.cond_perThisCombat(childString, getConditionText(), PGR.core.tooltips.discard.past()) : TEXT.cond_perThisTurn(childString, getConditionText(), PGR.core.tooltips.discard.past()))
                + getXRawString() + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        return EUIUtils.count(fields.forced ? CombatManager.cardsDiscardedThisCombat() : CombatManager.cardsDiscardedThisTurn(),
                c -> fields.getFullCardFilter().invoke(c));
    }
}
