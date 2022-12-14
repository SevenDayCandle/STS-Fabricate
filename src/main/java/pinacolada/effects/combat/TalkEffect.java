package pinacolada.effects.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.ui.DialogWord;
import com.megacrit.cardcrawl.vfx.SpeechTextEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.utilities.GameEffects;

public class TalkEffect extends PCLEffect
{
    private static final float SHADOW_OFFSET = 16f * Settings.scale;
    private static final float WAVY_DISTANCE = 2f * Settings.scale;
    private static final float ADJUST_X = 170f * Settings.scale;
    private static final float ADJUST_Y = 116f * Settings.scale;

    private DialogWord.AppearEffect appearEffect;
    private String message;
    private float shadowOffset;
    private float sourceX;
    private float sourceY;
    private float wavyY;
    private float wavyHelper;
    private float scaleTimer;
    private boolean facingRight;
    private Color shadowColor;

    public static float getDialogX(AbstractCreature creature)
    {
        return creature.isPlayer ? creature.dialogX : creature.hb.cX + creature.dialogX;
    }

    public static float getDialogY(AbstractCreature creature)
    {
        return creature.isPlayer ? creature.dialogY : creature.hb.cY + creature.dialogY;
    }

    public TalkEffect(AbstractCreature source, String message)
    {
        this(source, message, 2f);
    }

    public TalkEffect(AbstractCreature source, String message, float duration)
    {
        this(getDialogX(source), getDialogY(source), message, source.isPlayer);
    }

    public TalkEffect(float x, float y, String message, boolean isPlayer)
    {
        this(x, y, message, isPlayer, 2f);
    }

    public TalkEffect(float x, float y, String text, boolean isPlayer, float duration)
    {
        super(duration);

        message = text;
        sourceX = x + (isPlayer ? ADJUST_X : -ADJUST_X);
        sourceY = y + ADJUST_Y;

        isRealtime = true;
        shadowOffset = 0f;
        scaleTimer = 0.3f;
        shadowColor = new Color(0f, 0f, 0f, 0f);
        color = new Color(0.8f, 0.9f, 0.9f, 0f);
        facingRight = !isPlayer;
    }

    public TalkEffect setEffect(DialogWord.AppearEffect appearEffect)
    {
        this.appearEffect = appearEffect;

        return this;
    }

    @Override
    protected void firstUpdate()
    {
        if (appearEffect == null)
        {
            appearEffect = DialogWord.AppearEffect.BUMP_IN;
        }

        (GameEffects.TopLevelList.getList().contains(this) ? GameEffects.TopLevelQueue : GameEffects.Queue)
        .add(new SpeechTextEffect(sourceX, sourceY, duration, message, appearEffect));
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        super.updateInternal(deltaTime);

        scaleTimer -= deltaTime;
        if (scaleTimer < 0f)
        {
            scaleTimer = 0f;
        }

        scale = Interpolation.swingIn.apply(Settings.scale, Settings.scale / 2f, scaleTimer / 0.3f);
        wavyHelper += deltaTime * 4f;
        shadowOffset = MathUtils.lerp(shadowOffset, SHADOW_OFFSET, deltaTime * 4f);
        wavyY = MathUtils.sin(wavyHelper) * WAVY_DISTANCE;

        if (duration > 0.3f)
        {
            color.a = MathUtils.lerp(color.a, 1f, deltaTime * 12f);
        }
        else
        {
            color.a = MathUtils.lerp(color.a, 0f, deltaTime * 12f);
        }
    }

    @Override
    public void render(SpriteBatch sb)
    {
        final int size = 512;
        final int half = 256;

        shadowColor.a = color.a / 4f;

        sb.setColor(shadowColor);
        sb.draw(ImageMaster.SPEECH_BUBBLE_IMG, sourceX - half + shadowOffset, sourceY - half + wavyY - shadowOffset, half, half,
                size, size, scale, scale, rotation, 0, 0, size, size, facingRight, false);

        sb.setColor(color);
        sb.draw(ImageMaster.SPEECH_BUBBLE_IMG, sourceX - half, sourceY - half + wavyY, half, half, size, size, scale, scale,
                rotation, 0, 0, size, size, facingRight, false);
    }
}