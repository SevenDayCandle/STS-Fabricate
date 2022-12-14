package pinacolada.effects.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class SpawnRelicEffect extends AbstractGameEffect
{
    private final AbstractRelic relic;
    private final float x;
    private final float y;

    public SpawnRelicEffect(AbstractRelic relic, float x, float y)
    {
        this.duration = this.startingDuration = 1f;
        this.relic = relic;
        this.x = x;
        this.y = y;
    }

    public void update()
    {
        if (!this.isDone)
        {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(x, y, relic);
        }

        this.isDone = true;
    }

    public void render(SpriteBatch sb)
    {

    }

    public void dispose()
    {

    }
}