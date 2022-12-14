package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

import static pinacolada.skills.PSkill.PCLEffectType.Orb;

public class PMod_EvokePerOrb extends PMod
{

    public static final PSkillData DATA = register(PMod_EvokePerOrb.class, Orb).selfTarget();

    public PMod_EvokePerOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_EvokePerOrb()
    {
        super(DATA);
    }

    public PMod_EvokePerOrb(int amount, PCLOrbHelper... orbs)
    {
        super(DATA, PCLCardTarget.None, amount, orbs);
    }

    public PMod_EvokePerOrb(int amount, List<PCLOrbHelper> orbs)
    {
        super(DATA, PCLCardTarget.None, amount, orbs.toArray(new PCLOrbHelper[]{}));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.evoke(TEXT.conditions.per("X", TEXT.cardEditor.orbs));
    }

    @Override
    public String getSubText()
    {
        return this.amount <= 1 ? getOrbAndString(1) : EUIRM.strings.numNoun(getAmountRawString(), getOrbAndString(getRawString(EFFECT_CHAR)));
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return TEXT.actions.evoke(TEXT.subjects.allX(!orbs.isEmpty() ? getOrbString() : TEXT.cardEditor.orbs)) + EFFECT_SEPARATOR + super.getText(addPeriod);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info));
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info, index));
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (isUsing && childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info));
        }
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return AbstractDungeon.player == null ? 0 : be.baseAmount * (orbs.isEmpty() ? GameUtilities.getOrbCount() : EUIUtils.sumInt(orbs, GameUtilities::getOrbCount)) / Math.max(1, this.amount);
    }

    protected void useImpl(PCLUseInfo info, ActionT0 callback)
    {
        getActions().evokeOrb(1, GameUtilities.getOrbCount()).setFilter(getOrbFilter())
                .addCallback(callback);
    }

}
