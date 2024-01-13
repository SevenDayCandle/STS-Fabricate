package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_Heal extends PMove<PField_Empty> implements OutOfCombatMove {
    public static final PSkillData<PField_Empty> DATA = register(PMove_Heal.class, PField_Empty.class);

    public PMove_Heal() {
        this(1);
    }

    public PMove_Heal(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_Heal(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_Heal(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_heal(TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isSelfOnlyTarget(perspective)) {
            return TEXT.act_heal(getAmountRawString());
        }
        return TEXT.act_healOn(getAmountRawString(), getTargetStringPerspective(perspective));
    }

    @Override
    public boolean isDetrimental() {
        return target.targetsEnemies();
    }

    @Override
    public boolean isMetascaling() {
        return !isDetrimental();
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (AbstractCreature t : getTargetListAsNew(info)) {
            int actualAmount = refreshAmount(info);
            order.heal(info.source, t, actualAmount);
        }
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        super.useOutsideOfBattle(info);
        int actualAmount = refreshAmount(info);
        AbstractDungeon.player.heal(actualAmount);
    }
}
