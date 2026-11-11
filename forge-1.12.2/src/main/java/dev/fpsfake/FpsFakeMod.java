package dev.fpsfake;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = "fpsfake", name = "FPS Fake", version = "1.1.0", clientSideOnly = true, acceptedMinecraftVersions = "[1.12.2]")
public class FpsFakeMod {

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		File cfg = new File(Minecraft.getMinecraft().gameDir, "config/fpsfake.properties");
		FpsFake.init(cfg.toPath());

		ClientCommandHandler.instance.registerCommand(new Cmd("fpsfake", false));
		ClientCommandHandler.instance.registerCommand(new Cmd("fps", true));
		MinecraftForge.EVENT_BUS.register(this);
	}

	/** Todo frame, antes de desenhar: troca o número de FPS da linha do F3. */
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent e) {
		if (e.phase != TickEvent.Phase.START || !FpsFake.isEnabled()) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.debug != null) {
			mc.debug = FpsFake.patch(mc.debug);
		}
	}

	private static final class Cmd extends CommandBase {
		private final String name;
		private final boolean fps;

		Cmd(String name, boolean fps) {
			this.name = name;
			this.fps = fps;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return fps ? "/fps <min> <max>" : "/fpsfake on|off";
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			String reply = fps ? FpsFake.commandFps(args) : FpsFake.commandFpsfake(args);
			for (String line : reply.split("\n")) {
				sender.sendMessage(new TextComponentString(line));
			}
		}
	}
}
