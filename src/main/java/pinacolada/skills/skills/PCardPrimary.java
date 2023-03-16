package pinacolada.skills.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

public abstract class PCardPrimary<T extends PField> extends PPrimary<T>
{
    public PCardPrimary(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PCardPrimary(PSkillData<T> data)
    {
        super(data);
    }

    public PCardPrimary(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PCardPrimary(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    public PCardPrimary(PSkillData<T> data, PointerProvider card)
    {
        super(data);
        setProvider(card);
    }

    // We want to execute active mods first because they affect card output, but we want the PCardPrimary to be the parent in order to ensure type safety in the skills object
    @Override
    public void use(PCLUseInfo info)
    {
        if (this.childEffect instanceof PActiveMod)
        {
            // The child effect should be affecting a card's primary attribute, so we don't need to grab the value from it
            this.childEffect.use(info);
            this.useImpl(info);
        }
        else
        {
            useImpl(info);
            super.use(info);
        }
    }

    public abstract PCardPrimary<T> setProvider(PointerProvider card);
    protected abstract void useImpl(PCLUseInfo info);
}
