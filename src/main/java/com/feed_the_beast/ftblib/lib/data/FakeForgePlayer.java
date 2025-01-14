package com.feed_the_beast.ftblib.lib.data;

import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class FakeForgePlayer extends ForgePlayer {
    public FakeForgePlayer(Universe u) {
        super(u, ServerUtils.FAKE_PLAYER_PROFILE.getId(), ServerUtils.FAKE_PLAYER_PROFILE.getName());
    }

    @Override
    public GameProfile getProfile() {
        return ServerUtils.FAKE_PLAYER_PROFILE;
    }

    @Override
    public boolean isOnline() {
        return tempPlayer != null;
    }

    @Override
    public EntityPlayerMP getPlayer() {
        if (tempPlayer == null) {
            throw new NullPointerException("Fake player not set yet!");
        }

        return tempPlayer;
    }

    @Override
    public boolean isFake() {
        return true;
    }

    @Override
    public boolean isOP() {
        return false;
    }

    @Override
    public void markDirty() {
        team.universe.markDirty();
    }
}