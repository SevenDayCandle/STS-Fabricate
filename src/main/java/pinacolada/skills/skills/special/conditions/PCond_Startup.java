package pinacolada.skills.skills.special.conditions;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnBattleStartSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDelegateCond;

// Only for relics
public class PCond_Startup extends PDelegateCond<PField_Empty> implements OnBattleStartSubscriber {
    public static final PSkillData<PField_Empty> DATA = register(PCond_Startup.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PCond_Startup() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Startup(PSkillSaveData content) {
        super(DATA, content);
    }

    // This should not activate the child effect when played normally

    @Override
    public boolean canPlay(PCLUseInfo info) {
        return true;
    }

    @Override
    public void use(PCLUseInfo info) {
    }

    @Override
    public void use(PCLUseInfo info, int index) {
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return PGR.core.tooltips.startup.title;
    }

    @Override
    public String getSubText() {
        return PGR.core.tooltips.startup.title;
    }

    @Override
    public void onBattleStart() {
        useFromTrigger(makeInfo(null));
    }
}
