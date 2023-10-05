package pinacolada.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.google.gson.reflect.TypeToken;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.resources.PGR;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public abstract class PCLCustomEditorLoadable<T extends EditorMaker, U extends FabricateItem> extends PCLCustomLoadable {
    protected static final TypeToken<EffectItemForm> TTOKENFORM = new TypeToken<EffectItemForm>() {
    };
    protected transient String filePath;
    protected transient String imagePath;
    public transient ArrayList<T> builders = new ArrayList<>();

    public T getBuilder(int i) {
        return (builders.size() > i) ? builders.get(i) : null;
    }

    protected void writeFiles(Type type) {
        String newFilePath = makeFilePath();
        String newImagePath = makeImagePath();

        // If the file path has changed and the original file exists, we should move the file and its image
        FileHandle writer = Gdx.files.local(filePath);
        if (writer.exists() && !newFilePath.equals(filePath)) {
            writer.moveTo(Gdx.files.local(newFilePath));
            EUIUtils.logInfo(this.getClass(), "Moved Custom Item: " + filePath + ", New: " + newFilePath);
        }
        writer = Gdx.files.local(newFilePath);

        // The image should have the same file name as the file path
        FileHandle imgWriter = Gdx.files.local(imagePath);
        if (imgWriter.exists() && !newImagePath.equals(imagePath)) {
            imgWriter.moveTo(Gdx.files.local(newImagePath));
            EUIUtils.logInfo(this.getClass(), "Moved Custom Image: " + imagePath + ", New: " + newImagePath);
        }
        imgWriter = Gdx.files.local(newImagePath);

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

    public abstract U make();

    public static class EffectItemForm implements Serializable {
        static final long serialVersionUID = 1L;
        public String[] effects;
        public String[] powerEffects;
    }
}
