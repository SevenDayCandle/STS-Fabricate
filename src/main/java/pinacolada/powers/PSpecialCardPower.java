package pinacolada.powers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.cards.base.PCLCardData;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.subscribers.PCLCombatSubscriber;
import pinacolada.skills.PSkill;
import pinacolada.utilities.PCLRenderHelpers;

public abstract class PSpecialCardPower extends PCLClickablePower implements PCLCombatSubscriber {
    protected PSkill<?> move;

    public PSpecialCardPower(PCLPowerData data, AbstractCreature owner, AbstractCreature source, PSkill<?> move) {
        super(data, owner, source, move.amount);
        this.move = move;
        setupImageRegion();
        setupDescription();
    }

    public static PCLPowerData createFromCard(Class<? extends AbstractPower> powerClass, PCLCardData cardData) {
        PCLPowerData data = new PCLPowerData(powerClass, cardData.resources, deriveID(cardData.ID), new PowerStrings());
        data.type = PowerType.BUFF;
        data.strings.NAME = cardData.strings.NAME;
        data.strings.DESCRIPTIONS = cardData.strings.EXTENDED_DESCRIPTION;
        return data;
    }

    @Override
    public String getUpdatedDescription() {
        return move != null ? move.getPowerText() : "";
    }

    public void onInitialApplication() {
        super.onInitialApplication();
        powerSubscribeTo();
    }

    public void onRemove() {
        super.onRemove();
        unsubscribeFromAll();
    }

    // Override this if you do not want automatic subscription on your power
    public void powerSubscribeTo() {
        subscribeToAll();
    }

    protected void setupImageRegion() {
        if (move != null && move.sourceCard instanceof EditorCard) {
            Texture portraitTexture = ((EditorCard) move.sourceCard).getPortraitImageTexture();
            this.region128 = this.region48 = PCLRenderHelpers.generateIcon(portraitTexture);
        }
    }
}
