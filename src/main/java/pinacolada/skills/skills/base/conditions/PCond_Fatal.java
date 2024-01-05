package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnMonsterDeathSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Random;
import pinacolada.skills.skills.PActiveNonCheckCond;
import pinacolada.skills.skills.PLimit;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

@VisibleSkill
public class PCond_Fatal extends PActiveNonCheckCond<PField_Random> implements OnMonsterDeathSubscriber {
    public static final PSkillData<PField_Random> DATA = register(PCond_Fatal.class, PField_Random.class, 1, 1);

    public PCond_Fatal() {
        super(DATA, PCLCardTarget.Single, 0);
    }

    public PCond_Fatal(PCLCardTarget target) {
        super(DATA, target, 0);
    }

    public PCond_Fatal(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(PGR.core.tooltips.kill.present()) : TEXT.cond_ifX(PGR.core.tooltips.kill.past());
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause()) {
            if (fields.random) {
                return getWheneverAreString(PGR.core.tooltips.fatal.title, perspective);
            }
            return getWheneverString(PGR.core.tooltips.kill.present(), perspective);
        }
        if (fields.random) {
            return TEXT.cond_ifX(fields.not ? TEXT.cond_not(PGR.core.tooltips.fatal.title) : PGR.core.tooltips.fatal.title);
        }
        return fields.not ? getTargetIsString(getTargetForPerspective(perspective), TEXT.cond_not(PGR.core.tooltips.kill.past())) : getTargetIsString(getTargetForPerspective(perspective), PGR.core.tooltips.kill.past());
    }

    @Override
    public void onMonsterDeath(AbstractMonster monster, boolean triggerRelics) {
        if (GameUtilities.isFatal(monster, !fields.random)) {
            useFromTrigger(generateInfo(getOwnerCreature(), monster));
        }
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        fields.registerNotBoolean(editor);
        fields.registerRBoolean(editor, PGR.core.tooltips.fatal.title, PGR.core.tooltips.fatal.description);
    }

    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        // Copy list in case info list changes from other effects
        ArrayList<? extends AbstractCreature> targs = new ArrayList<>(fields.random ? info.fillWithTargets() : getTargetList(info));
        return PCLActions.last.callback(targs, (targets, __) -> {
            if (!targets.isEmpty() && EUIUtils.any(targets, t -> GameUtilities.isFatal(t, !fields.random)) && (!(parent instanceof PLimit) || ((PLimit) parent).tryActivate(info))) {
                onComplete.invoke(info);
            }
            else {
                onFail.invoke(info);
            }
        }).isCancellable(false);
    }
}
