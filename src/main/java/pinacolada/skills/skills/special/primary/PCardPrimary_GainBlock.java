package pinacolada.skills.skills.special.primary;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.utilities.ColoredString;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.*;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PActiveCond;
import pinacolada.skills.skills.PActiveMod;
import pinacolada.skills.skills.PBlockTrait;
import pinacolada.skills.skills.PCardPrimary;

@VisibleSkill
public class PCardPrimary_GainBlock extends PCardPrimary<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PCardPrimary_GainBlock.class, PField_Empty.class);

    public PCardPrimary_GainBlock()
    {
        super(DATA, PCLCardTarget.Self, 0);
    }

    public PCardPrimary_GainBlock(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCardPrimary_GainBlock(PointerProvider card)
    {
        super(DATA, card);
    }

    public PCardPrimary_GainBlock setProvider(PointerProvider card)
    {
        setSource(card, PCLCardValueSource.Block, PCLCardValueSource.RightCount);
        return this;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.block);
    }

    @Override
    public ColoredString getColoredValueString(Object displayBase, Object displayAmount)
    {
        return new ColoredString(displayAmount,
                (sourceCard != null ?
                        sourceCard.upgradedBlock ? Settings.GREEN_TEXT_COLOR :
                                sourceCard.isBlockModified ? (amount > baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR)
                                        : Settings.CREAM_COLOR : Settings.CREAM_COLOR));
    }

    @Override
    public void useImpl(PCLUseInfo info)
    {
        for (AbstractCreature c : getTargetList(info))
        {
            // Extra has the value of right count
            for (int i = 0; i < extra; i++) {
                getActions().gainBlock(amount);
            }
        }
    }

    @Override
    public boolean isCondAllowed(PSkill<?> skill)
    {
        return (!(skill instanceof PActiveCond));
    }

    @Override
    public boolean isModAllowed(PSkill<?> skill)
    {
        return (!(skill instanceof PActiveMod));
    }

    @Override
    public boolean isMoveAllowed(PSkill<?> skill)
    {
        return skill instanceof PBlockTrait;
    }

    @Override
    public PCardPrimary_GainBlock makeCopy()
    {
        return (PCardPrimary_GainBlock) super.makeCopy();
    }

    @Override
    public String getSubText()
    {
        int count = source != null ? getExtraFromCard() : 1;
        String amountString = count > 1 ? getAmountRawString() + "x" + getExtraRawString() : getAmountRawString();

        String targetShortString = target.getShortString();
        if (targetShortString != null)
        {
            return EUIRM.strings.numAdjNoun(amountString, targetShortString, PGR.core.tooltips.block);
        }
        return EUIRM.strings.numNoun(amountString, PGR.core.tooltips.block);
    }

    public PCardPrimary_GainBlock setBonus(PMod<?> mod, int amount)
    {
        setChain(mod, PTrait.block(amount));
        return this;
    }

    public PCardPrimary_GainBlock setBonus(PMod<?> mod, int amount, int... upgrade)
    {
        setChain(mod, PTrait.block(amount).setUpgrade(upgrade));
        return this;
    }

    public PCardPrimary_GainBlock setBonusPercent(PMod<?> mod, int amount)
    {
        setChain(mod, PTrait.blockMultiplier(amount));
        return this;
    }

    public PCardPrimary_GainBlock setBonusPercent(PMod<?> mod, int amount, int... upgrade)
    {
        setChain(mod, PTrait.blockMultiplier(amount).setUpgrade(upgrade));
        return this;
    }
}
