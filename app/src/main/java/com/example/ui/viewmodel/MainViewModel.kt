package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiContentEngine
import com.example.data.model.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationItem(val label: String, val icon: String) {
  DASHBOARD("Tổng quan", "home"),
  AI_STUDIO("AI Studio", "auto_awesome"),
  CONTENT_QUEUE("Lịch & Queue", "calendar_today"),
  ACCOUNTS("Tài khoản", "group"),
  ANALYTICS("Thống kê & CRM", "insights")
}

class MainViewModel : ViewModel() {

  // Navigation
  private val _currentTab = MutableStateFlow(NavigationItem.DASHBOARD)
  val currentTab: StateFlow<NavigationItem> = _currentTab.asStateFlow()

  fun setTab(tab: NavigationItem) {
    _currentTab.value = tab
  }

  // Repository Flows
  val accounts: StateFlow<List<SocialAccount>> = AppRepository.accounts
  val posts: StateFlow<List<PostModel>> = AppRepository.posts
  val campaigns: StateFlow<List<CampaignModel>> = AppRepository.campaigns
  val leads: StateFlow<List<LeadModel>> = AppRepository.leads

  // AI Studio Creator State
  private val _isRealEstateMode = MutableStateFlow(true)
  val isRealEstateMode: StateFlow<Boolean> = _isRealEstateMode.asStateFlow()

  private val _promptInput = MutableStateFlow("Bán nhà 3 tầng Hải Châu Đà Nẵng, đường 7.5m ô tô vào tận nơi, gần biển Mỹ Khê, giá 5.8 tỷ")
  val promptInput: StateFlow<String> = _promptInput.asStateFlow()

  private val _selectedTone = MutableStateFlow("Bán hàng & Chốt sale")
  val selectedTone: StateFlow<String> = _selectedTone.asStateFlow()

  private val _selectedPlatforms = MutableStateFlow<Set<SocialPlatform>>(
    setOf(SocialPlatform.FACEBOOK, SocialPlatform.TIKTOK, SocialPlatform.YOUTUBE, SocialPlatform.ZALO, SocialPlatform.INSTAGRAM)
  )
  val selectedPlatforms: StateFlow<Set<SocialPlatform>> = _selectedPlatforms.asStateFlow()

  private val _realEstateForm = MutableStateFlow(RealEstateForm())
  val realEstateForm: StateFlow<RealEstateForm> = _realEstateForm.asStateFlow()

  private val _isGenerating = MutableStateFlow(false)
  val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

  private val _generatedContents = MutableStateFlow<Map<SocialPlatform, PlatformContent>>(emptyMap())
  val generatedContents: StateFlow<Map<SocialPlatform, PlatformContent>> = _generatedContents.asStateFlow()

  private val _activePreviewPlatform = MutableStateFlow(SocialPlatform.FACEBOOK)
  val activePreviewPlatform: StateFlow<SocialPlatform> = _activePreviewPlatform.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  // Selected Campaign in Create Post
  private val _selectedCampaignName = MutableStateFlow("BÁN NHÀ ĐÀ NẴNG THÁNG 9")
  val selectedCampaignName: StateFlow<String> = _selectedCampaignName.asStateFlow()

  fun setRealEstateMode(isRealEstate: Boolean) {
    _isRealEstateMode.value = isRealEstate
  }

  fun setPromptInput(text: String) {
    _promptInput.value = text
  }

  fun setSelectedTone(tone: String) {
    _selectedTone.value = tone
  }

  fun togglePlatformSelection(platform: SocialPlatform) {
    val current = _selectedPlatforms.value.toMutableSet()
    if (current.contains(platform)) {
      if (current.size > 1) { // Keep at least one
        current.remove(platform)
      }
    } else {
      current.add(platform)
    }
    _selectedPlatforms.value = current
    if (!_selectedPlatforms.value.contains(_activePreviewPlatform.value)) {
      _activePreviewPlatform.value = current.firstOrNull() ?: SocialPlatform.FACEBOOK
    }
  }

  fun updateRealEstateField(transform: (RealEstateForm) -> RealEstateForm) {
    _realEstateForm.value = transform(_realEstateForm.value)
  }

  fun setActivePreviewPlatform(platform: SocialPlatform) {
    _activePreviewPlatform.value = platform
  }

  fun updatePlatformContent(platform: SocialPlatform, transform: (PlatformContent) -> PlatformContent) {
    val current = _generatedContents.value.toMutableMap()
    val existing = current[platform]
    if (existing != null) {
      current[platform] = transform(existing)
      _generatedContents.value = current
    }
  }

  fun generateAllContent() {
    viewModelScope.launch {
      _isGenerating.value = true
      val platforms = _selectedPlatforms.value.toList()
      val prompt = _promptInput.value
      val tone = _selectedTone.value
      val realEstate = if (_isRealEstateMode.value) _realEstateForm.value else null

      val result = AiContentEngine.generateMultiPlatformContent(
        topicOrPrompt = prompt,
        tone = tone,
        targetPlatforms = platforms,
        realEstateData = realEstate
      )

      _generatedContents.value = result
      _activePreviewPlatform.value = platforms.firstOrNull() ?: SocialPlatform.FACEBOOK
      _isGenerating.value = false
      _snackbarMessage.value = "✨ Đã tạo xong nội dung tối ưu cho ${platforms.size} nền tảng!"
    }
  }

  fun applyTemplate(template: PromptTemplate) {
    _promptInput.value = template.promptText
    _selectedTone.value = template.tone
    _isRealEstateMode.value = template.category.contains("Bất Động Sản")
    _snackbarMessage.value = "Đã áp dụng mẫu: ${template.title}"
  }

  fun publishImmediately() {
    val contents = _generatedContents.value
    if (contents.isEmpty()) {
      _snackbarMessage.value = "Vui lòng tạo nội dung trước khi đăng!"
      return
    }

    viewModelScope.launch {
      val title = contents.values.firstOrNull()?.title ?: "Bài đăng mới"
      val newPost = PostModel(
        id = "post_${UUID.randomUUID().toString().take(6)}",
        title = title,
        rawPrompt = _promptInput.value,
        tone = _selectedTone.value,
        platforms = _selectedPlatforms.value.toList(),
        platformContents = contents,
        mediaUrls = listOf("https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=600"),
        scheduledTime = "Vừa xong",
        status = PostStatus.PUBLISHING,
        campaignName = _selectedCampaignName.value
      )
      AppRepository.saveNewPost(newPost)
      _snackbarMessage.value = "🚀 Đang đăng bài đồng thời lên ${_selectedPlatforms.value.size} kênh..."
      AppRepository.publishPostImmediately(newPost.id)
      _snackbarMessage.value = "✅ Đăng bài thành công lên tất cả các kênh!"
      _currentTab.value = NavigationItem.CONTENT_QUEUE
    }
  }

  fun schedulePost(timeString: String = "09:00 Ngày mai") {
    val contents = _generatedContents.value
    if (contents.isEmpty()) {
      _snackbarMessage.value = "Vui lòng tạo nội dung trước khi lên lịch!"
      return
    }

    val title = contents.values.firstOrNull()?.title ?: "Bài đăng mới"
    val newPost = PostModel(
      id = "post_${UUID.randomUUID().toString().take(6)}",
      title = title,
      rawPrompt = _promptInput.value,
      tone = _selectedTone.value,
      platforms = _selectedPlatforms.value.toList(),
      platformContents = contents,
      mediaUrls = listOf("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600"),
      scheduledTime = timeString,
      status = PostStatus.SCHEDULED,
      campaignName = _selectedCampaignName.value
    )
    AppRepository.saveNewPost(newPost)
    _snackbarMessage.value = "📅 Đã lên lịch đăng vào $timeString!"
    _currentTab.value = NavigationItem.CONTENT_QUEUE
  }

  fun retryPost(postId: String) {
    viewModelScope.launch {
      _snackbarMessage.value = "Đang thử đăng lại bài viết..."
      AppRepository.retryPost(postId)
      _snackbarMessage.value = "Đăng lại thành công!"
    }
  }

  fun deletePost(postId: String) {
    AppRepository.deletePost(postId)
    _snackbarMessage.value = "Đã xóa bài viết khỏi hàng đợi."
  }

  fun refreshAccountToken(accountId: String) {
    AppRepository.refreshAccountToken(accountId)
    _snackbarMessage.value = "🔄 Token đã được làm mới thành công! (Hạn 60 ngày)"
  }

  fun toggleAccountConnection(accountId: String) {
    AppRepository.toggleAccountConnection(accountId)
    _snackbarMessage.value = "Đã cập nhật trạng thái kết nối tài khoản."
  }

  fun addSocialAccount(platform: SocialPlatform, name: String, handle: String) {
    AppRepository.addSocialAccount(platform, name, handle)
    _snackbarMessage.value = "Đã kết nối tài khoản ${platform.displayName}: $name"
  }

  fun addLead(name: String, phone: String, platform: SocialPlatform, property: String, notes: String) {
    AppRepository.addLead(name, phone, platform, property, notes)
    _snackbarMessage.value = "Đã lưu khách hàng tiềm năng vào CRM!"
  }

  fun updateLeadStatus(leadId: String, newStatus: String) {
    AppRepository.updateLeadStatus(leadId, newStatus)
    _snackbarMessage.value = "Đã cập nhật trạng thái: $newStatus"
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }
}
