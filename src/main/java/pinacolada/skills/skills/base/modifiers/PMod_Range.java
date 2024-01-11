package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PActiveMod;
import pinacolada.skills.skills.PMathMod;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_Range extends PMathMod {

    public static final PSkillData<PField_Empty> DATA = register(PMod_Range.class, PField_Empty.class).noTarget();

    public PMod_Range(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_Range() {
        super(DATA);
    }

    public PMod_Range(int amount) {
        super(DATA, amount);
    }

    @Override
    public int getModifiedAmount(PCLUseInfo info, int baseAmount, boolean isUsing) {
        if (isUsing) {
            Random rng = PGR.dungeon.getRNG();
            if (this.amount < 0) {
                return PGR.dungeon.getRNG().random(baseAmount + this.amount, baseAmount);
            }
            else {
                return PGR.dungeon.getRNG().random(baseAmount, baseAmount + this.amount);
            }
        }
        return baseAmount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.verbNoun(TEXT.cedit_random, TEXT.subjects_x);
    }

    @Override
    public String wrapTextAmountChild(String input) {
        // TODO alternate logic if amount is not numeric
        int value = EUIUtils.parseInt(input, 0);
        input = input + "-" + (value + this.amount);
        return parent != null ? parent.wrapTextAmountChild(input) : super.wrapTextAmountChild(input);
    }
}
