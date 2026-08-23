package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class SocialPlatform(
  val id: String,
  val displayName: String,
  val shortLabel: String,
  val brandColor: Color,
  val optimalMediaAspect: String,
  val characterLimit: Int,
  val defaultHashtags: List<String>
) {
  FACEBOOK(
    id = "facebook",
    displayName = "Facebook",
    shortLabel = "f",
    brandColor = BrandFacebook,
    optimalMediaAspect = "1:1 / 16:9",
    characterLimit = 63206,
    defaultHashtags = listOf("#batdongsan", "#nhadep", "#kinhdoanh", "#tintuc")
  ),
  TIKTOK(
    id = "tiktok",
    displayName = "TikTok",
    shortLabel = "J",
    brandColor = BrandTikTok,
    optimalMediaAspect = "9:16 (Video)",
    characterLimit = 2200,
    defaultHashtags = listOf("#xuhuong", "#fyp", "#reviewbds", "#nhadepdanang")
  ),
  YOUTUBE(
    id = "youtube",
    displayName = "YouTube",
    shortLabel = "▶",
    brandColor = BrandYouTube,
    optimalMediaAspect = "16:9 / 9:16 Shorts",
    characterLimit = 5000,
    defaultHashtags = listOf("#Shorts", "#ReviewNha", "#BatDongSan", "#VideoHD")
  ),
  ZALO(
    id = "zalo",
    displayName = "Zalo OA",
    shortLabel = "Z",
    brandColor = BrandZalo,
    optimalMediaAspect = "1:1",
    characterLimit = 2000,
    defaultHashtags = listOf("#ZaloOfficial", "#TuVanNhaDat", "#LienHeNgay")
  ),
  INSTAGRAM(
    id = "instagram",
    displayName = "Instagram",
    shortLabel = "ig",
    brandColor = BrandInstagram,
    optimalMediaAspect = "1:1 / 4:5",
    characterLimit = 2200,
    defaultHashtags = listOf("#homedecor", "#realestate", "#luxuryhomes", "#vietnam")
  ),
  WEB(
    id = "web",
    displayName = "Website / Blog",
    shortLabel = "W",
    brandColor = BrandWeb,
    optimalMediaAspect = "16:9",
    characterLimit = 50000,
    defaultHashtags = listOf("#SEO", "#BaiVietChuanSEO", "#BatDongSan2026")
  );

  companion object {
    fun fromId(id: String): SocialPlatform = entries.find { it.id.equals(id, ignoreCase = true) } ?: FACEBOOK
  }
}

enum class AccountStatus(val label: String, val badgeColor: Color, val badgeBg: Color) {
  CONNECTED("Hoạt động", Emerald500, Emerald50),
  EXPIRED_SOON("Sắp hết hạn", Amber500, Amber50),
  RECONNECT_REQUIRED("Cần kết nối lại", Rose500, Rose50),
  DISCONNECTED("Chưa kết nối", Slate400, Slate100)
}

enum class PostStatus(val label: String, val color: Color, val bg: Color) {
  DRAFT("Bản nháp", Slate500, Slate100),
  SCHEDULED("Đã lên lịch", Indigo600, Indigo50),
  PUBLISHING("Đang đăng...", Amber500, Amber50),
  PUBLISHED("Đã đăng", Emerald500, Emerald50),
  FAILED("Lỗi đăng", Rose500, Rose50)
}

data class SocialAccount(
  val id: String,
  val platform: SocialPlatform,
  val accountName: String,
  val accountHandle: String,
  val avatarSeed: String,
  val status: AccountStatus,
  val tokenExpiresDays: Int,
  val followersCount: String,
  val postsCount: Int,
  val isDefault: Boolean = false,
  val connectedDate: String = "2026-08-15"
)

data class PlatformContent(
  val platform: SocialPlatform,
  val title: String,
  val body: String,
  val hook: String = "",
  val cta: String = "Liên hệ ngay để nhận thông tin chi tiết!",
  val hashtags: List<String> = emptyList(),
  val videoScript: String? = null,
  val tags: List<String> = emptyList()
)

data class PostModel(
  val id: String,
  val title: String,
  val rawPrompt: String,
  val tone: String,
  val platforms: List<SocialPlatform>,
  val platformContents: Map<SocialPlatform, PlatformContent>,
  val mediaUrls: List<String> = emptyList(),
  val scheduledTime: String,
  val status: PostStatus,
  val campaignName: String? = null,
  val views: Int = 0,
  val likes: Int = 0,
  val comments: Int = 0,
  val shares: Int = 0,
  val createdAt: Long = System.currentTimeMillis(),
  val retryCount: Int = 0,
  val errorMessage: String? = null
)

data class CampaignModel(
  val id: String,
  val name: String,
  val target: String,
  val totalPosts: Int,
  val completedPosts: Int,
  val startDate: String,
  val endDate: String,
  val status: String,
  val channels: List<SocialPlatform>
)

data class LeadModel(
  val id: String,
  val customerName: String,
  val phone: String,
  val sourcePlatform: SocialPlatform,
  val propertyInterest: String,
  val status: String, // "Mới", "Đang tư vấn", "Đã chốt cọc"
  val timeAgo: String,
  val notes: String
)

data class RealEstateForm(
  val propertyName: String = "Bán Nhà Mặt Tiền Hải Châu, Đà Nẵng",
  val propertyType: String = "Nhà phố / Biệt thự",
  val price: String = "5.8 Tỷ",
  val area: String = "105 m² (Ngang 5m)",
  val bedrooms: String = "4 PN",
  val bathrooms: String = "4 WC",
  val floors: String = "3 Tầng",
  val address: String = "Đường Nguyễn Văn Linh, Q. Hải Châu",
  val legal: String = "Sổ hồng riêng chính chủ",
  val direction: String = "Đông Nam mát mẻ",
  val contactPhone: String = "0905.123.456",
  val mapLink: String = "https://maps.google.com/..."
)

data class PromptTemplate(
  val id: String,
  val title: String,
  val category: String,
  val promptText: String,
  val tone: String
)
