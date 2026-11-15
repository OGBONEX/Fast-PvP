package net.ogbonex.pollithium;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class PollithiumHud {
    private static int leftCps, rightCps, leftClicks, rightClicks;
    private static boolean leftWasDown, rightWasDown;
    private static long cpsWindowStart = System.currentTimeMillis();

    private PollithiumHud() {}

    public static void tick(MinecraftClient client) {
        if (client.getWindow() == null) return;
        boolean leftDown = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
        boolean rightDown = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS;
        if (leftDown && !leftWasDown) leftClicks++;
        if (rightDown && !rightWasDown) rightClicks++;
        leftWasDown = leftDown;
        rightWasDown = rightDown;
        long now=System.currentTimeMillis();
        if (now-cpsWindowStart >= 1000) {
            leftCps=leftClicks; rightCps=rightClicks;
            leftClicks=0; rightClicks=0; cpsWindowStart=now;
        }
    }

    public static void render(DrawContext context, MinecraftClient client) {
        int x=PollithiumConfig.hudX, y=PollithiumConfig.hudY;
        int width=150, lineCount=0;
        if(PollithiumConfig.showFps) lineCount++;
        if(PollithiumConfig.showPing) lineCount++;
        if(PollithiumConfig.showCoordinates) lineCount++;
        if(PollithiumConfig.showDirection) lineCount++;
        if(PollithiumConfig.showClock) lineCount++;
        if(PollithiumConfig.showCps) lineCount++;
        if(PollithiumConfig.showArmor) lineCount++;
        if(PollithiumConfig.showEffects && !client.player.getStatusEffects().isEmpty()) lineCount+=Math.min(4,client.player.getStatusEffects().size());
        if(PollithiumConfig.showKeystrokes) lineCount+=2;
        if(lineCount==0) return;
        int height=lineCount*12+8;
        context.fill(x,y,x+width,y+height,PollithiumConfig.panelColor);
        context.fill(x,y,x+width,y+2,PollithiumConfig.accentColor);
        int ty=y+6;
        if(PollithiumConfig.showFps){ text(context,client,"FPS  "+client.getCurrentFps(),x+7,ty); ty+=12; }
        if(PollithiumConfig.showPing){
            int ping=0;
            if(client.getNetworkHandler()!=null){
                PlayerListEntry e=client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                if(e!=null) ping=e.getLatency();
            }
            text(context,client,"PING  "+ping+" ms",x+7,ty); ty+=12;
        }
        if(PollithiumConfig.showCoordinates){
            text(context,client,String.format("XYZ  %.1f / %.1f / %.1f",client.player.getX(),client.player.getY(),client.player.getZ()),x+7,ty); ty+=12;
        }
        if(PollithiumConfig.showDirection){
            float yaw=(client.player.getYaw()%360+360)%360;
            String[] dirs={"S","SW","W","NW","N","NE","E","SE"};
            String direction=dirs[Math.round(yaw/45f)%8];
            text(context,client,"FACING  "+direction,x+7,ty); ty+=12;
        }
        if(PollithiumConfig.showClock){
            text(context,client,"TIME  "+LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),x+7,ty); ty+=12;
        }
        if(PollithiumConfig.showCps){
            text(context,client,"CPS  "+leftCps+" | "+rightCps,x+7,ty); ty+=12;
        }
        if(PollithiumConfig.showArmor){
            String armor=armorSummary(client);
            text(context,client,"ARMOR  "+armor,x+7,ty); ty+=12;
        }
        if(PollithiumConfig.showEffects){
            int count=0;
            for(StatusEffectInstance effect:client.player.getStatusEffects()){
                if(count++>=4) break;
                String name=effect.getEffectType().value().getName().getString();
                text(context,client,name+" "+(effect.getAmplifier()+1)+"  "+formatDuration(effect.getDuration()),x+7,ty);
                ty+=12;
            }
        }
        if(PollithiumConfig.showKeystrokes){
            boolean w=key(client,GLFW.GLFW_KEY_W),a=key(client,GLFW.GLFW_KEY_A);
            boolean s=key(client,GLFW.GLFW_KEY_S),d=key(client,GLFW.GLFW_KEY_D);
            drawKey(context,client,"W",x+67,ty,w); ty+=12;
            drawKey(context,client,"A",x+55,ty,a); drawKey(context,client,"S",x+67,ty,s); drawKey(context,client,"D",x+79,ty,d);
        }
    }

    private static String armorSummary(MinecraftClient client) {
        StringBuilder out=new StringBuilder();
        for(int i=3;i>=0;i--){
            ItemStack stack=client.player.getInventory().getArmorStack(i);
            if(stack.isEmpty()) out.append("--");
            else if(stack.isDamageable()) out.append(Math.max(0,stack.getMaxDamage()-stack.getDamage()));
            else out.append("OK");
            if(i>0) out.append(" ");
        }
        return out.toString();
    }

    private static String formatDuration(int ticks) {
        if(ticks<0) return "∞";
        int seconds=ticks/20;
        return String.format("%d:%02d",seconds/60,seconds%60);
    }

    private static boolean key(MinecraftClient client,int key){
        return GLFW.glfwGetKey(client.getWindow().getHandle(),key)==GLFW.GLFW_PRESS;
    }

    private static void text(DrawContext context,MinecraftClient client,String value,int x,int y){
        context.drawText(client.textRenderer,value,x,y,0xFFFFFFFF,false);
    }

    private static void drawKey(DrawContext context,MinecraftClient client,String label,int x,int y,boolean pressed){
        context.fill(x,y,x+10,y+10,pressed?PollithiumConfig.accentColor:0xCC252B38);
        context.drawText(client.textRenderer,label,x+3,y+1,0xFFFFFFFF,false);
    }

    public static void renderCustomCrosshair(DrawContext context,MinecraftClient client) {
        int cx=client.getWindow().getScaledWidth()/2, cy=client.getWindow().getScaledHeight()/2;
        int color=PollithiumConfig.accentColor;
        context.fill(cx-7,cy,cx-2,cy+1,color);
        context.fill(cx+2,cy,cx+7,cy+1,color);
        context.fill(cx,cy-7,cx+1,cy-2,color);
        context.fill(cx,cy+2,cx+1,cy+7,color);
    }
}
