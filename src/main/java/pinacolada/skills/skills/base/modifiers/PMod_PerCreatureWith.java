package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.List;

@VisibleSkill
public class PMod_PerCreatureWith extends PMod_Per<PField_Power> {

    public static final PSkillData<PField_Power> DATA = register(PMod_PerCreatureWith.class, PField_Power.class);

    public PMod_PerCreatureWith(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreatureWith() {
        super(DATA);
    }

    public PMod_PerCreatureWith(int amount, PCLPowerData... powerHelpers) {
        this(PCLCardTarget.AllEnemy, amount, powerHelpers);
    }

    public PMod_PerCreatureWith(PCLCardTarget target, int amount, PCLPowerData... powerHelpers) {
        super(DATA, target, amount);
        fields.setPower(powerHelpers);
    }

    @Override
    public int getModifiedAmount(PCLUseInfo info, int baseAmount, boolean isUsing) {
        return baseAmount * getMultiplier(info, isUsing);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        List<? extends AbstractCreature> targetList = getTargetList(info);
        return fields.powers.isEmpty() ? EUIUtils.count(targetList, t -> t.powers != null && EUIUtils.any(t.powers, po -> po.type == AbstractPower.PowerType.DEBUFF)) :
                EUIUtils.count(targetList, t -> fields.allOrAnyPower(t));
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_characterWithX(TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String baseString = (this.amount <= 1 ? "" : getAmountRawString() + " ") + (fields.powers.isEmpty() ? plural(PGR.core.tooltips.debuff) : fields.getPowerAndOrString());
        return target == PCLCardTarget.Any ? TEXT.subjects_characterWithX(baseString) : TEXT.subjects_enemyWithX(baseString);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRBoolean(editor, TEXT.cedit_or, null);
    }
}
