package org.itzstonlex.recon.minecraft.util;

public enum MinecraftVersion {
    
    V_UNKNOWN(-1),

    //--1.8--//
    V_1_8(47),
    V_1_8_1(47),
    V_1_8_2(47),
    V_1_8_3(47),
    V_1_8_4(47),
    V_1_8_5(47),
    V_1_8_6(47),
    V_1_8_7(47),
    V_1_8_8(47),
    V_1_8_9(47),

    //--1.9--//
    V_1_9(107),
    V_1_9_1(108),
    V_1_9_2(109),
    V_1_9_3(110),
    V_1_9_4(110),

    //--1.10--//
    V_1_10(210),
    V_1_10_1(210),
    V_1_10_2(210),

    //--1.11--//
    V_1_11(315),
    V_1_11_1(316),
    V_1_11_2(360),

    //--1.12--//
    V_1_12(335),
    V_1_12_1(338),
    V_1_12_2(340),

    //--1.13--//
    V_1_13(393),
    V_1_13_1(401),
    V_1_13_2(404),

    //--1.14--//
    V_1_14(477),
    V_1_14_1(480),
    V_1_14_2(485),
    V_1_14_3(490),
    V_1_14_4(498),

    //--1.15--//
    V_1_15(573),
    V_1_15_1(575),
    V_1_15_2(578),

    //--1.16--//
    V_1_16(735),
    V_1_16_1(736),
    V_1_16_2(751),
    V_1_16_3(753),
    V_1_16_4(754),
    V_1_16_5(754),

    //--1.17--//
    V_1_17(755),
    V_1_17_1(756),
    ;

    public static MinecraftVersion getByVersionId(int versionId) {
        for (MinecraftVersion clientVersion : MinecraftVersion.values()) {

            if (clientVersion.versionId == versionId) {
                return clientVersion;
            }
        }

        return MinecraftVersion.V_UNKNOWN;
    }


    private final int versionId;

    MinecraftVersion(int versionId) {
        this.versionId = versionId;
    }

    public String toClientName() {
        return name().substring(2).replace("_", ".");
    }

}
