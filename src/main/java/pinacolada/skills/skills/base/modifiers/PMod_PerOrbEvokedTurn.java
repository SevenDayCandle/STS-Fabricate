package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;

import java.util.List;

@VisibleSkill
public class PMod_PerOrbEvokedTurn extends PMod_Per<PField_Orb>
{

    public static final PSkillData<PField_Orb> DATA = register(PMod_PerOrbEvokedTurn.class, PField_Orb.class).selfTarget();

    public PMod_PerOrbEvokedTurn(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerOrbEvokedTurn()
    {
        super(DATA);
    }

    public PMod_PerOrbEvokedTurn(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, amount);
        fields.setOrb(orbs);
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        List<AbstractOrb> orbs = fields.random ? CombatManager.orbsEvokedThisCombat() : CombatManager.orbsEvokedThisTurn();
        return (fields.orbs.isEmpty() ? orbs.size() :
                EUIUtils.count(orbs, o -> EUIUtils.any(fields.orbs, orb -> orb.ID.equals(o.ID))));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.cond_perXY(TEXT.subjects_x, PGR.core.tooltips.orb.title, PGR.core.tooltips.evoke.past());
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.orb.title;
    }

    @Override
    public String getText(boolean addPeriod)
    {
        String childString = childEffect != null ? capital(childEffect.getText(false), addPeriod) : "";
        return (fields.random ? TEXT.cond_perThisCombat(childString, getConditionText(), PGR.core.tooltips.evoke.past()) :
                TEXT.cond_perThisTurn(childString, getConditionText(), PGR.core.tooltips.evoke.past())
        ) + getXRawString() + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public String getConditionText()
    {
        return this.amount <= 1 ? fields.getOrbAndString(1) : EUIRM.strings.numNoun(getAmountRawString(), fields.getOrbAndString());
    }
}
