package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public class WordPressHtmlBuilderService {

    public String buildHtml(ProductResponse product, AnalysisResult analysis) {
        StringBuilder html = new StringBuilder();

        html.append("<div style=\"font-family: Montserrat, Arial, sans-serif; color: #334155; max-width: 900px; margin: 0 auto;\">");

        // Üst Kısım: Kartlar (2 Sütunlu Grid)
        html.append("<div style=\"display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px;\">");

        // === SOL KART: Ürün Bilgisi ===
        html.append("<div style=\"border: 1px solid #e2e8f0; border-radius: 16px; padding: 20px; background: #fff;\">");
        html.append(String.format("<h2 style=\"font-size: 18px; font-weight: bold; margin-top: 0; color: #0f172a;\">%s</h2>", product.name()));
        html.append(String.format("<div style=\"font-size: 22px; font-weight: bold; color: #0f172a; margin: 10px 0;\">%s TL</div>", product.price()));
        html.append(String.format("<div style=\"color: #eab308; margin-bottom: 15px;\">★★★★★ <span style=\"color: #64748b; font-size: 12px;\">(%.1f/5.0) %d Yorum</span></div>", product.rating(), product.reviewCount()));

        html.append("<div style=\"display: flex; gap: 15px; align-items: flex-start;\">");
        if (product.imageUrl() != null && !product.imageUrl().isBlank()) {
            html.append(String.format("<img src=\"%s\" style=\"width: 100px; height: auto; object-fit: contain; border-radius: 8px;\" />", product.imageUrl()));
        }
        html.append(String.format("<div><strong style=\"display:block; margin-bottom:5px; color: #0f172a; font-size: 14px;\">Hızlı Özet</strong><p style=\"font-size: 12px; color: #64748b; line-height: 1.5; margin:0;\">%s</p></div>", analysis.summary()));
        html.append("</div></div>");

        // === SAĞ KART: AiPreferenceCard Uyumlu Sarı Kısım (Neden Alınır & Dikkat + Skor) ===
        html.append("<div style=\"border: 1px solid #E6C84A; border-radius: 16px; padding: 20px; background: #FFFFFC; display: flex; justify-content: space-between; align-items: flex-start; gap: 15px;\">");

        // Sol Taraf: Neden Alınır ve Dikkat Edilmesi Gerekenler (PRO / CON Filtreleme)
        html.append("<div style=\"flex: 1;\">");

        // Neden Alınır Başlık ve Liste
        html.append("<h4 style=\"font-weight: bold; color: #0f172a; font-size: 14px; margin: 0 0 8px 0;\">Neden Alınır?</h4>");
        html.append("<ul style=\"list-style: none; padding: 0; margin: 0 0 16px 0; font-size: 12px; color: #334155;\">");

        if (analysis.highlights() != null && !analysis.highlights().isEmpty()) {
            var pros = analysis.highlights().stream()
                    .filter(h -> "PRO".equalsIgnoreCase(h.commentType() != null ? h.commentType().toString() : ""))
                    .toList();

            for (var pro : pros) {
                html.append(String.format("<li style=\"display: flex; align-items: center; gap: 8px; margin-bottom: 6px;\"><span style=\"width: 8px; height: 8px; background-color: #22c55e; border-radius: 50%%; display: inline-block; flex-shrink: 0;\"></span>%s</li>", pro.aiComments()));
            }
            if (pros.isEmpty()) {
                html.append("<li style=\"color: #64748b;\">Öne çıkan olumlu özellik bulunmuyor.</li>");
            }
        }
        html.append("</ul>");

        // Dikkat Edilmesi Gerek! Başlık ve Liste
        html.append("<h4 style=\"font-weight: bold; color: #0f172a; font-size: 14px; margin: 0 0 8px 0;\">Dikkat Edilmesi Gerek !</h4>");
        html.append("<ul style=\"list-style: none; padding: 0; margin: 0; font-size: 12px; color: #334155;\">");

        if (analysis.highlights() != null && !analysis.highlights().isEmpty()) {
            var cons = analysis.highlights().stream()
                    .filter(h -> "CON".equalsIgnoreCase(h.commentType() != null ? h.commentType().toString() : ""))
                    .toList();

            for (var con : cons) {
                html.append(String.format("<li style=\"display: flex; align-items: center; gap: 8px; margin-bottom: 6px;\"><span style=\"width: 8px; height: 8px; background-color: #ef4444; border-radius: 50%%; display: inline-block; flex-shrink: 0;\"></span>%s</li>", con.aiComments()));
            }
            if (cons.isEmpty()) {
                html.append("<li style=\"color: #64748b;\">Dikkat edilmesi gereken olumsuz özellik bulunmuyor.</li>");
            }
        }
        html.append("</ul>");

        html.append("</div>"); // Sol taraf kapanış

        // Sağ Taraf: Skor Rozeti (Puan Halkası)
        html.append("<div style=\"display: flex; align-items: center; justify-content: center; flex-shrink: 0;\">");
        html.append("<div style=\"width: 75px; height: 75px; border-radius: 50%; border: 6px solid #22c55e; display: flex; align-items: center; justify-content: center; background: #ffffff;\">");
        html.append(String.format("<span style=\"font-size: 16px; font-weight: bold; color: #0f172a;\">★ %.1f</span>", analysis.aiScore()));
        html.append("</div></div>");

        html.append("</div></div>"); // Sağ kart ve üst grid kapanış

        // === ALT KISIM: MostLikedFeatures Uyumlu Özellik ve Yorum Kartları ===
        html.append("<div style=\"display: grid; grid-template-columns: 1fr 1fr; gap: 20px;\">");
        html.append(buildFeatureCard(analysis, "LOVED"));
        html.append(buildFeatureCard(analysis, "COMPLAINED"));
        html.append("</div>"); // Alt grid kapanış

        html.append("</div>"); // Ana kapsayıcı kapanış

        return html.toString();
    }

    // MostLikedFeatures.jsx Mantığını Taklit Eden Yardımcı Metot

    private String buildFeatureCard(AnalysisResult analysis, String type) {
        boolean isLoved = "LOVED".equalsIgnoreCase(type);
        String title = isLoved ? "En çok sevilen özellikler" : "En çok şikayet edilen özellikler";
        String commentTitle = isLoved ? "En çok beğeni alan olumlu yorum" : "En çok beğeni alan olumsuz yorum";
        String comment = isLoved ? analysis.topPositiveComment() : analysis.topNegativeComment();
        String icon = isLoved ? "👍" : "👎";
        String cardBg = isLoved ? "#F0FDF4" : "#FFF1F2";
        String cardBorder = isLoved ? "#22C55E" : "#EF4444";
        String trackBg = isLoved ? "#dcfce7" : "#fee2e2";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<div style=\"border: 1px solid %s; border-radius: 16px; padding: 20px; background: %s; display: flex; flex-direction: column; justify-content: space-between; font-family: Montserrat, Arial, sans-serif;\">", cardBorder, cardBg));

        sb.append("<div>");
        sb.append(String.format("<h3 style=\"font-weight: bold; color: #0f172a; font-size: 16px; margin-top: 0; margin-bottom: 16px;\">%s</h3>", title));

        sb.append("<div style=\"display: flex; flex-direction: column; gap: 12px;\">");

        if (analysis.featureResults() != null) {
            var features = analysis.featureResults().stream()
                    .filter(f -> {
                        String loveTypeStr = f.loveType() != null ? f.loveType().toString() : "";
                        return type.equalsIgnoreCase(loveTypeStr);
                    })
                    .toList();

            for (var feature : features) {
                String barColor = getFeatureBarColor(feature.percentage(), type);

                // Doğrudan Integer olarak kullanıyoruz (Hiçbir dönüşüm yok)
                int percentage = feature.percentage();

                sb.append("<div style=\"display: grid; grid-template-columns: 140px 1fr 50px; align-items: center; gap: 12px; font-size: 14px;\">");
                // 1. Özellik Adı
                sb.append(String.format("<span style=\"font-weight: 600; color: #1e293b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\" title=\"%s\">%s</span>", feature.featureName(), feature.featureName()));
                // 2. Progress Bar Arka Planı (width değeri %d ile integer olarak verildi)
                sb.append(String.format("<div style=\"width: 100%%; height: 16px; background: %s; overflow: hidden; border-radius: 4px;\">", trackBg));
                sb.append(String.format("<div style=\"height: 100%%; background: %s; width: %d%%;\"></div>", barColor, percentage));
                sb.append("</div>");
                // 3. Yüzde Değeri
                sb.append(String.format("<span style=\"font-weight: bold; color: #334155; text-align: right;\">%%%d</span>", percentage));
                sb.append("</div>");
            }
        }
        sb.append("</div>");
        sb.append("</div>");

        // Alt Yorum Alanı
        sb.append("<div style=\"margin-top: 24px; padding-top: 16px; border-top: 1px solid rgba(226, 232, 240, 0.6);\">");
        sb.append("<div style=\"display: flex; align-items: center; gap: 8px;\">");
        sb.append(String.format("<span style=\"font-size: 16px;\">%s</span>", icon));
        sb.append(String.format("<span style=\"font-weight: bold; font-size: 14px; color: #1e293b;\">%s</span>", commentTitle));
        sb.append("</div>");
        sb.append(String.format("<p style=\"color: #747373; font-size: 12px; margin-top: 8px; margin-bottom: 0; font-style: italic; padding-left: 24px; line-height: 1.5;\">\"%s\"</p>", comment));
        sb.append("</div>");

        sb.append("</div>");
        return sb.toString();
    }

    // MostLikedFeatures.jsx İçindeki getBarColor Mantığı
    private String getFeatureBarColor(int percentage, String type) {
        boolean isLoved = "LOVED".equalsIgnoreCase(type);
        if (isLoved) {
            if (percentage >= 90) return "#0BC505";
            if (percentage >= 75) return "#FFEB00";
            if (percentage >= 50) return "#FFAE4C";
            return "#FF0303";
        } else {
            if (percentage >= 90) return "#FF0303";
            if (percentage >= 75) return "#FFAE4C";
            if (percentage >= 50) return "#FFEB00";
            return "#0BC505";
        }
    }
}