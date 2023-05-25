package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.interfaces.subscribers.OnTryReducePowerSubscriber;
import pinacolada.resources.PGR;
import pinacolada.utilities.PCLRenderHelpers;

public class StabilizingPower extends PCLSubscribingPower implements OnTryReducePowerSubscriber {
    public static final String POWER_ID = PGR.core.createID(StabilizingPower.class.getSimpleName());
    protected AbstractPower originalPower;

    public StabilizingPower(AbstractCreature owner, AbstractPower originalPower) {
        super(owner, POWER_ID);
        this.originalPower = originalPower;
        this.img = originalPower.img;
        this.region48 = originalPower.region128;
    }

    public void atStartOfTurnPostDraw() {
        super.atStartOfTurnPostDraw();
        removePower();
    }

    @Override
    public boolean tryReducePower(AbstractPower power, AbstractCreature a, AbstractCreature b, AbstractGameAction c) {
        return power == null || !originalPower.ID.equals(power.ID);
    }

    @Override
    public String getUpdatedDescription() {
        if (originalPower == null) {
            return super.getUpdatedDescription();
        }
        return formatDescription(0, amount, originalPower.name);
    }


    @Override
    protected void renderIconsImpl(SpriteBatch sb, float x, float y, Color borderColor, Color imageColor) {
        PCLRenderHelpers.drawGrayscale(sb, (s) ->
                super.renderIconsImpl(s, x, y, borderColor, imageColor)
        );
    }
}
