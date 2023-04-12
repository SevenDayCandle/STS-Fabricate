package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMod_PerCard extends PMod_Per<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCard.class, PField_CardCategory.class)
            .setGroups(PCLCardGroupHelper.getAll())
            .selfTarget();

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
    public String getSubText()
    {
        return TEXT.subjects_card;
    }

    @Override
    public String getConditionText()
    {
        return this.amount <= 1 ? fields.getFullCardStringSingular() : EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardStringSingular());
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return !fields.hasGroups() ? super.getText(addPeriod) :
                TEXT.cond_perIn(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "", getConditionText(),
                        fields.getGroupString() + getXRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        return EUIUtils.sumInt(fields.groupTypes, g -> EUIUtils.count(g.getCards(),
                        c -> fields.getFullCardFilter().invoke(c))
                );
    }
}
