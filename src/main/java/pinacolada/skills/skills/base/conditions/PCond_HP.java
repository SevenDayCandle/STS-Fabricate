package pinacolada.skills.skills.base.conditions;

import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnCreatureHealSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_HP extends PPassiveCond<PField_Not> implements OnCreatureHealSubscriber {
    public static final PSkillData<PField_Not> DATA = register(PCond_HP.class, PField_Not.class);

    public PCond_HP(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_HP() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_HP(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PCond_HP(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, t -> fields.doesValueMatchThreshold(t.currentHealth + TempHPField.tempHp.get(t)));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_generic2(PGR.core.tooltips.hp.title, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String baseString = fields.getThresholdRawString(PGR.core.tooltips.hp.title);
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_generic2(PGR.core.tooltips.heal.present(), baseString), perspective);
        }
        return getTargetHasStringPerspective(perspective, baseString);
    }

    @Override
    public int onHeal(AbstractCreature creature, int heal) {
        AbstractCreature owner = getOwnerCreature();
        PCLUseInfo info = generateInfo(owner, creature);
        boolean eval = evaluateTargets(info, c -> c == creature);
        if (eval && fields.doesValueMatchThreshold(heal, amount)) {
            useFromTrigger(info.setData(heal));
        }
        return heal;
    }
}
