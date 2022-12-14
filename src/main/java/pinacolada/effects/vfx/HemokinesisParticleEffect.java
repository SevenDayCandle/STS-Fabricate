package pinacolada.effects.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.combat.DamageImpactLineEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.utilities.GameEffects;

import java.util.ArrayList;

public class HemokinesisParticleEffect extends PCLEffect
{
    protected static final float MAX_VELOCITY = 4000f * Settings.scale;
    protected static final float VELOCITY_RAMP_RATE = 3000f * Settings.scale;
    protected static final float DST_THRESHOLD = 42f * Settings.scale;
    protected static final int TRAIL_ACCURACY = 60;

    protected final CatmullRomSpline<Vector2> crs = new CatmullRomSpline<>();
    protected final ArrayList<Vector2> controlPoints = new ArrayList<>();
    protected final Vector2[] points = new Vector2[60];
    protected AtlasRegion img;
    protected Vector2 pos;
    protected Vector2 target;
    protected float currentSpeed;
    protected float rotation;
    protected boolean rotateClockwise;
    protected boolean stopRotating;
    protected boolean facingLeft;
    protected float rotationRate;

    public HemokinesisParticleEffect(float sX, float sY, float tX, float tY, boolean facingLeft)
    {
        super(0.7f);

        this.img = ImageMaster.GLOW_SPARK_2;
        this.pos = new Vector2(sX, sY);
        if (!facingLeft)
        {
            this.target = new Vector2(tX + DST_THRESHOLD, tY);
        }
        else
        {
            this.target = new Vector2(tX - DST_THRESHOLD, tY);
        }

        this.facingLeft = facingLeft;
        this.crs.controlPoints = new Vector2[1];
        this.rotateClockwise = MathUtils.randomBoolean();
        this.rotation = (float) MathUtils.random(0, 359);
        this.controlPoints.clear();
        this.rotationRate = MathUtils.random(600f, 650f) * Settings.scale;
        this.currentSpeed = 1000f * Settings.scale;
        this.color = new Color(1f, 0f, 0.02f, 0.6f);
        this.scale = Settings.scale;
        this.renderBehind = MathUtils.randomBoolean();
    }

    public void render(SpriteBatch sb)
    {
        if (!this.isDone)
        {
            sb.setColor(Color.BLACK);
            float scaleCpy = this.scale;

            int i;
            for (i = this.points.length - 1; i > 0; --i)
            {
                if (this.points[i] != null)
                {
                    sb.draw(this.img, this.points[i].x - (float) (this.img.packedWidth / 2), this.points[i].y - (float) (this.img.packedHeight / 2), (float) this.img.packedWidth / 2f, (float) this.img.packedHeight / 2f, (float) this.img.packedWidth, (float) this.img.packedHeight, scaleCpy * 1.5f, scaleCpy * 1.5f, this.rotation);
                    scaleCpy *= 0.98f;
                }
            }

            sb.setBlendFunction(770, 1);
            sb.setColor(this.color);
            scaleCpy = this.scale;

            for (i = this.points.length - 1; i > 0; --i)
            {
                if (this.points[i] != null)
                {
                    sb.draw(this.img, this.points[i].x - (float) (this.img.packedWidth / 2), this.points[i].y - (float) (this.img.packedHeight / 2), (float) this.img.packedWidth / 2f, (float) this.img.packedHeight / 2f, (float) this.img.packedWidth, (float) this.img.packedHeight, scaleCpy, scaleCpy, this.rotation);
                    scaleCpy *= 0.98f;
                }
            }

            sb.setBlendFunction(770, 771);
        }
    }

    public void update()
    {
        this.updateMovement();
    }

    private void updateMovement()
    {
        Vector2 tmp = new Vector2(this.pos.x - this.target.x, this.pos.y - this.target.y);
        tmp.nor();
        float targetAngle = tmp.angle();
        this.rotationRate += Gdx.graphics.getDeltaTime() * 2000f;
        this.scale += Gdx.graphics.getDeltaTime() * 1f * Settings.scale;
        if (!this.stopRotating)
        {
            if (this.rotateClockwise)
            {
                this.rotation += Gdx.graphics.getDeltaTime() * this.rotationRate;
            }
            else
            {
                this.rotation -= Gdx.graphics.getDeltaTime() * this.rotationRate;
                if (this.rotation < 0f)
                {
                    this.rotation += 360f;
                }
            }

            this.rotation %= 360f;
            if (!this.stopRotating && Math.abs(this.rotation - targetAngle) < Gdx.graphics.getDeltaTime() * this.rotationRate)
            {
                this.rotation = targetAngle;
                this.stopRotating = true;
            }
        }

        tmp.setAngle(this.rotation);
        tmp.x *= Gdx.graphics.getDeltaTime() * this.currentSpeed;
        tmp.y *= Gdx.graphics.getDeltaTime() * this.currentSpeed;
        this.pos.sub(tmp);
        if (this.stopRotating)
        {
            this.currentSpeed += Gdx.graphics.getDeltaTime() * VELOCITY_RAMP_RATE * 3f;
        }
        else
        {
            this.currentSpeed += Gdx.graphics.getDeltaTime() * VELOCITY_RAMP_RATE * 1.5f;
        }

        if (this.currentSpeed > MAX_VELOCITY)
        {
            this.currentSpeed = MAX_VELOCITY;
        }

        if (this.target.dst(this.pos) < DST_THRESHOLD)
        {
            for (int i = 0; i < 5; ++i)
            {
                if (this.facingLeft)
                {
                    GameEffects.Queue.add(new DamageImpactLineEffect(this.target.x + DST_THRESHOLD, this.target.y));
                }
                else
                {
                    GameEffects.Queue.add(new DamageImpactLineEffect(this.target.x - DST_THRESHOLD, this.target.y));
                }
            }

            //CardCrawlGame.sound.playAV("BLUNT_HEAVY", MathUtils.random(0.6f, 0.9f), 0.5f);
            //CardCrawlGame.screenShake.shake(ShakeIntensity.MED, ShakeDur.SHORT, false);
            this.isDone = true;
        }

        if (!this.controlPoints.isEmpty())
        {
            if (!this.controlPoints.get(0).equals(this.pos))
            {
                this.controlPoints.add(this.pos.cpy());
            }
        }
        else
        {
            this.controlPoints.add(this.pos.cpy());
        }

        if (this.controlPoints.size() > 3)
        {
            Vector2[] vec2Array = new Vector2[0];
            this.crs.set(this.controlPoints.toArray(vec2Array), false);

            for (int i = 0; i < 60; ++i)
            {
                this.points[i] = new Vector2();
                this.crs.valueAt(this.points[i], (float) i / 59f);
            }
        }

        if (this.controlPoints.size() > 10)
        {
            this.controlPoints.remove(0);
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0f)
        {
            this.isDone = true;
        }

    }
}
