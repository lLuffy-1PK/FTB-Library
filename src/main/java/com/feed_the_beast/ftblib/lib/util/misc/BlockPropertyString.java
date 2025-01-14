package com.feed_the_beast.ftblib.lib.util.misc;

import com.google.common.base.Optional;
import net.minecraft.block.properties.PropertyHelper;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public class BlockPropertyString extends PropertyHelper<String> {
    private final String name;
    private final Collection<String> allowedValues;

    public BlockPropertyString(String n, Collection<String> v) {
        super(n, String.class);
        name = n;
        allowedValues = v;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<String> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public Optional<String> parseValue(String value) {
        return Optional.of(value);
    }

    @Override
    public String getName(String value) {
        return value;
    }
}