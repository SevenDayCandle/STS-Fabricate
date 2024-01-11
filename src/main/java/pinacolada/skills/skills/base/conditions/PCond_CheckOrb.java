package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnOrbPassiveEffectSubscriber;
import pinacolada.orbs.PCLOrbData;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_CheckOrb extends PPassiveCond<PField_Orb> implements OnOrbPassiveEffectSubscriber {
    public static final PSkillData<PField_Orb> DATA = register(PCond_CheckOrb.class, PField_Orb.class)
            .noTarget();

    public PCond_CheckOrb() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_CheckOrb(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckOrb(int amount, PCLOrbData... orb) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orb);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (fields.orbs.isEmpty()) {
            return amount <= 0 ? GameUtilities.getOrbCount() == 0 : GameUtilities.getOrbCount() >= refreshAmount(info);
        }
        return fields.allOrAnyR(fields.orbs, o -> GameUtilities.getOrbCount(o) >= refreshAmount(info));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(TEXT.act_trigger(PGR.core.tooltips.orb.title)) : EUIRM.strings.numNoun(TEXT.subjects_x, PGR.core.tooltips.orb.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isBranch()) {
            return getWheneverString(PGR.core.tooltips.trigger.title, perspective);
        }
        String tt = fields.getOrbAndOrString();
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_trigger(tt), perspective);
        }
        return getTargetHasStringPerspective(perspective, fields.getThresholdRawString(tt));
    }

    @Override
    public void onOrbPassiveEffect(AbstractOrb orb) {
        if (fields.getOrbFilter().invoke(orb)) {
            useFromTrigger(generateInfo(null).setData(orb));
        }
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRBoolean(editor, TEXT.cedit_or, null);
    }
}
