package com.feed_the_beast.ftblib.lib.util.text_components;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * @author LatvianModder
 */
public class TextComponentCountdown extends TextComponentString {
    public final long countdown;

    public TextComponentCountdown(String s, long t) {
        super(s);
        countdown = t;
    }

    public TextComponentCountdown(long t) {
        this("", t);
    }

    @Override
    public String getText() {
        return Ticks.get(countdown - FTBLib.PROXY.getWorldTime() - (countdown % 20L)).toTimeString();
    }

    @Override
    public String getUnformattedComponentText() {
        return getText();
    }

    @Override
    public TextComponentCountdown createCopy() {
        TextComponentCountdown component = new TextComponentCountdown(countdown);
        component.setStyle(getStyle().createShallowCopy());

        for (ITextComponent itextcomponent : getSiblings()) {
            component.appendSibling(itextcomponent.createCopy());
        }

        return component;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof TextComponentCountdown) {
            TextComponentCountdown t = (TextComponentCountdown) o;
            return countdown == t.countdown && getStyle().equals(t.getStyle()) && getSiblings().equals(t.getSiblings());
        } else if (o instanceof TextComponentString) {
            TextComponentString t = (TextComponentString) o;
            return getText().equals(t.getText()) && getStyle().equals(t.getStyle()) && getSiblings().equals(t.getSiblings());
        }

        return false;
    }

    public String toString() {
        return "CountdownComponent{" + StringJoiner.with(", ").joinObjects("countdown=" + countdown, "text=" + getText(), "siblings=" + siblings, "style=" + getStyle()) + '}';
    }
}