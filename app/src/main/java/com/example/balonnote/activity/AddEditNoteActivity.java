package com.example.balonnote.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balonnote.R;
import com.example.balonnote.databinding.ActivityAddEditNoteBinding;
import com.example.balonnote.viewmodel.NoteViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class AddEditNoteActivity extends AppCompatActivity {

    private ActivityAddEditNoteBinding binding;

    private NoteViewModel noteViewModel;

    private boolean isUndoRedo = false;
    private String beforeString = "";
    private String currentString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        noteViewModel = new NoteViewModel(getApplicationContext());

        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.item_back) {
                    isUndoRedo = true;
                    binding.contentEditText.setText(beforeString);
                    isUndoRedo = false;
                    return true;
                }
                if (id == R.id.item_forward) {
                    isUndoRedo = true;
                    binding.contentEditText.setText(currentString);
                    isUndoRedo = false;
                    return true;
                }
                if (id == R.id.item_more) {
                    return true;
                }
                return false;
            }
        });

        binding.contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(isUndoRedo)
                    return;
                currentString = editable.toString();
                binding.toolBar.getMenu().getItem(0).setEnabled(!currentString.equals(beforeString));
                binding.toolBar.getMenu().getItem(1).setEnabled(!currentString.equals(beforeString));

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isUndoRedo)
                    return;
                beforeString = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int total = charSequence.length();
                binding.totalText.setText(total+"字");
            }
        });

    }
}