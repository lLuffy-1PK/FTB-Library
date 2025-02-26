package com.feed_the_beast.ftblib.lib.gui.misc;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.config.*;
import com.feed_the_beast.ftblib.lib.gui.*;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.util.InvUtils;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class GuiSelectItemStack extends GuiBase {
    private static boolean allItems = true;

    private class ItemStackButton extends Button {
        private final ItemStack stack;

        private ItemStackButton(Panel panel, ItemStack is) {
            super(panel, "", GuiIcons.BARRIER);
            setSize(18, 18);
            stack = is;
            title = null;
            icon = null;
        }

        public boolean shouldAdd(String search, String mod) {
            if (search.isEmpty()) {
                return true;
            }

            if (!mod.isEmpty()) {
                return stack.getItem().getRegistryName().getNamespace().contains(mod);
            }

            return stack.getDisplayName().toLowerCase().contains(search);
        }

        @Override
        public String getTitle() {
            if (title == null) {
                title = stack.getDisplayName();
            }

            return title;
        }

        @Override
        public void addMouseOverText(List<String> list) {
        }

        @Override
        public WidgetType getWidgetType() {
            return InvUtils.stacksAreEqual(stack, selected) ? WidgetType.MOUSE_OVER : super.getWidgetType();
        }

        @Override
        public void drawBackground(Theme theme, int x, int y, int w, int h) {
            (getWidgetType() == WidgetType.MOUSE_OVER ? Color4I.LIGHT_GREEN.withAlpha(70) : Color4I.BLACK.withAlpha(50)).draw(x, y, w, h);
        }

        @Override
        public void drawIcon(Theme theme, int x, int y, int w, int h) {
            if (stack.getItem() == FTBLib.CUSTOM_ICON_ITEM && stack.hasTagCompound() && !stack.getTagCompound().getString("icon").isEmpty()) {
                Icon.getIcon(stack.getTagCompound().getString("icon")).draw(x, y, w, h);
            } else {
                GuiHelper.drawItem(stack, x, y, w / 16D, h / 16D, true);
            }
        }

        @Override
        public void onClicked(MouseButton button) {
            GuiHelper.playClickSound();
            selected = stack.copy();
        }

        @Override
        public Object getIngredientUnderMouse() {
            return new WrappedIngredient(stack).tooltip();
        }
    }

    private class ButtonSwitchMode extends Button {
        private final Icon ICON_ALL = ItemIcon.getItemIcon(Items.COMPASS);
        private final Icon ICON_INV = ItemIcon.getItemIcon(Blocks.CHEST);

        public ButtonSwitchMode(Panel panel) {
            super(panel);
        }

        @Override
        public void drawIcon(Theme theme, int x, int y, int w, int h) {
            (allItems ? ICON_ALL : ICON_INV).draw(x, y, w, h);
        }

        @Override
        public String getTitle() {
            return I18n.format("ftblib.select_item.list_mode", TextFormatting.GRAY + (allItems ? I18n.format("ftblib.select_item.list_mode.all") : I18n.format("ftblib.select_item.list_mode.inv"))) + TextFormatting.DARK_GRAY + " [" + (panelStacks.widgets.size() - 1) + "]";
        }

        @Override
        public void onClicked(MouseButton button) {
            GuiHelper.playClickSound();
            allItems = !allItems;
            panelStacks.refreshWidgets();
        }
    }

    private abstract class ButtonStackConfig extends Button implements IConfigValueEditCallback {
        public ButtonStackConfig(Panel panel, String title, Icon icon) {
            super(panel, title, icon);
        }

        @Override
        public WidgetType getWidgetType() {
            return selected.isEmpty() ? WidgetType.DISABLED : super.getWidgetType();
        }
    }

    private class ButtonEditData extends Button implements IConfigValueEditCallback {
        public ButtonEditData(Panel panel) {
            super(panel, "", GuiIcons.BUG);
        }

        @Override
        public void drawIcon(Theme theme, int x, int y, int w, int h) {
            GuiHelper.drawItem(selected, x, y, w / 16D, h / 16D, true);
        }

        @Override
        public String getTitle() {
            return selected.getDisplayName();
        }

        @Override
        public void onClicked(MouseButton button) {
            GuiHelper.playClickSound();
            new GuiEditConfigValue("itemstack", new ConfigItemStack(selected.copy(), single), this).openGui();
        }

        @Override
        public void onCallback(ConfigValue value, boolean set) {
            if (set) {
                selected = ((ConfigItemStack) value).getStack();
            }

            openGui();
        }
    }

    private class ButtonCount extends ButtonStackConfig {
        public ButtonCount(Panel panel) {
            super(panel, I18n.format("ftblib.select_item.count"), ItemIcon.getItemIcon(Items.PAPER));
        }

        @Override
        public WidgetType getWidgetType() {
            return single ? WidgetType.DISABLED : super.getWidgetType();
        }

        @Override
        public void onClicked(MouseButton button) {
            GuiHelper.playClickSound();
            new GuiEditConfigValue("count", new ConfigInt(selected.getCount(), 1, selected.getMaxStackSize()), this).openGui();
        }

        @Override
        public void onCallback(ConfigValue value, boolean set) {
            if (set) {
                selected.setCount(value.getInt());
            }

            openGui();
        }
    }

    private class ButtonMeta extends ButtonStackConfig {
        public ButtonMeta(Panel panel) {
            super(panel, I18n.format("ftblib.select_item.meta"), ItemIcon.getItemIcon(new ItemStack(Blocks.STONEBRICK, 1, 2)));
        }

        @Override
        public void onClicked(MouseButton button) {
            GuiHelper.playClickSound();
            new GuiEditConfigValue("meta", new ConfigInt(selected.getMetadata(), 0, Short.MAX_VALUE), this).openGui();
        }

        @Override
        public void onCallback(ConfigValue value, boolean set) {
            if (set) {
                selected.setItemDamage(value.getInt());
            }

            openGui();
        }
    }

    private class ButtonNBT extends ButtonStackConfig {
        public ButtonNBT(Panel panel) {
            super(panel, I18n.format("ftblib.select_item.nbt"), ItemIcon.getItemIcon(Items.NAME_TAG));
        }

        @Override
        public void onClicked(MouseButton button) {
            GuiHelper.playClickSound();
            new GuiEditConfigValue("nbt", new ConfigNBT(selected.getTagCompound()), this).openGui();
        }

        @Override
        public void onCallback(ConfigValue value, boolean set) {
            if (set) {
                selected.setTagCompound(((ConfigNBT) value).getNBT());
            }

            openGui();
        }
    }

    private class ButtonCaps extends ButtonStackConfig {
        public ButtonCaps(Panel panel) {
            super(panel, I18n.format("ftblib.select_item.caps"), ItemIcon.getItemIcon(Blocks.ANVIL));
        }

        @Override
        public void onClicked(MouseButton button) {
            GuiHelper.playClickSound();

            NBTTagCompound nbt = selected.serializeNBT();
            new GuiEditConfigValue("caps", new ConfigNBT((NBTTagCompound) nbt.getTag("ForgeCaps")), this).openGui();
        }

        @Override
        public void onCallback(ConfigValue value, boolean set) {
            if (set) {
                NBTTagCompound caps = ((ConfigNBT) value).getNBT();
                NBTTagCompound nbt = selected.serializeNBT();

                if (caps == null) {
                    nbt.removeTag("ForgeCaps");
                } else {
                    nbt.setTag("ForgeCaps", caps);
                }

                selected = new ItemStack(nbt);
            }

            openGui();
        }
    }

    private class ButtonDisplayName extends ButtonStackConfig {
        public ButtonDisplayName(Panel panel) {
            super(panel, I18n.format("ftblib.select_item.display_name"), ItemIcon.getItemIcon(Items.SIGN));
        }

        @Override
        public void onClicked(MouseButton button) {
            GuiHelper.playClickSound();
            new GuiEditConfigValue("name", new ConfigString(selected.hasDisplayName() ? selected.getDisplayName() : ""), this).openGui();
        }

        @Override
        public void onCallback(ConfigValue value, boolean set) {
            if (set) {
                if (!value.isEmpty()) {
                    selected.setStackDisplayName(value.getString());
                } else if (selected.hasTagCompound()) {
                    selected.getTagCompound().getCompoundTag("display").removeTag("Name");
                    selected.setTagCompound(NBTUtils.minimize(selected.getTagCompound()));
                }
            }

            openGui();
        }
    }

    private class ThreadItemList extends Thread {
        private final String search;

        public ThreadItemList() {
            super("Item Search Thread");
            setDaemon(true);
            search = searchBox.getText().toLowerCase();
        }

        @Override
        public void run() {
            List<Widget> widgets = new ArrayList<>();
            NonNullList<ItemStack> list = NonNullList.create();

            if (allItems) {
                for (Item item : Item.REGISTRY) {
                    item.getSubItems(CreativeTabs.SEARCH, list);
                }

                list.add(new ItemStack(Blocks.COMMAND_BLOCK));
                list.add(new ItemStack(Blocks.BARRIER));
                list.add(new ItemStack(Blocks.STRUCTURE_VOID));
            } else {
                for (int i = 0; i < Minecraft.getMinecraft().player.inventory.getSizeInventory(); i++) {
                    ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);

                    if (!stack.isEmpty()) {
                        list.add(stack);
                    }
                }
            }

            String mod = "";

            if (search.startsWith("@")) {
                mod = search.substring(1);
            }

            ItemStackButton button = new ItemStackButton(panelStacks, ItemStack.EMPTY);

            if (button.shouldAdd(search, mod)) {
                widgets.add(new ItemStackButton(panelStacks, ItemStack.EMPTY));
            }

            for (ItemStack stack : list) {
                if (!stack.isEmpty()) {
                    button = new ItemStackButton(panelStacks, stack);

                    if (button.shouldAdd(search, mod)) {
                        widgets.add(button);
                    }
                }
            }

            for (int i = 0; i < widgets.size(); i++) {
                widgets.get(i).setPos(1 + (i % 9) * 19, 1 + (i / 9) * 19);
            }

            newStackWidgets = widgets;
        }
    }

    private final IOpenableGui callbackGui;
    private final Button buttonCancel, buttonAccept;
    private final Panel panelStacks;
    private final PanelScrollBar scrollBar;
    private final TextBox searchBox;
    private ItemStack selected;
    private final boolean single;
    private final Consumer<ItemStack> callback;
    private final Panel tabs;
    private ThreadItemList threadItemList;
    private List<Widget> newStackWidgets;
    public long update = Long.MAX_VALUE;

    public GuiSelectItemStack(IOpenableGui g, ItemStack is, boolean s, Consumer<ItemStack> c) {
        setSize(211, 150);
        callbackGui = g;
        selected = is;
        single = s;
        callback = c;

        int bsize = width / 2 - 10;

        buttonCancel = new SimpleTextButton(this, I18n.format("gui.cancel"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton button) {
                GuiHelper.playClickSound();
                onClosed();
                callbackGui.openGui();
            }

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }
        };

        buttonCancel.setPosAndSize(8, height - 24, bsize, 16);

        buttonAccept = new SimpleTextButton(this, I18n.format("gui.accept"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton button) {
                GuiHelper.playClickSound();
                callbackGui.openGui();
                callback.accept(selected);
            }

            @Override
            public boolean renderTitleInCenter() {
                return true;
            }
        };

        buttonAccept.setPosAndSize(width - bsize - 8, height - 24, bsize, 16);

        panelStacks = new BlankPanel(this) {
            @Override
            public void addWidgets() {
                update = System.currentTimeMillis() + 200L;
            }

            @Override
            public void drawBackground(Theme theme, int x, int y, int w, int h) {
                theme.drawPanelBackground(x, y, w, h);
            }
        };

        panelStacks.setPosAndSize(9, 24, 9 * 19 + 1, 5 * 19 + 1);

        scrollBar = new PanelScrollBar(this, panelStacks);
        scrollBar.setCanAlwaysScroll(true);
        scrollBar.setScrollStep(20);

        searchBox = new TextBox(this) {
            @Override
            public void onTextChanged() {
                panelStacks.refreshWidgets();
            }
        };

        searchBox.setPosAndSize(8, 7, width - 16, 12);
        searchBox.ghostText = I18n.format("gui.search_box");
        searchBox.setFocused(true);

        tabs = new Panel(this) {
            @Override
            public void addWidgets() {
                add(new ButtonSwitchMode(this));
                add(new ButtonEditData(this));
                add(new ButtonCount(this));
                add(new ButtonMeta(this));
                add(new ButtonNBT(this));
                add(new ButtonCaps(this));
                add(new ButtonDisplayName(this));
            }

            @Override
            public void alignWidgets() {
                for (Widget widget : widgets) {
                    widget.setSize(20, 20);
                }

                setHeight(align(WidgetLayout.VERTICAL));
            }
        };

        tabs.setPosAndSize(-19, 8, 20, 0);
        threadItemList = new ThreadItemList();
        threadItemList.start();
    }

    public GuiSelectItemStack(IOpenableGui g, Consumer<ItemStack> c) {
        this(g, ItemStack.EMPTY, false, c);
    }

    @Override
    public void addWidgets() {
        add(tabs);
        add(panelStacks);
        add(scrollBar);
        add(searchBox);
        add(buttonCancel);
        add(buttonAccept);
    }

    @Override
    public void onClosed() {
        super.onClosed();
        stopSearch();
    }

    private void stopSearch() {
        if (threadItemList != null) {
            try {
                threadItemList.interrupt();
            } catch (Exception ex) {
            }
        }

        threadItemList = null;
    }

    @Override
    public void drawBackground(Theme theme, int x, int y, int w, int h) {
        super.drawBackground(theme, x, y, w, h);

        if (newStackWidgets != null) {
            panelStacks.widgets.clear();
            panelStacks.addAll(newStackWidgets);
            scrollBar.setPosAndSize(panelStacks.posX + panelStacks.width + 6, panelStacks.posY - 1, 16, panelStacks.height + 2);
            scrollBar.setValue(0);
            scrollBar.setMaxValue(1 + MathHelper.ceil(panelStacks.widgets.size() / 9F) * 19);
            newStackWidgets = null;
        }

        long now = System.currentTimeMillis();

        if (now >= update) {
            update = Long.MAX_VALUE;
            stopSearch();
            threadItemList = new ThreadItemList();
            threadItemList.start();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        GuiScreen screen = getPrevScreen();
        return screen != null && screen.doesGuiPauseGame();
    }
}