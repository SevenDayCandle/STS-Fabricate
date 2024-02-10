package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbData;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PActiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_PayOrb extends PActiveCond<PField_Orb> {
    public static final PSkillData<PField_Orb> DATA = register(PCond_PayOrb.class, PField_Orb.class)
            .noTarget();

    public PCond_PayOrb(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_PayOrb() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_PayOrb(int amount, PCLOrbData... orbs) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return (!fields.orbs.isEmpty() || GameUtilities.getOrbCount() >= refreshAmount(info)) && !EUIUtils.any(fields.orbs, o -> GameUtilities.getOrbCount(o) < refreshAmount(info));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_remove(PGR.core.tooltips.orb.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        Object tt = fields.getOrbAndOrString();
        return TEXT.act_remove(amount <= 1 ? TEXT.subjects_yourFirst(tt) : TEXT.subjects_yourFirst(EUIRM.strings.numNoun(getAmountRawString(), tt)));
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        int am = refreshAmount(info);
        return order.removeOrb(am).setFilter(fields.getOrbFilter())
                .addCallback(orbs -> {
                    if (orbs.size() >= am) {
                        info.setData(orbs);
                        onComplete.invoke(info);
                    }
                    else {
                        onFail.invoke(info);
                    }
                });
    }
}
