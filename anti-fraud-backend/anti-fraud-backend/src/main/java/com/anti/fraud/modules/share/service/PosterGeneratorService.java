package com.anti.fraud.modules.share.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

/**
 * 分享海报生成服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PosterGeneratorService {

    // 海报尺寸配置
    private static final int POSTER_WIDTH = 750;
    private static final int POSTER_HEIGHT = 1334;
    private static final int LOGO_SIZE = 80;
    private static final int QRCODE_SIZE = 200;
    private static final int TITLE_FONT_SIZE = 24;
    private static final int SUBTITLE_FONT_SIZE = 16;
    private static final int DESCRIPTION_FONT_SIZE = 14;

    // 颜色配置
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color TITLE_COLOR = Color.BLACK;
    private static final Color SUBTITLE_COLOR = Color.GRAY;
    private static final Color DESCRIPTION_COLOR = Color.DARK_GRAY;
    private static final Color BORDER_COLOR = Color.LIGHT_GRAY;

    // 间距配置
    private static final int PADDING = 30;
    private static final int LOGO_TOP = 40;
    private static final int TITLE_TOP = 140;
    private static final int CONTENT_TOP = 200;
    private static final int QRCODE_TOP = 1000;

    /**
     * 生成分享海报
     * @param userId 用户ID
     * @param type 分享类型
     * @param targetId 目标ID
     * @param title 分享标题
     * @param description 分享描述
     * @param coverImageUrl 封面图片URL
     * @param qrCodeUrl 二维码URL
     * @return 海报图片Base64编码
     */
    public String generatePoster(Long userId, Integer type, Long targetId, String title, String description, String coverImageUrl, String qrCodeUrl) {
        try {
            // 创建海报画布
            BufferedImage poster = new BufferedImage(POSTER_WIDTH, POSTER_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = poster.createGraphics();

            // 设置背景
            g2d.setColor(BACKGROUND_COLOR);
            g2d.fillRect(0, 0, POSTER_WIDTH, POSTER_HEIGHT);

            // 绘制边框
            g2d.setColor(BORDER_COLOR);
            g2d.drawRect(0, 0, POSTER_WIDTH - 1, POSTER_HEIGHT - 1);

            // 绘制Logo
            drawLogo(g2d);

            // 绘制标题
            drawTitle(g2d, title);

            // 绘制封面图片
            if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                drawCoverImage(g2d, coverImageUrl);
            }

            // 绘制描述
            drawDescription(g2d, description);

            // 绘制二维码
            if (qrCodeUrl != null && !qrCodeUrl.isEmpty()) {
                drawQRCode(g2d, qrCodeUrl);
            }

            // 绘制底部文字
            drawFooter(g2d);

            // 释放资源
            g2d.dispose();

            // 转换为Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(poster, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            log.error("生成分享海报失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 绘制Logo
     */
    private void drawLogo(Graphics2D g2d) {
        try {
            // 绘制默认Logo
            g2d.setColor(Color.BLUE);
            g2d.fillRect((POSTER_WIDTH - LOGO_SIZE) / 2, LOGO_TOP, LOGO_SIZE, LOGO_SIZE);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
            g2d.drawString("反诈", (POSTER_WIDTH - 60) / 2, LOGO_TOP + LOGO_SIZE / 2 + 8);
        } catch (Exception e) {
            log.error("绘制Logo失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 绘制标题
     */
    private void drawTitle(Graphics2D g2d, String title) {
        try {
            g2d.setColor(TITLE_COLOR);
            g2d.setFont(new Font("微软雅黑", Font.BOLD, TITLE_FONT_SIZE));
            FontMetrics metrics = g2d.getFontMetrics();
            int titleWidth = metrics.stringWidth(title);
            int titleX = (POSTER_WIDTH - titleWidth) / 2;
            g2d.drawString(title, titleX, TITLE_TOP);
        } catch (Exception e) {
            log.error("绘制标题失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 绘制封面图片
     */
    private void drawCoverImage(Graphics2D g2d, String coverImageUrl) {
        try {
            URL url = new URL(coverImageUrl);
            Image image = ImageIO.read(url);
            if (image != null) {
                // 计算图片尺寸，保持宽高比
                int imageWidth = POSTER_WIDTH - 2 * PADDING;
                int imageHeight = imageWidth * image.getHeight(null) / image.getWidth(null);
                if (imageHeight > 400) {
                    imageHeight = 400;
                    imageWidth = imageHeight * image.getWidth(null) / image.getHeight(null);
                }
                int imageX = (POSTER_WIDTH - imageWidth) / 2;
                g2d.drawImage(image, imageX, CONTENT_TOP, imageWidth, imageHeight, null);
            }
        } catch (Exception e) {
            log.error("绘制封面图片失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 绘制描述
     */
    private void drawDescription(Graphics2D g2d, String description) {
        try {
            g2d.setColor(DESCRIPTION_COLOR);
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, DESCRIPTION_FONT_SIZE));
            FontMetrics metrics = g2d.getFontMetrics();
            
            // 文本换行
            String[] lines = description.split("\\n");
            int lineHeight = metrics.getHeight();
            int startY = CONTENT_TOP + 450;
            
            for (int i = 0; i < lines.length && i < 5; i++) { // 最多显示5行
                String line = lines[i];
                int lineWidth = metrics.stringWidth(line);
                int lineX = (POSTER_WIDTH - lineWidth) / 2;
                g2d.drawString(line, lineX, startY + i * lineHeight);
            }
        } catch (Exception e) {
            log.error("绘制描述失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 绘制二维码
     */
    private void drawQRCode(Graphics2D g2d, String qrCodeUrl) {
        try {
            URL url = new URL(qrCodeUrl);
            Image image = ImageIO.read(url);
            if (image != null) {
                int qrCodeX = (POSTER_WIDTH - QRCODE_SIZE) / 2;
                g2d.drawImage(image, qrCodeX, QRCODE_TOP, QRCODE_SIZE, QRCODE_SIZE, null);
            }
        } catch (Exception e) {
            log.error("绘制二维码失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 绘制底部文字
     */
    private void drawFooter(Graphics2D g2d) {
        try {
            g2d.setColor(SUBTITLE_COLOR);
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, SUBTITLE_FONT_SIZE));
            FontMetrics metrics = g2d.getFontMetrics();
            
            String footerText1 = "扫码了解更多反诈知识";
            String footerText2 = "反诈平台 - 守护您的财产安全";
            
            int footer1Width = metrics.stringWidth(footerText1);
            int footer1X = (POSTER_WIDTH - footer1Width) / 2;
            g2d.drawString(footerText1, footer1X, QRCODE_TOP + QRCODE_SIZE + 30);
            
            int footer2Width = metrics.stringWidth(footerText2);
            int footer2X = (POSTER_WIDTH - footer2Width) / 2;
            g2d.drawString(footerText2, footer2X, QRCODE_TOP + QRCODE_SIZE + 60);
        } catch (Exception e) {
            log.error("绘制底部文字失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 生成分享链接二维码
     * @param shareLink 分享链接
     * @return 二维码URL
     */
    public String generateQRCode(String shareLink) {
        try {
            // 实际实现需要调用二维码生成API
            // 暂时返回模拟URL
            return "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + java.net.URLEncoder.encode(shareLink, "UTF-8");
        } catch (Exception e) {
            log.error("生成二维码失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据分享类型获取分享标题
     * @param type 分享类型
     * @param targetId 目标ID
     * @return 分享标题
     */
    public String getShareTitle(Integer type, Long targetId) {
        switch (type) {
            case 1: // 知识分享
                return "【反诈知识】分享给你一篇实用的反诈文章";
            case 2: // 勋章分享
                return "【反诈成就】我获得了一枚反诈勋章，一起来看看吧";
            case 3: // 排行榜分享
                return "【反诈排行】我在反诈排行榜上的排名，一起来挑战吧";
            case 4: // 预警信息分享
                return "【反诈预警】最新反诈预警信息，快来看";
            default:
                return "【反诈平台】分享给你一个有用的反诈信息";
        }
    }

    /**
     * 根据分享类型获取分享描述
     * @param type 分享类型
     * @param targetId 目标ID
     * @return 分享描述
     */
    public String getShareDescription(Integer type, Long targetId) {
        switch (type) {
            case 1: // 知识分享
                return "了解最新的诈骗手段，提高防范意识，保护自己的财产安全。\n分享自反诈平台";
            case 2: // 勋章分享
                return "通过学习反诈知识，我获得了这枚勋章，你也来试试吧！\n分享自反诈平台";
            case 3: // 排行榜分享
                return "看看我在反诈排行榜上的表现，一起来学习反诈知识，提升排名！\n分享自反诈平台";
            case 4: // 预警信息分享
                return "最新的反诈预警信息，了解最新的诈骗手法，避免上当受骗。\n分享自反诈平台";
            default:
                return "反诈平台致力于提高全民反诈意识，保护大家的财产安全。\n分享自反诈平台";
        }
    }
}
