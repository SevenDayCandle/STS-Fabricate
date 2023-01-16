package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.GameUtilities;

public class PMod_XEnergy extends PMod<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMod_XEnergy.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .selfTarget();

    public PMod_XEnergy(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_XEnergy()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PMod_XEnergy(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.pay(TEXT.subjects.x, PGR.core.tooltips.energy.title);
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.energy.getTitleOrIcon();
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return childEffect != null ? childEffect.getText(addPeriod) : "";
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (this.childEffect != null)
        {
            updateChildAmount(info);
            GameUtilities.useXCostEnergy(sourceCard);
            this.childEffect.use(info);
        }
    }

    @Override
    public String wrapAmountChild(String input)
    {
        // If the value is not parseable, don't remove the numbers
        int value = EUIUtils.parseInt(input, 2);
        if (value == 1)
        {
            input = "";
        }
        input = this.amount > 0 ? input + TEXT.subjects.x + "+" + this.amount : input + TEXT.subjects.x;
        return parent != null ? parent.wrapAmountChild(input) : (input);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info)
    {
        return be.baseAmount * GameUtilities.getXCostEnergy(sourceCard, false) + this.amount;
    }
}
