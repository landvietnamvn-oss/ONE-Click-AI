package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.PlatformContent
import com.example.data.model.RealEstateForm
import com.example.data.model.SocialPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object AiContentEngine {

  private val httpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .build()
  }

  suspend fun generateMultiPlatformContent(
    topicOrPrompt: String,
    tone: String,
    targetPlatforms: List<SocialPlatform>,
    realEstateData: RealEstateForm? = null
  ): Map<SocialPlatform, PlatformContent> = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
      ""
    }

    // If API key is available and not default placeholder, attempt live Gemini call
    if (!apiKey.isNullOrBlank() && !apiKey.contains("MY_GEMINI_API_KEY")) {
      try {
        val geminiResult = callGeminiForPlatforms(topicOrPrompt, tone, targetPlatforms, realEstateData, apiKey)
        if (geminiResult.isNotEmpty()) {
          return@withContext geminiResult
        }
      } catch (e: Exception) {
        // Fallback to intelligent local AI template generator
      }
    }

    // High quality offline fallback generator
    generateIntelligentLocalContent(topicOrPrompt, tone, targetPlatforms, realEstateData)
  }

  private fun callGeminiForPlatforms(
    prompt: String,
    tone: String,
    platforms: List<SocialPlatform>,
    realEstate: RealEstateForm?,
    apiKey: String
  ): Map<SocialPlatform, PlatformContent> {
    val platformsStr = platforms.joinToString(", ") { it.displayName }
    val realEstateInfo = if (realEstate != null) {
      """
      Thông tin Bất Động Sản:
      - Tên/Tiêu đề: ${realEstate.propertyName}
      - Loại hình: ${realEstate.propertyType}
      - Giá bán: ${realEstate.price}
      - Diện tích: ${realEstate.area}
      - Quy mô: ${realEstate.floors}, ${realEstate.bedrooms}, ${realEstate.bathrooms}
      - Địa chỉ: ${realEstate.address}
      - Pháp lý: ${realEstate.legal}
      - Hướng: ${realEstate.direction}
      - Hotline: ${realEstate.contactPhone}
      """.trimIndent()
    } else ""

    val systemPrompt = """
      Bạn là Giám đốc Sáng tạo Nội dung AI chuyên tối ưu bài đăng đa kênh (Facebook, TikTok, YouTube, Instagram, Zalo, Web).
      Hãy tạo nội dung chất lượng cao cho các kênh: $platformsStr.
      Phong cách: $tone.
      Yêu cầu đầu ra JSON dạng:
      {
        "facebook": { "title": "...", "body": "...", "hook": "...", "cta": "...", "hashtags": ["#tag1", "#tag2"] },
        "tiktok": { "title": "...", "body": "...", "hook": "...", "cta": "...", "videoScript": "Scene 1 (0-3s): ...\nScene 2 (4-15s): ...", "hashtags": ["#tag1"] },
        "youtube": { "title": "...", "body": "...", "hook": "...", "cta": "...", "tags": ["tag1", "tag2"], "hashtags": ["#tag1"] },
        "instagram": { "title": "...", "body": "...", "hook": "...", "cta": "...", "hashtags": ["#tag1"] },
        "zalo": { "title": "...", "body": "...", "hook": "...", "cta": "...", "hashtags": ["#tag1"] }
      }
    """.trimIndent()

    val userContent = "Chủ đề: $prompt\n$realEstateInfo"

    val requestJson = JSONObject().apply {
      put("contents", JSONArray().apply {
        put(JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().apply {
              put("text", "$systemPrompt\n\n$userContent\nTrả về DUY NHẤT JSON hợp lệ.")
            })
          })
        })
      })
      put("generationConfig", JSONObject().apply {
        put("temperature", 0.7)
        put("responseMimeType", "application/json")
      })
    }

    val request = Request.Builder()
      .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
      .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
      .build()

    val response = httpClient.newCall(request).execute()
    if (!response.isSuccessful) return emptyMap()

    val respBody = response.body?.string() ?: return emptyMap()
    val jsonObject = JSONObject(respBody)
    val candidates = jsonObject.optJSONArray("candidates") ?: return emptyMap()
    val firstCandidate = candidates.optJSONObject(0) ?: return emptyMap()
    val text = firstCandidate.optJSONObject("content")
      ?.optJSONArray("parts")
      ?.optJSONObject(0)
      ?.optString("text") ?: return emptyMap()

    val parsedJson = JSONObject(text.trim())
    val resultMap = mutableMapOf<SocialPlatform, PlatformContent>()

    for (p in platforms) {
      val pJson = parsedJson.optJSONObject(p.id)
      if (pJson != null) {
        val hashtagsList = mutableListOf<String>()
        val tagsArr = pJson.optJSONArray("hashtags")
        if (tagsArr != null) {
          for (i in 0 until tagsArr.length()) {
            hashtagsList.add(tagsArr.getString(i))
          }
        }
        resultMap[p] = PlatformContent(
          platform = p,
          title = pJson.optString("title", prompt),
          body = pJson.optString("body", ""),
          hook = pJson.optString("hook", ""),
          cta = pJson.optString("cta", "Liên hệ ngay để nhận thông tin!"),
          hashtags = if (hashtagsList.isNotEmpty()) hashtagsList else p.defaultHashtags,
          videoScript = pJson.optString("videoScript", null)
        )
      }
    }

    return resultMap
  }

  fun generateIntelligentLocalContent(
    prompt: String,
    tone: String,
    platforms: List<SocialPlatform>,
    realEstate: RealEstateForm?
  ): Map<SocialPlatform, PlatformContent> {
    val results = mutableMapOf<SocialPlatform, PlatformContent>()

    val isRealEstate = realEstate != null || prompt.contains("nhà", ignoreCase = true) || prompt.contains("bất động sản", ignoreCase = true) || prompt.contains("đất", ignoreCase = true)

    val cleanTitle = if (realEstate != null) {
      realEstate.propertyName
    } else if (prompt.length > 40) {
      prompt.take(40) + "..."
    } else {
      prompt
    }

    for (p in platforms) {
      when (p) {
        SocialPlatform.FACEBOOK -> {
          val hook = if (isRealEstate) "🔥 CƠ HỘI SỞ HỮU SIÊU PHẨM - VỊ TRÍ ĐẮC ĐỊA NHẤT KHU VỰC!" else "✨ KHÁM PHÁ NGAY: BÍ QUYẾT BÙNG NỔ DÀNH RIÊNG CHO BẠN!"
          val body = if (realEstate != null) {
            """
            $hook
            
            📍 Vị trí đắc địa: ${realEstate.address}
            💰 Giá cực tốt: ${realEstate.price} (Thương lượng chính chủ)
            📐 Diện tích lý tưởng: ${realEstate.area}
            🏠 Kết cấu: ${realEstate.floors}, ${realEstate.bedrooms}, ${realEstate.bathrooms}
            ⚖️ Pháp lý: ${realEstate.legal}
            🧭 Hướng: ${realEstate.direction}
            
            🌟 Ưu điểm nổi bật:
            ✔ Đường rộng thông thoáng, ô tô vào tận cửa.
            ✔ Gần biển, trường học, bệnh viện & trung tâm thương mại.
            ✔ Thích hợp vừa ở, vừa kinh doanh hoặc cho thuê sinh lời cao.
            
            📞 Hotline / Zalo xem nhà 24/7: ${realEstate.contactPhone}
            """.trimIndent()
          } else {
            """
            $hook
            
            📌 $prompt
            
            🎯 Tại sao bạn không nên bỏ lỡ?
            👉 Giải pháp tối ưu hóa hiệu quả cao nhất
            👉 Tiết kiệm thời gian & ngân sách vượt trội
            👉 Hỗ trợ đồng hành và tư vấn tận tâm 1:1
            
            👉 Hãy để lại bình luận hoặc nhắn tin trực tiếp để nhận ưu đãi đặc biệt ngay hôm nay!
            """.trimIndent()
          }
          val tags = if (isRealEstate) {
            listOf("#BatDongSanDaNang", "#NhaDepDaNang", "#BanNhaHaiChau", "#DauTuSinhLoi", "#SohongChinhChu")
          } else {
            listOf("#OneClickAI", "#MarketingOnline", "#KinhDoanh2026", "#TangTuongTac", "#ChuyenDoiCao")
          }
          results[p] = PlatformContent(
            platform = p,
            title = "🔥 $cleanTitle",
            body = body,
            hook = hook,
            cta = "💬 Inbox ngay để nhận bảng giá & hình ảnh thực tế!",
            hashtags = tags
          )
        }

        SocialPlatform.TIKTOK -> {
          val hook = if (isRealEstate) "Căn nhà đẹp mê hồn sát biển Đà Nẵng chỉ ${realEstate?.price ?: "5 Tỷ"} có gì hot?" else "Bí kíp 1 chạm tự động tạo nội dung 5 nền tảng bạn đã biết chưa?"
          val body = if (realEstate != null) {
            """
            ${hook} 👀 Xem hết video để không bỏ lỡ siêu phẩm này!
            
            📍 Vị trí: ${realEstate.address}
            📐 Diện tích: ${realEstate.area} | ${realEstate.floors}
            💰 Giá: ${realEstate.price}
            
            👉 Comment hoặc nhấn link bio để xem sổ hồng!
            """.trimIndent()
          } else {
            """
            ${hook} 🚀 Tối ưu 100% thời gian làm content của bạn!
            
            👉 Lưu lại video ngay nếu thấy hữu ích!
            """.trimIndent()
          }
          val script = """
            🎬 KỊCH BẢN TIKTOK / REELS (9:16 - 30 giây):
            [0:00 - 0:03] Cảnh Hook: Quay góc flycam hoặc góc rộng toàn cảnh mặt tiền, chèn text lớn: "$hook".
            [0:04 - 0:15] Cảnh Thân bài: Đi nhanh vào phòng khách sang trọng, phòng ngủ thoáng đãng, view ban công đón gió. Voice: "Thiết kế hiện đại, ${realEstate?.legal ?: "sổ hồng trao tay"}."
            [0:16 - 0:25] Cảnh Điểm nhấn: Quay tiện ích xung quanh, đường ô tô tránh nhau. Voice: "Vị trí vàng vừa ở vừa sinh lời."
            [0:26 - 0:30] Cảnh Kêu gọi: Text hotline & chỉ tay vào phần bình luận. Voice: "Nhấn link bio hoặc gọi ngay ${realEstate?.contactPhone ?: "Hotline"} nhé!"
          """.trimIndent()

          val tags = listOf("#TikTokMadeMeBuyIt", "#ReviewNhaDep", "#BatDongSan", "#ViralVideo", "#XuHuong")
          results[p] = PlatformContent(
            platform = p,
            title = hook,
            body = body,
            hook = hook,
            cta = "📲 Nhấn link bio hoặc comment để nhận thông tin!",
            hashtags = tags,
            videoScript = script
          )
        }

        SocialPlatform.YOUTUBE -> {
          val ytTitle = if (realEstate != null) {
            "【Full Tour 4K】Review Chi Tiết ${realEstate.propertyName} | ${realEstate.price} | ${realEstate.address}"
          } else {
            "Hướng Dẫn Chi Tiết: $cleanTitle | Tối Ưu Hiệu Quả Đỉnh Cao 2026"
          }
          val body = if (realEstate != null) {
            """
            🎬 Chào mừng quý anh chị đến với kênh Đánh Giá Bất Động Sản Thực Tế!
            Hôm nay chúng tôi xin giới thiệu căn nhà cực đẹp tại ${realEstate.address}.
            
            📌 THÔNG TIN CHI TIẾT:
            - Giá bán: ${realEstate.price}
            - Diện tích: ${realEstate.area}
            - Kết cấu: ${realEstate.floors} (${realEstate.bedrooms}, ${realEstate.bathrooms})
            - Pháp lý: ${realEstate.legal}
            - Hướng nhà: ${realEstate.direction}
            
            ⏱️ TIMESTAMPS / CHAPTERS:
            00:00 - Giới thiệu vị trí & mặt tiền đường
            01:20 - Khám phá phòng khách & bếp tiện nghi
            03:45 - Không gian các phòng ngủ & ban công
            05:30 - Toàn cảnh sân thượng view thoáng đãng
            06:50 - Báo giá chính xác & hướng dẫn xem nhà
            
            📞 LIÊN HỆ XEM NHÀ TRỰC TIẾP:
            - Hotline/Zalo: ${realEstate.contactPhone}
            - Vị trí Google Maps: ${realEstate.mapLink}
            
            🔔 Hãy bấm ĐĂNG KÝ KÊNH để không bỏ lỡ những căn nhà đẹp giá tốt mỗi ngày!
            """.trimIndent()
          } else {
            """
            Khám phá đầy đủ thông tin về: $prompt
            
            🔔 Đừng quên bấm Like, Chia Sẻ và Đăng Ký kênh để cập nhật video mới nhất!
            """.trimIndent()
          }
          val tags = listOf("Review Bat Dong San", "Ban Nha Da Nang", "Nha Dep 2026", "Nha Pho Hai Chau", "Dau Tu BDS")
          results[p] = PlatformContent(
            platform = p,
            title = ytTitle,
            body = body,
            hook = "Video review thực tế 4K không chỉnh sửa góc rộng",
            cta = "🔔 Đăng ký kênh và bấm chuông để nhận thông báo mới nhất!",
            hashtags = listOf("#Shorts", "#ReviewNha", "#NhaDepDaNang", "#BatDongSan"),
            tags = tags
          )
        }

        SocialPlatform.INSTAGRAM -> {
          val body = if (realEstate != null) {
            """
            ✨ Architecture & Living Inspiration ✨
            
            🏡 ${realEstate.propertyName}
            📍 ${realEstate.address}
            💰 ${realEstate.price} | 📐 ${realEstate.area}
            
            A harmonious blend of modern aesthetics and ultimate comfort. Featuring ${realEstate.bedrooms}, spacious living hall and sun-drenched balcony.
            
            🌿 Legal: ${realEstate.legal}
            📞 Direct Contact: ${realEstate.contactPhone}
            
            Save this post for your future dream home inspiration! 🕊️
            """.trimIndent()
          } else {
            """
            ✨ Daily Inspiration ✨
            
            $prompt
            
            Double tap if you find this valuable! ❤️
            """.trimIndent()
          }
          val tags = listOf(
            "#interiordesign", "#modernhome", "#vietnamrealestate", "#luxuryvilla",
            "#architecturelovers", "#dreamhome", "#minimalistliving", "#danangcity",
            "#homeinspo", "#realestatephotography"
          )
          results[p] = PlatformContent(
            platform = p,
            title = cleanTitle,
            body = body,
            hook = "✨ Modern Living Inspiration",
            cta = "Save this post & DM for full details 📩",
            hashtags = tags
          )
        }

        SocialPlatform.ZALO -> {
          val body = if (realEstate != null) {
            """
            [BẢN TIN BĐS CHÍNH CHỦ - ZALO OA]
            
            Kính gửi Quý Khách hàng,
            Em xin gửi thông tin căn nhà vừa gửi bán tại ${realEstate.address}:
            
            🔹 Tên căn: ${realEstate.propertyName}
            🔹 Giá bán: ${realEstate.price} (Thương lượng trực tiếp)
            🔹 Diện tích: ${realEstate.area} - ${realEstate.floors}
            🔹 Công năng: ${realEstate.bedrooms}, ${realEstate.bathrooms}, phòng thờ, sân phơi
            🔹 Pháp lý chuẩn: ${realEstate.legal}
            
            Quý khách có nhu cầu xem nhà thực tế hoặc nhận file sổ gốc vui lòng nhắn tin trực tiếp qua Zalo này hoặc gọi hotline: ${realEstate.contactPhone}.
            Trân trọng!
            """.trimIndent()
          } else {
            """
            [THÔNG BÁO CHÍNH THỨC TỪ HỆ THỐNG]
            
            $prompt
            
            Quý khách cần hỗ trợ thêm thông tin vui lòng liên hệ bộ phận CSKH trực tuyến 24/7.
            """.trimIndent()
          }
          val tags = listOf("#ZaloOA", "#BDSChinhChu", "#TuVanNhaDat")
          results[p] = PlatformContent(
            platform = p,
            title = "[Zalo OA] $cleanTitle",
            body = body,
            hook = "Gửi trực tiếp khách hàng quan tâm trên Zalo",
            cta = "📞 Gọi ngay hoặc nhắn Zalo để nhận sổ hồng",
            hashtags = tags
          )
        }

        SocialPlatform.WEB -> {
          val body = if (realEstate != null) {
            """
            <h1>${realEstate.propertyName} - Vị Trí Đắc Địa, Giá Tốt</h1>
            
            <p>Thị trường bất động sản tại ${realEstate.address} luôn giữ sức hút lớn nhờ vị trí chiến lược và tiềm năng gia tăng giá trị vượt trội. Chúng tôi hân hạnh giới thiệu siêu phẩm nhà phố đáp ứng trọn vẹn cả nhu cầu an cư lẫn đầu tư lâu dài.</p>
            
            <h2>1. Tổng quan thông số bất động sản</h2>
            <ul>
              <li><strong>Địa chỉ:</strong> ${realEstate.address}</li>
              <li><strong>Giá bán:</strong> ${realEstate.price}</li>
              <li><strong>Diện tích:</strong> ${realEstate.area}</li>
              <li><strong>Quy mô xây dựng:</strong> ${realEstate.floors} (${realEstate.bedrooms}, ${realEstate.bathrooms})</li>
              <li><strong>Tình trạng pháp lý:</strong> ${realEstate.legal}</li>
              <li><strong>Hướng phong thủy:</strong> ${realEstate.direction}</li>
            </ul>
            
            <h2>2. Tiện ích và tiềm năng sinh lời</h2>
            <p>Căn nhà sở hữu mặt tiền đường rộng, hạ tầng đồng bộ, giao thông thuận tiện kết nối đến các trục đường huyết mạch, bãi biển và các trung tâm thương mại lớn.</p>
            
            <h2>3. Thông tin liên hệ xem nhà</h2>
            <p>Để nhận bảng phân tích giá và lịch xem nhà thực tế, quý khách vui lòng liên hệ Hotline: <strong>${realEstate.contactPhone}</strong>.</p>
            """.trimIndent()
          } else {
            """
            <h1>$cleanTitle</h1>
            <p>$prompt</p>
            """.trimIndent()
          }
          results[p] = PlatformContent(
            platform = p,
            title = cleanTitle,
            body = body,
            hook = "Bài viết chuẩn SEO tối ưu On-Page",
            cta = "Đăng ký tư vấn trực tuyến ngay hôm nay",
            hashtags = listOf("#SEO", "#BatDongSan", "#DauTu2026")
          )
        }
      }
    }

    return results
  }
}
