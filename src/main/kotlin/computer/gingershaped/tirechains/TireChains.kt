package computer.gingershaped.tirechains

import com.simibubi.create.foundation.data.AssetLookup
import com.simibubi.create.foundation.data.CreateRegistrate
import com.simibubi.create.foundation.item.ItemDescription
import com.simibubi.create.foundation.utility.FilesHelper
import com.tterrag.registrate.builders.ItemBuilder
import com.tterrag.registrate.providers.ProviderType
import com.tterrag.registrate.providers.RegistrateRecipeProvider
import com.tterrag.registrate.util.entry.ItemEntry
import dev.ryanhcode.offroad.Offroad
import dev.ryanhcode.offroad.content.components.TireLike
import dev.ryanhcode.offroad.content.items.tire.TireItem
import dev.ryanhcode.offroad.index.OffroadDataComponents
import dev.ryanhcode.offroad.index.OffroadItems
import dev.simulated_team.simulated.registrate.SimulatedRegistrate
import net.createmod.catnip.lang.FontHelper
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.minecraft.world.phys.Vec3
import net.neoforged.fml.common.Mod
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.util.Optional

@Mod(TireChains.ID)
object TireChains {
    const val ID = "tirechains"

    val REGISTRATE by lazy {
        @Suppress("TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        SimulatedRegistrate(Offroad.path(Offroad.MOD_ID), ID)
            .defaultCreativeTab(null as ResourceKey<CreativeModeTab>?)
            .setTooltipModifierFactory { item ->
                val tooltipPalette = FontHelper.Palette(
                    FontHelper.styleFromColor(0x2563eb),
                    FontHelper.styleFromColor(0x60a5fa),
                )

                ItemDescription.Modifier(item, tooltipPalette)
            }
    }

    val SMALL_CHAIN_TIRE: ItemEntry<TireItem> =
        REGISTRATE.tireItem("small_chain_tire", Tires.SMALL_CHAIN_TIRE, OffroadItems.SMALL_TIRE, 1) {
            onRegister { ItemDescription.referKey(it, CHAIN_TIRE) }
        }

    val CHAIN_TIRE: ItemEntry<TireItem> = REGISTRATE.tireItem("chain_tire", Tires.CHAIN_TIRE, OffroadItems.TIRE, 2) {}

    val LARGE_CHAIN_TIRE: ItemEntry<TireItem> =
        REGISTRATE.tireItem("large_chain_tire", Tires.LARGE_CHAIN_TIRE, OffroadItems.LARGE_TIRE, 4) {
            onRegister { ItemDescription.referKey(it, CHAIN_TIRE) }
        }

    val MONSTROUS_CHAIN_TIRE: ItemEntry<TireItem> =
        REGISTRATE.tireItem("monstrous_chain_tire", Tires.MONSTROUS_CHAIN_TIRE, OffroadItems.MONSTROUS_TIRE, 8) {
            onRegister { ItemDescription.referKey(it, CHAIN_TIRE) }
        }

    init {
        REGISTRATE.registerEventListeners(MOD_BUS)

        REGISTRATE.addDataGenerator(ProviderType.LANG) { provider ->
            val lang = FilesHelper.loadJsonResource("assets/${ID}/lang/default/en_us.json")!!.asJsonObject!!

            lang.entrySet().forEachIndexed { _, entry -> provider.add(entry.key, entry.value.asString!!) }
        }
    }

    object Tires {
        val ROTATION = Vec3(90.0, 0.0, 0.0)
        val OFFSET: Vec3 = Vec3.ZERO
        const val MINIMUM_FRICTION = 0.9f

        val SMALL_CHAIN_TIRE = TireLike(12.0f / 16.0f, ROTATION, OFFSET, Optional.empty(), MINIMUM_FRICTION)
        val CHAIN_TIRE = TireLike(15.5f / 16.0f, ROTATION, OFFSET, Optional.empty(), MINIMUM_FRICTION)
        val LARGE_CHAIN_TIRE = TireLike(1.0f + 4.0f / 16.0f, ROTATION, OFFSET, Optional.empty(), MINIMUM_FRICTION)
        val MONSTROUS_CHAIN_TIRE = TireLike(2.0f, ROTATION, OFFSET, Optional.empty(), MINIMUM_FRICTION)
    }

    fun CreateRegistrate.tireItem(
        id: String,
        tire: TireLike,
        baseTire: ItemLike,
        chains: Int,
        configure: ItemBuilder<TireItem, CreateRegistrate>.() -> Unit
    ): ItemEntry<TireItem> {
        val item = item(id, ::TireItem)
            .properties {
                it.component(OffroadDataComponents.TIRE, tire)
                    .craftRemainder(baseTire.asItem())
            }
            .recipe { context, provider ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, context.get())
                    .requires(baseTire)
                    .requires(Items.CHAIN, chains)
                    .unlockedBy("has_ingredient", RegistrateRecipeProvider.has(OffroadItems.TIRE.get()))
                    .save(provider)
            }
            .model(AssetLookup.itemModelWithPartials())
            .apply { configure() }
            .register()

        addDataGenerator(ProviderType.RECIPE) { provider ->
            ShapelessRecipeBuilder(RecipeCategory.MISC, Items.CHAIN, chains)
                .requires(item)
                .unlockedBy("has_ingredient", RegistrateRecipeProvider.has(OffroadItems.TIRE.get()))
                .save(provider, resource("${id}_disassemble"))
        }

        return item
    }

    fun resource(path: String) = ResourceLocation.fromNamespaceAndPath(ID, path)
}