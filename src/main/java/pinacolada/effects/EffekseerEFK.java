package pinacolada.effects;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.EUIUtils;
import extendedui.STSEffekseerManager;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.vfx.EffekseerEffect;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// TODO add audio paths, define hit delays
@JsonAdapter(EffekseerEFK.EffekseerEFKAdapter.class)
public class EffekseerEFK {
    private static final Map<String, EffekseerEFK> ALL = new HashMap<>();

    //public static final EffekseerEFK BLOW01 = new EffekseerEFK("effects/Blow01.efk"); //TODO rebuild so this doesn't crash the game
    public static final EffekseerEFK BLOW02 = new EffekseerEFK("effects/Blow02.efk");
    public static final EffekseerEFK BLOW03 = new EffekseerEFK("effects/Blow03.efk");
    public static final EffekseerEFK BLOW04 = new EffekseerEFK("effects/Blow04.efk");
    public static final EffekseerEFK BLOW05 = new EffekseerEFK("effects/Blow05.efk");
    public static final EffekseerEFK BLOW06 = new EffekseerEFK("effects/Blow06.efk");
    public static final EffekseerEFK BLOW07 = new EffekseerEFK("effects/Blow07.efk");
    public static final EffekseerEFK BLOW08 = new EffekseerEFK("effects/Blow08.efk");
    public static final EffekseerEFK BLOW09 = new EffekseerEFK("effects/Blow09.efk");
    public static final EffekseerEFK BLOW11 = new EffekseerEFK("effects/Blow11.efk");
    public static final EffekseerEFK BLOW12 = new EffekseerEFK("effects/Blow12.efk");
    public static final EffekseerEFK BLOW13 = new EffekseerEFK("effects/Blow13.efk");
    public static final EffekseerEFK BLOW14 = new EffekseerEFK("effects/Blow14.efk");
    public static final EffekseerEFK BLOW15 = new EffekseerEFK("effects/Blow15.efk");
    public static final EffekseerEFK BLOW16 = new EffekseerEFK("effects/Blow16.efk");
    public static final EffekseerEFK BLOW17 = new EffekseerEFK("effects/Blow17.efk");
    public static final EffekseerEFK BLOW18 = new EffekseerEFK("effects/Blow18.efk");
    public static final EffekseerEFK BLOW19 = new EffekseerEFK("effects/Blow19.efk");
    public static final EffekseerEFK BLOW20 = new EffekseerEFK("effects/Blow20.efk");
    public static final EffekseerEFK BLOW21 = new EffekseerEFK("effects/Blow21.efk");
    public static final EffekseerEFK CLAW01 = new EffekseerEFK("effects/Claw01.efk");
    public static final EffekseerEFK CLAW02 = new EffekseerEFK("effects/Claw02.efk");
    public static final EffekseerEFK CLAW03 = new EffekseerEFK("effects/Claw03.efk");
    public static final EffekseerEFK CLAW04 = new EffekseerEFK("effects/Claw04.efk");
    public static final EffekseerEFK CURE01 = new EffekseerEFK("effects/Cure01.efk");
    public static final EffekseerEFK CURE02 = new EffekseerEFK("effects/Cure02.efk");
    public static final EffekseerEFK CURE03 = new EffekseerEFK("effects/Cure03.efk");
    public static final EffekseerEFK CURE04 = new EffekseerEFK("effects/Cure04.efk");
    public static final EffekseerEFK CURE05 = new EffekseerEFK("effects/Cure05.efk");
    public static final EffekseerEFK CURE06 = new EffekseerEFK("effects/Cure06.efk");
    public static final EffekseerEFK CURE07 = new EffekseerEFK("effects/Cure07.efk");
    public static final EffekseerEFK DARK01 = new EffekseerEFK("effects/Dark01.efk");
    public static final EffekseerEFK DARK02 = new EffekseerEFK("effects/Dark02.efk");
    public static final EffekseerEFK DARK03 = new EffekseerEFK("effects/Dark03.efk");
    public static final EffekseerEFK DARK04 = new EffekseerEFK("effects/Dark04.efk");
    public static final EffekseerEFK DARK05 = new EffekseerEFK("effects/Dark05.efk");
    public static final EffekseerEFK FIRE01 = new EffekseerEFK("effects/Fire01.efk");
    public static final EffekseerEFK FIRE02 = new EffekseerEFK("effects/Fire02.efk");
    public static final EffekseerEFK FIRE03 = new EffekseerEFK("effects/Fire03.efk");
    public static final EffekseerEFK FIRE04 = new EffekseerEFK("effects/Fire04.efk");
    public static final EffekseerEFK FIRE05 = new EffekseerEFK("effects/Fire05.efk");
    public static final EffekseerEFK FIRE06 = new EffekseerEFK("effects/Fire06.efk");
    public static final EffekseerEFK FIRE07 = new EffekseerEFK("effects/Fire07.efk");
    public static final EffekseerEFK FIRE08 = new EffekseerEFK("effects/Fire08.efk");
    public static final EffekseerEFK FIRE09 = new EffekseerEFK("effects/Fire09.efk");
    public static final EffekseerEFK FIRE10 = new EffekseerEFK("effects/Fire10.efk");
    public static final EffekseerEFK FIRE11 = new EffekseerEFK("effects/Fire11.efk");
    public static final EffekseerEFK FIRE12 = new EffekseerEFK("effects/Fire12.efk");
    public static final EffekseerEFK FIRE13 = new EffekseerEFK("effects/Fire13.efk");
    public static final EffekseerEFK FIRE14 = new EffekseerEFK("effects/Fire14.efk");
    public static final EffekseerEFK FIRE15 = new EffekseerEFK("effects/Fire15.efk");
    public static final EffekseerEFK FIRE16 = new EffekseerEFK("effects/Fire16.efk");
    public static final EffekseerEFK GUN01 = new EffekseerEFK("effects/Gun01.efk");
    public static final EffekseerEFK GUN02 = new EffekseerEFK("effects/Gun02.efk");
    public static final EffekseerEFK GUN03 = new EffekseerEFK("effects/Gun03.efk");
    public static final EffekseerEFK GUN04 = new EffekseerEFK("effects/Gun04.efk");
    public static final EffekseerEFK GUN05 = new EffekseerEFK("effects/Gun05.efk");
    public static final EffekseerEFK GUN06 = new EffekseerEFK("effects/Gun06.efk");
    public static final EffekseerEFK GUN07 = new EffekseerEFK("effects/Gun07.efk");
    public static final EffekseerEFK GUN08 = new EffekseerEFK("effects/Gun08.efk");
    public static final EffekseerEFK GUN09 = new EffekseerEFK("effects/Gun09.efk");
    public static final EffekseerEFK HOZYO01 = new EffekseerEFK("effects/Hozyo01.efk");
    public static final EffekseerEFK HOZYO02 = new EffekseerEFK("effects/Hozyo02.efk");
    public static final EffekseerEFK HOZYO03 = new EffekseerEFK("effects/Hozyo03.efk");
    public static final EffekseerEFK HOZYO04 = new EffekseerEFK("effects/Hozyo04.efk");
    public static final EffekseerEFK HOZYO05 = new EffekseerEFK("effects/Hozyo05.efk");
    public static final EffekseerEFK LIGHT01 = new EffekseerEFK("effects/Light01.efk");
    public static final EffekseerEFK LIGHT02 = new EffekseerEFK("effects/Light02.efk");
    public static final EffekseerEFK LIGHT03 = new EffekseerEFK("effects/Light03.efk");
    public static final EffekseerEFK LIGHT04 = new EffekseerEFK("effects/Light04.efk");
    public static final EffekseerEFK MAGIC01 = new EffekseerEFK("effects/Magic01.efk");
    public static final EffekseerEFK MAGIC02 = new EffekseerEFK("effects/Magic02.efk");
    public static final EffekseerEFK SPEAR01 = new EffekseerEFK("effects/Spear01.efk");
    public static final EffekseerEFK SPEAR02 = new EffekseerEFK("effects/Spear02.efk");
    public static final EffekseerEFK SPEAR03 = new EffekseerEFK("effects/Spear03.efk");
    public static final EffekseerEFK SPEAR04 = new EffekseerEFK("effects/Spear04.efk");
    public static final EffekseerEFK SWORD01 = new EffekseerEFK("effects/Sword01.efk");
    public static final EffekseerEFK SWORD02 = new EffekseerEFK("effects/Sword02.efk");
    public static final EffekseerEFK SWORD03 = new EffekseerEFK("effects/Sword03.efk");
    public static final EffekseerEFK SWORD04 = new EffekseerEFK("effects/Sword04.efk");
    public static final EffekseerEFK SWORD05 = new EffekseerEFK("effects/Sword05.efk");
    public static final EffekseerEFK SWORD06 = new EffekseerEFK("effects/Sword06.efk");
    public static final EffekseerEFK SWORD07 = new EffekseerEFK("effects/Sword07.efk");
    public static final EffekseerEFK SWORD08 = new EffekseerEFK("effects/Sword08.efk");
    public static final EffekseerEFK SWORD09 = new EffekseerEFK("effects/Sword09.efk");
    public static final EffekseerEFK SWORD11 = new EffekseerEFK("effects/Sword11.efk");
    public static final EffekseerEFK SWORD12 = new EffekseerEFK("effects/Sword12.efk");
    public static final EffekseerEFK SWORD13 = new EffekseerEFK("effects/Sword13.efk");
    public static final EffekseerEFK SWORD14 = new EffekseerEFK("effects/Sword14.efk");
    public static final EffekseerEFK SWORD15 = new EffekseerEFK("effects/Sword15.efk");
    public static final EffekseerEFK SWORD16 = new EffekseerEFK("effects/Sword16.efk");
    public static final EffekseerEFK SWORD17 = new EffekseerEFK("effects/Sword17.efk");
    public static final EffekseerEFK SWORD18 = new EffekseerEFK("effects/Sword18.efk");
    public static final EffekseerEFK SWORD19 = new EffekseerEFK("effects/Sword19.efk");
    public static final EffekseerEFK SWORD20 = new EffekseerEFK("effects/Sword20.efk");
    public static final EffekseerEFK SWORD21 = new EffekseerEFK("effects/Sword21.efk");
    public static final EffekseerEFK SWORD22 = new EffekseerEFK("effects/Sword22.efk");
    public static final EffekseerEFK SWORD23 = new EffekseerEFK("effects/Sword23.efk");
    public static final EffekseerEFK SWORD24 = new EffekseerEFK("effects/Sword24.efk");
    public static final EffekseerEFK SWORD25 = new EffekseerEFK("effects/Sword25.efk");
    public static final EffekseerEFK SWORD26 = new EffekseerEFK("effects/Sword26.efk");
    public static final EffekseerEFK SWORD27 = new EffekseerEFK("effects/Sword27.efk");
    public static final EffekseerEFK SWORD28 = new EffekseerEFK("effects/Sword28.efk");
    public static final EffekseerEFK WIND01 = new EffekseerEFK("effects/Wind01.efk");
    public static final EffekseerEFK WIND02 = new EffekseerEFK("effects/Wind02.efk");

    public final String ID;
    public final String path;

    public EffekseerEFK(String path) {
        this(makeID(path), path);
    }

    public EffekseerEFK(String id, String path) {
        this.path = path;
        this.ID = id;
        ALL.putIfAbsent(ID, this);
    }

    public static EffekseerEffect efk(EffekseerEFK key) {
        return efk(key, Settings.WIDTH * 0.75f, AbstractDungeon.player != null ? AbstractDungeon.player.hb.cY : Settings.HEIGHT * 0.35f);
    }

    public static EffekseerEffect efk(EffekseerEFK key, float x, float y) {
        return new EffekseerEffect(key, x, y);
    }

    public static EffekseerEffect efk(EffekseerEFK key, Hitbox hb) {
        return efk(key, hb.cX, hb.cY);
    }

    public static EffekseerEFK get(String id) {
        return ALL.get(id);
    }

    public static void initialize() {
        STSEffekseerManager.register(EUIUtils.map(sortedValues(), v -> v.path));
    }

    private static String makeID(String path) {
        String[] splitPath = path.split("/");
        return splitPath[splitPath.length - 1].split("\\.")[0];
    }

    public static Collection<EffekseerEFK> sortedValues() {
        return ALL.values().stream().sorted((a, b) -> StringUtils.compare(a.ID, b.ID)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        String[] split = EUIUtils.splitString("/", path);
        if (split.length > 0) {
            String base = split[split.length - 1];
            String[] split2 = EUIUtils.splitString(".", base);
            return split2.length > 0 ? split2[0] : base;
        }
        return EUIUtils.EMPTY_STRING;
    }

    public static class EffekseerEFKAdapter extends TypeAdapter<EffekseerEFK> {
        @Override
        public EffekseerEFK read(JsonReader in) throws IOException {
            return get(in.nextString());
        }

        @Override
        public void write(JsonWriter writer, EffekseerEFK value) throws IOException {
            writer.value(value != null ? value.ID : EUIUtils.EMPTY_STRING);
        }
    }
}
