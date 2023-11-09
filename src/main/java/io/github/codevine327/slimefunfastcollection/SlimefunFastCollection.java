package io.github.codevine327.slimefunfastcollection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class SlimefunFastCollection extends JavaPlugin implements Listener {
    final Set<Material> allowedMaterials = new HashSet<>();
    private Inventory lastInventory;

    final

    @Override
    public void onEnable() {
        allowedMaterials.add(Material.WORKBENCH);
        allowedMaterials.add(Material.FENCE);
        allowedMaterials.add(Material.NETHER_FENCE);
        allowedMaterials.add(Material.ANVIL);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerPreInteract(PlayerInteractEvent event) {
        Inventory inventory = getInventory(event, true);
        if (inventory != null) {
            lastInventory = inventory;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerAfterInteract(PlayerInteractEvent event) {
        Inventory inventory = getInventory(event, false);
        if (inventory == null) {
            return;
        }

        Player player = event.getPlayer();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);
            ItemStack lastItem = lastInventory.getItem(i);

            if (currentItem == null) {
                continue;
            }

            if (currentItem.equals(lastItem)) {
                continue;
            }

            if (currentItem.isSimilar(lastItem) && currentItem.getAmount() < lastItem.getAmount()) {
                continue;
            }

            HashMap<Integer, ItemStack> map = player.getInventory().addItem(currentItem);
            map.forEach((key, value) -> player.getWorld().dropItem(player.getLocation(), value));
            inventory.remove(currentItem);
        }
    }

    private Inventory getInventory(PlayerInteractEvent event, boolean useSnapshot) {
        Block clickedBlock = event.getClickedBlock();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return null;
        }

        if (!allowedMaterials.contains(clickedBlock.getType())) {
            return null;
        }

        Block nearBlock = clickedBlock.getRelative(BlockFace.DOWN);
        if (!(nearBlock.getState() instanceof Dispenser)) {
            nearBlock = clickedBlock.getRelative(BlockFace.UP);
        }
        if (!(nearBlock.getState() instanceof Dispenser)) {
            nearBlock = clickedBlock.getRelative(BlockFace.EAST);
        }
        if (!(nearBlock.getState() instanceof Dispenser)) {
            nearBlock = clickedBlock.getRelative(BlockFace.NORTH);
        }
        if (!(nearBlock.getState() instanceof Dispenser)) {
            nearBlock = clickedBlock.getRelative(BlockFace.SOUTH);
        }
        if (!(nearBlock.getState() instanceof Dispenser)) {
            nearBlock = clickedBlock.getRelative(BlockFace.WEST);
        }
        if (!(nearBlock.getState() instanceof Dispenser)) {
            return null;
        }

        Dispenser clickedDispenser = (Dispenser) nearBlock.getState();
        return useSnapshot ? clickedDispenser.getSnapshotInventory() : clickedDispenser.getInventory();
    }
}
