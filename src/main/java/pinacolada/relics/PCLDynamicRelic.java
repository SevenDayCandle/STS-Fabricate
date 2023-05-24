package pinacolada.relics;

import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
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
