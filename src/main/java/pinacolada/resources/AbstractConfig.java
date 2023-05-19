package pinacolada.resources;

import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.evacipated.cardcrawl.modthespire.lib.ConfigUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import extendedui.ui.EUIHoverable;
import extendedui.ui.settings.BasemodSettingsPage;
import extendedui.ui.settings.ModSettingsScreen;
import extendedui.ui.settings.ModSettingsToggle;
import extendedui.utilities.EUIFontHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class AbstractConfig {
    private static final String CONFIG_ID = "PCLConfig";
    protected static final int BASE_OPTION_OFFSET_X = 400;
    protected static final int BASE_OPTION_OFFSET_Y = 720;
    protected static final int BASE_OPTION_OPTION_HEIGHT = 32;
    protected static ModSettingsScreen.Category pclCategory;

    protected final String id;
    protected SpireConfig config;
    protected BasemodSettingsPage settingsBlock;
    protected ModPanel panel;

    public AbstractConfig(String id) {
        this.id = id;
    }

    protected static ModSettingsToggle makeModToggle(STSConfigItem<Boolean> option, String label, String tip) {
        ModSettingsToggle toggle = makeModToggle(option, label);
        if (toggle != null) {
            toggle.setTooltip(label, tip);
            toggle.tooltip.setAutoWidth();
        }
        return toggle;
    }

    protected static ModSettingsToggle makeModToggle(STSConfigItem<Boolean> option, String label) {
        // Must be initialized after Settings.scale is set, or the mod options will be in the wrong position
        if (pclCategory == null) {
            pclCategory = ModSettingsScreen.registerByClass(AbstractConfig.class);
        }
        return ModSettingsScreen.addBoolean(pclCategory, option, label);
    }

    protected int addGenericElement(int page, EUIHoverable renderable, int ypos) {
        settingsBlock.addUIElement(page, renderable);
        return (int) (ypos - renderable.hb.height);
    }

    protected int addToggle(int page, STSConfigItem<Boolean> option, String label, int ypos, String tip) {
        settingsBlock.addUIElement(page, new ModLabeledToggleButton(label, tip, BASE_OPTION_OFFSET_X, ypos, Settings.CREAM_COLOR.cpy(), EUIFontHelper.cardDescriptionFontNormal, option.get(), panel, (__) -> {
        }, (c) -> option.set(c.enabled, true)));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    public FileHandle getConfigFolder() {
        FileHandle folder = Gdx.files.absolute(getConfigPath());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public String getConfigPath() {
        return ConfigUtils.CONFIG_DIR + File.separator + id + File.separator;
    }

    public void initializeOptions() {

    }

    public void load(int slot) {
        try {
            final String fileName = CONFIG_ID + slot;
            if (slot == 0) {
                final File file = new File(SpireConfig.makeFilePath(id, fileName));
                if (!file.exists()) {
                    final File previousFile = new File(SpireConfig.makeFilePath(id, CONFIG_ID));
                    if (previousFile.exists()) {
                        Files.copy(previousFile.toPath(), file.toPath());
                    }
                }
            }
            config = new SpireConfig(id, fileName);
            EUIUtils.logInfoIfDebug(this, "Loaded: " + fileName);
            loadImpl();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean save() {
        try {
            config.save();
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    abstract public void loadImpl();
}
