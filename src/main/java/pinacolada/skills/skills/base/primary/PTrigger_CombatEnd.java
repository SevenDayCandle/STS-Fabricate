package pinacolada.skills.skills.base.primary;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnBattleEndSubscriber;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.PPassiveMod;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.moves.PMove_LoseHP;

@VisibleSkill
public class PTrigger_CombatEnd extends PTrigger implements OnBattleEndSubscriber {
    public static final PSkillData<PField_Not> DATA = register(PTrigger_CombatEnd.class, PField_Not.class, 0, 0);

    public PTrigger_CombatEnd() {
        super(DATA);
    }

    public PTrigger_CombatEnd(PSkillSaveData content) {
        super(DATA, content);
    }

    public PTrigger_CombatEnd(int maxUses) {
        super(DATA, PCLCardTarget.None, maxUses);
    }

    public PTrigger_CombatEnd(PCLCardTarget target, int maxUses) {
        super(DATA, target, maxUses);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.cond_atEndOfCombat();
    }

    @Override
    public String getSubText() {
        return TEXT.cond_atEndOfCombat() + ": ";
    }

    @Override
    public void onBattleEnd() {
        if (this.childEffect != null) {
            this.childEffect.use(makeInfo(null));
        }
    }

    @Override
    public void subscribeChildren() {
        subscribeToAll();
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return skill instanceof PPassiveCond ||
                skill instanceof PPassiveMod ||
                skill instanceof PMove_LoseHP ||
                skill.isMetascaling();
    }
}
