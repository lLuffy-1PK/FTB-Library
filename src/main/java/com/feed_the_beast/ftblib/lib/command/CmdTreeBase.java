package com.feed_the_beast.ftblib.lib.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

/**
 * @author LatvianModder
 */
public class CmdTreeBase extends CommandTreeBase implements ICommandWithParent {
    private final String name;
    private ICommand parent;

    public CmdTreeBase(String n) {
        name = n;
    }

    @Override
    public void addSubcommand(ICommand command) {
        super.addSubcommand(command);

        if (command instanceof ICommandWithParent) {
            ((ICommandWithParent) command).setParent(this);
        }
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public int getRequiredPermissionLevel() {
        int level = 4;

        for (ICommand command : getSubCommands()) {
            if (command instanceof CommandBase) {
                level = Math.min(level, ((CommandBase) command).getRequiredPermissionLevel());
            }
        }

        return level;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        for (ICommand command : getSubCommands()) {
            if (command.checkPermission(server, sender)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ICommand getParent() {
        return parent;
    }

    @Override
    public void setParent(ICommand c) {
        parent = c;
    }
}