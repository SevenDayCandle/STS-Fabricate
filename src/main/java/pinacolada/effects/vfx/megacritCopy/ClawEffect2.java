package pinacolada.effects.vfx.megacritCopy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.AnimatedSlashEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;

@Deprecated
public class ClawEffect2 extends PCLEffect {
    private final float x;
    private final float y;
    private Color color2;
    private boolean flipX;
    private boolean playSFX;

    public ClawEffect2(float x, float y, Color color1, Color color2) {
        super(0.1f, false);

        this.x = x;
        this.y = y;
        this.playSFX = true;

        setColors(color1, color2);
    }

    public ClawEffect2 setColors(Color color1, Color color2) {
        this.color = color1.cpy();
        this.color2 = color2.cpy();

        return this;
    }

    public ClawEffect2 flipX(boolean flipX) {
        this.flipX = flipX;

        return this;
    }

    public ClawEffect2 playSFX(boolean playSFX) {
        this.playSFX = playSFX;

        return this;
    }

    public ClawEffect2 setScale(float scale) {
        this.scale = scale;

        return this;
    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }

    @Override
    protected void firstUpdate() {
        if (playSFX) {
            SFX.play(randomBoolean() ? SFX.ATTACK_DAGGER_5 : SFX.ATTACK_DAGGER_6, 0.7f / scale, 1f / scale);
        }

        float angle = random(115f, 155f);
        float offset = 35 * scale * Settings.scale;
        if (flipX) {
            slash(this.x - offset, this.y + offset, -150.0F, -150.0F, angle);
            slash(this.x, this.y, 150.0F, -150.0F, angle);
            slash(this.x + offset, this.y - offset, -150.0F, -150.0F, angle);
        }
        else {
            slash(this.x + offset, this.y + offset, 150.0F, -150.0F, -angle);
            slash(this.x, this.y, 150.0F, -150.0F, -angle);
            slash(this.x - offset, this.y - offset, 150.0F, -150.0F, -angle);
        }

        complete();
    }

    protected void slash(float x, float y, float dX, float dY, float angle) {
        PCLEffects.Queue.add(new AnimatedSlashEffect(x, y, dX, dY, angle, scale * 2f, this.color, this.color2));
    }
}
