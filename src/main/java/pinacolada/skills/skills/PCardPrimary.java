package pinacolada.skills.skills;

import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.skills.*;
import pinacolada.skills.fields.PField;

public abstract class PCardPrimary<T extends PField> extends PPrimary<T> {
    public PCardPrimary(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PCardPrimary(PSkillData<T> data) {
        super(data);
    }

    public PCardPrimary(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PCardPrimary(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    public PCardPrimary(PSkillData<T> data, EditorCard card) {
        super(data);
        setProvider(card);
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return (skill instanceof PCond && !(skill instanceof PActiveNonCheckCond))
                || (skill instanceof PMod);
    }

    // Only set the amount for this effect and not its children. Used in refresh to avoid refreshing chained children twice
    public PCardPrimary<T> setAmountFromCardForUpdateOnly() {
        this.amount = getAmountFromCard();
        this.baseAmount = getAmountBaseFromCard();
        this.extra = getExtraFromCard();
        this.baseExtra = getExtraBaseFromCard();
        return this;
    }

    // We want to execute children first because they affect card output, but we want the PCardPrimary to be the parent in order to ensure type safety in the skills object
    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        super.use(info, order);
        useImpl(info, order);
    }

    // Obtains the target and numerical values from the given card
    public abstract PCardPrimary<T> setProvider(EditorCard card);

    protected abstract void useImpl(PCLUseInfo info, PCLActions order);
}
