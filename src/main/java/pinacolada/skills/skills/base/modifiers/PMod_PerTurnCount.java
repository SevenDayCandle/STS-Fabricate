package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerTurnCount extends PMod_Per<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PMod_PerTurnCount.class, PField_Not.class).noTarget();

    public PMod_PerTurnCount(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerTurnCount() {
        super(DATA);
    }

    public PMod_PerTurnCount(int amount) {
        super(DATA, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return GameActionManager.turn;
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.strings.subjects_turnCount;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return PGR.core.strings.subjects_turnCount;
    }
}
