package com.example.balonnote;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.balonnote.databinding.ActivityMainBinding;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController controller;
    private int currentIndex = 0;
    private boolean isTitleAnimated = false;
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
        // 设置导航路由
        bindNavigation();

        // 设置滚动标题渐变
        float fadeRange = Utils.getUtils().dp2px(50, getResources().getDisplayMetrics().density);
        // 给小标题设置初始状态：隐藏+向下偏移10dp（待弹出位置）
        float offsetY = Utils.getUtils().dp2px(10, getResources().getDisplayMetrics().density);
        binding.lyContent.toolbarTitle.setAlpha(0f);
        binding.lyContent.toolbarTitle.setTranslationY(offsetY);

        binding.lyContent.appbar.addOnOffsetChangedListener((appBarLayout, i) -> {
            int currentScroll = Math.abs(i);
            float alpha;

            // 阶段1：大标题渐变消失（此时小标题完全不显示）
            if (currentScroll <= fadeRange) {
                // 大标题透明度从1降到0
                alpha = 1f - (currentScroll / fadeRange);
                binding.lyContent.collapseText.setAlpha(alpha);

                // 往下滑时，重置小标题状态，准备下次弹出
                if (isTitleAnimated) {
                    binding.lyContent.toolbarTitle.animate().cancel();
                    binding.lyContent.toolbarTitle.setAlpha(0f);
                    binding.lyContent.toolbarTitle.setTranslationY(offsetY);
                    isTitleAnimated = false;
                }
            }
            // 阶段2：大标题完全消失后，触发小标题弹出动画（只执行一次）
            else {
                binding.lyContent.collapseText.setAlpha(0f);
                if (!isTitleAnimated) {
                    isTitleAnimated = true;
                    // 弹出动画：从偏移位置移到正常位置 + 淡入，200ms快速弹出
                    binding.lyContent.toolbarTitle.animate().alpha(1f).translationY(0).setDuration(200).setInterpolator(new DecelerateInterpolator()) // 减速插值，弹出更自然
                            .start();
                }
            }
        });

    }

    private void bindNavigation() {
        for (Map.Entry<Integer, MotionLayout> entry : destinationMap.entrySet()) {
            int fragmentID = entry.getKey();
            MotionLayout itemLayout = entry.getValue();
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int targetIndex = pageIndex.indexOf(fragmentID);
                    if (controller == null || currentIndex == targetIndex) return;

                    NavOptions options;
                    if (currentIndex < targetIndex) {
                        options = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).setLaunchSingleTop(true).build();
                    } else {
                        options = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_left).setExitAnim(R.anim.slide_out_right).setLaunchSingleTop(true).build();
                    }
                    currentIndex = targetIndex;
                    controller.navigate(fragmentID, null, options);

                }
            });
        }

        controller.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination == null) return;
                controller.popBackStack();
                for (MotionLayout layout : destinationMap.values()) {
                    layout.setProgress(0f);
                }
                MotionLayout current = destinationMap.get(navDestination.getId());
                //设置标题的文字
                String titleText = (current == destinationMap.get(R.id.noteFragment)) ? getResources().getString(R.string.note_text) : getResources().getString(R.string.todo_text);
                binding.lyContent.collapseText.setText(titleText);
                binding.lyContent.toolbarTitle.setText(titleText);

                if (current != null) {
                    current.transitionToEnd();
                }

            }
        });
    }
}