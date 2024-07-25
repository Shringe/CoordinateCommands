package com.github.coordinatecommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.minecraft.text.ClickEvent.Action.COPY_TO_CLIPBOARD;

public class CoordinateCommands implements ClientModInitializer {
	public static final String MOD_ID = "coordinatecommands";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ModConfig config;
	private final CoordinateHelper coordinateHelper = new CoordinateHelper();

	@Override
	public void onInitializeClient() {
		LOGGER.atDebug();

		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		this.config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		ClientCommandRegistrationCallback.EVENT.register(this::initializeCommands);
	}

	private void initializeCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		LOGGER.info("Initializing commands");
		if (coordinateHelper.player == null) {
			LOGGER.debug("coordinateHelper.player is null");
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null) {
				LOGGER.debug("MinecraftClient.getInstance().player is null. returning");
				return;
			} else {
				LOGGER.info("Player Initialized");
				coordinateHelper.player = player;
			}
		}


		dispatcher.register(literal("coords")
				.executes(context -> {
					coordinateHelper.setPlayerPosition();

					BlockPos position = coordinateHelper.getCurrentPosition();
					context.getSource().sendFeedback(copyableText(format(position), 0xffffff));
					return 1;
				})
				.then(literal("nether")
						.executes(context -> {
							coordinateHelper.setPlayerPosition();

							BlockPos position = coordinateHelper.getCurrentPosition();
							RegistryKey<World> dimension = MinecraftClient.getInstance().player.getWorld().getRegistryKey();

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
							coordinateHelper.setPlayerPosition();

							BlockPos position = coordinateHelper.getCurrentPosition();
							RegistryKey<World> dimension = MinecraftClient.getInstance().player.getWorld().getRegistryKey();

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
							coordinateHelper.setPlayerPosition();

							BlockPos position = coordinateHelper.getCurrentPosition();
							RegistryKey<World> dimension = MinecraftClient.getInstance().player.getWorld().getRegistryKey();

							context.getSource().sendFeedback(copyableText(format(getReverseCords(position, dimension)), 0xcc9900));
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

													context.getSource().sendFeedback(copyableText(format(getReverseCords(position, World.NETHER)), 0xcc9900));
													return 1;
												})
										)
								)
						)
				)
				.then(literal("point")
						.executes(context -> {
							BlockPos position = coordinateHelper.getPlayerPosition();
							if (coordinateHelper.point1 == null) {
								coordinateHelper.point1 = position;
							} else {
								coordinateHelper.point2 = position;
								context.getSource().sendFeedback(copyableText(String.valueOf(coordinateHelper.pointDistance()) + " blocks away", 0xffffff));

								coordinateHelper.clearPoints();
							}
							return 1;
						})
				)
				.then(literal("debug")
						.executes(context -> {
							if (coordinateHelper.player == null) {
								context.getSource().sendFeedback(Text.literal("Player is null"));
							} else {
								context.getSource().sendFeedback(Text.literal("Player is not null"));
							}
							return 1;
						})
				)
		);
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