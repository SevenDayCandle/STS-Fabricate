package pinacolada.relics;

import com.badlogic.gdx.graphics.Texture;
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

    public PCLDynamicRelic(PCLDynamicRelicData data, Texture texture, RelicTier tier, LandingSound sfx) {
        super(data, texture, tier, sfx);
        setupBuilder(data);
    }

    @Override
    public EditorMaker getDynamicData() {
        return builder;
    }

    @Override
    public PCLDynamicRelic makeCopy() {
        return new PCLDynamicRelic(builder);
    }

    public void setupBuilder(PCLDynamicRelicData builder) {
        this.builder = builder;
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

        initializePCLTips();
        updateDescription(null);
    }
}
