package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PActiveMod;

@VisibleSkill
public class PMod_IncreaseOnUse extends PActiveMod<PField_Empty> {

    public static final PSkillData<PField_Empty> DATA = register(PMod_IncreaseOnUse.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(-1, DEFAULT_MAX)
            .noTarget();

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
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_increaseBy(TEXT.subjects_x, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return amount < 0 ? TEXT.act_reduceBy(TEXT.subjects_this, getAmountRawString(requestor)) : TEXT.act_increaseBy(TEXT.subjects_this, getAmountRawString(requestor));
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return TEXT.cond_xThenY(childEffect != null ? capital(childEffect.getText(perspective, requestor, false), addPeriod) : "", extra > 0 ? getSubText(perspective, requestor) + getMaxExtraString() : getSubText(perspective, requestor)) + PCLCoreStrings.period(addPeriod);
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
            PCLActions.last.callback(() -> {
                this.childEffect.addAmountForCombat(refreshAmount(info), extra > 0 ? extra : Integer.MAX_VALUE);
            });
        }
    }
}
