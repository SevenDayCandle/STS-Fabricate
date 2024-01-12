package pinacolada.effects.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pinacolada.utilities.GameUtilities;

public class SpawnRelicEffect extends AbstractGameEffect {
    private final AbstractRelic relic;
    private final float x;
    private final float y;

    public SpawnRelicEffect(AbstractRelic relic, float x, float y) {
        this.duration = this.startingDuration = 1f;
        this.relic = relic;
        this.x = x;
        this.y = y;
    }

    public void dispose() {

    }

    public void render(SpriteBatch sb) {

    }

    public void update() {
        if (!this.isDone) {
            AbstractRoom r = GameUtilities.getCurrentRoom();
            if (r != null) {
                r.spawnRelicAndObtain(x, y, relic);
            }
        }

        this.isDone = true;
    }
}