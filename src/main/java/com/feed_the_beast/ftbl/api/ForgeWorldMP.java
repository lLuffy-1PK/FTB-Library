package com.feed_the_beast.ftbl.api;

import com.feed_the_beast.ftbl.api.events.ForgeWorldEvent;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.LMNBTUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import latmod.lib.LMJsonUtils;
import latmod.lib.LMUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by LatvianModder on 09.02.2016.
 */
public final class ForgeWorldMP extends ForgeWorld
{
    public static ForgeWorldMP inst = null;

    /**
     * Only used for capabilities, so they know which player currently is being saved / loaded
     */
    public static ForgePlayerMP currentPlayer;

    public ForgeWorldMP()
    {
        currentMode = PackModes.instance().getDefault();
    }

    @Override
    public Side getSide()
    {
        return Side.SERVER;
    }

    @Override
    public World getMCWorld()
    {
        return FTBLib.getServer().getEntityWorld();
    }

    @Override
    public ForgeWorldMP toWorldMP()
    {
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ForgeWorldSP toWorldSP()
    {
        return null;
    }

    @Override
    public ForgePlayerMP getPlayer(Object o)
    {
        if(o instanceof FakePlayer)
        {
            return new ForgePlayerFake((FakePlayer) o);
        }
        ForgePlayer p = super.getPlayer(o);
        return (p == null) ? null : p.toMP();
    }

    public void load() throws Exception
    {
        JsonElement worldData = LMJsonUtils.fromJson(new File(FTBLib.folderWorld, "world_data.json"));

        if(worldData.isJsonObject())
        {
            JsonObject group = worldData.getAsJsonObject();
            worldID = group.has("world_id") ? LMUtils.fromString(group.get("world_id").getAsString()) : null;
            getID();

            currentMode = group.has("mode") ? PackModes.instance().getMode(group.get("mode").getAsString()) : PackModes.instance().getDefault();
        }

        playerMap.clear();

        NBTTagCompound nbt = LMNBTUtils.readTag(new File(FTBLib.folderWorld, "data/FTBLib.dat"));

        if(nbt != null)
        {
            if(capabilities != null)
            {
                capabilities.deserializeNBT(nbt.getCompoundTag("ForgeCaps"));
            }

            MinecraftForge.EVENT_BUS.post(new ForgeWorldEvent.OnLoadedBeforePlayers(this));

            NBTTagList list = nbt.getTagList("Players", Constants.NBT.TAG_COMPOUND);

            for(int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                UUID id = LMUtils.fromString(tag.getString("UUID"));

                if(id != null)
                {
                    ForgePlayerMP p = new ForgePlayerMP(new GameProfile(id, tag.getString("Name")));
                    currentPlayer = p;
                    p.deserializeNBT(tag);
                    playerMap.put(id, p);
                }
            }

            currentPlayer = null;

            MinecraftForge.EVENT_BUS.post(new ForgeWorldEvent.OnPostLoaded(this));
        }
    }

    public void save() throws Exception
    {
        JsonObject group = new JsonObject();
        group.add("world_id", new JsonPrimitive(LMUtils.fromUUID(getID())));
        group.add("mode", new JsonPrimitive(currentMode.getID()));
        LMJsonUtils.toJson(new File(FTBLib.folderWorld, "world_data.json"), group);

        FTBLib.dev_logger.info("ForgeWorldMP Saved: " + group);

        NBTTagCompound tag = new NBTTagCompound();

        if(capabilities != null)
        {
            tag.setTag("ForgeCaps", capabilities.serializeNBT());
        }

        NBTTagList tagPlayers = new NBTTagList();

        for(ForgePlayer p : playerMap.values())
        {
            currentPlayer = p.toMP();
            NBTTagCompound tagP = p.toMP().serializeNBT();
            tagP.setString("Name", p.getProfile().getName());
            tagP.setString("UUID", p.getStringUUID());
            tagPlayers.appendTag(tagP);
        }

        currentPlayer = null;

        tag.setTag("Players", tagPlayers);

        LMNBTUtils.writeTag(new File(FTBLib.folderWorld, "data/FTBLib.dat"), tag);
        
		/*
        
		if(!customData.isEmpty())
		{
			JsonObject customGroup = new JsonObject();
			JsonObject group1;
			
			for(ForgeWorldData d : customData.values())
			{
				group1 = new JsonObject();
				d.saveData(group1);
				
				if(!group1.entrySet().isEmpty())
				{
					customGroup.add(d.getID(), group1);
				}
			}
			
			if(!customGroup.entrySet().isEmpty())
			{
				group.add("custom", customGroup);
			}
		}
		
		JsonObject group = new JsonObject();
		ForgeWorldMP.inst.save(group);
		LMJsonUtils.toJson(new File(ForgeWorldMP.inst.latmodFolder, "world.json"), group);
		
		NBTTagCompound tag = new NBTTagCompound();
		
		for(ForgePlayer p : LMMapUtils.values(ForgeWorldMP.inst.playerMap, null))
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			p.toPlayerMP().writeToServer(tag1);
			tag1.setString("Name", p.getProfile().getName());
			tag.setTag(p.getStringUUID(), tag1);
		}
		
		LMNBTUtils.writeTag(new File(ForgeWorldMP.inst.latmodFolder, "LMPlayers.dat"), tag);
		
		// Export player list //
		
		try
		{
			ArrayList<String> l = new ArrayList<>();
			ArrayList<ForgePlayer> players1 = new ArrayList<>();
			players1.addAll(ForgeWorldMP.inst.playerMap.values());
			Collections.sort(players1);
			
			for(ForgePlayer p : players1)
			{
				l.add(p.getStringUUID() + " :: " + p.getProfile().getName());
			}
			
			LMFileUtils.save(new File(ForgeWorldMP.inst.latmodFolder, "LMPlayers.txt"), l);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		*/
    }

    public void writeDataToNet(NBTTagCompound tag, ForgePlayerMP self, boolean login)
    {
        tag.setLong("IDM", getID().getMostSignificantBits());
        tag.setLong("IDL", getID().getLeastSignificantBits());
        tag.setString("M", currentMode.getID());

        NBTTagCompound tag1, tag2;

        if(login)
        {
            tag1 = new NBTTagCompound();

            Collection<ForgePlayerMP> onlinePlayers = new HashSet<>();
            for(ForgePlayer p : playerMap.values())
            {
                tag1.setString(p.getStringUUID(), p.getProfile().getName());
                if(p.isOnline() && !p.equalsPlayer(self))
                {
                    onlinePlayers.add(p.toMP());
                }
            }

            if(!tag1.hasNoTags())
            {
                tag.setTag("PM", tag1);
            }

            tag1 = new NBTTagCompound();

            for(ForgePlayerMP p : onlinePlayers)
            {
                tag2 = new NBTTagCompound();
                p.writeToNet(tag2, false);
                tag1.setTag(p.getStringUUID(), tag2);
            }

            tag2 = new NBTTagCompound();
            self.writeToNet(tag2, true);
            tag1.setTag(self.getStringUUID(), tag2);

            if(!tag1.hasNoTags())
            {
                tag.setTag("PMD", tag1);
            }
        }
    }

    public Collection<ForgePlayerMP> getServerPlayers()
    {
        Collection<ForgePlayerMP> list = new HashSet<>();
        for(ForgePlayer p : playerMap.values())
        {
            list.add(p.toMP());
        }
        return list;
    }
}