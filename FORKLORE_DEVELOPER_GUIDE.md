# ForkLore UI Redesign - Developer Quick Reference

## 📋 Files Modified/Created

### Resource Files
```
✅ values/dimens.xml (NEW)
   - All spacing, radius, and size constants
   - Use via @dimen/spacing_lg, @dimen/radius_xl, etc.

✅ values/styles.xml (NEW)  
   - Typography styles: TextAppearance.ForkLore.*
   - Use via android:textAppearance="@style/TextAppearance.ForkLore.Title"
   - Component styles: Widget.ForkLore.*

✅ values/themes.xml (UPDATED)
   - Material 3 theme with Material 3 component styles
   - AppBar, Button, TextInput styling

✅ values/colors.xml (VERIFIED - No changes needed)
   - Already has complete ForkLore palette

✅ values/strings.xml (UPDATED)
   - Added all new screen strings
   - All UI text now in strings resource
```

### Layout Files
```
✅ fragment_splash.xml (REDESIGNED)
   - Welcome/onboarding screen
   - Uses NestedScrollView for responsiveness
   - IDs: logo_icon, title_text, subtitle_text, btn_get_started, btn_login

✅ fragment_register.xml (REDESIGNED)
   - Registration screen with back button
   - White card with form fields
   - IDs: full_name_input, email_input, password_input, btn_create_account

✅ fragment_feed.xml (UPDATED)
   - AppBarLayout with white background
   - RecyclerView for posts
   - FAB for adding post
   - IDs: feed_toolbar, recycler_view, fab_add_post

✅ fragment_discover.xml (UPDATED)
   - AppBarLayout with refresh icon
   - RecyclerView for article cards
   - IDs: recycler_view_articles, refresh_icon

✅ fragment_profile.xml (REDESIGNED)
   - Large avatar, stats, action buttons
   - Posts RecyclerView
   - IDs: profile_image, profile_name, profile_bio, posts_count, 
          followers_count, following_count, edit_profile_button, 
          my_recipes_button, recycler_view_profile_posts
```

### Drawable Files Created
```
✅ ic_sparkle.xml - Logo/branding icon
✅ ic_heart.xml - Benefits section icon
✅ ic_people.xml - Community icon
✅ ic_settings.xml - Settings button icon
✅ ic_refresh.xml - Discover refresh icon
(ic_add.xml already existed)
```

---

## 🎯 Using the Design System

### Spacing
```xml
<!-- In XML layouts, use: -->
android:layout_margin="@dimen/spacing_lg"
android:padding="@dimen/spacing_xl"

<!-- Predefined values: -->
spacing_xs   = 4dp
spacing_sm   = 8dp
spacing_md   = 12dp
spacing_lg   = 16dp
spacing_xl   = 20dp
spacing_xxl  = 24dp
```

### Colors
```xml
<!-- Primary colors: -->
android:backgroundTint="@color/fl_coral"          <!-- Main CTA buttons -->
android:backgroundTint="@color/fl_blue"           <!-- "My Recipes" button -->
android:background="@color/fl_bg"                 <!-- Screen backgrounds -->
android:background="@color/fl_surface"            <!-- Cards, toolbars -->

<!-- Text colors: -->
android:textColor="@color/fl_text"                <!-- Main text (#2B2623) -->
android:textColor="@color/fl_text_muted"          <!-- Secondary text (#8A7F79) -->
```

### Typography
```xml
<!-- Use ForkLore text styles: -->
android:textAppearance="@style/TextAppearance.ForkLore.Headline"    <!-- 28sp, bold -->
android:textAppearance="@style/TextAppearance.ForkLore.Title"       <!-- 20sp, bold -->
android:textAppearance="@style/TextAppearance.ForkLore.TitleSmall"  <!-- 18sp, bold -->
android:textAppearance="@style/TextAppearance.ForkLore.Body"        <!-- 16sp -->
android:textAppearance="@style/TextAppearance.ForkLore.BodySmall"   <!-- 14sp, muted -->
android:textAppearance="@style/TextAppearance.ForkLore.Caption"     <!-- 12sp, muted -->
```

### Components
```xml
<!-- Buttons (use predefined styles): -->
<com.google.android.material.button.MaterialButton
    style="@style/Widget.ForkLore.Button.Filled"      <!-- Solid primary button -->
    android:backgroundTint="@color/fl_coral" />

<com.google.android.material.button.MaterialButton
    style="@style/Widget.ForkLore.Button.Outlined"    <!-- Outline button -->
    android:textColor="@color/fl_coral" />

<!-- Cards: -->
<com.google.android.material.card.MaterialCardView
    app:cardCornerRadius="@dimen/radius_xl"
    app:cardElevation="@dimen/elevation_md" />

<!-- Text Input: -->
<com.google.android.material.textfield.TextInputLayout
    style="@style/Widget.ForkLore.TextInputLayout" />
```

---

## 🔄 Navigation & Back Button

All secondary screens have back buttons in the toolbar:
```kotlin
// In Fragment's onViewCreated():
setupToolbarNavigation(binding.toolbar)
// This automatically handles back navigation with AppBarConfiguration

// The toolbar needs:
app:navigationIcon="@drawable/ic_arrow_back"
app:navigationIconTint="@color/fl_coral"
```

---

## 📱 Responsive Design Patterns

### Use NestedScrollView for Long Content
```xml
<androidx.core.widget.NestedScrollView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    android:fillViewport="true">
    <!-- Content here -->
</androidx.core.widget.NestedScrollView>
```

### RecyclerView with Padding
```xml
<androidx.recyclerview.widget.RecyclerView
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:paddingStart="@dimen/spacing_lg"
    android:paddingEnd="@dimen/spacing_lg"
    android:paddingTop="@dimen/spacing_md"
    android:paddingBottom="@dimen/spacing_xxl"
    app:layout_constraintBottom_toBottomOf="parent" />
```

### Proper ConstraintLayout Constraints
```xml
app:layout_constraintTop_toTopOf="parent"
app:layout_constraintBottom_toBottomOf="parent"
app:layout_constraintStart_toStartOf="parent"
app:layout_constraintEnd_toEndOf="parent"
```

---

## 🎨 Common Patterns

### Card with Content
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="@dimen/radius_xl"
    app:cardElevation="@dimen/elevation_md">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="@dimen/spacing_lg">
        <!-- Card content -->
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### Button Pair (Side by Side)
```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal">
    
    <com.google.android.material.button.MaterialButton
        style="@style/Widget.ForkLore.Button.Outlined"
        android:layout_width="0dp"
        android:layout_weight="1"
        android:layout_marginEnd="@dimen/spacing_md" />
    
    <com.google.android.material.button.MaterialButton
        style="@style/Widget.ForkLore.Button.Filled"
        android:layout_width="0dp"
        android:layout_weight="1" />
</LinearLayout>
```

### Icon with Text (Horizontal)
```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal">
    
    <ImageView
        android:layout_width="@dimen/icon_size_lg"
        android:layout_height="@dimen/icon_size_lg"
        android:tint="@color/fl_coral"
        android:layout_marginEnd="@dimen/spacing_lg" />
    
    <LinearLayout
        android:layout_width="0dp"
        android:layout_weight="1"
        android:orientation="vertical">
        <TextView android:text="Title" android:textStyle="bold" />
        <TextView android:text="Subtitle" android:textColor="@color/fl_text_muted" />
    </LinearLayout>
</LinearLayout>
```

---

## 🔧 Kotlin Code Patterns

### Setting Up Toolbar Navigation
```kotlin
class YourFragment : BaseAuthFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarNavigation(binding.toolbar)
        // Rest of setup
    }
}
```

### Accessing Colors
```kotlin
// In code:
val coralColor = ContextCompat.getColor(context, R.color.fl_coral)
val creamBg = ContextCompat.getColor(context, R.color.fl_bg)
```

### Setting Icon Tint
```kotlin
binding.searchIcon.setImageTintList(
    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.fl_coral))
)
```

---

## ⚙️ Important Notes

1. **All Kotlin logic is preserved** - Only UI/layout/resources changed
2. **ViewBinding IDs mostly preserved** - Check specific screens if unsure
3. **Navigation unchanged** - All routes and actions still work
4. **Material 3 throughout** - Use Material 3 components only
5. **Responsive by default** - Use ConstraintLayout + NestedScrollView patterns

---

## 🚀 Building & Testing

```bash
# Build
./gradlew build

# Install debug APK
./gradlew installDebug

# Run tests
./gradlew test
```

---

## 📚 Resource References

- **Colors**: `res/values/colors.xml`
- **Dimensions**: `res/values/dimens.xml`
- **Styles**: `res/values/styles.xml`
- **Strings**: `res/values/strings.xml`
- **Themes**: `res/values/themes.xml`

---

## ✅ Verification Checklist for New Features

When adding new screens:

- [ ] Use `@color/fl_coral` for CTA buttons
- [ ] Use `@color/fl_bg` for screen background
- [ ] Use `@color/fl_surface` for cards/toolbar
- [ ] Use `@dimen/radius_xl` for cards (24dp)
- [ ] Use `@dimen/spacing_lg` for padding (16dp)
- [ ] Use ForkLore text styles for all text
- [ ] Add back button if not root screen
- [ ] Test responsive on small and large screens
- [ ] Verify colors match design system

---

## 🎯 Design System Summary

| Element | Color | Size | Notes |
|---------|-------|------|-------|
| CTA Buttons | #F17559 (coral) | 52dp height | Use `Widget.ForkLore.Button.Filled` |
| Outline Buttons | Coral stroke | 52dp height | Use `Widget.ForkLore.Button.Outlined` |
| Secondary Buttons | #3B82F6 (blue) | 52dp height | "My Recipes" button |
| Screen Background | #FAF3EE (cream) | Full screen | Use `android:background="@color/fl_bg"` |
| Cards/Toolbars | #FFFFFF (white) | N/A | Use white with 24dp radius |
| Primary Text | #2B2623 (dark) | Various | Use ForkLore text styles |
| Secondary Text | #8A7F79 (muted) | Various | Use `TextAppearance.ForkLore.BodySmall` |
| Accent Icons | #F17559 (coral) | 20-32dp | For CTAs, selected states |
| Border/Stroke | #EADFD8 | 1dp | For text fields, outlines |

---

**Version**: 1.0 - February 20, 2026
**Status**: Complete & Production Ready

