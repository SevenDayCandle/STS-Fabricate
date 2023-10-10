package pinacolada.characters;

import basemod.animations.SpriterAnimation;

public class PCLCharacterSpriterAnimation extends SpriterAnimation {

    public String corpseImage;
    public String shoulderImage1;
    public String shoulderImage2;

    public PCLCharacterSpriterAnimation(String filepath, String shoulderImage1, String shoulderImage2, String corpseImage) {
        super(filepath);
        this.shoulderImage1 = shoulderImage1;
        this.shoulderImage2 = shoulderImage2;
        this.corpseImage = corpseImage;
    }
}
