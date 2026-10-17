package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PromptTemplate
import com.example.data.model.RealEstateForm
import com.example.data.model.SocialPlatform
import com.example.data.repository.AppRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AiStudioScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val isRealEstateMode by viewModel.isRealEstateMode.collectAsState()
  val promptInput by viewModel.promptInput.collectAsState()
  val selectedTone by viewModel.selectedTone.collectAsState()
  val selectedPlatforms by viewModel.selectedPlatforms.collectAsState()
  val realEstateForm by viewModel.realEstateForm.collectAsState()
  val isGenerating by viewModel.isGenerating.collectAsState()
  val generatedContents by viewModel.generatedContents.collectAsState()
  val activePlatform by viewModel.activePreviewPlatform.collectAsState()

  val tones = listOf(
    "Bán hàng & Chốt sale",
    "Chuyên nghiệp",
    "Viral & Hook mạnh",
    "Tư vấn Chuyên sâu",
    "Cao cấp & Sang trọng",
    "Hài hước & Gần gũi",
    "Ngắn gọn súc tích"
  )

  var showTemplateDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundCanvas)
  ) {
    // Header
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = SurfaceWhite,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "AI Content Studio",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Slate900
            )
          )
          Text(
            text = "1 nội dung → Tự động viết cho 5+ nền tảng",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Slate500,
              fontSize = 11.5.sp
            )
          )
        }

        OutlinedButton(
          onClick = { showTemplateDialog = true },
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Indigo100),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Indigo600
          ),
          modifier = Modifier.testTag("btn_templates")
        ) {
          Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = "Templates",
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Mẫu Prompt",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          )
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
      // 1. Content Mode Selector
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Slate100)
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isRealEstateMode) SurfaceWhite else Color.Transparent)
              .clickable { viewModel.setRealEstateMode(true) }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "🏠 Bất Động Sản",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isRealEstateMode) FontWeight.Bold else FontWeight.Medium,
                  color = if (isRealEstateMode) Indigo600 else Slate600,
                  fontSize = 12.5.sp
                )
              )
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (!isRealEstateMode) SurfaceWhite else Color.Transparent)
              .clickable { viewModel.setRealEstateMode(false) }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "✍️ Viết Bài Chung",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (!isRealEstateMode) FontWeight.Bold else FontWeight.Medium,
                color = if (!isRealEstateMode) Indigo600 else Slate600,
                fontSize = 12.5.sp
              )
            )
          }
        }
      }

      // 2. Real Estate Input Form or General Prompt Input
      item {
        if (isRealEstateMode) {
          RealEstateInputCard(
            form = realEstateForm,
            onUpdate = { transform -> viewModel.updateRealEstateField(transform) }
          )
        } else {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(24.dp))
              .background(SurfaceWhite)
              .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "CHỦ ĐỀ HOẶC NỘI DUNG GỐC",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Slate500,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  letterSpacing = 0.5.sp
                )
              )
              OutlinedTextField(
                value = promptInput,
                onValueChange = { viewModel.setPromptInput(it) },
                placeholder = { Text("Nhập ý tưởng, sản phẩm, ưu đãi hoặc nội dung thô...") },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_prompt"),
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Indigo600,
                  unfocusedBorderColor = BorderSubtle
                )
              )
            }
          }
        }
      }

      // 3. Tone / Style Selector
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "PHONG CÁCH VIẾT BÀI",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Slate500,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              letterSpacing = 0.5.sp
            )
          )
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tones) { tone ->
              val isSelected = selectedTone == tone
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) Indigo600 else SurfaceWhite)
                  .border(1.dp, if (isSelected) Indigo600 else BorderSubtle, RoundedCornerShape(12.dp))
                  .clickable { viewModel.setSelectedTone(tone) }
                  .padding(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Text(
                  text = tone,
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = if (isSelected) SurfaceWhite else Slate700,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                  )
                )
              }
            }
          }
        }
      }

      // 4. Target Platforms Multi-select
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "CHỌN NỀN TẢNG ĐĂNG BÀI",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Slate500,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              letterSpacing = 0.5.sp
            )
          )
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(SocialPlatform.entries) { platform ->
              val isSelected = selectedPlatforms.contains(platform)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .background(if (isSelected) platform.brandColor.copy(alpha = 0.1f) else SurfaceWhite)
                  .border(
                    1.5.dp,
                    if (isSelected) platform.brandColor else BorderSubtle,
                    RoundedCornerShape(14.dp)
                  )
                  .clickable { viewModel.togglePlatformSelection(platform) }
                  .padding(horizontal = 12.dp, vertical = 8.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = platform.shortLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = platform.brandColor
                    )
                  )
                  Text(
                    text = platform.displayName,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) Slate900 else Slate600,
                      fontSize = 11.5.sp
                    )
                  )
                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = platform.brandColor,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 5. Generate Button
      item {
        Button(
          onClick = { viewModel.generateAllContent() },
          enabled = !isGenerating,
          colors = ButtonDefaults.buttonColors(
            containerColor = Indigo600,
            contentColor = SurfaceWhite
          ),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("btn_generate_content")
        ) {
          if (isGenerating) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              color = SurfaceWhite,
              strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "AI Đang Tối Ưu Nội Dung...",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            )
          } else {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Tạo Nội Dung Đa Nền Tảng (1-Click AI)",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            )
          }
        }
      }

      // 6. Generated Content Preview & Editor Tabs
      if (generatedContents.isNotEmpty()) {
        item {
          Text(
            text = "KẾT QUẢ ĐÃ TỐI ƯU THEO TỪNG NỀN TẢNG",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Slate500,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              letterSpacing = 0.5.sp
            )
          )
        }

        // Platform Tab Row
        item {
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(selectedPlatforms.toList()) { platform ->
              val isActive = activePlatform == platform
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .background(if (isActive) platform.brandColor else SurfaceWhite)
                  .border(
                    1.dp,
                    if (isActive) platform.brandColor else BorderSubtle,
                    RoundedCornerShape(14.dp)
                  )
                  .clickable { viewModel.setActivePreviewPlatform(platform) }
                  .padding(horizontal = 14.dp, vertical = 9.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = platform.shortLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (isActive) SurfaceWhite else platform.brandColor
                    )
                  )
                  Text(
                    text = platform.displayName,
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (isActive) SurfaceWhite else Slate800,
                      fontSize = 12.sp
                    )
                  )
                }
              }
            }
          }
        }

        // Platform Preview Editor Card
        item {
          val currentContent = generatedContents[activePlatform]
          if (currentContent != null) {
            PlatformEditorCard(
              content = currentContent,
              platform = activePlatform,
              onTitleChange = { newTitle ->
                viewModel.updatePlatformContent(activePlatform) { it.copy(title = newTitle) }
              },
              onBodyChange = { newBody ->
                viewModel.updatePlatformContent(activePlatform) { it.copy(body = newBody) }
              },
              onHookChange = { newHook ->
                viewModel.updatePlatformContent(activePlatform) { it.copy(hook = newHook) }
              },
              onCtaChange = { newCta ->
                viewModel.updatePlatformContent(activePlatform) { it.copy(cta = newCta) }
              },
              onRemoveHashtag = { tagToRemove ->
                viewModel.updatePlatformContent(activePlatform) {
                  it.copy(hashtags = it.hashtags.filter { tag -> tag != tagToRemove })
                }
              },
              onAddHashtag = { newTag ->
                val formatted = if (newTag.startsWith("#")) newTag else "#$newTag"
                viewModel.updatePlatformContent(activePlatform) {
                  it.copy(hashtags = it.hashtags + formatted)
                }
              }
            )
          }
        }

        // Action Buttons (Publish Now, Schedule)
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = { viewModel.schedulePost("09:00 Ngày mai") },
              modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .testTag("btn_schedule_post"),
              shape = RoundedCornerShape(16.dp),
              border = androidx.compose.foundation.BorderStroke(1.5.dp, Indigo600),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Indigo600
              )
            ) {
              Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Lên Lịch Đăng",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              )
            }

            Button(
              onClick = { viewModel.publishImmediately() },
              modifier = Modifier
                .weight(1.2f)
                .height(50.dp)
                .testTag("btn_publish_all_now"),
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Emerald600,
                contentColor = SurfaceWhite
              )
            ) {
              Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Đăng Ngay Tất Cả",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              )
            }
          }
        }
      }
    }
  }

  // Template Selection Dialog
  if (showTemplateDialog) {
    AlertDialog(
      onDismissRequest = { showTemplateDialog = false },
      title = {
        Text(
          text = "Chọn Mẫu Prompt AI",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Slate900
          )
        )
      },
      text = {
        Column(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          AppRepository.templates.forEach { tpl ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Slate50)
                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                .clickable {
                  viewModel.applyTemplate(tpl)
                  showTemplateDialog = false
                }
                .padding(12.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = tpl.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Slate900,
                      fontSize = 13.sp
                    )
                  )
                  Text(
                    text = tpl.category,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Indigo600,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 10.sp
                    )
                  )
                }
                Text(
                  text = tpl.promptText,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate600,
                    fontSize = 11.5.sp
                  ),
                  maxLines = 2
                )
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showTemplateDialog = false }) {
          Text("Đóng")
        }
      },
      shape = RoundedCornerShape(24.dp),
      containerColor = SurfaceWhite
    )
  }
}

@Composable
fun RealEstateInputCard(
  form: RealEstateForm,
  onUpdate: ((RealEstateForm) -> RealEstateForm) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(SurfaceWhite)
      .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
      .padding(18.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text(
        text = "THÔNG TIN BẤT ĐỘNG SẢN CẦN ĐĂNG TIN",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Slate500,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          letterSpacing = 0.5.sp
        )
      )

      OutlinedTextField(
        value = form.propertyName,
        onValueChange = { text -> onUpdate { it.copy(propertyName = text) } },
        label = { Text("Tên/Tiêu đề BĐS") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Indigo600,
          unfocusedBorderColor = BorderSubtle
        )
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = form.price,
          onValueChange = { text -> onUpdate { it.copy(price = text) } },
          label = { Text("Giá bán") },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indigo600,
            unfocusedBorderColor = BorderSubtle
          )
        )
        OutlinedTextField(
          value = form.area,
          onValueChange = { text -> onUpdate { it.copy(area = text) } },
          label = { Text("Diện tích") },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indigo600,
            unfocusedBorderColor = BorderSubtle
          )
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = form.floors,
          onValueChange = { text -> onUpdate { it.copy(floors = text) } },
          label = { Text("Số tầng") },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indigo600,
            unfocusedBorderColor = BorderSubtle
          )
        )
        OutlinedTextField(
          value = form.bedrooms,
          onValueChange = { text -> onUpdate { it.copy(bedrooms = text) } },
          label = { Text("Số phòng ngủ") },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indigo600,
            unfocusedBorderColor = BorderSubtle
          )
        )
      }

      OutlinedTextField(
        value = form.address,
        onValueChange = { text -> onUpdate { it.copy(address = text) } },
        label = { Text("Địa chỉ & Vị trí") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Indigo600,
          unfocusedBorderColor = BorderSubtle
        )
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = form.legal,
          onValueChange = { text -> onUpdate { it.copy(legal = text) } },
          label = { Text("Pháp lý") },
          modifier = Modifier.weight(1.2f),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indigo600,
            unfocusedBorderColor = BorderSubtle
          )
        )
        OutlinedTextField(
          value = form.contactPhone,
          onValueChange = { text -> onUpdate { it.copy(contactPhone = text) } },
          label = { Text("Hotline / Zalo") },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indigo600,
            unfocusedBorderColor = BorderSubtle
          )
        )
      }
    }
  }
}

@Composable
fun PlatformEditorCard(
  content: com.example.data.model.PlatformContent,
  platform: SocialPlatform,
  onTitleChange: (String) -> Unit,
  onBodyChange: (String) -> Unit,
  onHookChange: (String) -> Unit,
  onCtaChange: (String) -> Unit,
  onRemoveHashtag: (String) -> Unit,
  onAddHashtag: (String) -> Unit
) {
  var newTagInput by remember { mutableStateOf("") }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(SurfaceWhite)
      .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
      .padding(18.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      // Platform Card Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(platform.brandColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = platform.shortLabel,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = platform.brandColor
              )
            )
          }
          Text(
            text = "Xem trước & Chỉnh sửa: ${platform.displayName}",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Slate900,
              fontSize = 13.5.sp
            )
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Slate100)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = platform.optimalMediaAspect,
            style = MaterialTheme.typography.labelSmall.copy(
              color = Slate600,
              fontWeight = FontWeight.SemiBold,
              fontSize = 10.sp
            )
          )
        }
      }

      // Title
      OutlinedTextField(
        value = content.title,
        onValueChange = onTitleChange,
        label = { Text("Tiêu đề / Caption mở đầu") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = platform.brandColor,
          unfocusedBorderColor = BorderSubtle
        )
      )

      // Video Script (If TikTok / Reels / Shorts)
      if (content.videoScript != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Indigo50)
            .border(1.dp, Indigo100, RoundedCornerShape(14.dp))
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                tint = Indigo600,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "KỊCH BẢN VIDEO QUAY DỰNG (AI SCRIPT)",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Indigo700,
                  fontSize = 10.sp
                )
              )
            }
            Text(
              text = content.videoScript,
              style = MaterialTheme.typography.bodySmall.copy(
                color = Slate800,
                fontSize = 11.5.sp,
                lineHeight = 16.sp
              )
            )
          }
        }
      }

      // Main Body
      OutlinedTextField(
        value = content.body,
        onValueChange = onBodyChange,
        label = { Text("Nội dung chi tiết") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 4,
        maxLines = 10,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = platform.brandColor,
          unfocusedBorderColor = BorderSubtle
        )
      )

      // CTA
      OutlinedTextField(
        value = content.cta,
        onValueChange = onCtaChange,
        label = { Text("Kêu gọi hành động (CTA)") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = platform.brandColor,
          unfocusedBorderColor = BorderSubtle
        )
      )

      // Hashtags Chip Cloud
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "HASHTAG ĐỀ XUẤT",
          style = MaterialTheme.typography.labelSmall.copy(
            color = Slate500,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp
          )
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = newTagInput,
            onValueChange = { newTagInput = it },
            placeholder = { Text("Thêm #hashtag...", fontSize = 11.sp) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
          )
          Button(
            onClick = {
              if (newTagInput.isNotBlank()) {
                onAddHashtag(newTagInput.trim())
                newTagInput = ""
              }
            },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Slate800)
          ) {
            Text("Thêm", fontSize = 11.sp)
          }
        }

        // Hashtag List Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(content.hashtags) { tag ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Indigo50)
                .clickable { onRemoveHashtag(tag) }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(
                  text = tag,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Indigo700,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                  )
                )
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Remove",
                  tint = Indigo500,
                  modifier = Modifier.size(12.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
