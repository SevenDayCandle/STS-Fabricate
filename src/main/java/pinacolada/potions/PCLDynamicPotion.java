package pinacolada.potions;

import extendedui.EUIUtils;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

public class PCLDynamicPotion extends PCLPotion implements FabricateItem {
    protected PCLDynamicPotionData builder;

    public PCLDynamicPotion(PCLDynamicPotionData data) {
        super(data);
        setupBuilder(data);
    }

    @Override
    public EditorMaker getDynamicData() {
        return builder;
    }

    @Override
    public PCLDynamicPotion makeCopy() {
        return new PCLDynamicPotion(builder);
    }

    public void setupBuilder(PCLDynamicPotionData builder) {
        this.builder = builder;

        setupMoves(builder);
        initializeTips();
    }

    public void setupMoves(PCLDynamicPotionData builder) {
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

        this.targetRequired = this.isThrown = EUIUtils.any(getEffects(), e -> e.target.targetsSingle());
    }
}
