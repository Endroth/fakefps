package dev.fpsfake;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;

/** Só é carregada no cliente. */
public final class ClientSetup {
	public static void init() {
		FpsFake.init(FMLPaths.CONFIGDIR.get().resolve("fpsfake.properties"));
		MinecraftForge.EVENT_BUS.register(new ClientSetup());
	}

	/** Todo frame, antes de desenhar: troca o número de FPS da linha do F3. */
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent e) {
		if (e.phase != TickEvent.Phase.START || !FpsFake.isEnabled()) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc.fpsString != null) {
			mc.fpsString = FpsFake.patch(mc.fpsString);
		}
	}

	/** 1.16.5 não tem comandos de cliente no Forge: lê o chat e cancela o envio. */
	@SubscribeEvent
	public void onChat(ClientChatEvent e) {
		String msg = e.getMessage().trim();
		String[] parts = msg.split("\\s+");
		if (parts.length == 0) {
			return;
		}
		String[] args = new String[parts.length - 1];
		System.arraycopy(parts, 1, args, 0, args.length);

		String reply;
		if (parts[0].equalsIgnoreCase("/fpsfake")) {
			reply = FpsFake.commandFpsfake(args);
		} else if (parts[0].equalsIgnoreCase("/fps")) {
			reply = FpsFake.commandFps(args);
		} else {
			return;
		}
		e.setCanceled(true);
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			for (String line : reply.split("\n")) {
				mc.player.sendMessage(new StringTextComponent(line), new UUID(0L, 0L));
			}
		}
	}
}
