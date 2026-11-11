package dev.fpsfake.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.fpsfake.FpsFake;
import net.minecraft.client.gui.hud.DebugHud;

@Mixin(DebugHud.class)
public class DebugHudMixin {
	/** Chamado todo frame com F3 aberto: troca o número na linha "NNN fps ...". */
	@Inject(method = "getLeftText", at = @At("RETURN"))
	private void fpsfake$patch(CallbackInfoReturnable<List<String>> cir) {
		if (!FpsFake.isEnabled()) {
			return;
		}
		List<String> lines = cir.getReturnValue();
		for (int i = 0; i < lines.size(); i++) {
			lines.set(i, FpsFake.patch(lines.get(i)));
		}
	}
}
