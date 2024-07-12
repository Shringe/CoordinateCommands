package com.github.coordinatecommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.minecraft.text.ClickEvent.Action.COPY_TO_CLIPBOARD;

public class CoordinateCommands implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerCommands();
	}

	private void registerCommands() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("coords")
			.executes(context -> {
				ClientPlayerEntity player = MinecraftClient.getInstance().player;
				BlockPos position = player.getBlockPos();

				context.getSource().sendFeedback(copyableText(format(position), 0xffffff));
				return 1;
			})
			.then(literal("nether")
				.executes(context -> {
					ClientPlayerEntity player = MinecraftClient.getInstance().player;
					BlockPos position = player.getBlockPos();
					RegistryKey<World> dimension = player.getWorld().getRegistryKey();

					context.getSource().sendFeedback(copyableText(format(getNetherCords(position, dimension)), 0x990033));
					return 1;
				})
				.then(argument("x", IntegerArgumentType.integer())
					.then(argument("y", IntegerArgumentType.integer())
						.then(argument("z", IntegerArgumentType.integer())
							.executes(context -> {
								BlockPos position = new BlockPos(
										IntegerArgumentType.getInteger(context, "x"),
										IntegerArgumentType.getInteger(context, "y"),
										IntegerArgumentType.getInteger(context, "z")
								);

								context.getSource().sendFeedback(copyableText(format(getNetherCords(position, World.OVERWORLD)), 0x990033));
								return 1;
							})
						)
					)
				)
			)
			.then(literal("overworld")
				.executes(context -> {
					ClientPlayerEntity player = MinecraftClient.getInstance().player;

					BlockPos position = player.getBlockPos();
					RegistryKey<World> dimension = player.getWorld().getRegistryKey();
					context.getSource().sendFeedback(copyableText(format(getOverworldCords(position, dimension)), 0x00cc00));
					return 1;
				})
				.then(argument("x", IntegerArgumentType.integer())
					.then(argument("y", IntegerArgumentType.integer())
						.then(argument("z", IntegerArgumentType.integer())
							.executes(context -> {
								BlockPos position = new BlockPos(
										IntegerArgumentType.getInteger(context, "x"),
										IntegerArgumentType.getInteger(context, "y"),
										IntegerArgumentType.getInteger(context, "z")
								);

								context.getSource().sendFeedback(copyableText(format(getOverworldCords(position, World.NETHER)), 0x00cc00));
								return 1;
							})
						)
					)
				)
			)
			.then(literal("reverse")
				.executes(context -> {
					ClientPlayerEntity player = MinecraftClient.getInstance().player;
					BlockPos position = player.getBlockPos();
					RegistryKey<World> dimension = player.getWorld().getRegistryKey();

					context.getSource().sendFeedback(copyableText(format(getReverseCords(position, dimension)), 0xcc9900));
					return 1;
				})
				.then(argument("x", IntegerArgumentType.integer())
					.then(argument("y", IntegerArgumentType.integer())
						.then(argument("z", IntegerArgumentType.integer())
							.executes(context -> {
								ClientPlayerEntity player = MinecraftClient.getInstance().player;
								RegistryKey<World> dimension = player.getWorld().getRegistryKey();

								BlockPos position = new BlockPos(
										IntegerArgumentType.getInteger(context, "x"),
										IntegerArgumentType.getInteger(context, "y"),
										IntegerArgumentType.getInteger(context, "z")
								);

								context.getSource().sendFeedback(copyableText(format(getReverseCords(position, dimension)), 0xcc9900));
								return 1;
							})
						)
					)
				)
			)
		));
	}

	private MutableText copyableText(String text, int color) {
		return Text.literal(text).setStyle(Style.EMPTY
				.withColor(TextColor.fromRgb(color))
				.withBold(false)
				.withUnderline(true)
				.withClickEvent(new ClickEvent(COPY_TO_CLIPBOARD, text)));
	}

	private String format(BlockPos pos) {
		return String.format("%s %s %s", pos.getX(), pos.getY(), pos.getZ());
	}

	private BlockPos getNetherCords(BlockPos pos, RegistryKey<World> dimension) {
		if (dimension.equals(World.NETHER)) {
			return pos;
		} else {
			return new BlockPos(pos.getX() / 8, pos.getY(), pos.getZ() / 8);
		}
	}

	private BlockPos getOverworldCords(BlockPos pos, RegistryKey<World> dimension) {
		if (dimension.equals(World.NETHER)) {
			return new BlockPos(pos.getX() * 8, pos.getY(), pos.getZ() * 8);
		} else {
			return pos;
		}
	}

	private BlockPos getReverseCords(BlockPos pos, RegistryKey<World> dimension) {
		if (dimension.equals(World.NETHER)) {
			return getOverworldCords(pos, dimension);
		} else {
			return getNetherCords(pos, dimension);
		}
	}
}