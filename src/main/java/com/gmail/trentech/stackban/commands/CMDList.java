package com.gmail.trentech.stackban.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.stackban.utils.ConfigManager;
import com.gmail.trentech.stackban.utils.Help;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class CMDList implements CommandExecutor {

	public CMDList() {
		Help help = new Help("list", "list", " List all banned items");
		help.setSyntax(" /sban list <world>\n /sb ls <world>");
		help.setExample(" /sban list world");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String worldName = args.<String>getOne("world").get();

		if(!Sponge.getServer().getWorld(worldName).isPresent() && !worldName.equalsIgnoreCase("global")) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " does not exist"), false);
		}
		
		List<Text> list = new ArrayList<>();

		for (Entry<Object, ? extends CommentedConfigurationNode> item : ConfigManager.get(worldName).getConfig().getNode("items").getChildrenMap().entrySet()) {
			list.add(Text.of(TextColors.GREEN, item.getValue().getKey().toString()));
			list.add(Text.of(TextColors.YELLOW, "  - break: ", TextColors.WHITE, item.getValue().getNode("break").getBoolean()));
			list.add(Text.of(TextColors.YELLOW, "  - craft: ", TextColors.WHITE, item.getValue().getNode("craft").getBoolean()));
			list.add(Text.of(TextColors.YELLOW, "  - drop: ", TextColors.WHITE, item.getValue().getNode("drop").getBoolean()));
			list.add(Text.of(TextColors.YELLOW, "  - hold: ", TextColors.WHITE, item.getValue().getNode("hold").getBoolean()));
			list.add(Text.of(TextColors.YELLOW, "  - modify: ", TextColors.WHITE, item.getValue().getNode("modify").getBoolean()));
			list.add(Text.of(TextColors.YELLOW, "  - pickup: ", TextColors.WHITE, item.getValue().getNode("pickup").getBoolean()));
			list.add(Text.of(TextColors.YELLOW, "  - place: ", TextColors.WHITE, item.getValue().getNode("place").getBoolean()));
			list.add(Text.of(TextColors.YELLOW, "  - use: ", TextColors.WHITE, item.getValue().getNode("use").getBoolean()));
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

			pages.linesPerPage(20).title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Items")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}

		return CommandResult.success();
	}
	
}
