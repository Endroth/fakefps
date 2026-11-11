package dev.fpsfake;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = "fpsfake", name = "FPS Fake", version = "1.1.0", clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class FpsFakeMod {

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		File cfg = new File(Minecraft.getMinecraft().mcDataDir, "config/fpsfake.properties");
		FpsFake.init(cfg.toPath());

		ClientCommandHandler.instance.registerCommand(new Cmd("fpsfake", false));
		ClientCommandHandler.instance.registerCommand(new Cmd("fps", true));
		FMLCommonHandler.instance().bus().register(this);
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
		public String getCommandName() {
			return name;
		}

		@Override
		public String getCommandUsage(ICommandSender sender) {
			return fps ? "/fps <min> <max>" : "/fpsfake on|off";
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 0;
		}

		@Override
		public boolean canCommandSenderUseCommand(ICommandSender sender) {
			return true;
		}

		@Override
		public void processCommand(ICommandSender sender, String[] args) {
			String reply = fps ? FpsFake.commandFps(args) : FpsFake.commandFpsfake(args);
			for (String line : reply.split("\n")) {
				sender.addChatMessage(new ChatComponentText(line));
			}
		}
	}
}
