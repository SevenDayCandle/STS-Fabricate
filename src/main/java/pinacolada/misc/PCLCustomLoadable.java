package pinacolada.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;

public abstract class PCLCustomLoadable {
    public static final int ID_SIZE = 4;
    public static final String FOLDER = "custom";

    public String ID;

    protected static String getBaseIDPrefix(String baseIDPrefix, AbstractCard.CardColor color) {
        return baseIDPrefix + "_" + color.name() + "_";
    }

    protected static FileHandle getCustomFolder(String subfolder) {
        FileHandle folder = Gdx.files.local(FOLDER + "/" + subfolder);
        if (!folder.exists()) {
            folder.mkdirs();
            EUIUtils.logInfo(PCLCustomLoadable.class, "Created Custom Folder: " + folder.path());
        }
        return folder;
    }

    protected static boolean isIDDuplicate(String input, Iterable<? extends PCLCustomLoadable> items) {
        return EUIUtils.any(items, c -> c.ID.equals(input));
    }

    protected static String makeNewID(String baseIDPrefix, Iterable<? extends PCLCustomLoadable> items) {
        StringBuilder sb = new StringBuilder(baseIDPrefix);
        for (int i = 0; i < ID_SIZE; i++) {
            sb.append(makeRandomCharIndex());
        }

        while (isIDDuplicate(sb.toString(), items)) {
            sb.append(makeRandomCharIndex());
        }
        return sb.toString();
    }

    protected static char makeRandomCharIndex() {
        int i = MathUtils.random(65, 100);
        return (char) (i > 90 ? i - 43 : i);
    }

    protected final String getBaseFolderPath() {
        return FOLDER + "/" + getSubfolderPath();
    }

    protected final FileHandle getCustomFolder() {
        return getCustomFolder(getSubfolderPath());
    }

    abstract protected String getSubfolderPath();

    protected String makeFilePath() {
        return getBaseFolderPath() + "/" + ID + ".json";
    }

    protected String makeImagePath() {
        return getBaseFolderPath() + "/" + ID + ".png";
    }

    protected String makeMigrationImagePath() {
        return FOLDER + "/" + ID + ".png";
    }
}
