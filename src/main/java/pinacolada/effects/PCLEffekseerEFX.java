package pinacolada.effects;

import extendedui.EUIUtils;
import extendedui.STSEffekseerManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// TODO add audio paths
public class PCLEffekseerEFX
{
    protected static final Map<String, PCLEffekseerEFX> ALL = new HashMap<>();

    public static final PCLEffekseerEFX BLOW01 = new PCLEffekseerEFX("effects/Blow01.efk");
    public static final PCLEffekseerEFX BLOW02 = new PCLEffekseerEFX("effects/Blow02.efk");
    public static final PCLEffekseerEFX BLOW03 = new PCLEffekseerEFX("effects/Blow03.efk");
    public static final PCLEffekseerEFX BLOW04 = new PCLEffekseerEFX("effects/Blow04.efk");
    public static final PCLEffekseerEFX BLOW05 = new PCLEffekseerEFX("effects/Blow05.efk");
    public static final PCLEffekseerEFX BLOW06 = new PCLEffekseerEFX("effects/Blow06.efk");
    public static final PCLEffekseerEFX BLOW07 = new PCLEffekseerEFX("effects/Blow07.efk");
    public static final PCLEffekseerEFX BLOW08 = new PCLEffekseerEFX("effects/Blow08.efk");
    public static final PCLEffekseerEFX BLOW09 = new PCLEffekseerEFX("effects/Blow09.efk");
    public static final PCLEffekseerEFX BLOW11 = new PCLEffekseerEFX("effects/Blow11.efk");
    public static final PCLEffekseerEFX BLOW12 = new PCLEffekseerEFX("effects/Blow12.efk");
    public static final PCLEffekseerEFX BLOW13 = new PCLEffekseerEFX("effects/Blow13.efk");
    public static final PCLEffekseerEFX BLOW14 = new PCLEffekseerEFX("effects/Blow14.efk");
    public static final PCLEffekseerEFX BLOW15 = new PCLEffekseerEFX("effects/Blow15.efk");
    public static final PCLEffekseerEFX BLOW16 = new PCLEffekseerEFX("effects/Blow16.efk");
    public static final PCLEffekseerEFX BLOW17 = new PCLEffekseerEFX("effects/Blow17.efk");
    public static final PCLEffekseerEFX BLOW18 = new PCLEffekseerEFX("effects/Blow18.efk");
    public static final PCLEffekseerEFX BLOW19 = new PCLEffekseerEFX("effects/Blow19.efk");
    public static final PCLEffekseerEFX BLOW20 = new PCLEffekseerEFX("effects/Blow20.efk");
    public static final PCLEffekseerEFX BLOW21 = new PCLEffekseerEFX("effects/Blow21.efk");
    public static final PCLEffekseerEFX CLAW01 = new PCLEffekseerEFX("effects/Claw01.efk");
    public static final PCLEffekseerEFX CLAW02 = new PCLEffekseerEFX("effects/Claw02.efk");
    public static final PCLEffekseerEFX CLAW03 = new PCLEffekseerEFX("effects/Claw03.efk");
    public static final PCLEffekseerEFX CLAW04 = new PCLEffekseerEFX("effects/Claw04.efk");
    public static final PCLEffekseerEFX CURE01 = new PCLEffekseerEFX("effects/Cure01.efk");
    public static final PCLEffekseerEFX CURE02 = new PCLEffekseerEFX("effects/Cure02.efk");
    public static final PCLEffekseerEFX CURE03 = new PCLEffekseerEFX("effects/Cure03.efk");
    public static final PCLEffekseerEFX CURE04 = new PCLEffekseerEFX("effects/Cure04.efk");
    public static final PCLEffekseerEFX CURE05 = new PCLEffekseerEFX("effects/Cure05.efk");
    public static final PCLEffekseerEFX CURE06 = new PCLEffekseerEFX("effects/Cure06.efk");
    public static final PCLEffekseerEFX CURE07 = new PCLEffekseerEFX("effects/Cure07.efk");
    public static final PCLEffekseerEFX DARK01 = new PCLEffekseerEFX("effects/Dark01.efk");
    public static final PCLEffekseerEFX DARK02 = new PCLEffekseerEFX("effects/Dark02.efk");
    public static final PCLEffekseerEFX DARK03 = new PCLEffekseerEFX("effects/Dark03.efk");
    public static final PCLEffekseerEFX DARK04 = new PCLEffekseerEFX("effects/Dark04.efk");
    public static final PCLEffekseerEFX DARK05 = new PCLEffekseerEFX("effects/Dark05.efk");
    public static final PCLEffekseerEFX EVFX02_02_TwinEdge = new PCLEffekseerEFX("effects/EVFX02_02_TwinEdge.efk");
    public static final PCLEffekseerEFX EVFX02_09_EruptionClaw = new PCLEffekseerEFX("effects/EVFX02_09_EruptionClaw.efk");
    public static final PCLEffekseerEFX EVFX02_11_QuickBlade = new PCLEffekseerEFX("effects/EVFX02_11_QuickBlade.efk");
    public static final PCLEffekseerEFX EVFX03_02_OrbitalSmash = new PCLEffekseerEFX("effects/EVFX03_02_OrbitalSmash.efk");
    public static final PCLEffekseerEFX EVFX03_08_ThrustingStroke = new PCLEffekseerEFX("effects/EVFX03_08_ThrustingStroke.efk");
    public static final PCLEffekseerEFX EVFX03_12_OneTwoStrike = new PCLEffekseerEFX("effects/EVFX03_12_OneTwoStrike.efk");
    public static final PCLEffekseerEFX EVFX04_03_ThreefoldArrow = new PCLEffekseerEFX("effects/EVFX04_03_ThreefoldArrow.efk");
    public static final PCLEffekseerEFX EVFX04_09_ArcaneArrow = new PCLEffekseerEFX("effects/EVFX04_09_ArcaneArrow.efk");
    public static final PCLEffekseerEFX EVFX04_15_PairedShot = new PCLEffekseerEFX("effects/EVFX04_15_PairedShot.efk");
    public static final PCLEffekseerEFX EVFX05_09_ParallelLunge = new PCLEffekseerEFX("effects/EVFX05_09_ParallelLunge.efk");
    public static final PCLEffekseerEFX EVFX05_13_BoringDrill = new PCLEffekseerEFX("effects/EVFX05_13_BoringDrill.efk");
    public static final PCLEffekseerEFX EVFX05_16_AerialKunai = new PCLEffekseerEFX("effects/EVFX05_16_AerialKunai.efk");
    public static final PCLEffekseerEFX EVFX06_06_SpreadBeam = new PCLEffekseerEFX("effects/EVFX06_06_SpreadBeam.efk");
    public static final PCLEffekseerEFX EVFX06_14_HomingMissile = new PCLEffekseerEFX("effects/EVFX06_14_HomingMissile.efk");
    public static final PCLEffekseerEFX EVFX06_17_QuadraDrone = new PCLEffekseerEFX("effects/EVFX06_17_QuadraDrone.efk");
    public static final PCLEffekseerEFX EVFXForge01_10_FrostforgeSlash = new PCLEffekseerEFX("effects/EVFXForge01_10_FrostforgeSlash.efk");
    public static final PCLEffekseerEFX EVFXForge01_12_FrostforgeShoot = new PCLEffekseerEFX("effects/EVFXForge01_12_FrostforgeShoot.efk");
    public static final PCLEffekseerEFX EVFXForge02_08_BloomforgeWard = new PCLEffekseerEFX("effects/EVFXForge02_08_BloomforgeWard.efk");
    public static final PCLEffekseerEFX EVFXForge02_11_BloomforgeStrike = new PCLEffekseerEFX("effects/EVFXForge02_11_BloomforgeStrike.efk");
    public static final PCLEffekseerEFX EVFXForge03_01_SparkInvocation = new PCLEffekseerEFX("effects/EVFXForge03_01_SparkInvocation.efk");
    public static final PCLEffekseerEFX EVFXForge03_14_SparkforgeBlast = new PCLEffekseerEFX("effects/EVFXForge03_14_SparkforgeBlast.efk");
    public static final PCLEffekseerEFX EVFXForge04_07_SpikeCrush = new PCLEffekseerEFX("effects/EVFXForge04_07_SpikeCrush.efk");
    public static final PCLEffekseerEFX EVFXForge04_11_BloodforgeStrike = new PCLEffekseerEFX("effects/EVFXForge04_11_BloodforgeStrike.efk");
    public static final PCLEffekseerEFX EVFXForge05_06_LiftingGust = new PCLEffekseerEFX("effects/EVFXForge05_06_LiftingGust.efk");
    public static final PCLEffekseerEFX EVFXForge05_09_StormforgeAura = new PCLEffekseerEFX("effects/EVFXForge05_09_StormforgeAura.efk");
    public static final PCLEffekseerEFX EVFXForge09_08_FloodforgeWard = new PCLEffekseerEFX("effects/EVFXForge09_08_FloodforgeWard.efk");
    public static final PCLEffekseerEFX MGC_EarthSpell_LV1_Impact = new PCLEffekseerEFX("effects/MGC_EarthSpell_LV1_Impact.efk");
    public static final PCLEffekseerEFX MGC_EarthSpell_LV2 = new PCLEffekseerEFX("effects/MGC_EarthSpell_LV2.efk");
    public static final PCLEffekseerEFX MGC_EarthSpell_LV3 = new PCLEffekseerEFX("effects/MGC_EarthSpell_LV3.efk");
    public static final PCLEffekseerEFX MGC_EarthSpell_Projectile = new PCLEffekseerEFX("effects/MGC_EarthSpell_Projectile.efk");
    public static final PCLEffekseerEFX MGC_W2_BlueBall = new PCLEffekseerEFX("effects/MGC_W2_BlueBall.efk");
    public static final PCLEffekseerEFX MGC_W2_BlueBall_Projectile = new PCLEffekseerEFX("effects/MGC_W2_BlueBall_Projectile.efk");
    public static final PCLEffekseerEFX MGC_W2_BlueBeam = new PCLEffekseerEFX("effects/MGC_W2_BlueBeam.efk");
    public static final PCLEffekseerEFX MGC_W2_BlueBeam_BeamOnly = new PCLEffekseerEFX("effects/MGC_W2_BlueBeam_BeamOnly.efk");
    public static final PCLEffekseerEFX MGC_W2_BlueBeam_ChargeUp = new PCLEffekseerEFX("effects/MGC_W2_BlueBeam_ChargeUp.efk");
    public static final PCLEffekseerEFX MGC_W2_BlueFlame = new PCLEffekseerEFX("effects/MGC_W2_BlueFlame.efk");
    public static final PCLEffekseerEFX MGC_W2_ExploDome = new PCLEffekseerEFX("effects/MGC_W2_ExploDome.efk");
    public static final PCLEffekseerEFX MGC_W2_ExploDome_ExplosionOnly = new PCLEffekseerEFX("effects/MGC_W2_ExploDome_ExplosionOnly.efk");
    public static final PCLEffekseerEFX MGC_W2_ManaFire = new PCLEffekseerEFX("effects/MGC_W2_ManaFire.efk");
    public static final PCLEffekseerEFX MGC_W2_Shield_Apply = new PCLEffekseerEFX("effects/MGC_W2_Shield_Apply.efk");
    public static final PCLEffekseerEFX MGC_W2_Shield_Break = new PCLEffekseerEFX("effects/MGC_W2_Shield_Break.efk");
    public static final PCLEffekseerEFX MGC_W2_Shield_Loop = new PCLEffekseerEFX("effects/MGC_W2_Shield_Loop.efk");
    public static final PCLEffekseerEFX MGC_W2_Shield_OnHit = new PCLEffekseerEFX("effects/MGC_W2_Shield_OnHit.efk");
    public static final PCLEffekseerEFX MGC_W2_SuperSphereAttackImpact = new PCLEffekseerEFX("effects/MGC_W2_SuperSphereAttack - Impact.efk");
    public static final PCLEffekseerEFX MGC_W2_SuperSphereAttack = new PCLEffekseerEFX("effects/MGC_W2_SuperSphereAttack.efk");
    public static final PCLEffekseerEFX MGC_HealingSpell_LV1 = new PCLEffekseerEFX("effects/MGC_HealingSpell_LV1.efk");
    public static final PCLEffekseerEFX MGC_HealingSpell_LV2 = new PCLEffekseerEFX("effects/MGC_HealingSpell_LV2.efk");
    public static final PCLEffekseerEFX MGC_HealingSpell_LV3 = new PCLEffekseerEFX("effects/MGC_HealingSpell_LV3.efk");
    public static final PCLEffekseerEFX FIRE01 = new PCLEffekseerEFX("effects/Fire01.efk");
    public static final PCLEffekseerEFX FIRE02 = new PCLEffekseerEFX("effects/Fire02.efk");
    public static final PCLEffekseerEFX FIRE03 = new PCLEffekseerEFX("effects/Fire03.efk");
    public static final PCLEffekseerEFX FIRE04 = new PCLEffekseerEFX("effects/Fire04.efk");
    public static final PCLEffekseerEFX FIRE05 = new PCLEffekseerEFX("effects/Fire05.efk");
    public static final PCLEffekseerEFX FIRE06 = new PCLEffekseerEFX("effects/Fire06.efk");
    public static final PCLEffekseerEFX FIRE07 = new PCLEffekseerEFX("effects/Fire07.efk");
    public static final PCLEffekseerEFX FIRE08 = new PCLEffekseerEFX("effects/Fire08.efk");
    public static final PCLEffekseerEFX FIRE09 = new PCLEffekseerEFX("effects/Fire09.efk");
    public static final PCLEffekseerEFX FIRE10 = new PCLEffekseerEFX("effects/Fire10.efk");
    public static final PCLEffekseerEFX FIRE11 = new PCLEffekseerEFX("effects/Fire11.efk");
    public static final PCLEffekseerEFX FIRE12 = new PCLEffekseerEFX("effects/Fire12.efk");
    public static final PCLEffekseerEFX FIRE13 = new PCLEffekseerEFX("effects/Fire13.efk");
    public static final PCLEffekseerEFX FIRE14 = new PCLEffekseerEFX("effects/Fire14.efk");
    public static final PCLEffekseerEFX FIRE15 = new PCLEffekseerEFX("effects/Fire15.efk");
    public static final PCLEffekseerEFX FIRE16 = new PCLEffekseerEFX("effects/Fire16.efk");
    public static final PCLEffekseerEFX GUN01 = new PCLEffekseerEFX("effects/Gun01.efk");
    public static final PCLEffekseerEFX GUN02 = new PCLEffekseerEFX("effects/Gun02.efk");
    public static final PCLEffekseerEFX GUN03 = new PCLEffekseerEFX("effects/Gun03.efk");
    public static final PCLEffekseerEFX GUN04 = new PCLEffekseerEFX("effects/Gun04.efk");
    public static final PCLEffekseerEFX GUN05 = new PCLEffekseerEFX("effects/Gun05.efk");
    public static final PCLEffekseerEFX GUN06 = new PCLEffekseerEFX("effects/Gun06.efk");
    public static final PCLEffekseerEFX GUN07 = new PCLEffekseerEFX("effects/Gun07.efk");
    public static final PCLEffekseerEFX GUN08 = new PCLEffekseerEFX("effects/Gun08.efk");
    public static final PCLEffekseerEFX GUN09 = new PCLEffekseerEFX("effects/Gun09.efk");
    public static final PCLEffekseerEFX HOZYO01 = new PCLEffekseerEFX("effects/Hozyo01.efk");
    public static final PCLEffekseerEFX HOZYO02 = new PCLEffekseerEFX("effects/Hozyo02.efk");
    public static final PCLEffekseerEFX HOZYO03 = new PCLEffekseerEFX("effects/Hozyo03.efk");
    public static final PCLEffekseerEFX HOZYO04 = new PCLEffekseerEFX("effects/Hozyo04.efk");
    public static final PCLEffekseerEFX HOZYO05 = new PCLEffekseerEFX("effects/Hozyo05.efk");
    public static final PCLEffekseerEFX KAMEHAMEHA = new PCLEffekseerEFX("effects/Kamehameha.efk");
    public static final PCLEffekseerEFX LIGHT01 = new PCLEffekseerEFX("effects/Light01.efk");
    public static final PCLEffekseerEFX LIGHT02 = new PCLEffekseerEFX("effects/Light02.efk");
    public static final PCLEffekseerEFX LIGHT03 = new PCLEffekseerEFX("effects/Light03.efk");
    public static final PCLEffekseerEFX LIGHT04 = new PCLEffekseerEFX("effects/Light04.efk");
    public static final PCLEffekseerEFX MAGIC01 = new PCLEffekseerEFX("effects/Magic01.efk");
    public static final PCLEffekseerEFX MAGIC02 = new PCLEffekseerEFX("effects/Magic02.efk");
    public static final PCLEffekseerEFX SPEAR01 = new PCLEffekseerEFX("effects/Spear01.efk");
    public static final PCLEffekseerEFX SPEAR02 = new PCLEffekseerEFX("effects/Spear02.efk");
    public static final PCLEffekseerEFX SPEAR03 = new PCLEffekseerEFX("effects/Spear03.efk");
    public static final PCLEffekseerEFX SPEAR04 = new PCLEffekseerEFX("effects/Spear04.efk");
    public static final PCLEffekseerEFX SWORD01 = new PCLEffekseerEFX("effects/Sword01.efk");
    public static final PCLEffekseerEFX SWORD02 = new PCLEffekseerEFX("effects/Sword02.efk");
    public static final PCLEffekseerEFX SWORD03 = new PCLEffekseerEFX("effects/Sword03.efk");
    public static final PCLEffekseerEFX SWORD04 = new PCLEffekseerEFX("effects/Sword04.efk");
    public static final PCLEffekseerEFX SWORD05 = new PCLEffekseerEFX("effects/Sword05.efk");
    public static final PCLEffekseerEFX SWORD06 = new PCLEffekseerEFX("effects/Sword06.efk");
    public static final PCLEffekseerEFX SWORD07 = new PCLEffekseerEFX("effects/Sword07.efk");
    public static final PCLEffekseerEFX SWORD08 = new PCLEffekseerEFX("effects/Sword08.efk");
    public static final PCLEffekseerEFX SWORD09 = new PCLEffekseerEFX("effects/Sword09.efk");
    public static final PCLEffekseerEFX SWORD11 = new PCLEffekseerEFX("effects/Sword11.efk");
    public static final PCLEffekseerEFX SWORD12 = new PCLEffekseerEFX("effects/Sword12.efk");
    public static final PCLEffekseerEFX SWORD13 = new PCLEffekseerEFX("effects/Sword13.efk");
    public static final PCLEffekseerEFX SWORD14 = new PCLEffekseerEFX("effects/Sword14.efk");
    public static final PCLEffekseerEFX SWORD15 = new PCLEffekseerEFX("effects/Sword15.efk");
    public static final PCLEffekseerEFX SWORD16 = new PCLEffekseerEFX("effects/Sword16.efk");
    public static final PCLEffekseerEFX SWORD17 = new PCLEffekseerEFX("effects/Sword17.efk");
    public static final PCLEffekseerEFX SWORD18 = new PCLEffekseerEFX("effects/Sword18.efk");
    public static final PCLEffekseerEFX SWORD19 = new PCLEffekseerEFX("effects/Sword19.efk");
    public static final PCLEffekseerEFX SWORD20 = new PCLEffekseerEFX("effects/Sword20.efk");
    public static final PCLEffekseerEFX SWORD21 = new PCLEffekseerEFX("effects/Sword21.efk");
    public static final PCLEffekseerEFX SWORD22 = new PCLEffekseerEFX("effects/Sword22.efk");
    public static final PCLEffekseerEFX SWORD23 = new PCLEffekseerEFX("effects/Sword23.efk");
    public static final PCLEffekseerEFX SWORD24 = new PCLEffekseerEFX("effects/Sword24.efk");
    public static final PCLEffekseerEFX SWORD25 = new PCLEffekseerEFX("effects/Sword25.efk");
    public static final PCLEffekseerEFX SWORD26 = new PCLEffekseerEFX("effects/Sword26.efk");
    public static final PCLEffekseerEFX SWORD27 = new PCLEffekseerEFX("effects/Sword27.efk");
    public static final PCLEffekseerEFX SWORD28 = new PCLEffekseerEFX("effects/Sword28.efk");
    public static final PCLEffekseerEFX WIND01 = new PCLEffekseerEFX("effects/Wind01.efk");
    public static final PCLEffekseerEFX WIND02 = new PCLEffekseerEFX("effects/Wind02.efk");

    public final String ID;
    public final String path;

    public PCLEffekseerEFX(String path)
    {
        this(makeID(path), path);
    }

    public PCLEffekseerEFX(String id, String path)
    {
        this.path = path;
        this.ID = id;
        ALL.putIfAbsent(ID, this);
    }

    public static PCLEffekseerEFX get(String id)
    {
        return ALL.get(id);
    }

    public static void initialize()
    {
        STSEffekseerManager.register(EUIUtils.map(ALL.values(), v -> v.path));
    }

    public static Collection<PCLEffekseerEFX> sortedValues()
    {
        return ALL.values().stream().sorted((a, b) -> StringUtils.compare(a.ID, b.ID)).collect(Collectors.toList());
    }

    private static String makeID(String path)
    {
        String[] splitPath = path.split("/");
        return splitPath[splitPath.length - 1].split("\\.")[0];
    }
}
