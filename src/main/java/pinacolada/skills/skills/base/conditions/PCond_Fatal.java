package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnCreatureDeathSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Random;
import pinacolada.skills.skills.PActiveNonCheckCond;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

@VisibleSkill
public class PCond_Fatal extends PActiveNonCheckCond<PField_Random> implements OnCreatureDeathSubscriber {
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
            PCLCardTarget actual = getTargetForPerspective(perspective);
            if (fields.not || actual == PCLCardTarget.None || (actual == PCLCardTarget.Self && !isFromCreature())) {
                return TEXT.cond_ifTargetWouldDie(getTargetString(actual));
            }
            if (fields.random) {
                return getWheneverAreString(EUIRM.strings.adjNoun(PGR.core.tooltips.fatal.title, PGR.core.tooltips.kill.present()), perspective);
            }
            return getWheneverString(PGR.core.tooltips.kill.present(), perspective);
        }
        if (fields.random) {
            return TEXT.cond_ifX(fields.not ? TEXT.cond_not(PGR.core.tooltips.fatal.title) : PGR.core.tooltips.fatal.title);
        }
        return fields.not ? getTargetIsString(getTargetForPerspective(perspective), TEXT.cond_not(PGR.core.tooltips.kill.past())) : getTargetIsString(getTargetForPerspective(perspective), PGR.core.tooltips.kill.past());
    }

    @Override
    public boolean onDeath(AbstractCreature t, boolean triggerRelics) {
        AbstractCreature owner = getOwnerCreature();
        PCLUseInfo info = generateInfo(owner, t);
        boolean eval = evaluateTargets(info, c -> c == t);
        if (GameUtilities.isFatal(t, !fields.random) && eval) {
            useFromTrigger(info);
            return !fields.not || t == AbstractDungeon.player; // fields.not prevents death when true or if its a player (because it would be completely useless if you died)
        }
        return true;
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        fields.registerNotBoolean(editor);
        fields.registerRBoolean(editor, PGR.core.tooltips.fatal.title, PGR.core.tooltips.fatal.description);
    }

    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        // Copy list in case info list changes from other effects
        ArrayList<? extends AbstractCreature> targs = new ArrayList<>(fields.random ? info.fillWithTargets() : getTargetList(info));
        return PCLActions.last.callback(targs, (targets, __) -> {
            if (!targets.isEmpty() && EUIUtils.any(targets, t -> GameUtilities.isFatal(t, !fields.random)) && tryPassParent(this, info)) {
                onComplete.invoke(info);
            }
            else {
                onFail.invoke(info);
            }
        }).isCancellable(false);
    }
}
