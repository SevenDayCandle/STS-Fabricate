package pinacolada.relics;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import extendedui.EUIRM;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.misc.PCLCollectibleSaveData;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;

public class PCLDynamicRelic extends PCLPointerRelic implements FabricateItem, CustomSavable<PCLCollectibleSaveData> {
    protected PCLDynamicRelicData builder;
    protected ArrayList<PCLDynamicRelicData> forms;

    public PCLDynamicRelic(PCLDynamicRelicData data) {
        super(data);
        isSeen = true; // Always seen
    }

    protected void findForms() {
        PCLCustomRelicSlot cSlot = PCLCustomRelicSlot.get(relicId);
        if (cSlot != null) {
            this.forms = cSlot.builders;
        }
    }

    // Use descriptions from the relicData because DESCRIPTIONS in AbstractRelic will contain the wrong value
    @Override
    public String[] getDescriptions() {
        return relicData.strings.DESCRIPTIONS;
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
    public PCLDynamicRelic makeCopy() {
        return new PCLDynamicRelic(builder);
    }

    @Override
    protected void preSetup(PCLRelicData builder) {
        super.preSetup(builder);

        this.builder = (PCLDynamicRelicData) builder; // Should always be PCLDynamicRelicData
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
    public PCLRelic setForm(int form) {
        PCLDynamicRelicData lastBuilder = null;
        this.auxiliaryData.form = form;
        if (forms != null && forms.size() > form) {
            lastBuilder = forms.get(form);
        }
        if (lastBuilder != null && lastBuilder != this.builder) {
            this.builder = lastBuilder;
            setupMoves(this.builder);
        }
        initializePCLTips();
        return this;
    }

    @Override
    public void setupImages(String path) {
        // No-op, handle images in setupBuilder
    }

    public void setupMoves(PCLDynamicRelicData builder) {
        clearSkills();
        for (PSkill<?> effect : builder.moves) {
            if (effect == null || effect.isBlank()) {
                continue;
            }
            addUseMove(effect.makeCopy());
        }

        for (PTrigger pe : builder.powers) {
            if (pe == null || pe.isBlank()) {
                continue;
            }
            addPowerMove(pe.makeCopy());
        }

    }
}
