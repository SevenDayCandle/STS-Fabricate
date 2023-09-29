package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.utilities.ColoredString;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PPassiveMod;

@VisibleSkill
public class PMod_IncreaseOnUse extends PPassiveMod<PField_Empty> {

    public static final PSkillData<PField_Empty> DATA = register(PMod_IncreaseOnUse.class, PField_Empty.class).noTarget();

    public PMod_IncreaseOnUse(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_IncreaseOnUse() {
        this(0);
    }

    public PMod_IncreaseOnUse(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    @Override
    public final ColoredString getColoredValueString() {
        if (baseAmount != amount) {
            return new ColoredString(amount > 0 ? "+" + amount : amount, amount >= baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        }

        return new ColoredString(amount > 0 ? "+" + amount : amount, Settings.CREAM_COLOR);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info, boolean isUsing) {
        return amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_increaseBy(TEXT.subjects_x, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return amount < 0 ? TEXT.act_reduceBy(TEXT.subjects_this, getAmountRawString()) : TEXT.act_increaseBy(TEXT.subjects_this, getAmountRawString());
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        return TEXT.cond_xThenY(childEffect != null ? capital(childEffect.getText(perspective, false), addPeriod) : "", getSubText(perspective)) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public void onDrag(AbstractMonster m) {
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet, boolean isUsing) {
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (this.childEffect != null) {
            this.childEffect.use(info, order);
            order.callback(() -> {
                this.childEffect.addAmountForCombat(amount);
            });
        }
    }
}
