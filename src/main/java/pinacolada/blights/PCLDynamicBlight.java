package pinacolada.blights;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import extendedui.EUIRM;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.blights.*;
import pinacolada.misc.PCLCollectibleSaveData;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;

public class PCLDynamicBlight extends PCLPointerBlight implements FabricateItem, CustomSavable<PCLCollectibleSaveData> {
    protected PCLDynamicBlightData builder;
    protected ArrayList<PCLDynamicBlightData> forms;

    public PCLDynamicBlight(PCLDynamicBlightData data) {
        super(data);
        isSeen = true; // Always seen
    }

    protected void findForms() {
        PCLCustomBlightSlot cSlot = PCLCustomBlightSlot.get(blightID);
        if (cSlot != null) {
            this.forms = cSlot.builders;
        }
    }

    @Override
    public EditorMaker getDynamicData() {
        return builder;
    }

    // Do not show errors for custom image paths
    public void loadImage(String path) {
        Texture t = EUIRM.getTexture(path, true, false);
        if (t == null) {
            path = PCLCoreImages.CardAffinity.unknown.path();
            t = EUIRM.getTexture(path, true, false);
        }
        this.img = t;
        this.outlineImg = t;
    }

    @Override
    public PCLDynamicBlight makeCopy() {
        return new PCLDynamicBlight(builder);
    }

    @Override
    protected void preSetup(PCLBlightData builder) {
        super.preSetup(builder);

        this.builder = (PCLDynamicBlightData) builder; // Should always be PCLDynamicBlightData
        findForms();

        if (this.builder.portraitImage != null) {
            this.outlineImg = this.img = this.builder.portraitImage;
        }
        else {
            String path = builder.imagePath;
            Texture t = EUIRM.getTexture(path, true, false);
            if (t == null) {
                path = PCLCoreImages.CardAffinity.unknown.path();
                t = EUIRM.getTexture(path, true, false);
            }
            this.img = t;
            this.outlineImg = t;
        }

        setupMoves(this.builder);
        updateName();
    }

    @Override
    public void reset() {
        super.reset();
        setupMoves(builder);
    }

    @Override
    public PCLBlight setForm(int form) {
        PCLDynamicBlightData lastBuilder = null;
        this.auxiliaryData.form = form;
        if (forms != null && forms.size() > form) {
            lastBuilder = forms.get(form);
        }
        if (lastBuilder != null && lastBuilder != this.builder) {
            this.builder = lastBuilder;
            setupMoves(this.builder);
        }
        initializeTips();
        return this;
    }

    @Override
    public void setupImages() {
        // No-op, handle images in setupBuilder
    }

    public void setupMoves(PCLDynamicBlightData builder) {
        clearSkills();
        for (PSkill<?> effect : builder.moves) {
            if (PSkill.isSkillBlank(effect)) {
                continue;
            }
            addUseMove(effect.makeCopy());
        }

        for (PSkill<?> pe : builder.powers) {
            if (PSkill.isSkillBlank(pe)) {
                continue;
            }
            addPowerMove(pe.makeCopy());
        }

    }
}
