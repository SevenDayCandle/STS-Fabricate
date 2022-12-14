package pinacolada.effects.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

public class ShuffleEnemiesEffect extends AbstractGameEffect
{
    private AbstractMonster enemy1 = null;
    private AbstractMonster enemy2 = null;

    private float drawX1;
    private float drawY1;
    private float drawX2;
    private float drawY2;

    public ShuffleEnemiesEffect()
    {
        this.duration = 1f;
        this.isDone = false;
    }

    public void update()
    {
        if (enemy1 == null)
        {
            RandomizedList<AbstractMonster> enemies = new RandomizedList<>(GameUtilities.getEnemies(true));
            if (enemies.size() > 1)
            {
                enemy1 = enemies.retrieve(AbstractDungeon.miscRng);
                enemy2 = enemies.retrieve(AbstractDungeon.miscRng);

                drawX1 = enemy1.drawX;
                drawY1 = enemy1.drawY;

                drawX2 = enemy2.drawX;
                drawY2 = enemy2.drawY;

                if (Math.abs(drawY1 - drawY2) > 30)
                {
                    this.isDone = true;
                    return;
                }
            }
            else
            {
                this.isDone = true;
            }

            return;
        }

        boolean targetReached = true;
        float delta = Gdx.graphics.getDeltaTime() * 500;

        if (enemy1.drawY != drawY2)
        {
            enemy1.drawY = translate(enemy1.drawY, drawY2, delta);
            targetReached = false;
        }

        if (enemy1.drawX != drawX2)
        {
            enemy1.drawX = translate(enemy1.drawX, drawX2, delta);
            targetReached = false;
        }

        if (enemy2.drawY != drawY1)
        {
            enemy2.drawY = translate(enemy2.drawY, drawY1, delta);
            targetReached = false;
        }

        if (enemy2.drawX != drawX1)
        {
            enemy2.drawX = translate(enemy2.drawX, drawX1, delta);
            targetReached = false;
        }

        if (targetReached)
        {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) { }

    public void dispose()
    {

    }

    private float translate(float point1, float point2, float alpha)
    {
        if (point1 <= point2)
        {
            float result = point1 + alpha;

            return result > point2 ? point2 : result;
        }
        else
        {
            float result = point1 - alpha;

            return result < point2 ? point2 : result;
        }
    }
}