package com.example.balonnote;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private NavController controller;
    private int currentIndex = 0;
    private final ArrayList<Integer> pageIndex = new ArrayList<>();
    private final Map<Integer, MotionLayout> destinationMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
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

        for (Map.Entry<Integer, MotionLayout> entry : destinationMap.entrySet()) {
            int fragmentID = entry.getKey();
            MotionLayout itemLayout = entry.getValue();
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int targetIndex = pageIndex.indexOf(fragmentID);
                    if (controller == null || currentIndex==targetIndex) return;

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
                if (current != null) {
                    current.transitionToEnd();
                }

            }
        });

    }
}