package com.eventyay.organizer.ui.editor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.databinding.DataBindingUtil;

import com.eventyay.organizer.R;
import com.eventyay.organizer.databinding.TextEditorLayoutBinding;

public class RichEditorActivity extends AppCompatActivity {

    public static final String TAG_RICH_TEXT = "rich_text";

    private TextEditorLayoutBinding binding;
    private AlertDialog linkDialog;
    private AlertDialog saveAlertDialog;
    private String description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.text_editor_layout);

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.editor.setPlaceholder(getString(R.string.enter_text));

        Intent intent = getIntent();
        if (intent != null) {
            description = intent.getStringExtra(TAG_RICH_TEXT);
            if (!TextUtils.isEmpty(description) && !description.equals(getString(R.string.describe_event))) {
                binding.editor.setHtml(description);
            }
        }

        binding.actionUndo.setOnClickListener(v -> binding.editor.undo());
        binding.actionRedo.setOnClickListener(v -> binding.editor.redo());
        binding.actionBold.setOnClickListener(v -> binding.editor.setBold());
        binding.actionItalic.setOnClickListener(v -> binding.editor.setItalic());
        binding.actionStrikethrough.setOnClickListener(v -> binding.editor.setStrikeThrough());
        binding.actionInsertBullets.setOnClickListener(v -> binding.editor.setBullets());
        binding.actionInsertNumbers.setOnClickListener(v -> binding.editor.setNumbers());
        binding.actionInsertLink.setOnClickListener(v -> {
            if (linkDialog == null) {
                createLinkDialog();
            }
            linkDialog.show();
        });
    }

    private void createLinkDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText text = new EditText(this);
        text.setHint(getString(R.string.text));
        layout.addView(text);

        final EditText link = new EditText(this);
        link.setHint(getString(R.string.insert_url));
        layout.addView(link);

        linkDialog = new AlertDialog.Builder(this)
            .setPositiveButton(getString(R.string.create), (dialog, which) -> {
                binding.editor.insertLink(link.getText().toString(), text.getText().toString());
            })
            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                dialog.dismiss();
            })
            .setView(layout)
            .create();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rich_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                saveChanges(binding.editor.getHtml());
                return true;
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (binding.editor.getHtml() == null || binding.editor.getHtml().equals(description)) {
            saveChanges(description);
            return;
        }
        if (saveAlertDialog == null) {
            saveAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog))
                .setMessage(getString(R.string.save_changes))
                .setPositiveButton(getString(R.string.save), (dialog, which) -> {
                    saveChanges(binding.editor.getHtml());
                })
                .setNegativeButton(getString(R.string.discard), (dialog, which) -> {
                    dialog.dismiss();
                    saveChanges(description);
                })
                .create();
        }
        saveAlertDialog.show();
    }

    private void saveChanges(String descriptionToSave) {
        getIntent().putExtra(TAG_RICH_TEXT, descriptionToSave);
        setResult(RESULT_OK, getIntent());
        finish();
    }
}
