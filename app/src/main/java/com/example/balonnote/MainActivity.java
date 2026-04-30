package com.example.balonnote;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.balonnote.databinding.ActivityMainBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController controller;
    private int currentIndex = 0;
    // 记录SearchBar原始高度，用于动画恢复
    private int originalSearchBarHeight = 0;

    private final ArrayList<Integer> pageIndex = new ArrayList<>();
    private final Map<Integer, MotionLayout> destinationMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        if (navHostFragment != null) {
            controller = navHostFragment.getNavController();
        }
        pageIndex.add(R.id.noteFragment);
        pageIndex.add(R.id.todoFragment);

        MotionLayout noteMotion = findViewById(R.id.note_motion_layout);
        MotionLayout todoMotion = findViewById(R.id.todo_motion_layout);
        destinationMap.put(R.id.noteFragment, noteMotion);
        destinationMap.put(R.id.todoFragment, todoMotion);

        bindNavigation();

        // ========== 滚动动画：SearchBar收缩 + 大标题渐变 + 小标题弹出 ==========
        binding.lyContent.appbar.post(() -> {
            // 1. 获取基础尺寸
            int searchBarHeight = binding.lyContent.searchBar.getHeight();
            originalSearchBarHeight = searchBarHeight; // 保存原始高度
            int toolbarHeight = binding.lyContent.toolbar.getHeight();
            int totalScrollRange = binding.lyContent.appbar.getHeight() - toolbarHeight;

            // 2. 关键阈值
            int triggerOffset = searchBarHeight;               // SearchBar完全消失阈值
            int halfSearchHeight = triggerOffset / 2;          // SearchBar一半高度（动画起点）
            float fadeRange = dp2px(50);                      // 大标题渐变范围
            float titleOffsetY = dp2px(10);                   // 小标题偏移量

            // 3. 初始化状态

            // 4. 滚动监听（核心动画）
            binding.lyContent.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                int currentScroll = Math.abs(verticalOffset);

                // ==============================================
                // 【新增】SearchBar 三段式收缩动画
                // ==============================================
                ConstraintLayout searchBar = binding.lyContent.searchBar;
                ImageView searchIcon = binding.lyContent.ivSearchIcon;
                EditText searchEdit = binding.lyContent.etSearch;
                CollapsingToolbarLayout.LayoutParams searchParams = (CollapsingToolbarLayout.LayoutParams) searchBar.getLayoutParams();

                // 阶段1：滚动 < 一半Search高度 → 正常显示
                if (currentScroll <= halfSearchHeight) {
                    // 恢复原始状态
                    searchParams.height = originalSearchBarHeight;
                    searchBar.setLayoutParams(searchParams);
                    searchBar.setVisibility(View.VISIBLE);
                    searchIcon.setAlpha(1f);
                    searchIcon.setVisibility(View.VISIBLE);
                    searchEdit.setAlpha(1f);
                    searchEdit.setVisibility(View.VISIBLE);
                }
                // 阶段2：一半高度 ~ 全高度 → 内部控件消失 + 高度收缩一半
                else if (currentScroll <= triggerOffset) {
                    // 计算动画进度（0~1）
                    float progress = (currentScroll - halfSearchHeight) / (float) halfSearchHeight;
                    // 内部控件渐变消失
//                    searchIcon.setAlpha(1f - progress);
                    searchIcon.setVisibility(View.GONE);
//                    searchEdit.setAlpha(1f - progress);
                    searchEdit.setVisibility(View.GONE);
                    // 高度收缩到一半
                    searchParams.height = originalSearchBarHeight - (int) (originalSearchBarHeight * progress);
                    searchBar.setLayoutParams(searchParams);
                }
                // 阶段3：超过阈值 → SearchBar直接隐藏（无中间态）
                else {
                    searchBar.setVisibility(View.GONE);
                }

                // ==============================================
                // 标题切换逻辑：任何时刻只显示一个标题
                // 不允许两个同时出现，也不允许两个同时消失
                // ==============================================

                int titleEndOffset = triggerOffset + (int) fadeRange;
                int titleSwitchOffset = triggerOffset + (int) fadeRange / 2;

                // 最低透明度，避免切换前太淡导致像是消失了
                float minTitleAlpha = 0.2f;

                // search bar消失前
                if (currentScroll <= triggerOffset) {
                    // 完全显示大标题，隐藏顶部标题
                    binding.lyContent.collapseText.setVisibility(View.VISIBLE);
                    binding.lyContent.collapseText.setAlpha(1f);

                    binding.lyContent.toolbarTitle.setVisibility(View.INVISIBLE);
                    binding.lyContent.toolbarTitle.setAlpha(0f);
                    binding.lyContent.toolbarTitle.setTranslationY(titleOffsetY);


                } else if (currentScroll < titleSwitchOffset) {
                    // 只显示大标题，让大标题稍微淡出
                    float progress = (currentScroll - triggerOffset)
                            / (float) (titleSwitchOffset - triggerOffset);

                    float bigTitleAlpha = 1f - (1f - minTitleAlpha) * progress;

                    binding.lyContent.collapseText.setVisibility(View.VISIBLE);
                    binding.lyContent.collapseText.setAlpha(bigTitleAlpha);

                    binding.lyContent.toolbarTitle.setVisibility(View.INVISIBLE);
                    binding.lyContent.toolbarTitle.setAlpha(0f);
                    binding.lyContent.toolbarTitle.setTranslationY(titleOffsetY);


                } else if (currentScroll < titleEndOffset) {
                    // 只显示顶部标题，让顶部标题继续淡入
                    float progress = (currentScroll - titleSwitchOffset)
                            / (float) (titleEndOffset - titleSwitchOffset);

                    float toolbarTitleAlpha = minTitleAlpha + (1f - minTitleAlpha) * progress;

                    binding.lyContent.collapseText.setVisibility(View.INVISIBLE);
                    binding.lyContent.collapseText.setAlpha(0f);

                    binding.lyContent.toolbarTitle.setVisibility(View.VISIBLE);
                    binding.lyContent.toolbarTitle.setAlpha(toolbarTitleAlpha);
                    binding.lyContent.toolbarTitle.setTranslationY(titleOffsetY * (1f - progress));


                } else {
                    // 完全显示顶部标题，隐藏大标题
                    binding.lyContent.collapseText.setVisibility(View.INVISIBLE);
                    binding.lyContent.collapseText.setAlpha(0f);

                    binding.lyContent.toolbarTitle.setVisibility(View.VISIBLE);
                    binding.lyContent.toolbarTitle.setAlpha(1f);
                    binding.lyContent.toolbarTitle.setTranslationY(0f);

                }
            });
        });
    }

    // dp转px
    private int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void bindNavigation() {
        for (Map.Entry<Integer, MotionLayout> entry : destinationMap.entrySet()) {
            int fragmentID = entry.getKey();
            MotionLayout itemLayout = entry.getValue();
            itemLayout.setOnClickListener(view -> {
                int targetIndex = pageIndex.indexOf(fragmentID);
                if (controller == null || currentIndex == targetIndex) return;

                NavOptions options;
                if (currentIndex < targetIndex) {
                    options = new NavOptions.Builder()
                            .setEnterAnim(R.anim.slide_in_right)
                            .setExitAnim(R.anim.slide_out_left)
                            .setLaunchSingleTop(true)
                            .build();
                } else {
                    options = new NavOptions.Builder()
                            .setEnterAnim(R.anim.slide_in_left)
                            .setExitAnim(R.anim.slide_out_right)
                            .setLaunchSingleTop(true)
                            .build();
                }
                currentIndex = targetIndex;
                controller.navigate(fragmentID, null, options);
            });
        }

        controller.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (navDestination == null) return;
            controller.popBackStack();
            for (MotionLayout layout : destinationMap.values()) {
                layout.setProgress(0f);
            }
            MotionLayout current = destinationMap.get(navDestination.getId());
            String titleText = (current == destinationMap.get(R.id.noteFragment)) ?
                    getString(R.string.note_text) : getString(R.string.todo_text);
            binding.lyContent.collapseText.setText(titleText);
            binding.lyContent.toolbarTitle.setText(titleText);
            if (current != null) {
                current.transitionToEnd();
            }
        });
    }
}