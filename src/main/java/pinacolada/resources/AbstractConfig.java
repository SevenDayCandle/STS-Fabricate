package pinacolada.resources;

import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.ConfigUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import extendedui.configuration.STSSerializedConfigItem;
import extendedui.configuration.STSStringConfigItem;
import extendedui.ui.settings.ModSettingsScreen;
import extendedui.ui.settings.ModSettingsToggle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;

public abstract class AbstractConfig
{
    private static final String CONFIG_ID = "PCLConfig";
    protected static final int BASE_OPTION_OFFSET_X = 400;
    protected static final int BASE_OPTION_OFFSET_Y = 700;
    protected static final int BASE_OPTION_OPTION_HEIGHT = 50;
    protected static ModSettingsScreen.Category pclCategory;
    public STSSerializedConfigItem<HashSet<String>> bannedCards;
    public STSSerializedConfigItem<HashSet<String>> bannedRelics;
    public STSConfigItem<Integer> cardsCount;
    public STSSerializedConfigItem<Vector2> meterPosition;
    public STSStringConfigItem trophies;

    protected final String id;
    protected SpireConfig config;

    protected static ModSettingsToggle addModToggle(STSConfigItem<Boolean> option, String label)
    {
        // Must be initialized after Settings.scale is set, or the mod options will be in the wrong position
        if (pclCategory == null)
        {
            pclCategory = ModSettingsScreen.registerByClass(AbstractConfig.class);
        }
        return ModSettingsScreen.addBoolean(pclCategory, option, label);
    }

    protected static int addToggle(ModPanel panel, STSConfigItem<Boolean> option, String label, int ypos)
    {
        panel.addUIElement(new ModLabeledToggleButton(label, BASE_OPTION_OFFSET_X, ypos, Settings.CREAM_COLOR.cpy(), FontHelper.charDescFont, option.get(), panel, (__) -> {
        }, (c) -> {
            option.set(c.enabled, true);
        }));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    public AbstractConfig(String id)
    {
        this.id = id;
    }

    public FileHandle getConfigFolder()
    {
        FileHandle folder = Gdx.files.absolute(getConfigPath());
        if (!folder.exists())
        {
            folder.mkdirs();
        }
        return folder;
    }

    public String getConfigPath()
    {
        return ConfigUtils.CONFIG_DIR + File.separator + id + File.separator;
    }

    public void initializeOptions()
    {

    }

    public void load(int slot)
    {
        try
        {
            final String fileName = CONFIG_ID + slot;
            if (slot == 0)
            {
                final File file = new File(SpireConfig.makeFilePath(id, fileName));
                if (!file.exists())
                {
                    final File previousFile = new File(SpireConfig.makeFilePath(id, CONFIG_ID));
                    if (previousFile.exists())
                    {
                        Files.copy(previousFile.toPath(), file.toPath());
                    }
                }
            }
            config = new SpireConfig(id, fileName);
            EUIUtils.logInfoIfDebug(this, "Loaded: " + fileName);
            loadImpl();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean save()
    {
        try
        {
            config.save();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    abstract public void loadImpl();
}
