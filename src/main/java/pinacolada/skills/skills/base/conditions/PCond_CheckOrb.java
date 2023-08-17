package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnOrbPassiveEffectSubscriber;
import pinacolada.orbs.PCLOrbHelper;
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
            .selfTarget();

    public PCond_CheckOrb(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckOrb(int amount, PCLOrbHelper... orb) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orb);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (fields.orbs.isEmpty()) {
            return amount <= 0 ? GameUtilities.getOrbCount() == 0 : GameUtilities.getOrbCount() >= amount;
        }
        return fields.random ? EUIUtils.any(fields.orbs, o -> GameUtilities.getOrbCount(o.ID) >= amount) : EUIUtils.all(fields.orbs, o -> GameUtilities.getOrbCount(o.ID) >= amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.cedit_powers);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (isBranch()) {
            return getWheneverString(PGR.core.tooltips.trigger.title, perspective);
        }
        String tt = fields.getOrbAndOrString();
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_trigger(tt), perspective);
        }
        return getTargetHasStringPerspective(perspective, amount == 1 ? tt : EUIRM.strings.numNoun(amount <= 0 ? amount : amount + "+", tt));
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
