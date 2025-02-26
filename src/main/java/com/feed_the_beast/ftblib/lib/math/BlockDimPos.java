package com.feed_the_beast.ftblib.lib.math;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public final class BlockDimPos {
    public final int posX, posY, posZ, dim;

    @Nullable
    public static BlockDimPos fromIntArray(@Nullable int[] ai) {
        return ai == null || ai.length < 3 ? null : new BlockDimPos(ai[0], ai[1], ai[2], ai.length > 3 ? ai[3] : 0);
    }

    public BlockDimPos(int x, int y, int z, int d) {
        posX = x;
        posY = y;
        posZ = z;
        dim = d;
    }

    public BlockDimPos(double x, double y, double z, int d) {
        this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), d);
    }

    public BlockDimPos(Vec3i p, int d) {
        this(p.getX(), p.getY(), p.getZ(), d);
    }

    public BlockDimPos(Entity entity) {
        this(entity.posX, entity.posY + 0.1D, entity.posZ, entity.dimension);
    }

    public BlockDimPos(ICommandSender sender) {
        this(sender.getPosition(), sender.getEntityWorld().provider.getDimension());
    }

    public int[] toIntArray() {
        return new int[]{posX, posY, posZ, dim};
    }

    public String toString() {
        return "[" + dim + '@' + posX + ',' + posY + ',' + posZ + ']';
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o == this) {
            return true;
        } else if (o instanceof BlockDimPos) {
            return equalsPos((BlockDimPos) o);
        }

        return false;
    }

    public int hashCode() {
        return 31 * (31 * (31 * posX + posY) + posZ) + dim;
    }

    public Vec3d toVec() {
        return new Vec3d(posX + 0.5D, posY + 0.5D, posZ + 0.5D);
    }

    public ChunkDimPos toChunkPos() {
        return new ChunkDimPos(MathUtils.chunk(posX), MathUtils.chunk(posZ), dim);
    }

    public BlockPos getBlockPos() {
        return new BlockPos(posX, posY, posZ);
    }

    public BlockDimPos copy() {
        return new BlockDimPos(posX, posY, posZ, dim);
    }

    public boolean equalsPos(BlockDimPos p) {
        return p == this || (p.dim == dim && p.posX == posX && p.posY == posY && p.posZ == posZ);
    }

    public BlockDimPos add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockDimPos(posX + x, posY + y, posZ + z, dim);
    }

    public TeleporterDimPos teleporter() {
        return TeleporterDimPos.of(posX + 0.5D, posY + 0.1D, posZ + 0.5D, dim);
    }
}