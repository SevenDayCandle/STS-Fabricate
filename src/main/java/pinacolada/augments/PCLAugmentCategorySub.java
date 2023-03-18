package pinacolada.augments;

public class PCLAugmentCategorySub
{
    public static final PCLAugmentCategorySub AffinityBlue = new PCLAugmentCategorySub(PCLAugmentCategory.General);
    public static final PCLAugmentCategorySub AffinityGreen = new PCLAugmentCategorySub(PCLAugmentCategory.General);
    public static final PCLAugmentCategorySub AffinityOrange = new PCLAugmentCategorySub(PCLAugmentCategory.General);
    public static final PCLAugmentCategorySub AffinityRed = new PCLAugmentCategorySub(PCLAugmentCategory.General);
    public static final PCLAugmentCategorySub Block = new PCLAugmentCategorySub(PCLAugmentCategory.Summon);
    public static final PCLAugmentCategorySub BlockCount = new PCLAugmentCategorySub(PCLAugmentCategory.Summon);
    public static final PCLAugmentCategorySub Damage = new PCLAugmentCategorySub(PCLAugmentCategory.Summon);
    public static final PCLAugmentCategorySub DamageCount = new PCLAugmentCategorySub(PCLAugmentCategory.Summon);
    public static final PCLAugmentCategorySub DamageType = new PCLAugmentCategorySub(PCLAugmentCategory.Summon);
    public static final PCLAugmentCategorySub HP = new PCLAugmentCategorySub(PCLAugmentCategory.Summon);
    public static final PCLAugmentCategorySub Priority = new PCLAugmentCategorySub(PCLAugmentCategory.Summon);
    public static final PCLAugmentCategorySub TagDelayed = new PCLAugmentCategorySub(PCLAugmentCategory.Played);
    public static final PCLAugmentCategorySub TagHaste = new PCLAugmentCategorySub(PCLAugmentCategory.Played);
    public static final PCLAugmentCategorySub TagInnate = new PCLAugmentCategorySub(PCLAugmentCategory.Played);
    public static final PCLAugmentCategorySub TagRetain = new PCLAugmentCategorySub(PCLAugmentCategory.Played);

    public final PCLAugmentCategory parent;

    public PCLAugmentCategorySub(PCLAugmentCategory parent)
    {
        this.parent = parent;
    }
}
