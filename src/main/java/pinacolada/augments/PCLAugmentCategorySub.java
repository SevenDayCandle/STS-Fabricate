package pinacolada.augments;

import com.badlogic.gdx.graphics.Texture;
import extendedui.EUIRM;
import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PCLAugmentCategorySub {
    private static final HashMap<String, PCLAugmentCategorySub> ALL = new HashMap<>();

    public static final PCLAugmentCategorySub AffinityBlue = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.General, "AffinityBlue");
    public static final PCLAugmentCategorySub AffinityGreen = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.General, "AffinityGreen");
    public static final PCLAugmentCategorySub AffinityOrange = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.General, "AffinityOrange");
    public static final PCLAugmentCategorySub AffinityRed = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.General, "AffinityRed");
    public static final PCLAugmentCategorySub Block = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Summon, "Block");
    public static final PCLAugmentCategorySub BlockCount = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Summon, "BlockCount");
    public static final PCLAugmentCategorySub Damage = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Summon, "Damage");
    public static final PCLAugmentCategorySub DamageCount = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Summon, "DamageCount");
    public static final PCLAugmentCategorySub DamageType = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Summon, "DamageType");
    public static final PCLAugmentCategorySub HP = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Summon, "HP");
    public static final PCLAugmentCategorySub Priority = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Summon, "Priority");
    public static final PCLAugmentCategorySub TagBounce = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Played, "TagBounce");
    public static final PCLAugmentCategorySub TagDelayed = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Played, "TagDelayed");
    public static final PCLAugmentCategorySub TagExhaust = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Played, "TagExhaust");
    public static final PCLAugmentCategorySub TagHaste = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Played, "TagHaste");
    public static final PCLAugmentCategorySub TagInnate = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Played, "TagInnate");
    public static final PCLAugmentCategorySub TagLoyal = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Played, "TagLoyal");
    public static final PCLAugmentCategorySub TagRecast = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Played, "TagRecast");
    public static final PCLAugmentCategorySub TagRetain = new PCLAugmentCategorySub(PGR.core, PCLAugmentCategory.Played, "TagRetain");

    public final String ID;
    public final String suffix;
    public final PCLResources<?, ?, ?, ?> resources;
    public final PCLAugmentCategory parent;

    public PCLAugmentCategorySub(PCLResources<?, ?, ?, ?> resources, PCLAugmentCategory parent, String suffix) {
        this.resources = resources;
        this.parent = parent;
        this.suffix = suffix;
        this.ID = resources.createID(suffix);
        ALL.put(ID, this);
    }

    public static PCLAugmentCategorySub get(String powerID) {
        return ALL.get(powerID);
    }

    public static Collection<PCLAugmentCategorySub> sortedValues() {
        return ALL.values().stream().sorted((a, b) -> StringUtils.compare(a.getName(), b.getName())).collect(Collectors.toList());
    }

    public String getImagePath() {
        return PGR.getAugmentImage(ID);
    }

    public String getName() {
        return ID;
    }

    public Texture getTexture() {
        return EUIRM.getTexture(getImagePath());
    }
}
