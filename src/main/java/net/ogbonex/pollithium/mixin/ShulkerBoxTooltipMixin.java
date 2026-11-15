package net.ogbonex.pollithium.mixin;

import net.ogbonex.pollithium.PollithiumConfig;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ShulkerBoxTooltipMixin {
    @Inject(method="getTooltip", at=@At("RETURN"), cancellable=true)
    private void pollithium$appendShulkerContents(net.minecraft.item.Item.TooltipContext context,
            net.minecraft.entity.player.PlayerEntity player, net.minecraft.item.tooltip.TooltipType type,
            CallbackInfoReturnable<List<Text>> cir) {
        if (!PollithiumConfig.shulkerBoxTooltipEnabled) return;
        ItemStack stack=(ItemStack)(Object)this;
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof ShulkerBoxBlock)) return;
        ContainerComponent contents=stack.get(DataComponentTypes.CONTAINER);
        if (contents==null) return;
        List<Text> tooltip=new ArrayList<>(cir.getReturnValue());
        tooltip.add(Text.literal("§8§m                    "));
        tooltip.add(Text.literal("§6Shulker contents"));
        int shown=0;
        for(ItemStack item:contents.iterateNonEmpty()) {
            if(shown++>=9) break;
            tooltip.add(Text.literal("§7• §f"+item.getCount()+"x "+item.getName().getString()));
        }
        int total=(int)contents.streamNonEmpty().count();
        if(total==0) tooltip.add(Text.literal("§8Empty"));
        else if(total>9) tooltip.add(Text.literal("§8and "+(total-9)+" more item types"));
        cir.setReturnValue(tooltip);
    }
}