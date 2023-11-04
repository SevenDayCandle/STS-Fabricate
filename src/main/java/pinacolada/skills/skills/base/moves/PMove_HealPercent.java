package pinacolada.skills.skills.base.moves;

import com.badlogic.gdx.math.MathUtils;
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
public class PMove_HealPercent extends PMove<PField_Empty> implements OutOfCombatMove {
    public static final PSkillData<PField_Empty> DATA = register(PMove_HealPercent.class, PField_Empty.class);

    public PMove_HealPercent() {
        this(1);
    }

    public PMove_HealPercent(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_HealPercent(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_HealPercent(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_heal(TEXT.subjects_x + "%");
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String percentLoss = getAmountRawString() + "%";
        if (isSelfOnlyTarget(perspective)) {
            return TEXT.act_heal(percentLoss);
        }
        return TEXT.act_healOn(percentLoss, getTargetStringPerspective(perspective));
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
        for (AbstractCreature t : getTargetList(info)) {
            int heal = MathUtils.ceil(t.maxHealth * amount / 100f);
            order.heal(info.source, t, heal);
        }
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        int heal = MathUtils.ceil(AbstractDungeon.player.maxHealth * amount / 100f);
        AbstractDungeon.player.heal(heal);
    }
}
