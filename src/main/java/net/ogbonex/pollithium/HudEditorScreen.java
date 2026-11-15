package net.ogbonex.pollithium;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class HudEditorScreen extends Screen {
    private int dragOffsetX,dragOffsetY;
    private boolean dragging;

    public HudEditorScreen(){ super(Text.literal("Pollithium Settings")); }

    @Override protected void init(){
        int left=width/2-210, top=height/2-132;
        addToggle("FPS",PollithiumConfig.showFps,left,top,()->PollithiumConfig.showFps=!PollithiumConfig.showFps);
        addToggle("Ping",PollithiumConfig.showPing,left,top+24,()->PollithiumConfig.showPing=!PollithiumConfig.showPing);
        addToggle("Coordinates",PollithiumConfig.showCoordinates,left,top+48,()->PollithiumConfig.showCoordinates=!PollithiumConfig.showCoordinates);
        addToggle("Keystrokes",PollithiumConfig.showKeystrokes,left,top+72,()->PollithiumConfig.showKeystrokes=!PollithiumConfig.showKeystrokes);
        addToggle("CPS",PollithiumConfig.showCps,left,top+96,()->PollithiumConfig.showCps=!PollithiumConfig.showCps);
        addToggle("Armor durability",PollithiumConfig.showArmor,left,top+120,()->PollithiumConfig.showArmor=!PollithiumConfig.showArmor);
        addToggle("Low fire",PollithiumConfig.lowFireEnabled,left,top+144,()->PollithiumConfig.lowFireEnabled=!PollithiumConfig.lowFireEnabled);
        addToggle("Shulker box tooltip",PollithiumConfig.shulkerBoxTooltipEnabled,left,top+168,()->PollithiumConfig.shulkerBoxTooltipEnabled=!PollithiumConfig.shulkerBoxTooltipEnabled);
        addToggle("Potion effects",PollithiumConfig.showEffects,left+215,top,()->PollithiumConfig.showEffects=!PollithiumConfig.showEffects);
        addToggle("Clock",PollithiumConfig.showClock,left+215,top+24,()->PollithiumConfig.showClock=!PollithiumConfig.showClock);
        addToggle("Direction",PollithiumConfig.showDirection,left+215,top+48,()->PollithiumConfig.showDirection=!PollithiumConfig.showDirection);
        addToggle("Custom crosshair",PollithiumConfig.showCrosshair,left+215,top+72,()->PollithiumConfig.showCrosshair=!PollithiumConfig.showCrosshair);
        addToggle("Toggle sprint",PollithiumConfig.toggleSprintEnabled,left+215,top+96,()->PollithiumConfig.toggleSprintEnabled=!PollithiumConfig.toggleSprintEnabled);
        addToggle("Custom hit sound",PollithiumConfig.hitSoundEnabled,left+215,top+120,()->PollithiumConfig.hitSoundEnabled=!PollithiumConfig.hitSoundEnabled);
        addToggle("Hit marker",PollithiumConfig.hitMarkerEnabled,left+215,top+144,()->PollithiumConfig.hitMarkerEnabled=!PollithiumConfig.hitMarkerEnabled);
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset HUD position"),b->{PollithiumConfig.hudX=8;PollithiumConfig.hudY=8;PollithiumConfig.save();})
                .dimensions(left,top+202,200,20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Cycle HUD color"),b->{
            int[] colors={0xFF8B5CF6,0xFF38BDF8,0xFF34D399,0xFFFBBF24,0xFFF472B6,0xFFFFFFFF};
            PollithiumConfig.accentColor=nextColor(PollithiumConfig.accentColor,colors);PollithiumConfig.save();
        }).dimensions(left,top+226,200,20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Cycle hit color"),b->{
            int[] colors={0xFFFF5555,0xFFFFAA00,0xFF55FF55,0xFF55AAFF,0xFFFFFFFF,0xFFFF55FF};
            PollithiumConfig.hitColor=nextColor(PollithiumConfig.hitColor,colors);PollithiumConfig.save();
        }).dimensions(left+215,top+226,200,20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"),b->close())
                .dimensions(left+215,top+202,200,20).build());
    }

    private static int nextColor(int current,int[] palette){
        for(int i=0;i<palette.length;i++)if(palette[i]==current)return palette[(i+1)%palette.length];
        return palette[0];
    }

    private void addToggle(String name,boolean enabled,int x,int y,Runnable change){
        addDrawableChild(ButtonWidget.builder(Text.literal(name+": "+(enabled?"ON":"OFF")),b->{change.run();PollithiumConfig.save();refresh();})
                .dimensions(x,y,200,20).build());
    }

    private void refresh(){clearChildren();init();}

    @Override public void render(DrawContext context,int mouseX,int mouseY,float delta){
        renderBackground(context,mouseX,mouseY,delta);
        context.drawCenteredTextWithShadow(textRenderer,title,width/2,height/2-158,0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,"Drag the preview to move your HUD",width/2,height/2+139,0xFFCCCCCC);
        int x=PollithiumConfig.hudX,y=PollithiumConfig.hudY;
        context.fill(x,y,x+150,y+34,PollithiumConfig.panelColor);
        context.fill(x,y,x+150,y+2,PollithiumConfig.accentColor);
        context.drawText(textRenderer,"Pollithium HUD preview",x+7,y+12,0xFFFFFFFF,false);
        super.render(context,mouseX,mouseY,delta);
    }


    @Override public boolean mouseClicked(double mx,double my,int button){
        if(button==0&&mx>=PollithiumConfig.hudX&&mx<=PollithiumConfig.hudX+150&&my>=PollithiumConfig.hudY&&my<=PollithiumConfig.hudY+34){
            dragging=true;dragOffsetX=(int)mx-PollithiumConfig.hudX;dragOffsetY=(int)my-PollithiumConfig.hudY;return true;
        }
        return super.mouseClicked(mx,my,button);
    }

    @Override public boolean mouseDragged(double mx,double my,int button,double dx,double dy){
        if(dragging&&button==0){
            PollithiumConfig.hudX=Math.max(0,Math.min(width-150,((int)mx-dragOffsetX)/8*8));
            PollithiumConfig.hudY=Math.max(0,Math.min(height-34,((int)my-dragOffsetY)/8*8));return true;
        }
        return super.mouseDragged(mx,my,button,dx,dy);
    }

    @Override public boolean mouseReleased(double mx,double my,int button){
        if(button==0&&dragging){dragging=false;PollithiumConfig.save();return true;}
        return super.mouseReleased(mx,my,button);
    }

    @Override public void close(){PollithiumConfig.save();if(client!=null)client.setScreen(null);}
}