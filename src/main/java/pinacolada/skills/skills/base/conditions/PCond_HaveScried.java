package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnScryActionSubscriber;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_HaveScried extends PPassiveCond<PField_Not> implements OnScryActionSubscriber {

    public static final PSkillData<PField_Not> DATA = register(PCond_HaveScried.class, PField_Not.class, 1, 1)
            .selfTarget();

    public PCond_HaveScried() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_HaveScried(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return info != null && (fields.not ^ CombatManager.haveScried);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.cond_ifYouDidThisTurn(fields.not ? TEXT.cond_not(PGR.core.tooltips.scry.past()) : PGR.core.tooltips.scry.past(), PCLCoreStrings.pluralForce(TEXT.subjects_cardN));
    }

    @Override
    public void onScryAction(AbstractGameAction action) {
        useFromTrigger(getInfo(null));
    }
}
