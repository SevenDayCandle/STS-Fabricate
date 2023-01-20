package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMod_PerCard extends PMod<PField_CardCategory>
{

    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCard.class, PField_CardCategory.class).selfTarget();

    public PMod_PerCard(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerCard()
    {
        super(DATA);
    }

    public PMod_PerCard(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setCardGroup(groups);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per(TEXT.subjects.x, TEXT.subjects.card);
    }

    @Override
    public String getSubText()
    {
        return this.amount <= 1 ? fields.getFullCardOrString(1) : EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardOrString(1));
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return !fields.hasGroups() ? super.getText(addPeriod) :
                TEXT.conditions.perIn(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "", getSubText(),
                        fields.getGroupString() + getXRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info)
    {
        return be.baseAmount *
                EUIUtils.sumInt(fields.groupTypes, g -> EUIUtils.count(g.getCards(),
                        c -> fields.getFullCardFilter().invoke(c))
                ) / Math.max(1, this.amount);
    }
}
