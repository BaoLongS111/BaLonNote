package com.example.balonnote.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.balonnote.R;
import com.example.balonnote.adapter.NoteAdapter;
import com.example.balonnote.databinding.FragmentNoteBinding;
import com.example.balonnote.entity.Note;
import com.example.balonnote.viewmodel.NoteViewModel;

import java.util.List;


public class NoteFragment extends Fragment {

    private static final String TAG = "MYTAG";
    private FragmentNoteBinding binding;
    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private int lastNotesCount = 0;

    public static NoteFragment newInstance() {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoteBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noteViewModel = new NoteViewModel(getContext());
        adapter = new NoteAdapter(getContext());
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        binding.recyclearView.setLayoutManager(staggeredGridLayoutManager);
        binding.recyclearView.setAdapter(adapter);
        noteViewModel.getAllNotes().observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                boolean isScroll = notes.size()> lastNotesCount;
                lastNotesCount = notes.size();
                adapter.submitList(notes,()->{
                    staggeredGridLayoutManager.invalidateSpanAssignments();

                    if(isScroll){
                        binding.recyclearView.post(()->{
                           binding.recyclearView.scrollToPosition(0);
                        });
                    }
                });
            }
        });
    }
}