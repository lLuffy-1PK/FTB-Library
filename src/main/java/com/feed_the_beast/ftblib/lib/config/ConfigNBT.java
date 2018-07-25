package com.feed_the_beast.ftblib.lib.config;

import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ConfigNBT extends ConfigValue
{
	public static final String ID = "nbt";
	public static final Color4I COLOR = Color4I.rgb(0xFFAA49);

	private NBTTagCompound value;

	public ConfigNBT()
	{
		this(null);
	}

	public ConfigNBT(@Nullable NBTTagCompound nbt)
	{
		value = nbt;
	}

	@Override
	public String getName()
	{
		return ID;
	}

	@Override
	public String getString()
	{
		return value == null ? "null" : value.toString();
	}

	@Nullable
	public NBTTagCompound getNBT()
	{
		return value;
	}

	public void setNBT(@Nullable NBTTagCompound nbt)
	{
		value = nbt;
	}

	@Override
	public boolean getBoolean()
	{
		value = getNBT();
		return value != null && !value.isEmpty();
	}

	@Override
	public int getInt()
	{
		value = getNBT();
		return value == null ? 0 : value.getSize();
	}

	@Override
	public ConfigNBT copy()
	{
		value = getNBT();
		return new ConfigNBT(value == null ? null : value.copy());
	}

	@Override
	public Color4I getColor()
	{
		return COLOR;
	}

	@Override
	public ITextComponent getStringForGUI()
	{
		return new TextComponentString(getNBT() == null ? "null" : "{...}");
	}

	@Override
	public boolean setValueFromString(String string, boolean simulate)
	{
		if (string.equals("null"))
		{
			if (!simulate)
			{
				setNBT(null);
			}

			return true;
		}

		try
		{
			value = JsonToNBT.getTagFromJson(string);

			if (!simulate)
			{
				setNBT(value);
			}

			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	@Override
	public void addInfo(ConfigValueInstance inst, List<String> list)
	{
		list.add(TextFormatting.AQUA + "Value: " + TextFormatting.RESET + NBTUtils.getColoredNBTString(getNBT()));

		if (inst.getValue() instanceof ConfigNBT)
		{
			list.add(TextFormatting.AQUA + "Default: " + TextFormatting.RESET + NBTUtils.getColoredNBTString(((ConfigNBT) inst.getValue()).getNBT()));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String key)
	{
		value = getNBT();

		if (value != null)
		{
			nbt.setTag(key, value);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String key)
	{
		value = nbt.hasKey(key) ? nbt.getCompoundTag(key) : null;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeNBT(getNBT());
	}

	@Override
	public void readData(DataIn data)
	{
		setNBT(data.readNBT());
	}

	@Override
	public boolean isEmpty()
	{
		value = getNBT();
		return value == null || value.isEmpty();
	}

	@Override
	public void setValueFromOtherValue(ConfigValue value)
	{
		if (value instanceof ConfigNBT)
		{
			NBTTagCompound nbt = ((ConfigNBT) value).getNBT();
			setNBT(nbt == null ? null : nbt.copy());
		}
		else
		{
			super.setValueFromOtherValue(value);
		}
	}
}