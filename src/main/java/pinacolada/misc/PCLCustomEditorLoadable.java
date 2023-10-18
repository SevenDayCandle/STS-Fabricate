package pinacolada.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.google.gson.reflect.TypeToken;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.blights.PCLDynamicBlight;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.resources.PGR;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public abstract class PCLCustomEditorLoadable<T extends EditorMaker<U>, U extends FabricateItem> extends PCLCustomLoadable {
    protected static final TypeToken<EffectItemForm> TTOKENFORM = new TypeToken<EffectItemForm>() {
    };
    protected transient String imagePath;
    public transient ArrayList<T> builders = new ArrayList<>();

    public final T getBuilder(int i) {
        return (builders.size() > i) ? builders.get(i) : null;
    }

    public final FileHandle getFileHandle() {
        return workshopFolder != null ? Gdx.files.absolute(filePath) : Gdx.files.local(filePath);
    }

    protected final FileHandle getGenericHandle(String path) {
        return workshopFolder != null ? Gdx.files.absolute(path) : Gdx.files.local(path);
    }

    public final FileHandle getImageHandle() {
        return workshopFolder != null ? Gdx.files.absolute(imagePath) : Gdx.files.local(imagePath);
    }

    public final String getImagePath() {
        return imagePath;
    }

    public U make() {
        T builder = getBuilder(0);
        return builder != null ? builder.create() : null;
    }

    protected void wipeBuilder() {
        if (isInternal) {
            EUIUtils.logInfo(this.getClass(), "Cannot delete internal item: " + filePath);
            return;
        }
        FileHandle writer = getImageHandle();
        writer.delete();
        EUIUtils.logInfo(this.getClass(), "Deleted Custom Image: " + imagePath);
        writer = getFileHandle();
        writer.delete();
        EUIUtils.logInfo(this.getClass(), "Deleted Custom File: " + filePath);
    }

    protected void writeFiles(Type type) {
        if (isInternal) {
            EUIUtils.logInfo(this.getClass(), "Cannot overwrite internal item: " + filePath);
            return;
        }
        String newFilePath = makeFilePath();
        String newImagePath = makeImagePath();

        // If the file path has changed and the original file exists, we should move the file and its image
        FileHandle writer = getGenericHandle(filePath);
        if (writer.exists() && !newFilePath.equals(filePath)) {
            writer.moveTo(getGenericHandle(newFilePath));
            EUIUtils.logInfo(this.getClass(), "Moved Custom Item: " + filePath + ", New: " + newFilePath);
        }
        writer = getGenericHandle(newFilePath);

        // The image should have the same file name as the file path
        FileHandle imgWriter = getGenericHandle(imagePath);
        if (imgWriter.exists() && !newImagePath.equals(imagePath)) {
            imgWriter.moveTo(getGenericHandle(newImagePath));
            EUIUtils.logInfo(this.getClass(), "Moved Custom Image: " + imagePath + ", New: " + newImagePath);
        }
        imgWriter = getGenericHandle(newImagePath);

        filePath = newFilePath;
        imagePath = newImagePath;

        // If the image in the builder was updated, we need to overwrite the existing image
        // All builders should have the same image
        T builder = getBuilder(0);
        if (builder != null) {
            Texture portrait = builder.getImage();
            if (portrait != null) {
                PixmapIO.writePNG(imgWriter, portrait.getTextureData().consumePixmap());
                // Forcibly reload the image
                EUIRM.reloadTexture(newImagePath, true, PGR.config.lowVRAM.get());
            }
        }

        writer.writeString(EUIUtils.serialize(this, type), false, HttpParametersUtils.defaultEncoding);
    }

    protected abstract void recordBuilder();

    protected abstract void setupBuilder(String filePath, String workshopPath, boolean isInternal);

    public static class EffectItemForm implements Serializable {
        static final long serialVersionUID = 1L;
        public String[] effects;
        public String[] powerEffects;
    }
}
