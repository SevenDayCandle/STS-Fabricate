package pinacolada.ui.customRun;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLCustomRunCharacterButton extends EUIButton
{

    protected static Color highlightColor = new Color(1.0F, 0.8F, 0.2F, 0.0F);
    public final PCLCustomRunScreen screen;
    public final CharacterOption character;
    public boolean glowing;

    public PCLCustomRunCharacterButton(PCLCustomRunScreen screen, AbstractPlayer p)
    {
        super(p.getCustomModeCharacterButtonImage(), new EUIHitbox(scale(100), scale(100)));
        // Textures are dummies; we just need the info
        this.character = new CharacterOption("", p, PCLCoreImages.CardFrames.cardBannerL.texture(), PCLCoreImages.CardFrames.cardBannerL.texture());
        this.screen = screen;
        setTooltip(!this.character.locked ? p.getLocalizedCharacterName() : CharacterOption.TEXT[0], "");
        setOnClick(() -> screen.setCharacter(this.character));

        // PCL icons use a customized custom character texture method
        if (GameUtilities.isPCLPlayerClass(p.chosenClass))
        {
            setButtonScale(0.7f, 0.7f);
        }
    }

    // This hardcoding is killing me...
    public static String getCharacterUnlockID(AbstractPlayer.PlayerClass p)
    {
        switch (p)
        {
            case THE_SILENT:
                return "The Silent";
            case DEFECT:
                return "Defect";
            case WATCHER:
                return "Watcher";
        }
        return "";
    }

    public void renderCentered(SpriteBatch sb)
    {
        if (glowing)
        {
            highlightColor.a = 0.25F + (MathUtils.cosDeg((float) (System.currentTimeMillis() / 4L % 360L)) + 1.25F) / 3.5F;
            PCLRenderHelpers.drawCentered(sb, highlightColor, ImageMaster.FILTER_GLOW_BG, this.hb.cX, this.hb.cY, this.hb.width, this.hb.height, this.background.scaleX, 0);
        }
        super.renderCentered(sb);
    }
}
