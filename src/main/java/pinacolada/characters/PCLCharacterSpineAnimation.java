package pinacolada.characters;

public class PCLCharacterSpineAnimation extends PCLCharacterAnimation {
    public String corpseImage;
    public String shoulderImage1;
    public String shoulderImage2;

    public PCLCharacterSpineAnimation(String atlasUrl, String skeletonUrl, String shoulderImage1, String shoulderImage2, String corpseImage) {
        this(atlasUrl, skeletonUrl, shoulderImage1, shoulderImage2, corpseImage, 1f);
    }

    public PCLCharacterSpineAnimation(String atlasUrl, String skeletonUrl, String shoulderImage1, String shoulderImage2, String corpseImage, float scale) {
        super(atlasUrl, skeletonUrl, scale);
        this.shoulderImage1 = shoulderImage1;
        this.shoulderImage2 = shoulderImage2;
        this.corpseImage = corpseImage;
    }

    @Override
    public Type type() {
        return Type.NONE;
    }
}
