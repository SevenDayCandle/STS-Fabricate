package pinacolada.relics;

import com.badlogic.gdx.graphics.Texture;
import extendedui.EUIRM;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

public class PCLDynamicRelic extends PCLPointerRelic implements FabricateItem {
    protected PCLDynamicRelicData builder;

    public PCLDynamicRelic(PCLDynamicRelicData data) {
        super(data);
        setupBuilder(data);
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
    @Override
    public void loadImage(String path) {
        Texture t = EUIRM.getTexture(path, true, true);
        if (t == null) {
            path = PCLCoreImages.CardAffinity.unknown.path();
            t = EUIRM.getTexture(path, true, true);
        }
        this.img = t;
        this.outlineImg = t;
    }

    @Override
    public PCLDynamicRelic makeCopy() {
        return new PCLDynamicRelic(builder);
    }

    @Override
    public void setupImages(String path) {
        // No-op, handle images in setupBuilder
    }

    @Override
    public void reset() {
        super.reset();
        setupMoves(builder);
    }

    public void setupBuilder(PCLDynamicRelicData builder) {
        this.builder = builder;

        if (builder.portraitImage != null) {
            this.outlineImg = this.img = builder.portraitImage;
        }
        else {
            loadImage(builder.imagePath);
        }

        setupMoves(builder);
        initializePCLTips();
        updateDescription(null);
    }

    public void setupMoves(PCLDynamicRelicData builder) {
        for (PSkill<?> effect : builder.moves) {
            if (effect == null) {
                continue;
            }
            addUseMove(effect.makeCopy());
        }

        for (PTrigger pe : builder.powers) {
            if (pe == null) {
                continue;
            }
            addPowerMove(pe.makeCopy());
        }

    }
}
