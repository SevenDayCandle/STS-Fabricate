package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

import static pinacolada.skills.PSkill.PCLEffectType.CardGroupFull;

public class PMod_PerCard extends PMod
{

    public static final PSkillData DATA = register(PMod_PerCard.class, CardGroupFull).selfTarget();

    public PMod_PerCard(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_PerCard()
    {
        super(DATA);
    }

    public PMod_PerCard(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    public PMod_PerCard(int amount, List<PCLCardGroupHelper> groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups.toArray(new PCLCardGroupHelper[]{}));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", TEXT.subjects.card);
    }

    @Override
    public String getSubText()
    {
        return this.amount <= 1 ? getFullCardOrString(1) : EUIRM.strings.numNoun(getAmountRawString(), getFullCardOrString(1));
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return groupTypes != null && groupTypes.isEmpty() ? super.getText(addPeriod) :
                TEXT.conditions.perIn(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "", getSubText(),
                        getGroupString() + getXRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return be.baseAmount *
                EUIUtils.sumInt(groupTypes, g -> EUIUtils.count(g.getCards(),
                        c -> getFullCardFilter().invoke(c))
                ) / Math.max(1, this.amount);
    }
}
