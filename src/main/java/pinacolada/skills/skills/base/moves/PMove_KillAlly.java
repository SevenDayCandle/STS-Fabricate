package pinacolada.skills.skills.base.moves;

import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.List;

@VisibleSkill
public class PMove_KillAlly extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_KillAlly.class, PField_Empty.class)
            .setTargets(PCLCardTarget.AllAlly, PCLCardTarget.RandomAlly, PCLCardTarget.SingleAlly);

    public PMove_KillAlly() {
        this(1);
    }

    public PMove_KillAlly(int amount) {
        super(DATA, PCLCardTarget.SingleAlly, amount);
    }

    public PMove_KillAlly(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_KillAlly(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_kill(PGR.core.tooltips.summon.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.act_kill(getTargetStringPerspective(perspective));
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        List<PCLCardAlly> targets = EUIUtils.map(getTargetList(info), t -> EUIUtils.safeCast(t, PCLCardAlly.class));
        order.withdrawAlly(targets, 0)
                .setDestination(CombatManager.PURGED_CARDS)
                .showEffect(true).addCallback(cards ->
                {
                    if (this.childEffect != null) {
                        info.setData(cards);
                        this.childEffect.use(info, order);
                    }
                });
    }
}
