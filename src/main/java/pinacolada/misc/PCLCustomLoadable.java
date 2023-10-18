package pinacolada.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.steam.SteamSearch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.utilities.TupleT2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public abstract class PCLCustomLoadable implements Serializable {
    static final long serialVersionUID = 1L;
    public static final int ID_SIZE = 4;
    public static final String FOLDER = "custom";
    protected transient String filePath;

    protected boolean isInternal;
    protected String workshopFolder;
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

    protected static ArrayList<TupleT2<SteamSearch.WorkshopInfo, FileHandle>> getWorkshopFolders(String subfolder) {
        ArrayList<TupleT2<SteamSearch.WorkshopInfo, FileHandle>> folders = new ArrayList<>();
        for (SteamSearch.WorkshopInfo s : Loader.getWorkshopInfos()) {
            FileHandle folder = Gdx.files.absolute(s.getInstallPath() + "/" + FOLDER + "/" + subfolder);
            if (folder.exists()) {
                folders.add(new TupleT2<>(s, folder));
            }
        }
        return folders;
    }

    protected static boolean isIDDuplicate(String input, Iterable<? extends PCLCustomLoadable> items) {
        return EUIUtils.any(items, c -> c.ID.equals(input));
    }

    protected static boolean isIDDuplicateByKey(String input, Iterable<String> items) {
        return EUIUtils.any(items, c -> c.equals(input));
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

    protected static String makeNewIDByKey(String baseIDPrefix, Iterable<String> items) {
        StringBuilder sb = new StringBuilder(baseIDPrefix);
        for (int i = 0; i < ID_SIZE; i++) {
            sb.append(makeRandomCharIndex());
        }

        while (isIDDuplicateByKey(sb.toString(), items)) {
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

    public boolean getIsInternal() {
        return isInternal;
    }

    protected final String makeFilePath() {
        String base = getBaseFolderPath() + "/" + ID + ".json";
        return workshopFolder != null ? workshopFolder + "/" + base : base;
    }

    protected final String makeImagePath() {
        String base = getBaseFolderPath() + "/" + ID + ".png";
        return workshopFolder != null ? workshopFolder + "/" + base : base;
    }

    abstract protected String getSubfolderPath();

}
