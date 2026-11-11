package dev.fpsfake;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
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

	@SubscribeEvent
	public void onCommands(RegisterClientCommandsEvent e) {
		CommandDispatcher<CommandSourceStack> d = e.getDispatcher();

		d.register(Commands.literal("fpsfake")
			.executes(c -> reply(c, FpsFake.commandFpsfake(new String[0])))
			.then(Commands.literal("on").executes(c -> reply(c, FpsFake.commandFpsfake(new String[] {"on"}))))
			.then(Commands.literal("off").executes(c -> reply(c, FpsFake.commandFpsfake(new String[] {"off"})))));

		d.register(Commands.literal("fps")
			.executes(c -> reply(c, FpsFake.commandFps(new String[0])))
			.then(Commands.argument("min", IntegerArgumentType.integer(1))
				.then(Commands.argument("max", IntegerArgumentType.integer(1))
					.executes(c -> reply(c, FpsFake.commandFps(new String[] {
						String.valueOf(IntegerArgumentType.getInteger(c, "min")),
						String.valueOf(IntegerArgumentType.getInteger(c, "max"))
					}))))));
	}

	private static int reply(CommandContext<CommandSourceStack> c, String msg) {
		for (String line : msg.split("\n")) {
			c.getSource().sendSystemMessage(Component.literal(line));
		}
		return 1;
	}
}
