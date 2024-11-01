package com.natamus.shadowmounts.data;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;

import java.util.Random;

public class Constants {
    public static final Minecraft mc = Minecraft.getInstance();
    public static final Random random = new Random();

    public static final int shadowSaddleFlag = 128;
    public static final int shadowMountColour = ARGB.color(169, 169, 169, 150);
    public static final int shadowMountMarkingsColour = ARGB.color(89, 220, 226, 200);
}
