package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;


@VisibleSkill
public class PMod_PerCardPlayed extends PMod_Per<PField_CardCategory>
{

    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCardPlayed.class, PField_CardCategory.class).selfTarget();

    public PMod_PerCardPlayed(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerCardPlayed()
    {
        super(DATA);
    }

    public PMod_PerCardPlayed(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setCardGroup(groups);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.cond_perThisTurn(TEXT.subjects_x, TEXT.subjects_x, PGR.core.tooltips.play.past(), "");
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
        return (fields.forced ? TEXT.cond_perThisCombat(childString, getConditionText(), PGR.core.tooltips.play.past(), getXRawString()) : TEXT.cond_perThisTurn(childString, getConditionText(), PGR.core.tooltips.play.past(), getXRawString()))
                + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        return EUIUtils.count(fields.forced ? AbstractDungeon.actionManager.cardsPlayedThisCombat : AbstractDungeon.actionManager.cardsPlayedThisTurn,
                c -> fields.getFullCardFilter().invoke(c));
    }
}
