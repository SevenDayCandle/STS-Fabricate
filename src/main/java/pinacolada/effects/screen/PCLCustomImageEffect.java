package pinacolada.effects.screen;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIVerticalScrollBar;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;

import static extendedui.ui.controls.EUIButton.createHexagonalButton;

public class PCLCustomImageEffect extends PCLEffectWithCallback<Pixmap> {
    private static final FileNameExtensionFilter EXTENSIONS = new FileNameExtensionFilter("Image files (*.png, *.bmp, *.jpg, *.jpeg)", "png", "bmp", "jpg", "jpeg");
    public static final int CARD_IMG_WIDTH = 500;
    public static final int CARD_IMG_HEIGHT = 380;
    public static final int RELIC_IMG_SIZE = 128;
    private final DraggableHitbox hb;
    private final EUILabel instructionsLabel;
    private final EUIButton cancelButton;
    private final EUIButton loadButton;
    private final EUIButton pasteButton;
    private final EUIButton saveButton;
    private final EUIButton selectExistingButton;
    private final EUIVerticalScrollBar zoomBar;
    private final SpriteBatch sb;
    private final FrameBuffer imageBuffer;
    private final OrthographicCamera camera;
    private Pixmap insideImage;
    private Texture baseTexture;
    private TextureRegion insideImageRenderable;
    private TextureRegion outsideImage;
    private PCLGenericSelectCardEffect existingCardSelection;
    protected float minZoom;
    protected float maxZoom = 1f;
    protected float scale = 1f;
    protected int targetWidth = CARD_IMG_WIDTH;
    protected int targetHeight = CARD_IMG_HEIGHT;

    public static PCLCustomImageEffect forCard(Texture texture)
    {
        return new PCLCustomImageEffect(texture, CARD_IMG_WIDTH, CARD_IMG_HEIGHT);
    }

    public static PCLCustomImageEffect forRelic(Texture texture)
    {
        return new PCLCustomImageEffect(texture, RELIC_IMG_SIZE, RELIC_IMG_SIZE);
    }

    public PCLCustomImageEffect(Texture base, int imageWidth, int imageHeight) {
        final float buttonHeight = Settings.HEIGHT * (0.055f);
        final float labelHeight = Settings.HEIGHT * (0.04f);
        final float buttonWidth = Settings.WIDTH * (0.16f);
        final float labelWidth = Settings.WIDTH * (0.20f);
        final float button_cY = buttonHeight * 1.5f;

        hb = new DraggableHitbox(0, 0, Settings.WIDTH * 2, Settings.HEIGHT * 2, true);
        targetWidth = imageWidth;
        targetHeight = imageHeight;

        instructionsLabel = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(Settings.WIDTH * 0.35f, Settings.HEIGHT * 0.1f, buttonWidth * 2f, buttonHeight))
                .setAlignment(0.5f, 0f, true)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.8f)
                .setLabel(PGR.core.strings.cetut_imageSelect);

        cancelButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(buttonWidth * 0.6f, button_cY)
                .setColor(Color.FIREBRICK)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, GridCardSelectScreen.TEXT[1])
                .setOnClick((ActionT0) this::complete);

        saveButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.FOREST)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, GridCardSelectScreen.TEXT[0])
                .setInteractable(false)
                .setOnClick(this::commit);

        pasteButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, saveButton.hb.y + saveButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, PGR.core.strings.cedit_paste)
                .setOnClick(this::getImageFromClipboard);

        selectExistingButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, pasteButton.hb.y + pasteButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, PGR.core.strings.cedit_loadFromCard)
                .setOnClick(this::selectExistingCards);

        loadButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, selectExistingButton.hb.y + selectExistingButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, PGR.core.strings.cedit_loadFile)
                .setOnClick(this::getImageFromFileDialog);


        zoomBar = new EUIVerticalScrollBar(new EUIHitbox(Settings.WIDTH * 0.03f, Settings.HEIGHT * 0.7f))
                .setPosition(Settings.WIDTH * 0.9f, Settings.HEIGHT * 0.5f)
                .setOnScroll(this::updateZoom);
        zoomBar.setActive(false);

        imageBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, false);
        camera = new OrthographicCamera((float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        sb = new SpriteBatch();
        sb.setProjectionMatrix(camera.combined.scl(1, -1, 1));
    }

    protected void commit() {
        if (baseTexture == null) {
            complete(null);
        }
        else {
            updateBuffer(true);
            complete(insideImage);
        }
    }

    public void complete(Pixmap pixmap) {
        super.complete(pixmap);
        if (pixmap == null) {
            if (insideImage != null) {
                try {
                    insideImage.dispose();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void complete() {
        super.complete();
        // Ensure textures are unloaded to avoid memory leaks
        if (baseTexture != null) {
            baseTexture.dispose();
        }
        if (insideImageRenderable != null) {
            insideImageRenderable.getTexture().dispose();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (existingCardSelection != null) {
            existingCardSelection.render(sb);
        }
        else {
            hb.render(sb);
            cancelButton.tryRender(sb);
            saveButton.tryRender(sb);
            loadButton.tryRender(sb);
            selectExistingButton.tryRender(sb);
            pasteButton.tryRender(sb);
            instructionsLabel.tryRender(sb);
            zoomBar.tryRender(sb);

            if (outsideImage != null) {
                PCLRenderHelpers.drawCentered(sb, Color.GRAY.cpy(), outsideImage, Settings.WIDTH / 2f, Settings.HEIGHT / 2f, outsideImage.getRegionWidth(), outsideImage.getRegionHeight(), 1, 0);
            }
            if (insideImageRenderable != null) {
                PCLRenderHelpers.drawCentered(sb, Color.WHITE.cpy(), insideImageRenderable, Settings.WIDTH / 2f, Settings.HEIGHT / 2f, insideImageRenderable.getRegionWidth(), insideImageRenderable.getRegionHeight(), 1, 0);
            }
        }
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (existingCardSelection != null) {
            existingCardSelection.update();
            if (existingCardSelection.isDone) {
                existingCardSelection = null;
            }
        }
        else {
            cancelButton.tryUpdate();
            saveButton.tryUpdate();
            loadButton.tryUpdate();
            selectExistingButton.tryUpdate();
            pasteButton.tryUpdate();
            instructionsLabel.tryUpdate();
            camera.update();
            if (baseTexture != null) {
                if (!hb.isDragging()) {
                    zoomBar.tryUpdate();
                }
                if (!zoomBar.isDragging) {
                    hb.update();
                    if (hb.isDragging()) {
                        updatePictures();
                    }
                }
            }

            // TODO see if there is a way to check for the "paste" function for different operating systems
            if ((Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) && Gdx.input.isKeyJustPressed(Input.Keys.V)) {
                getImageFromClipboard();
            }
        }
    }

    private void getImageFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                BufferedImage image = ((BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor));
                updateImage(new Texture(PCLRenderHelpers.getPixmapFromBufferedImage(image), true));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getImageFromFileDialog() {
        try {
            File openedFile = EUIUtils.chooseFile(EXTENSIONS, PGR.config.lastImagePath);
            updateImage(new Texture(new FileHandle(openedFile), true));
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(this, "Failed to load card image.");
        }
    }

    private void selectExistingCards() {
        CardGroup group = GameUtilities.createCardGroup(CardLibrary.getAllCards());
        group.sortAlphabetically(true);
        existingCardSelection = (PCLGenericSelectCardEffect) new PCLGenericSelectCardEffect(group)
                .addCallback(card -> {
                            if (card != null) {
                                // TODO handle EYBCardBase with PCLCard check
                                updateImage(
                                        card instanceof PCLCard ? new Texture(Gdx.files.internal(card.assetUrl), true) :
                                                card instanceof CustomCard ? CustomCard.getPortraitImage((CustomCard) card)
                                                        : new Texture(Gdx.files.internal(GameUtilities.toInternalAtlasPath(card.assetUrl)), true));
                            }
                        }
                );
    }

    private void updateBuffer(boolean forCommit) {
        imageBuffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        sb.begin();

        if (forCommit) {
            // Generate a temporary resized image and capture it into the framebuffer
            Texture resized = new Texture(PCLRenderHelpers.scalrScaleAsPixmap(baseTexture, scale, scale));
            sb.draw(resized, hb.x - resized.getWidth() / 2f, hb.y - resized.getHeight() / 2f, resized.getWidth() / 2f, resized.getHeight() / 2f, resized.getWidth(), resized.getHeight(), 1f, 1f, 0f, 0, 0, resized.getWidth(), resized.getHeight(), false, false);
            updateBufferEnding();
            resized.dispose();
        }
        else {
            sb.draw(baseTexture, hb.x - baseTexture.getWidth() / 2f, hb.y - baseTexture.getHeight() / 2f, baseTexture.getWidth() / 2f, baseTexture.getHeight() / 2f, baseTexture.getWidth(), baseTexture.getHeight(), scale, scale, 0f, 0, 0, baseTexture.getWidth(), baseTexture.getHeight(), false, false);
            updateBufferEnding();
        }
    }

    private void updateBufferEnding() {
        sb.end();
        // Dispose the existing pixmap
        if (insideImage != null) {
            try {
                insideImage.dispose();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        insideImage = ScreenUtils.getFrameBufferPixmap((Settings.WIDTH - targetWidth) / 2, (Settings.HEIGHT - targetHeight) / 2, targetWidth, targetHeight);
        imageBuffer.end();
    }

    // Update the images shown on the screen. Textures are explicitly not saved with EUI because we don't want to keep them after this effect is over
    private void updateImage(Texture texture) {
        if (texture != null) {
            // Flush the existing texture before dropping it
            if (baseTexture != null) {
                baseTexture.dispose();
            }

            baseTexture = texture;
            hb.setCenter(Settings.WIDTH, Settings.HEIGHT);
            //hb.SetBounds(texture.getWidth() * Settings.scale * 0.5f, texture.getWidth() * Settings.scale * 1.5f, texture.getWidth() * Settings.scale * -0.5f, texture.getHeight() * Settings.scale);

            baseTexture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
            minZoom = Math.max(targetWidth / baseTexture.getWidth(), targetHeight / baseTexture.getHeight());
            zoomBar.setActive(true);
            updateZoom(1f);
            instructionsLabel.setLabel(PGR.core.strings.cetut_imageCrop);
            saveButton.setInteractable(true);
        }
    }

    private void updatePictures() {
        updateBuffer(false);

        // Dispose the inner image texture. Outside image texture is handled by the image buffer
        if (insideImageRenderable != null) {
            insideImageRenderable.getTexture().dispose();
        }

        // Textures are explicitly not saved with EUI because we don't want to keep them after this effect is over
        outsideImage = new TextureRegion(imageBuffer.getColorBufferTexture(), Settings.WIDTH / 3, Settings.HEIGHT / 4, Settings.WIDTH / 3, Settings.HEIGHT / 2);
        insideImageRenderable = new TextureRegion(new Texture(insideImage));
        outsideImage.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        insideImageRenderable.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private void updateZoom(float scrollPercentage) {
        zoomBar.scroll(scrollPercentage, false);
        scale = MathUtils.lerp(minZoom, maxZoom, scrollPercentage);

        updatePictures();
    }
}
