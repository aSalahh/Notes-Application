package com.q_silver.notes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.text.SimpleDateFormat;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.q_silver.notes.ETRA_ID";
    public static final String EXTRA_TITLE =
            "com.q_silver.notes.ETRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.q_silver.notes.ETRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY =
            "com.q_silver.notes.EXTRA_PRIORITY";
    private EditText etTitle;
    private EditText etDescription;
    private NumberPicker numberPickerPriority;
    private static final int STORAGE_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        etTitle = findViewById(R.id.edit_text_title);
        etDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle(getString(R.string.edit_note));
            etTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            etDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
        } else {
            setTitle(getString(R.string.add_note));
        }
    }

    private void saveNote() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        int priority = numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toasty.info(this, getString(R.string.check_empety_validation), Toast.LENGTH_SHORT, false).show();
            return;
        }

        Intent dataIntent = new Intent();
        dataIntent.putExtra(EXTRA_TITLE, title);
        dataIntent.putExtra(EXTRA_DESCRIPTION, description);
        dataIntent.putExtra(EXTRA_PRIORITY, priority);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            dataIntent.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, dataIntent);
        finish();


    }

    private void convertToPdf() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, STORAGE_CODE);

            } else {
                savePdf();

            }

        } else {
            savePdf();

        }
    }

    private void savePdf() {
        Document mDocument = new Document();
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String fileName = title;

        String filePath = Environment.getExternalStorageDirectory() + "/" + fileName + ".pdf";

        try {

            PdfWriter.getInstance(mDocument, new FileOutputStream(filePath));
            mDocument.open();
            Font chapterFont = FontFactory.getFont("assets/arial.ttf", BaseFont.IDENTITY_H, 16, Font.BOLDITALIC);
            Chunk chunk = new Chunk(title, chapterFont);
            mDocument.addTitle(title);
            mDocument.add(new Paragraph(chunk));
            mDocument.add(new Paragraph(description));
            mDocument.close();

            Toasty.info(this, getString(R.string.generate_in) + filePath, Toasty.LENGTH_LONG, false).show();


        } catch (Exception e) {
            Toasty.info(this, e.getMessage(), Toasty.LENGTH_LONG, false).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePdf();

                } else {
                    Toasty.info(this, getString(R.string.Permission_denied), Toasty.LENGTH_LONG, false).show();

                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            case R.id.convert_to_pdf:
                convertToPdf();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


}