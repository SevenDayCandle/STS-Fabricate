package pinacolada.skills.skills.base.primary;

import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.subscribers.OnBattleEndSubscriber;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.PPassiveMod;
import pinacolada.skills.skills.PTrigger;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.Collections;

@VisibleSkill
public class PTrigger_CombatEnd extends PTrigger implements OnBattleEndSubscriber {
    public static final PSkillData<PField_CardGeneric> DATA = register(PTrigger_CombatEnd.class, PField_CardGeneric.class, 0, 0)
            .noTarget();

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
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_atEndOfCombat();
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.cond_atEndOfCombat();
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return skill instanceof PMultiBase ||
                skill instanceof PPassiveCond ||
                skill instanceof PPassiveMod ||
                skill instanceof OutOfCombatMove;
    }

    @Override
    public void onBattleEnd() {
        if (this.childEffect != null) {
            this.childEffect.useOutsideOfBattle();
        }
    }

    @Override
    public PTrigger_CombatEnd scanForTips(String source) {
        if (tips == null) {
            tips = Collections.singletonList(new EUIKeywordTooltip(StringUtils.capitalize(TEXT.cond_atEndOfCombat()), TEXT.cetut_combatEnd));
        }
        return this;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        fields.setupEditor(editor);
    }

    @Override
    public void subscribeChildren() {
        subscribeToAll();
    }
}
