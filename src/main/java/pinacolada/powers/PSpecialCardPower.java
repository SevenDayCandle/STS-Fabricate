package pinacolada.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.cards.base.PCLCardData;
import pinacolada.skills.PSkill;

public class PSpecialCardPower extends PCLSubscribingPower {
    protected PSkill<?> move;

    public PSpecialCardPower(PCLCardData data, AbstractCreature owner, PSkill<?> move) {
        super(owner, data);
        this.move = move;
    }

    @Override
    public String getUpdatedDescription() {
        return move != null ? move.getPowerText() : "";
    }
}
