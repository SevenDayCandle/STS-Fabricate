package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
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
    public String gainText() {
        return PGR.core.tooltips.maxHP.title;
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(() -> {
            if (amount < 0) {
                for (AbstractCreature t : getTargetList(info)) {
                    t.decreaseMaxHealth(-amount);
                }
            }
            else {
                for (AbstractCreature t : getTargetList(info)) {
                    t.increaseMaxHp(amount, true);
                }
            }
        });
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        super.useOutsideOfBattle(info);
        if (amount < 0) {
            AbstractDungeon.player.decreaseMaxHealth(-amount);
        }
        else {
            AbstractDungeon.player.increaseMaxHp(amount, true);
        }
    }
}
