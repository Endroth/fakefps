package dev.fpsfake;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("fpsfake")
public class FpsFakeMod {
	public FpsFakeMod() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ClientSetup.init();
		}
	}
}
