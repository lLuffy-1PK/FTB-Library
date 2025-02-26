package com.feed_the_beast.ftblib.lib.command;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

/**
 * @author LatvianModder
 */
public class CommandUtils {
    public static CommandException error(ITextComponent component) {
        return new CommandException("disconnect.genericReason", component);
    }

    public static ForgePlayer getForgePlayer(ICommandSender sender) throws CommandException {
        ForgePlayer p = Universe.get().getPlayer(sender);

        if (p.isFake()) {
            throw new CommandException("commands.generic.player.notFound", sender.getName());
        }

        return p;
    }

    public static ForgePlayer getForgePlayer(ICommandSender sender, String name) throws CommandException {
        ForgePlayer p;

        switch (name) {
            case "@r": {
                ForgePlayer[] players = Universe.get().getOnlinePlayers().toArray(new ForgePlayer[0]);
                p = players.length == 0 ? null : players[MathUtils.RAND.nextInt(players.length)];
                break;
            }
            case "@ra": {
                ForgePlayer[] players = Universe.get().getPlayers().toArray(new ForgePlayer[0]);
                p = players.length == 0 ? null : players[MathUtils.RAND.nextInt(players.length)];
                break;
            }
            case "@p": {
                if (sender instanceof EntityPlayerMP && !ServerUtils.isFake((EntityPlayerMP) sender)) {
                    return Universe.get().getPlayer(sender);
                }

                p = null;
                double dist = Double.POSITIVE_INFINITY;

                for (ForgePlayer p1 : Universe.get().getOnlinePlayers()) {
                    if (p == null) {
                        p = p1;
                    } else {
                        Vec3d pos = sender.getPositionVector();
                        double d = p1.getPlayer().getDistanceSq(pos.x, pos.y, pos.z);

                        if (d < dist) {
                            dist = d;
                            p = p1;
                        }
                    }
                }
                break;
            }
            default: {
                EntityPlayerMP e = EntitySelector.matchOneEntity(sender, name, EntityPlayerMP.class);

                if (e == null) {
                    p = Universe.get().getPlayer(name);
                } else {
                    p = Universe.get().getPlayer(e);
                }
            }
        }

        if (p == null || p.isFake()) {
            throw new CommandException("commands.generic.player.notFound", name);
        }

        return p;
    }

    public static ForgeTeam getTeam(ICommandSender sender, String id) throws CommandException {
        ForgeTeam team = Universe.get().getTeam(id);

        if (team.isValid()) {
            return team;
        }

        throw FTBLib.error(sender, "ftblib.lang.team.error.not_found", id);
    }

    public static ForgePlayer getSelfOrOther(ICommandSender sender, String[] args, int index) throws CommandException {
        return getSelfOrOther(sender, args, index, "");
    }

    public static ForgePlayer getSelfOrOther(ICommandSender sender, String[] args, int index, String specialPermForOther) throws CommandException {
        if (args.length <= index) {
            return getForgePlayer(sender);
        }

        ForgePlayer p = getForgePlayer(sender, args[index]);

        if (!specialPermForOther.isEmpty() && sender instanceof EntityPlayerMP && !p.getId().equals(((EntityPlayerMP) sender).getUniqueID()) && !PermissionAPI.hasPermission((EntityPlayerMP) sender, specialPermForOther)) {
            throw new CommandException("commands.generic.permission");
        }

        return p;
    }

    public static List<String> getDimensionNames() {
        List<String> list = new ArrayList<>();
        list.add("all");
        list.add("overworld");
        list.add("nether");
        list.add("end");

        for (Integer dim : DimensionManager.getStaticDimensionIDs()) {
            if (dim != null && (dim < -1 || dim > 1)) {
                list.add(dim.toString());
            }
        }

        return list;
    }

    public static OptionalInt parseDimension(ICommandSender sender, String[] args, int index) throws CommandException {
        if (args.length <= index) {
            return OptionalInt.empty();
        }

        switch (args[index].toLowerCase()) {
            case "overworld":
            case "0":
                return OptionalInt.of(0);
            case "nether":
            case "-1":
                return OptionalInt.of(-1);
            case "end":
            case "1":
                return OptionalInt.of(1);
            case "this":
            case "~":
                return OptionalInt.of(sender.getEntityWorld().provider.getDimension());
            case "all":
            case "*":
                return OptionalInt.empty();
            default:
                return OptionalInt.of(CommandBase.parseInt(args[index]));
        }
    }
}