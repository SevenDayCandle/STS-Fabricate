package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;



public class PMod_PerCardPlayedCombat extends PMod<PField_CardCategory>
{

    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCardPlayedCombat.class, PField_CardCategory.class).selfTarget();

    public PMod_PerCardPlayedCombat(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_PerCardPlayedCombat()
    {
        super(DATA);
    }

    public PMod_PerCardPlayedCombat(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setCardGroup(groups);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.perThisCombat("X", "Y", PGR.core.tooltips.play.past(), "");
    }

    @Override
    public String getSubText()
    {
        return this.amount <= 1 ? fields.getFullCardOrString() : EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardOrString());
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return TEXT.conditions.perThisCombat(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "", getSubText(), PGR.core.tooltips.play.past(), getXRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return be.baseAmount * EUIUtils.count(AbstractDungeon.actionManager.cardsPlayedThisCombat,
                c -> fields.getFullCardFilter().invoke(c)) / Math.max(1, this.amount);
    }
}
