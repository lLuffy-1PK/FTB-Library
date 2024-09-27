package com.feed_the_beast.ftblib.lib.data;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.events.player.*;
import com.feed_the_beast.ftblib.events.team.ForgeTeamCreatedEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamPlayerJoinedEvent;
import com.feed_the_beast.ftblib.lib.EnumTeamColor;
import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.IConfigCallback;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.icon.PlayerHeadIcon;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.EnumPrivacyLevel;
import com.feed_the_beast.ftblib.net.MessageSyncData;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.server.permission.context.PlayerContext;
import net.minecraftforge.server.permission.context.WorldContext;

import javax.annotation.Nullable;
import java.util.UUID;

import static tech.funkyra.ftb.collections.PlayersCollection.getPlayerIfExists;
import static tech.funkyra.ftb.collections.PlayersCollection.updatePlayer;

/**
 * @author LatvianModder
 */
public class ForgePlayer implements INBTSerializable<NBTTagCompound>, Comparable<ForgePlayer>, IConfigCallback {
	private static FakePlayer playerForStats;

	public GameProfile profile;
	private final NBTDataStorage dataStorage;
	public ForgeTeam team;
	private boolean hideTeamNotification;
	public NBTTagCompound cachedPlayerNBT;
	private ConfigGroup cachedConfig;
	public long lastTimeSeen;
	public boolean needsSaving;
	public EntityPlayerMP tempPlayer;

	public ForgePlayer(Universe u, GameProfile p) {
		profile = p;
		dataStorage = new NBTDataStorage();
		team = u.getTeam("");
		hideTeamNotification = false;
		new ForgePlayerDataEvent(this, dataStorage).post();
		needsSaving = false;
	}

	public ForgePlayer(Universe u, UUID id, String name) {
		this(u, new GameProfile(id, name));
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("HideTeamNotification", hideTeamNotification);
		nbt.setLong("LastTimeSeen", lastTimeSeen);
		nbt.setTag("Data", dataStorage.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		hideTeamNotification = nbt.getBoolean("HideTeamNotification");
		lastTimeSeen = nbt.getLong("LastTimeSeen");
		dataStorage.deserializeNBT(nbt.getCompoundTag("Data"));
	}

	public void clearCache() {
		cachedPlayerNBT = null;
		cachedConfig = null;
		dataStorage.clearCache();
	}

	public void markDirty() {
		needsSaving = true;
		team.universe.checkSaving = true;
	}

	public boolean hasTeam() {
		return team.isValid();
	}

	public GameProfile getProfile() {
		return profile;
	}

	public final UUID getId() {
		return profile.getId();
	}

	public final String getName() {
		return profile.getName();
	}

	public final String getDisplayNameString() {
		if (isOnline()) {
			try {
				return getPlayer().getDisplayNameString();
			} catch (Exception ex) {
			}
		}

		return getName();
	}

	public final ITextComponent getDisplayName() {
		if (isOnline()) {
			try {
				return getPlayer().getDisplayName();
			} catch (Exception ex) {
			}
		}

		return new TextComponentString(getName());
	}

	public EntityPlayerMP getCommandPlayer(ICommandSender sender) throws CommandException {
		if (!isOnline()) {
			throw FTBLib.error(sender, "player_must_be_online");
		}

		return getPlayer();
	}

	public NBTDataStorage getData() {
		return dataStorage;
	}

	public boolean equalsPlayer(@Nullable ForgePlayer player) {
		return player == this || (player != null && getId().equals(player.getId()));
	}

	public boolean equalsPlayer(@Nullable ICommandSender player) {
		return player instanceof EntityPlayerMP && ((EntityPlayerMP) player).getUniqueID().equals(getId());
	}

	@Override
	public final int compareTo(ForgePlayer o) {
		return StringUtils.IGNORE_CASE_COMPARATOR.compare(getDisplayNameString(), o.getDisplayNameString());
	}

	public final String toString() {
		return getName();
	}

	public final int hashCode() {
		return getId().hashCode();
	}

	public boolean equals(Object o) {
		return o == this || o instanceof ForgePlayer && equalsPlayer((ForgePlayer) o);
	}

	public boolean canInteract(@Nullable ForgePlayer owner, EnumPrivacyLevel level) {
		if (level == EnumPrivacyLevel.PUBLIC || owner == null) {
			return true;
		} else if (owner.equalsPlayer(this)) {
			return true;
		} else if (level == EnumPrivacyLevel.PRIVATE) {
			return false;
		} else if (level == EnumPrivacyLevel.TEAM) {
			return owner.team.isAlly(this);
		}

		return false;
	}

	public boolean isOnline() {
		return getNullablePlayer() != null;
	}

	@Nullable
	public EntityPlayerMP getNullablePlayer() {
		if (tempPlayer != null) {
			return tempPlayer;
		}

		return team.universe.server.getPlayerList().getPlayerByUUID(getId());
	}

	public EntityPlayerMP getPlayer() {
		EntityPlayerMP p = getNullablePlayer();

		if (p == null) {
			throw new NullPointerException(getName() + " is not online!");
		}

		return p;
	}

	public boolean isFake() {
		return tempPlayer instanceof FakePlayer;
	}

	public boolean isOP() {
		return ServerUtils.isOP(team.universe.server, getProfile());
	}

	void onLoggedIn(EntityPlayerMP player, Universe universe, boolean firstLogin) {
		tempPlayer = player;

		boolean sendTeamJoinEvent = false, sendTeamCreatedEvent = false;

		if (firstLogin) {
			sendTeamCreatedEvent = true;
			sendTeamJoinEvent = true;
		}

		if (!isFake()) {
			lastTimeSeen = universe.ticks.ticks();
			//FTBLibStats.updateLastSeen(stats());
			new MessageSyncData(true, player, this).sendTo(player);
		}

		new ForgePlayerLoggedInEvent(this).post();

		if (sendTeamCreatedEvent) {
			new ForgeTeamCreatedEvent(team).post();
		}

		if (sendTeamJoinEvent) {
			ForgeTeamPlayerJoinedEvent event = new ForgeTeamPlayerJoinedEvent(this);
			event.post();

			if (event.getDisplayGui() != null) {
				event.getDisplayGui().run();
			}
		}

		if (!hideTeamNotification() && !hasTeam()) {
			ITextComponent b1 = FTBLib.lang(player, "click_here");
			b1.getStyle().setColor(TextFormatting.GOLD);
			b1.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftblib_simulate_button custom:ftblib:my_team_gui"));
			b1.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, FTBLib.lang(player, "sidebar_button.ftblib.my_team")));
			ITextComponent b2 = FTBLib.lang(player, "click_here");
			b2.getStyle().setColor(TextFormatting.GOLD);
			b2.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/my_settings " + FTBLib.MOD_ID + ".hide_team_notification toggle"));
			b2.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, FTBLib.lang(player, "ftblib.lang.team.notification.hide")));
			player.sendMessage(FTBLib.lang(player, "ftblib.lang.team.notification", b1, b2));
		}

		universe.clearCache();
		tempPlayer = null;
		markDirty();
	}

	void onLoggedOut(EntityPlayerMP p) {
		tempPlayer = p;
		lastTimeSeen = p.world.getTotalWorldTime();
		new ForgePlayerLoggedOutEvent(this).post();
		clearCache();
		tempPlayer = null;
		markDirty();
	}

	public StatisticsManagerServer stats() {
		if (playerForStats == null) {
			playerForStats = new FakePlayer(team.universe.world, ServerUtils.FAKE_PLAYER_PROFILE);
		}

		playerForStats.setWorld(team.universe.world);
		playerForStats.setUniqueId(getId());
		return team.universe.server.getPlayerList().getPlayerStatsFile(playerForStats);
	}

	public ConfigGroup getSettings() {
		if (cachedConfig == null) {
			cachedConfig = ConfigGroup.newGroup("player_config");
			cachedConfig.setDisplayName(new TextComponentTranslation("player_config"));
			ForgePlayerConfigEvent event = new ForgePlayerConfigEvent(this, cachedConfig);
			event.post();

			ConfigGroup config = cachedConfig.getGroup(FTBLib.MOD_ID);
			config.setDisplayName(new TextComponentString(FTBLib.MOD_NAME));
			config.addBool("hide_team_notification", () -> hideTeamNotification, v -> hideTeamNotification = v, false);
		}

		return cachedConfig;
	}

	public NBTTagCompound getPlayerNBT() {
		return getPlayerIfExists(this.getId().toString());
	}

	public void setPlayerNBT(NBTTagCompound nbt) {
		short teamUID = nbt.getShort("TeamUID");
		String teamID = nbt.getString("TeamID");
		String uuid = nbt.getString("UUID");
		String nick = nbt.getString("Name");

		updatePlayer(teamUID, teamID, uuid, nick, nbt);
	}

	public boolean hideTeamNotification() {
		return FTBLibConfig.teams.hide_team_notification || hideTeamNotification || isFake();
	}

	public long getLastTimeSeen() {
		return isOnline() ? team.universe.ticks.ticks() : lastTimeSeen;
	}

	public boolean hasPermission(String node, @Nullable IContext context) {
		return PermissionAPI.hasPermission(getProfile(), node, context);
	}

	public IContext getContext() {
		if (isOnline()) {
			return new PlayerContext(getPlayer());
		}

		return new WorldContext(team.universe.world);
	}

	public boolean hasPermission(String node) {
		return PermissionAPI.hasPermission(getProfile(), node, getContext());
	}

	public ConfigValue getRankConfig(String node) {
		return RankConfigAPI.get(team.universe.server, getProfile(), node);
	}

//    public File getDataFile(String ext) {
//        File dir = new File(team.universe.getWorldDirectory(), "data/ftb_lib/players/");
//
//        if (ext.isEmpty()) {
//            return new File(dir, getName().toLowerCase() + ".dat");
//        }
//
//        File extFolder = new File(dir, ext);
//
//        if (!extFolder.exists()) {
//            extFolder.mkdirs();
//        }
//
//        File extFile = new File(extFolder, getName().toLowerCase() + ".dat");
//
//        if (!extFile.exists()) {
//            File oldExtFile = new File(dir, getName().toLowerCase() + "." + ext + ".dat");
//
//            if (oldExtFile.exists()) {
//                oldExtFile.renameTo(extFile);
//            }
//        }
//
//        return extFile;
//    }

	@Override
	public void onConfigSaved(ConfigGroup group, ICommandSender sender) {
		clearCache();
		markDirty();
		new ForgePlayerConfigSavedEvent(this, group, sender).post();
	}
}