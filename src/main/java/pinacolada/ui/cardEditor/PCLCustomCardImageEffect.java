package pinacolada.ui.cardEditor;

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
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIVerticalScrollBar;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCardBuilder;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;
import pinacolada.utilities.PCLRenderHelpers;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static extendedui.ui.AbstractScreen.createHexagonalButton;

public class PCLCustomCardImageEffect extends PCLEffectWithCallback<Pixmap>
{

    protected static final int IMG_WIDTH = 500;
    protected static final int IMG_HEIGHT = 380;
    private static final FileNameExtensionFilter EXTENSIONS = new FileNameExtensionFilter("Image files (*.png, *.bmp, *.jpg, *.jpeg)", "png", "bmp", "jpg", "jpeg");
    protected DraggableHitbox hb;
    protected float minZoom;
    protected float maxZoom = 1f;
    protected float scale = 1f;
    protected FrameBuffer imageBuffer;
    protected EUILabel instructionsLabel;
    protected EUIButton cancelButton;
    protected EUIButton loadButton;
    protected EUIButton pasteButton;
    protected EUIButton saveButton;
    protected EUIVerticalScrollBar zoomBar;
    protected Pixmap insideImage;
    protected Texture baseTexture;
    protected TextureRegion insideImageRenderable;
    protected TextureRegion outsideImage;
    protected SpriteBatch sb;
    protected OrthographicCamera camera;

    public PCLCustomCardImageEffect(PCLCardBuilder builder)
    {
        final float buttonHeight = Settings.HEIGHT * (0.055f);
        final float labelHeight = Settings.HEIGHT * (0.04f);
        final float buttonWidth = Settings.WIDTH * (0.16f);
        final float labelWidth = Settings.WIDTH * (0.20f);
        final float button_cY = buttonHeight * 1.5f;

        hb = new DraggableHitbox(0, 0, Settings.WIDTH * 2, Settings.HEIGHT * 2, true);

        instructionsLabel = new EUILabel(EUIFontHelper.cardtitlefontSmall,
                new EUIHitbox(Settings.WIDTH * 0.35f, Settings.HEIGHT * 0.1f, buttonWidth * 2f, buttonHeight))
                .setAlignment(0.5f, 0f, true)
                .setFont(EUIFontHelper.cardtitlefontSmall, 0.8f)
                .setLabel(PGR.core.strings.cardEditorTutorial.imageSelect);

        cancelButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(buttonWidth * 0.6f, button_cY)
                .setColor(Color.FIREBRICK)
                .setText(GridCardSelectScreen.TEXT[1])
                .setFont(FontHelper.buttonLabelFont, 0.85f)
                .setOnClick((ActionT0) this::complete);

        saveButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, cancelButton.hb.y + cancelButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.FOREST)
                .setText(GridCardSelectScreen.TEXT[0])
                .setFont(FontHelper.buttonLabelFont, 0.85f)
                .setInteractable(false)
                .setOnClick(this::commit);

        loadButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, saveButton.hb.y + saveButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cardEditor.loadFile)
                .setFont(FontHelper.buttonLabelFont, 0.85f)
                .setOnClick(this::getImageFromFileDialog);

        pasteButton = createHexagonalButton(0, 0, buttonWidth, buttonHeight)
                .setPosition(cancelButton.hb.cX, loadButton.hb.y + loadButton.hb.height + labelHeight * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cardEditor.paste)
                .setFont(FontHelper.buttonLabelFont, 0.85f)
                .setOnClick(this::getImageFromClipboard);

        zoomBar = new EUIVerticalScrollBar(new EUIHitbox(Settings.WIDTH * 0.03f, Settings.HEIGHT * 0.7f))
                .setPosition(Settings.WIDTH * 0.9f, Settings.HEIGHT * 0.5f)
                .setOnScroll(this::updateZoom);
        zoomBar.setActive(false);

        imageBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, false);
        camera = new OrthographicCamera((float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        sb = new SpriteBatch();
        sb.setProjectionMatrix(camera.combined.scl(1, -1, 1));
    }

    protected void commit()
    {
        if (baseTexture == null)
        {
            complete();
        }
        else
        {
            updateBuffer(true);
            complete(insideImage);
        }
    }

    protected void getImageFromClipboard()
    {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor))
        {
            try
            {
                BufferedImage image = ((BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor));
                updateImage(new Texture(PCLRenderHelpers.getPixmapFromBufferedImage(image), true));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void getImageFromFileDialog()
    {
        try
        {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(EXTENSIONS);
            fc.setDropTarget(new DropTarget()
            {
                public synchronized void drop(DropTargetDropEvent evt)
                {
                    try
                    {
                        evt.acceptDrop(DnDConstants.ACTION_COPY);
                        Transferable t = evt.getTransferable();
                        if (t != null && t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
                        {
                            List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                            for (File file : droppedFiles)
                            {
                                fc.setSelectedFiles(droppedFiles.toArray(new File[]{}));
                            }
                            evt.dropComplete(true);
                        }
                        else
                        {
                            evt.dropComplete(false);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        evt.dropComplete(false);
                    }
                }
            });

            File cd = new File(PGR.core.config.lastImagePath.get());
            if (cd.isDirectory())
            {
                fc.setCurrentDirectory(cd);
            }

            JFrame f = new JFrame();
            f.toFront();
            f.setAlwaysOnTop(true);
            f.setLocationRelativeTo(null);
            f.setPreferredSize(new Dimension(Settings.WIDTH / 2, Settings.HEIGHT / 2));
            f.setVisible(true);

            int result = fc.showOpenDialog(f);
            f.setVisible(false);
            f.dispose();

            cd = fc.getCurrentDirectory();
            if (cd != null && cd.isDirectory())
            {
                PGR.core.config.lastImagePath.set(cd.getAbsolutePath(), true);
            }

            if (result == JFileChooser.APPROVE_OPTION)
            {
                File openedFile = fc.getSelectedFile();
                updateImage(new Texture(new FileHandle(openedFile), true));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EUIUtils.logError(this, "Failed to load card image.");
        }
    }

    protected void updateBuffer(boolean forCommit)
    {
        imageBuffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        sb.begin();

        if (forCommit)
        {
            Texture resized = new Texture(PCLRenderHelpers.scalrScaleAsPixmap(baseTexture, scale, scale));
            sb.draw(resized, hb.x - resized.getWidth() / 2, hb.y - resized.getHeight() / 2, resized.getWidth() / 2, resized.getHeight() / 2, resized.getWidth(), resized.getHeight(), 1f, 1f, 0f, 0, 0, resized.getWidth(), resized.getHeight(), false, false);
        }
        else
        {
            sb.draw(baseTexture, hb.x - baseTexture.getWidth() / 2, hb.y - baseTexture.getHeight() / 2, baseTexture.getWidth() / 2, baseTexture.getHeight() / 2, baseTexture.getWidth(), baseTexture.getHeight(), scale, scale, 0f, 0, 0, baseTexture.getWidth(), baseTexture.getHeight(), false, false);
        }

        sb.end();
        insideImage = ScreenUtils.getFrameBufferPixmap((Settings.WIDTH - IMG_WIDTH) / 2, (Settings.HEIGHT - IMG_HEIGHT) / 2, IMG_WIDTH, IMG_HEIGHT);
        imageBuffer.end();
    }

    protected void updateImage(Texture texture)
    {
        if (texture != null)
        {
            baseTexture = texture;
            hb.setCenter(Settings.WIDTH, Settings.HEIGHT);
            //hb.SetBounds(texture.getWidth() * Settings.scale * 0.5f, texture.getWidth() * Settings.scale * 1.5f, texture.getWidth() * Settings.scale * -0.5f, texture.getHeight() * Settings.scale);

            baseTexture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
            minZoom = Math.max(IMG_WIDTH / baseTexture.getWidth(), IMG_HEIGHT / baseTexture.getHeight());
            zoomBar.setActive(true);
            updateZoom(1f);
            instructionsLabel.setLabel(PGR.core.strings.cardEditorTutorial.imageCrop);
            saveButton.setInteractable(true);
        }
    }

    protected void updatePictures()
    {
        updateBuffer(false);

        outsideImage = new TextureRegion(imageBuffer.getColorBufferTexture(), Settings.WIDTH / 3, Settings.HEIGHT / 4, Settings.WIDTH / 3, Settings.HEIGHT / 2);
        insideImageRenderable = new TextureRegion(new Texture(insideImage));
        outsideImage.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        insideImageRenderable.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    protected void updateZoom(float scrollPercentage)
    {
        zoomBar.scroll(scrollPercentage, false);
        scale = MathUtils.lerp(minZoom, maxZoom, scrollPercentage);

        updatePictures();
    }

    @Override
    public void render(SpriteBatch sb)
    {
        hb.render(sb);
        cancelButton.tryRender(sb);
        saveButton.tryRender(sb);
        loadButton.tryRender(sb);
        pasteButton.tryRender(sb);
        instructionsLabel.tryRender(sb);
        zoomBar.tryRender(sb);

        if (outsideImage != null)
        {
            PCLRenderHelpers.drawCentered(sb, Color.GRAY.cpy(), outsideImage, Settings.WIDTH / 2, Settings.HEIGHT / 2, outsideImage.getRegionWidth(), outsideImage.getRegionHeight(), 1, 0);
        }
        if (insideImageRenderable != null)
        {
            PCLRenderHelpers.drawCentered(sb, Color.WHITE.cpy(), insideImageRenderable, Settings.WIDTH / 2, Settings.HEIGHT / 2, insideImageRenderable.getRegionWidth(), insideImageRenderable.getRegionHeight(), 1, 0);
        }

    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        cancelButton.tryUpdate();
        saveButton.tryUpdate();
        loadButton.tryUpdate();
        pasteButton.tryUpdate();
        instructionsLabel.tryUpdate();
        camera.update();
        if (baseTexture != null)
        {
            if (!hb.isDragging())
            {
                zoomBar.tryUpdate();
            }
            if (!zoomBar.isDragging)
            {
                hb.update();
                if (hb.isDragging())
                {
                    updatePictures();
                }
            }
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) && Gdx.input.isKeyJustPressed(Input.Keys.V))
        {
            getImageFromClipboard();
        }
    }
}
