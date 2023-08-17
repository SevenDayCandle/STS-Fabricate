package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.powers.ThornsPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PActiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_TriggerTo extends PActiveCond<PField_Orb> {
    public static final PSkillData<PField_Orb> DATA = register(PCond_TriggerTo.class, PField_Orb.class)
            .selfTarget();

    public PCond_TriggerTo(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_TriggerTo() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_TriggerTo(int amount, PCLOrbHelper... orbs) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return (!fields.orbs.isEmpty() || GameUtilities.getOrbCount() >= amount) && !EUIUtils.any(fields.orbs, o -> GameUtilities.getOrbCount(o.ID) < amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_trigger(TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        Object tt = fields.getOrbOrString();
        return TEXT.act_trigger(amount <= 1 ? TEXT.subjects_yourFirst(tt) : TEXT.subjects_yourFirst(EUIRM.strings.numNoun(getAmountRawString(), tt)));
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        return order.triggerOrbPassive(1, amount, false)
                .setFilter(fields.getOrbFilter())
                .addCallback(orbs -> {
                    if (orbs.size() >= amount) {
                        info.setData(orbs);
                        onComplete.invoke(info);
                    }
                    else {
                        onFail.invoke(info);
                    }
                });
    }
}
