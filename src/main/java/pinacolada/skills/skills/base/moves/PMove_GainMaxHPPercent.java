package pinacolada.skills.skills.base.moves;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_GainMaxHPPercent extends PMove_Gain implements OutOfCombatMove {
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainMaxHPPercent.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX);

    public PMove_GainMaxHPPercent() {
        this(1);
    }

    public PMove_GainMaxHPPercent(int amount) {
        super(DATA, amount);
    }

    public PMove_GainMaxHPPercent(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String gainAmountText() {
        return getAmountRawString() + "%";
    }

    @Override
    public String gainText() {
        return PGR.core.tooltips.maxHP.title;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_gainAmount(TEXT.subjects_x + "%", gainText());
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
                    int reduction = MathUtils.ceil(t.maxHealth * amount / 100f);
                    t.decreaseMaxHealth(-reduction);
                }
            }
            else {
                for (AbstractCreature t : getTargetList(info)) {
                    int inc = MathUtils.ceil(t.maxHealth * amount / 100f);
                    t.increaseMaxHp(inc, true);
                }
            }
        });
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        super.useOutsideOfBattle(info);
        int am = MathUtils.ceil(AbstractDungeon.player.maxHealth * amount / 100f);
        if (amount < 0) {
            AbstractDungeon.player.decreaseMaxHealth(-am);
        }
        else {
            AbstractDungeon.player.increaseMaxHp(am, true);
        }
    }
}
