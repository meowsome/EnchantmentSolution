package org.ctp.enchantmentsolution.utils.items;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.ctp.enchantmentsolution.enchantments.RegisterEnchantments;
import org.ctp.enchantmentsolution.utils.abillityhelpers.ParticleEffect;

public class AbilityUtils {

	private static List<Block> WAND_BLOCKS = new ArrayList<Block>();
	private static List<Location> HEIGHT_WIDTH_BLOCKS = new ArrayList<Location>();
	private static List<Material> CROPS = Arrays.asList(Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.NETHER_WART, Material.BEETROOTS, Material.COCOA_BEANS);

	public static ItemStack getGoldDiggerItems(ItemStack item, Block brokenBlock) {

		if (brokenBlock.getBlockData() instanceof Ageable) {
			Ageable age = (Ageable) brokenBlock.getBlockData();
			if (CROPS.contains(brokenBlock.getType())) {
				if (age.getAge() != age.getMaximumAge()) return null;
			} else
				return null;
		} else
			return null;
		int level = ItemUtils.getLevel(item, RegisterEnchantments.GOLD_DIGGER);
		int amount = 0;
		while (level > 0) {
			double random = Math.random();
			double chance = 1.0 / 6.0;
			if (chance > random) amount++;
			level--;
		}
		if (amount > 0) return new ItemStack(Material.GOLD_NUGGET, amount);

		return null;
	}

	public static void dropExperience(Location loc, int amount) {
		if (amount > 0) loc.getWorld().spawn(loc, ExperienceOrb.class).setExperience(amount);
	}

	public static void giveExperience(Player player, int amount) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		PlayerInventory playerInv = player.getInventory();
		for(ItemStack i: playerInv.getArmorContents())
			if (i != null && ItemUtils.hasEnchantment(i, Enchantment.MENDING)) items.add(i);
		if (playerInv.getItemInMainHand() != null && ItemUtils.hasEnchantment(playerInv.getItemInMainHand(), Enchantment.MENDING)) items.add(playerInv.getItemInMainHand());
		if (playerInv.getItemInOffHand() != null && ItemUtils.hasEnchantment(playerInv.getItemInOffHand(), Enchantment.MENDING)) items.add(playerInv.getItemInOffHand());

		if (items.size() > 0) {
			Collections.shuffle(items);
			ItemStack item = items.get(0);
			int durability = DamageUtils.getDamage(item.getItemMeta());
			while (amount > 0 && durability > 0) {
				durability -= 2;
				amount--;
			}
			if (durability < 0) durability = 0;
			DamageUtils.setDamage(item, durability);
			if (amount > 0) player.giveExp(amount);
		} else
			player.giveExp(amount);
	}

	public static int setExp(int exp, int level) {
		int totalExp = exp;
		if (exp > 0) for(int i = 0; i < exp * level; i++) {
			double chance = .50;
			double random = Math.random();
			if (chance > random) totalExp++;
		}
		return totalExp;
	}

	public static List<ParticleEffect> createEffects(Player player) {
		int random = (int) (Math.random() * 5 + 2);
		List<ParticleEffect> particles = new ArrayList<ParticleEffect>();
		for(int i = 0; i < random; i++)
			particles.add(generateParticle());
		return particles;
	}

	private static ParticleEffect generateParticle() {
		Particle particle = null;
		int tries = 0;
		int numParticles = (int) (Math.random() * 400 + 11);
		while (particle == null && tries < 10) {
			int particleType = (int) (Math.random() * Particle.values().length);
			particle = Particle.values()[particleType];
			if (!particle.getDataType().isAssignableFrom(Void.class)) particle = null;

		}
		return new ParticleEffect(particle, numParticles);
	}

	public static List<DamageCause> getContactCauses() {
		return Arrays.asList(DamageCause.BLOCK_EXPLOSION, DamageCause.CONTACT, DamageCause.CUSTOM, DamageCause.ENTITY_ATTACK, DamageCause.ENTITY_EXPLOSION, DamageCause.ENTITY_SWEEP_ATTACK, DamageCause.LIGHTNING, DamageCause.PROJECTILE, DamageCause.THORNS);
	}

	public static int getExhaustionCurse(Player player) {
		Enchantment curse = RegisterEnchantments.CURSE_OF_EXHAUSTION;
		int exhaustionCurse = 0;
		for(ItemStack item: player.getInventory().getArmorContents())
			if (item != null && ItemUtils.hasEnchantment(item, curse)) exhaustionCurse += ItemUtils.getLevel(item, curse);
		ItemStack mainHand = player.getInventory().getItemInMainHand();
		if (mainHand != null && ItemUtils.hasEnchantment(mainHand, curse)) exhaustionCurse += ItemUtils.getLevel(mainHand, curse);
		ItemStack offHand = player.getInventory().getItemInOffHand();
		if (offHand != null && ItemUtils.hasEnchantment(offHand, curse)) exhaustionCurse += ItemUtils.getLevel(offHand, curse);
		return exhaustionCurse;
	}

	public static float getExhaustion(Player player) {
		return player.getFoodLevel() * 4 + player.getSaturation() * 4 - player.getExhaustion();
	}

	public static List<Block> getWandBlocks() {
		return WAND_BLOCKS;
	}

	public static void addWandBlock(Block block) {
		WAND_BLOCKS.add(block);
	}

	public static void removeWandBlock(Block block) {
		WAND_BLOCKS.remove(block);
	}

	public static List<Location> getHeightWidthBlocks() {
		return HEIGHT_WIDTH_BLOCKS;
	}

	public static void addHeightWidthBlock(Location loc) {
		HEIGHT_WIDTH_BLOCKS.add(loc);
	}

	public static void removeHeightWidthBlock(Location loc) {
		HEIGHT_WIDTH_BLOCKS.remove(loc);
	}
}
