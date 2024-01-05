package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryPath;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_CheckCreatureElite extends PPassiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckCreatureElite.class, PField_Not.class, 1, 1)
            .pclOnly();

    public PCond_CheckCreatureElite(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckCreatureElite() {
        super(DATA, PCLCardTarget.Single, 1);
    }

    public PCond_CheckCreatureElite(PCLCardTarget target) {
        super(DATA, target, 1);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return GameUtilities.inEliteRoom() && evaluateTargets(info, m -> !GameUtilities.isDeadOrEscaped(m) && !GameUtilities.isMinion(m));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, PTEXT[4]);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.cond_ifTargetIs(getTargetStringPerspective(perspective), getTargetOrdinal(target), PTEXT[4]);
    }
}
