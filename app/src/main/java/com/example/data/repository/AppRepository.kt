package com.example.data.repository

import com.example.data.ai.AiContentEngine
import com.example.data.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object AppRepository {

  // Initial Mock Social Accounts
  private val initialAccounts = listOf(
    SocialAccount(
      id = "acc_fb_1",
      platform = SocialPlatform.FACEBOOK,
      accountName = "Bất Động Sản Đà Nẵng Official",
      accountHandle = "@danangrealestate",
      avatarSeed = "Felix",
      status = AccountStatus.CONNECTED,
      tokenExpiresDays = 45,
      followersCount = "18.5k",
      postsCount = 86,
      isDefault = true,
      connectedDate = "2026-06-10"
    ),
    SocialAccount(
      id = "acc_tt_1",
      platform = SocialPlatform.TIKTOK,
      accountName = "Nhà Đẹp Miền Trung",
      accountHandle = "@nhadepmientrung",
      avatarSeed = "Aneka",
      status = AccountStatus.CONNECTED,
      tokenExpiresDays = 28,
      followersCount = "54.2k",
      postsCount = 112,
      isDefault = true,
      connectedDate = "2026-07-01"
    ),
    SocialAccount(
      id = "acc_yt_1",
      platform = SocialPlatform.YOUTUBE,
      accountName = "Review Nhà Đất TV",
      accountHandle = "@ReviewNhaDatTV",
      avatarSeed = "Jack",
      status = AccountStatus.EXPIRED_SOON,
      tokenExpiresDays = 3,
      followersCount = "12.8k",
      postsCount = 45,
      isDefault = true,
      connectedDate = "2026-05-20"
    ),
    SocialAccount(
      id = "acc_zalo_1",
      platform = SocialPlatform.ZALO,
      accountName = "Zalo OA Bất Động Sản",
      accountHandle = "OA: 2983749281",
      avatarSeed = "Milo",
      status = AccountStatus.CONNECTED,
      tokenExpiresDays = 90,
      followersCount = "6.4k",
      postsCount = 64,
      isDefault = true,
      connectedDate = "2026-08-01"
    ),
    SocialAccount(
      id = "acc_ig_1",
      platform = SocialPlatform.INSTAGRAM,
      accountName = "Danang.LuxuryHomes",
      accountHandle = "@danang.luxuryhomes",
      avatarSeed = "Sara",
      status = AccountStatus.RECONNECT_REQUIRED,
      tokenExpiresDays = 0,
      followersCount = "9.1k",
      postsCount = 38,
      isDefault = false,
      connectedDate = "2026-04-12"
    )
  )

  private val _accounts = MutableStateFlow<List<SocialAccount>>(initialAccounts)
  val accounts: StateFlow<List<SocialAccount>> = _accounts.asStateFlow()

  // Sample Preloaded Posts
  private val initialPosts = mutableListOf(
    PostModel(
      id = "post_1",
      title = "🔥 Bán nhà 3 tầng Hải Châu gần biển 5.8 Tỷ",
      rawPrompt = "Bán nhà 3 tầng Hải Châu Đà Nẵng, đường 7.5m, sổ hồng chính chủ, 4 phòng ngủ, gần biển Mỹ Khê giá 5.8 tỷ",
      tone = "Bán hàng & Chốt sale",
      platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.TIKTOK, SocialPlatform.YOUTUBE, SocialPlatform.ZALO),
      platformContents = AiContentEngine.generateIntelligentLocalContent(
        prompt = "Bán nhà 3 tầng Hải Châu Đà Nẵng, đường 7.5m, sổ hồng chính chủ, 4 phòng ngủ, gần biển Mỹ Khê giá 5.8 tỷ",
        tone = "Bán hàng & Chốt sale",
        platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.TIKTOK, SocialPlatform.YOUTUBE, SocialPlatform.ZALO),
        realEstate = RealEstateForm()
      ),
      mediaUrls = listOf("https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=600"),
      scheduledTime = "09:00 Hôm nay",
      status = PostStatus.PUBLISHED,
      campaignName = "BÁN NHÀ ĐÀ NẴNG THÁNG 9",
      views = 14200,
      likes = 890,
      comments = 142,
      shares = 54,
      createdAt = System.currentTimeMillis() - 86400000
    ),
    PostModel(
      id = "post_2",
      title = "🌊 Biệt thự sân vườn view sông Hàn giá tốt",
      rawPrompt = "Giới thiệu biệt thự sân vườn 250m2 view trực diện sông Hàn, nội thất gỗ óc chó cao cấp",
      tone = "Cao cấp & Sang trọng",
      platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.TIKTOK, SocialPlatform.INSTAGRAM),
      platformContents = AiContentEngine.generateIntelligentLocalContent(
        prompt = "Giới thiệu biệt thự sân vườn 250m2 view trực diện sông Hàn, nội thất gỗ óc chó cao cấp",
        tone = "Cao cấp & Sang trọng",
        platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.TIKTOK, SocialPlatform.INSTAGRAM),
        realEstate = null
      ),
      mediaUrls = listOf("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600"),
      scheduledTime = "14:30 Chiều nay",
      status = PostStatus.SCHEDULED,
      campaignName = "BÁN NHÀ ĐÀ NẴNG THÁNG 9",
      views = 0,
      likes = 0,
      comments = 0,
      shares = 0,
      createdAt = System.currentTimeMillis() - 36000000
    ),
    PostModel(
      id = "post_3",
      title = "📌 Căn hộ studio ven biển Mỹ Khê full nội thất",
      rawPrompt = "Cho thuê / bán căn hộ studio biển Sơn Trà 45m2 chỉ 1.4 tỷ",
      tone = "Viral & Hook mạnh",
      platforms = listOf(SocialPlatform.TIKTOK, SocialPlatform.YOUTUBE),
      platformContents = AiContentEngine.generateIntelligentLocalContent(
        prompt = "Cho thuê / bán căn hộ studio biển Sơn Trà 45m2 chỉ 1.4 tỷ",
        tone = "Viral & Hook mạnh",
        platforms = listOf(SocialPlatform.TIKTOK, SocialPlatform.YOUTUBE),
        realEstate = null
      ),
      mediaUrls = listOf("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=600"),
      scheduledTime = "19:00 Tối nay",
      status = PostStatus.FAILED,
      campaignName = "DỰ ÁN STUDIO SƠN TRÀ",
      views = 0,
      likes = 0,
      comments = 0,
      shares = 0,
      createdAt = System.currentTimeMillis() - 7200000,
      retryCount = 1,
      errorMessage = "Instagram token expired - Vui lòng kết nối lại tài khoản"
    )
  )

  private val _posts = MutableStateFlow<List<PostModel>>(initialPosts)
  val posts: StateFlow<List<PostModel>> = _posts.asStateFlow()

  // Campaigns
  private val _campaigns = MutableStateFlow<List<CampaignModel>>(
    listOf(
      CampaignModel(
        id = "camp_1",
        name = "BÁN NHÀ ĐÀ NẴNG THÁNG 9",
        target = "Quảng bá 20 căn nhà phố & biệt thự tại Hải Châu, Sơn Trà",
        totalPosts = 50,
        completedPosts = 34,
        startDate = "01/09/2026",
        endDate = "30/09/2026",
        status = "Đang chạy",
        channels = listOf(SocialPlatform.FACEBOOK, SocialPlatform.TIKTOK, SocialPlatform.YOUTUBE, SocialPlatform.ZALO)
      ),
      CampaignModel(
        id = "camp_2",
        name = "RA MẮT SHOPHOUSE BIỂN SƠN TRÀ",
        target = "Tạo chuỗi 30 video ngắn TikTok & Reels tiếp cận 100k khách",
        totalPosts = 30,
        completedPosts = 18,
        startDate = "15/08/2026",
        endDate = "15/09/2026",
        status = "Đang chạy",
        channels = listOf(SocialPlatform.TIKTOK, SocialPlatform.INSTAGRAM, SocialPlatform.YOUTUBE)
      )
    )
  )
  val campaigns: StateFlow<List<CampaignModel>> = _campaigns.asStateFlow()

  // Leads / CRM
  private val _leads = MutableStateFlow<List<LeadModel>>(
    listOf(
      LeadModel(
        id = "lead_1",
        customerName = "Anh Tuấn Nguyễn",
        phone = "0912.888.777",
        sourcePlatform = SocialPlatform.FACEBOOK,
        propertyInterest = "Nhà 3 tầng Hải Châu 5.8 Tỷ",
        status = "Mới",
        timeAgo = "15 phút trước",
        notes = "Khách comment hỏi xem sổ hồng và hẹn chiều mai 16h đi xem nhà."
      ),
      LeadModel(
        id = "lead_2",
        customerName = "Chị Mai Linh",
        phone = "0987.654.321",
        sourcePlatform = SocialPlatform.TIKTOK,
        propertyInterest = "Biệt thự ven sông Hàn",
        status = "Đang tư vấn",
        timeAgo = "2 giờ trước",
        notes = "Nhắn tin qua link bio, đang gửi video flycam thực tế."
      ),
      LeadModel(
        id = "lead_3",
        customerName = "Bác Hoàng",
        phone = "0903.112.233",
        sourcePlatform = SocialPlatform.ZALO,
        propertyInterest = "Căn hộ Studio Sơn Trà",
        status = "Đã chốt cọc",
        timeAgo = "1 ngày trước",
        notes = "Đã cọc 50 triệu giữ chỗ căn góc tầng 8 view biển."
      )
    )
  )
  val leads: StateFlow<List<LeadModel>> = _leads.asStateFlow()

  // Templates
  val templates = listOf(
    PromptTemplate(
      id = "tpl_1",
      title = "Bán Nhà Phố Chính Chủ",
      category = "Bất Động Sản",
      promptText = "Viết bài bán nhà phố chính chủ đầy đủ thông số vị trí, giá, diện tích, pháp lý và hotline rõ ràng, giọng văn tin cậy, chuẩn SEO.",
      tone = "Bán hàng & Chốt sale"
    ),
    PromptTemplate(
      id = "tpl_2",
      title = "Kịch Bản TikTok Hook Siêu Viral",
      category = "Video Ngắn",
      promptText = "Tạo kịch bản TikTok 30s với 3s đầu hook bất ngờ về mức giá hoặc thiết kế độc lạ, kèm hướng dẫn cảnh quay chi tiết.",
      tone = "Viral & Hook mạnh"
    ),
    PromptTemplate(
      id = "tpl_3",
      title = "Bản Tin Zalo OA Chăm Sóc Khách",
      category = "Tin Nhắn Trực Tiếp",
      promptText = "Soạn tin nhắn thông báo bất động sản mới đến khách hàng VIP qua Zalo, ngắn gọn, lịch sự, có CTA gọi điện.",
      tone = "Chuyên nghiệp"
    ),
    PromptTemplate(
      id = "tpl_4",
      title = "Bài Viết Review YouTube Chuẩn SEO",
      category = "Video Dài",
      promptText = "Viết tiêu đề giật tít, mô tả chi tiết kèm mốc thời gian chapters và bộ từ khóa SEO tìm kiếm.",
      tone = "Tư vấn Chuyên sâu"
    )
  )

  // Actions
  suspend fun publishPostImmediately(postId: String) {
    val current = _posts.value.toMutableList()
    val index = current.indexOfFirst { it.id == postId }
    if (index != -1) {
      current[index] = current[index].copy(status = PostStatus.PUBLISHING)
      _posts.value = current
      delay(1800) // Simulate multi-channel API publishing
      current[index] = current[index].copy(
        status = PostStatus.PUBLISHED,
        views = (500..2500).random(),
        likes = (30..200).random(),
        comments = (5..30).random()
      )
      _posts.value = current
    }
  }

  suspend fun retryPost(postId: String) {
    val current = _posts.value.toMutableList()
    val index = current.indexOfFirst { it.id == postId }
    if (index != -1) {
      current[index] = current[index].copy(status = PostStatus.PUBLISHING, errorMessage = null)
      _posts.value = current
      delay(2000)
      current[index] = current[index].copy(
        status = PostStatus.PUBLISHED,
        views = (800..3200).random(),
        likes = (60..240).random(),
        comments = (10..40).random()
      )
      _posts.value = current
    }
  }

  fun saveNewPost(post: PostModel) {
    val current = _posts.value.toMutableList()
    current.add(0, post)
    _posts.value = current
  }

  fun deletePost(postId: String) {
    val current = _posts.value.filter { it.id != postId }
    _posts.value = current
  }

  fun refreshAccountToken(accountId: String) {
    val current = _accounts.value.toMutableList()
    val index = current.indexOfFirst { it.id == accountId }
    if (index != -1) {
      val acc = current[index]
      current[index] = acc.copy(
        status = AccountStatus.CONNECTED,
        tokenExpiresDays = 60
      )
      _accounts.value = current
    }
  }

  fun toggleAccountConnection(accountId: String) {
    val current = _accounts.value.toMutableList()
    val index = current.indexOfFirst { it.id == accountId }
    if (index != -1) {
      val acc = current[index]
      val newStatus = if (acc.status == AccountStatus.DISCONNECTED) AccountStatus.CONNECTED else AccountStatus.DISCONNECTED
      current[index] = acc.copy(
        status = newStatus,
        tokenExpiresDays = if (newStatus == AccountStatus.CONNECTED) 60 else 0
      )
      _accounts.value = current
    }
  }

  fun addSocialAccount(platform: SocialPlatform, name: String, handle: String) {
    val current = _accounts.value.toMutableList()
    val newAcc = SocialAccount(
      id = "acc_${UUID.randomUUID().toString().take(6)}",
      platform = platform,
      accountName = name,
      accountHandle = handle,
      avatarSeed = name.replace(" ", ""),
      status = AccountStatus.CONNECTED,
      tokenExpiresDays = 60,
      followersCount = "1.0k",
      postsCount = 0,
      isDefault = false
    )
    current.add(newAcc)
    _accounts.value = current
  }

  fun addLead(customerName: String, phone: String, platform: SocialPlatform, propertyInterest: String, notes: String) {
    val current = _leads.value.toMutableList()
    val newLead = LeadModel(
      id = "lead_${UUID.randomUUID().toString().take(6)}",
      customerName = customerName,
      phone = phone,
      sourcePlatform = platform,
      propertyInterest = propertyInterest,
      status = "Mới",
      timeAgo = "Vừa xong",
      notes = notes
    )
    current.add(0, newLead)
    _leads.value = current
  }

  fun updateLeadStatus(leadId: String, newStatus: String) {
    val current = _leads.value.toMutableList()
    val index = current.indexOfFirst { it.id == leadId }
    if (index != -1) {
      current[index] = current[index].copy(status = newStatus)
      _leads.value = current
    }
  }
}
