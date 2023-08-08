package pinacolada.effects.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.pcl.PCLCoreImages;

public class BleedParticleEffect extends PCLEffect {
    protected static final Texture image = PCLCoreImages.Effects.droplet.texture();
    protected static final int SIZE = 72;
    protected static final float INTERVAL = 0.01F;

    protected float x;
    protected float vX;
    protected float y;
    protected float vY;
    protected float gravity;
    protected float vR;
    protected float floor;
    protected float smokeTimer = 0.0F;
    protected boolean enableFloor = true;

    public BleedParticleEffect(float x, float y) {
        this.x = x;
        this.y = y;
        this.vX = MathUtils.random(-600.0F, 600.0F) * Settings.scale;
        this.vY = MathUtils.random(-200.0F, 600.0F) * Settings.scale;
        this.gravity = 800.0F * Settings.scale;
        this.scale = MathUtils.random(0.2F, 0.4F) * Settings.scale;
        this.rotation = random(-10f, 10f);
        this.vR = random(-600f, 600f);
        this.floor = random(100f, 250f) * Settings.scale;
        this.duration = MathUtils.random(0.3F, 0.6F);

        this.color = new Color(1.0F, 0.1F, MathUtils.random(0.02F, 0.4F), 1.0F);
    }

    public BleedParticleEffect disableFloor() {
        enableFloor = false;
        return this;
    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        sb.setColor(this.color);
        sb.draw(image, x, y, SIZE * 0.5f, SIZE * 0.5f, SIZE, SIZE, scale, scale, rotation, 0, 0, SIZE, SIZE, false, false);
        sb.setColor(Color.WHITE);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void dispose() {
    }

    @Override
    protected void updateInternal(float deltaTime) {
        this.x += this.vX * Gdx.graphics.getDeltaTime();
        this.y += this.vY * Gdx.graphics.getDeltaTime();
        this.vY -= this.gravity * Gdx.graphics.getDeltaTime();
        this.rotation += vR * deltaTime;

        if (enableFloor && y < floor) {
            vY = -vY * 0.5f;
            y = floor + 0.1f;
            vX *= 1.1f;
        }


        this.smokeTimer -= Gdx.graphics.getDeltaTime();
        if (this.smokeTimer < 0.0F) {
            this.smokeTimer = 0.01F;
            PCLEffects.Queue.particle(image, this.x, this.y)
                    .setColor(new Color(1.0F, 0.1F, MathUtils.random(0.02F, 0.4F), 1.0F))
                    .setScale(scale * MathUtils.random(0.45f, 1f)).setRotation(36000f, MathUtils.random(300f, 500f))
                    .setTargetPosition(this.x + MathUtils.random(-500f, 500f), this.y + MathUtils.random(-500f, 500f))
                    .setDuration(MathUtils.random(0.14F, 0.18F), true);
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }

        if ((1f - duration) < 0.1f) {
            color.a = Interpolation.fade.apply(0f, 1f, (1f - duration) * 10f);
        }
        else {
            color.a = Interpolation.pow2Out.apply(0f, 1f, duration);
        }
    }
}
