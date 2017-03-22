package com.feed_the_beast.ftbl.cmd;

import com.feed_the_beast.ftbl.api.EnumReloadType;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibIntegrationInternal;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CmdReload extends CommandLM
{
    @Override
    public String getName()
    {
        return "reload";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        FTBLibIntegrationInternal.API.reload(sender, EnumReloadType.SERVER_COMMAND);
    }
}