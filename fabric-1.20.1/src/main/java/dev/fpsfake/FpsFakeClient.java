package dev.fpsfake;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public class FpsFakeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FpsFake.init(FabricLoader.getInstance().getConfigDir().resolve("fpsfake.properties"));

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("fpsfake")
				.executes(c -> reply(c, FpsFake.commandFpsfake(new String[0])))
				.then(ClientCommandManager.literal("on").executes(c -> reply(c, FpsFake.commandFpsfake(new String[] {"on"}))))
				.then(ClientCommandManager.literal("off").executes(c -> reply(c, FpsFake.commandFpsfake(new String[] {"off"})))));

			dispatcher.register(ClientCommandManager.literal("fps")
				.executes(c -> reply(c, FpsFake.commandFps(new String[0])))
				.then(ClientCommandManager.argument("min", IntegerArgumentType.integer(1))
					.then(ClientCommandManager.argument("max", IntegerArgumentType.integer(1))
						.executes(c -> reply(c, FpsFake.commandFps(new String[] {
							String.valueOf(IntegerArgumentType.getInteger(c, "min")),
							String.valueOf(IntegerArgumentType.getInteger(c, "max"))
						}))))));
		});
	}

	private static int reply(CommandContext<FabricClientCommandSource> c, String msg) {
		for (String line : msg.split("\n")) {
			c.getSource().sendFeedback(Text.literal(line));
		}
		return 1;
	}
}
