package net.ogbonex.pollithium;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class HudEditorScreen extends Screen {
    private int dragOffsetX,dragOffsetY;
    private boolean dragging;

    public HudEditorScreen(){ super(Text.literal("Pollithium HUD Editor")); }

    @Override protected void init(){
        int left=width/2-105, top=height/2-112;
        addToggle("FPS",PollithiumConfig.showFps,left,top,()->PollithiumConfig.showFps=!PollithiumConfig.showFps);
        addToggle("Ping",PollithiumConfig.showPing,left,top+24,()->PollithiumConfig.showPing=!PollithiumConfig.showPing);
        addToggle("Coordinates",PollithiumConfig.showCoordinates,left,top+48,()->PollithiumConfig.showCoordinates=!PollithiumConfig.showCoordinates);
        addToggle("Keystrokes",PollithiumConfig.showKeystrokes,left,top+72,()->PollithiumConfig.showKeystrokes=!PollithiumConfig.showKeystrokes);
        addToggle("CPS",PollithiumConfig.showCps,left,top+96,()->PollithiumConfig.showCps=!PollithiumConfig.showCps);
        addToggle("Armor durability",PollithiumConfig.showArmor,left,top+120,()->PollithiumConfig.showArmor=!PollithiumConfig.showArmor);
        addToggle("Potion effects",PollithiumConfig.showEffects,left+215,top,()->PollithiumConfig.showEffects=!PollithiumConfig.showEffects);
        addToggle("Clock",PollithiumConfig.showClock,left+215,top+24,()->PollithiumConfig.showClock=!PollithiumConfig.showClock);
        addToggle("Direction",PollithiumConfig.showDirection,left+215,top+48,()->PollithiumConfig.showDirection=!PollithiumConfig.showDirection);
        addToggle("Custom crosshair",PollithiumConfig.showCrosshair,left+215,top+72,()->PollithiumConfig.showCrosshair=!PollithiumConfig.showCrosshair);
        addToggle("Toggle sprint",PollithiumConfig.toggleSprintEnabled,left+215,top+96,()->PollithiumConfig.toggleSprintEnabled=!PollithiumConfig.toggleSprintEnabled);
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset HUD position"),b->{PollithiumConfig.hudX=8;PollithiumConfig.hudY=8;PollithiumConfig.save();})
                .dimensions(left,top+151,200,20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"),b->close())
                .dimensions(left+215,top+151,200,20).build());
    }

    private void addToggle(String name,boolean enabled,int x,int y,Runnable change){
        addDrawableChild(ButtonWidget.builder(Text.literal(name+": "+(enabled?"ON":"OFF")),b->{change.run();PollithiumConfig.save();refresh();})
                .dimensions(x,y,200,20).build());
    }

    private void refresh(){clearChildren();init();}

    @Override public void render(DrawContext context,int mouseX,int mouseY,float delta){
        renderBackground(context,mouseX,mouseY,delta);
        context.drawCenteredTextWithShadow(textRenderer,title,width/2,height/2-137,0xFFFFFFFF);
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
            PollithiumConfig.hudX=Math.max(0,Math.min(width-150,(int)mx-dragOffsetX));
            PollithiumConfig.hudY=Math.max(0,Math.min(height-34,(int)my-dragOffsetY));return true;
        }
        return super.mouseDragged(mx,my,button,dx,dy);
    }

    @Override public boolean mouseReleased(double mx,double my,int button){
        if(button==0&&dragging){dragging=false;PollithiumConfig.save();return true;}
        return super.mouseReleased(mx,my,button);
    }

    @Override public void close(){PollithiumConfig.save();if(client!=null)client.setScreen(null);}
}
