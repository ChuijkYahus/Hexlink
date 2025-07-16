package jempasam.hexlink.hud

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext

class SpellHUD : HudRenderCallback{
    override fun onHudRender(drawContext: DrawContext?, tickDelta: Float) {
        TODO("Not yet implemented")
    }
/*    override fun onHudRender(ctx: DrawContext, tickDelta: Float) {
        val mc=MinecraftClient.getInstance()
        val tessellator= Tessellator.getInstance()
        val buf= tessellator.buffer
        val matrix= ctx.matrices.peek().positionMatrix
        var normal= ctx.matrices.peek().normalMatrix

        fun drawPattern(color: Int, pattern: HexPattern){
            val a: ItemFrameEntity?=null
            with(RenderHelper){
                RenderHelper.color =ColorHelper.Argb.getArgb(100, 255, 255, 255)
                lineWidth=10
                renderPattern(ctx.matrices, pattern, 300f, 300f, 100F, 100F)

                RenderHelper.color =color
                lineWidth=5
                renderPattern(ctx.matrices, pattern, 300f, 300f, 100F, 100F)
            }
        }

        with(RenderHelper){
            color=ColorHelper.Argb.getArgb(255, 0, 0, 255)
            lineWidth=5
            renderLine(ctx.matrices, 10f, 10f, 100f, 100f)

            color=ColorHelper.Argb.getArgb(100, 255, 255, 0)
            lineWidth=10
            renderLine(ctx.matrices, 10f, 100f, 100f, 10f)

            color=ColorHelper.Argb.getArgb(255, 0, 255, 255)
            lineWidth=20
            renderLine(ctx.matrices, 100f, 10f, 100f, 100f)

            // val pattern=PatternRegistry.lookupPattern(Identifier("hexcasting", "craft/artifact"))
            // drawPattern(ColorHelper.Argb.getArgb(255,255,0,0), pattern.prototype)

            // val pattern2=PatternRegistry.lookupPattern(Identifier("hexcasting", "craft/battery"))
            // drawPattern(ColorHelper.Argb.getArgb(255,0,255,0), pattern2.prototype)

            // val pattern3=PatternRegistry.lookupPattern(Identifier("hexcasting", "potion/weakness"))
            // drawPattern(ColorHelper.Argb.getArgb(255,0,255,255), pattern3.prototype)
        }

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()
        // RenderSystem.disableTexture()
        // RenderSystem.setShader(GameRenderer::getPositionColorShader)
        val color3=ColorHelper.Argb.getArgb(255, 0, 100, 255)
        buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        buf.vertex(matrix, 75f, 0f, 0f).color(color3).next()
        buf.vertex(matrix, 125f, 0f, 0f).color(color3).next()
        buf.vertex(matrix, 125f, 50f, 0f).color(color3).next()
        buf.vertex(matrix, 75f, 50f, 0f).color(color3).next()
        tessellator.draw()


        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
        //RenderSystem.enableTexture()

    }
*/
}