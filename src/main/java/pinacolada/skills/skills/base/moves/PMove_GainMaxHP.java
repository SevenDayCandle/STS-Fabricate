package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_GainMaxHP extends PMove_Gain implements OutOfCombatMove {
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainMaxHP.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX);

    public PMove_GainMaxHP() {
        this(1);
    }

    public PMove_GainMaxHP(int amount) {
        super(DATA, amount);
    }

    public PMove_GainMaxHP(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public boolean isDetrimental() {
        return amount < 0;
    }

    @Override
    public String gainText() {
        return PGR.core.tooltips.maxHP.title;
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        if (amount < 0) {
            AbstractDungeon.player.decreaseMaxHealth(-amount);
        }
        else {
            AbstractDungeon.player.increaseMaxHp(amount, true);
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (amount < 0) {
            info.source.decreaseMaxHealth(-amount);
        }
        else {
            info.source.increaseMaxHp(amount, true);
        }
        super.use(info, order);
    }
}
