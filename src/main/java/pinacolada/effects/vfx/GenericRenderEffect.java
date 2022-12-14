package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pinacolada.effects.PCLEffect;
import pinacolada.utilities.PCLRenderHelpers;

public class GenericRenderEffect extends PCLEffect
{
    public final TextureRegion image;
    private final float x;
    private final float y;
    private boolean flipX;
    private boolean flipY;
    private float rotationSpeed;

    public GenericRenderEffect(Texture texture, float x, float y)
    {
        this(new TextureRegion(texture), x, y);
    }

    public GenericRenderEffect(TextureRegion region, float x, float y)
    {
        super(0.6f, true);

        this.image = region;
        this.color = Color.WHITE.cpy();
        this.scale = 1;
        this.x = x;
        this.y = y;

        if (image == null)
        {
            complete();
        }
    }

    public GenericRenderEffect flip(boolean horizontally, boolean vertically)
    {
        this.flipX = horizontally;
        this.flipY = vertically;

        return this;
    }

    public GenericRenderEffect setRotation(float degrees)
    {
        super.setRotation(degrees);

        return this;
    }

    @Override
    public void render(SpriteBatch sb)
    {
        if (this.image != null)
        {
            PCLRenderHelpers.drawCentered(sb, color, image, x, y, image.getRegionWidth(), image.getRegionHeight(), scale, rotation, flipX, flipY);
        }
    }

    @Override
    public void dispose()
    {
    }

    @Override
    protected void firstUpdate()
    {
        updateInternal(getDeltaTime());
    }
}
